package ch.bbw.controller;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ch.bbw.model.data.Invite;
import ch.bbw.model.data.InviteManager;
import ch.bbw.model.data.User;
import ch.bbw.model.data.UserManager;
import ch.bbw.model.network.Client;
import ch.bbw.model.network.InviteSender;
import ch.bbw.model.network.NetToolsSearch;
import ch.bbw.model.network.Server;
import ch.bbw.model.network.packets.AcceptPacket;
import ch.bbw.model.network.packets.InvitePacket;
import ch.bbw.model.network.packets.Packet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author TheBromo
 */
public class FXMLLobbyController implements Initializable, Observer {

    private long inviteTime = 30000;
    @FXML
    private Label username;
    @FXML
    private VBox users, receivedInvites;
    @FXML
    private TextField fieldCount;

    private UserManager userManager;
    private InviteManager inviteManager;
    private InviteSender inviteSender;
    private NetToolsSearch search;

    private boolean isFieldCountSet() {
        //checks if text was entered
        if (fieldCount.getText().equals("")) {
            return false;
        }
        String number = fieldCount.getText();
        for (int index = 0; index < number.length(); index++) {
            char c = number.charAt(index);
            //if the char is not a number
            if (!(c >= 48 && c <= 57)) {
                return false;
            }
        }
        //if the string just consists of numbers
        return true;
    }

    private int getDimension() {
        //Dimension = Field height/width
        if (isFieldCountSet()) {
            return Integer.parseInt(fieldCount.getText());
        } else {
            //if the dimensions haven't been set correctly returns the standard value
            return 10;
        }
    }

    private void addUser(InetAddress address) {
        //adds a button with the users address
        Button button = new Button(address.getHostAddress());
        //handleUser will be called if the button is pressed
        button.setOnAction(this::handleUser);
        button.setStyle("-fx-background-color: #1F2D3F");
        button.setTextFill(Color.WHITE);
        users.getChildren().add(button);
        //adds the user, that if the button is clicked the according data can be called
        userManager.addUser(new User(address, button));
    }

    @FXML
    private void handleUser(ActionEvent event) {
        //sends an invite to the according user
        Button button = (Button) event.getSource();
        System.out.println("Sending invite...");
        sendInvite(userManager.getUserByButton(button).getAddress());
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        //send an acceptPacket to the according user
        Button button = (Button) event.getSource();
        Invite invite = inviteManager.getInviteByButton(button);
        sendAccept(invite);
    }

    private void sendInvite(InetAddress address) {
        //sends an UDP invite to the address
        Random random = new Random();
        Invite invite = new Invite(System.currentTimeMillis(), System.currentTimeMillis() + inviteTime, address.toString(), random.nextInt(), getDimension());

        if (!inviteManager.inviteExists(invite)) {

            inviteManager.addSentInvite(invite);

            Packet packet = null;
            try {
                //packet is built
                packet = new InvitePacket(username.getText(), invite.getDeprecationTime(), invite.getId(), getDimension(), InetAddress.getLocalHost().getHostAddress().toString());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            packet.addTarget(new InetSocketAddress(address, InviteSender.port));

            try {
                //sends packet
                inviteSender.sendPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void receivedInvite(InvitePacket packet) {
        //creates a HBox with a button to accept the invite and the name of the sender
        HBox box = new HBox();
        Button button = new Button("Accept");
        button.setOnAction(this::handleAccept);
        Label label = new Label(packet.getName());
        System.out.println("Invite received from: " + packet.getName() + " :" + packet.getRecallAdress());

        //Invite is created to use if the invite gets accepted
        Invite invite = new Invite(packet.getDeprecationTime() - inviteTime, packet.getDeprecationTime(), packet.getId(), packet.getFieldSize(), packet.getRecallAdress());
        invite.setName(packet.getName());
        invite.setAcceptButton(button);

        if (!inviteManager.inviteExists(invite)) {
            inviteManager.addReceivedInvite(invite);

            button.setStyle("-fx-background-color: #1F2D3F");
            button.setTextFill(Color.WHITE);
            label.setTextFill(Color.WHITE);

            //adds the parts together and into the received invites box
            box.getChildren().add(button);
            box.getChildren().add(label);
            receivedInvites.getChildren().add(box);
        }
    }

    private void sendAccept(Invite invite) {
        //creates a Packet to answer an invite
        Packet packet = new AcceptPacket(invite.getId(), true, username.getText());
        try {
            //Sends Packet
            System.out.println(invite.getRecallAddress());
            packet.addTarget(new InetSocketAddress(InetAddress.getByName(invite.getRecallAddress()), InviteSender.port));
            inviteSender.sendPacket(packet);

            //starts Game
            gameUserCountDown(InetAddress.getByName(invite.getRecallAddress()), invite.getName(), invite);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveAccept(AcceptPacket packet) {
        //Starts game if invite has been accepted
        if (packet.hasAccepted()) {
            Invite invite = inviteManager.getInviteById(packet.getId());
            if (invite != null) {
                try {
                    gameServerCountDown(packet.getName(), invite);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void gameServerCountDown(String secondUserName, Invite invite) throws IOException {
        //gets own username
        String username = this.username.getText();
        //closes current window
        Stage stage1 = (Stage) this.username.getScene().getWindow();
        stage1.close();

        //creates new window
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ch/bbw/view/FXMLGame.fxml"));
        Parent root1 = fxmlLoader.load();

        //gets controller
        FXMLGameController controller = fxmlLoader.getController();
        //inits own name
        controller.initName(username);
        //creates server and client
        Server server = new Server();
        Client client = new Client(new InetSocketAddress(InetAddress.getLocalHost(), Client.port));

        //init all other variables
        controller.initClient(client);
        //Server address
        controller.initServerAddress(InetAddress.getLocalHost());
        //if it's the host
        controller.initHost(true);
        //inits names
        controller.initNames(username, secondUserName);
        //init the cellmanger with width and height
        controller.initCellManager(invite.getFieldSize(), invite.getFieldSize());
        //Adds the controller as an observer
        client.addObserver(controller);
        //starts server and client
        new Thread(server).start();
        new Thread(client).start();

        Scene scene = new Scene(root1);
        stage.setOnCloseRequest((e) -> {
            //closes all other threads
            System.out.println("Shutting down");
            server.setRunning(false);
            client.setRunning(false);
        });

        stage.setTitle("Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        //Stops nettools search
        search.setRunning(false);
    }

    private void gameUserCountDown(InetAddress secondPlayer, String secondUserName, Invite invite) throws IOException {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String username = this.username.getText();
        System.out.println("Username: " + username);
        Stage stage1 = (Stage) this.username.getScene().getWindow();
        stage1.close();

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ch/bbw/view/FXMLGame.fxml"));
        Parent root1 = fxmlLoader.load();

        FXMLGameController controller = fxmlLoader.getController();

        Client client = new Client(new InetSocketAddress(secondPlayer, Client.port));

        controller.initClient(client);
        controller.initServerAddress(secondPlayer);
        controller.initHost(false);
        controller.initName(username);
        controller.initNames(secondUserName, username);
        controller.initCellManager(invite.getFieldSize(), invite.getFieldSize());

        client.addObserver(controller);

        new Thread(client).start();

        Scene scene = new Scene(root1);
        stage.setOnCloseRequest((e) -> {
            System.out.println("Shutting down");
            client.setRunning(false);
        });

        stage.setTitle("Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        search.setRunning(false);
    }

    public void initUserName(String username) {
        this.username.setText(username);
    }

    public void initNetThread(NetToolsSearch thread) {
        this.search = thread;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        inviteManager = new InviteManager();
        userManager = new UserManager();

        try {
            inviteSender = new InviteSender();
        } catch (IOException e) {
            System.err.print("Failed to initialize InviteSender");
            e.printStackTrace();
        }

        Timeline fiveSeconds = new Timeline(new KeyFrame(Duration.millis(50), (e) -> {
            try {
                ArrayList<Packet> packets = inviteSender.readReceivedPacket();
                if (packets.size() != 0) {
                    for (Packet packet : packets) {
                        if (packet instanceof InvitePacket) {

                            InvitePacket invitePacket = (InvitePacket) packet;
                            receivedInvite(invitePacket);
                        } else if (packet instanceof AcceptPacket) {

                            AcceptPacket acceptPacket = (AcceptPacket) packet;
                            receiveAccept(acceptPacket);
                        }
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println();
            }
        }));

        fiveSeconds.setCycleCount(Timeline.INDEFINITE);
        fiveSeconds.play();
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> addUser((InetAddress) arg));
    }
}
