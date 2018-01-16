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
import ch.bbw.model.utils.TimeConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    long inviteTime = 30000;
    boolean inviteSent;
    @FXML
    private Label username;
    @FXML
    private VBox users, receivedInvites;
    @FXML
    private TextField ipadress;
    private TimeConverter timeConverter;
    private UserManager userManager;
    private InviteManager inviteManager;
    private InviteSender inviteSender;
    private Timeline fiveSeconds;
    private NetToolsSearch search;

    public void addUser(InetAddress address) {

        Button button = new Button(address.getHostAddress());
        button.setOnAction(this::handleUser);
        button.setStyle("-fx-background-color: #1F2D3F");
        button.setTextFill(Color.WHITE);
        users.getChildren().add(button);
        userManager.addUser(new User(address, button));
    }

    @FXML
    private void handleUser(ActionEvent event) {

        Button button = (Button) event.getSource();
        for (User user : userManager.getUsers()) {
            if (user.getButton().equals(button)) {
                System.out.println("Sending invite...");
                sendInvite(user.getAddress());
            }
        }
    }

    @FXML
    private void handleAccept(ActionEvent event) {

        Button button = (Button) event.getSource();
        Invite invite = inviteManager.getInviteByButton(button);
        sendAccept(invite);
    }

    @FXML
    private void handleInviteButton(ActionEvent event) {
        try {
            gameUserCountDown(InetAddress.getLocalHost(), "TestGame");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendInvite(InetAddress address) {

        Random random = new Random();
        Invite invite = new Invite(System.currentTimeMillis(), System.currentTimeMillis() + inviteTime, address.toString(), random.nextInt(), address);

        if (!inviteManager.inviteExists(invite)) {

            inviteManager.addSentInvite(invite);

            Packet packet = null;
            try {
                packet = new InvitePacket(username.getText(), invite.getDeprecationTime(), invite.getId(), InetAddress.getLocalHost().getHostAddress().toString());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            packet.addTarget(new InetSocketAddress(address, InviteSender.port));

            HBox field = new HBox();
            Label name = new Label(invite.getRecallAddress());
            field.getChildren().add(name);
            invite.setContainer(field);

            try {
                inviteSender.sendPacket(packet);
                inviteSent = true;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to send Packet");
            }
        }

    }

    private void receivedInvite(InvitePacket packet) throws IOException {

        HBox box = new HBox();
        Button button = new Button("Accept");
        button.setOnAction(this::handleAccept);
        Label label = new Label(packet.getName());
        System.out.println("Invite received from: " + packet.getName() + " :" + packet.getRecallAdress());

        Invite invite = new Invite(packet.getDeprecationTime() - inviteTime, packet.getDeprecationTime(), packet.getId(), box, packet.getRecallAdress());
        invite.setName(packet.getName());
        invite.setAcceptButton(button);

        if (!inviteManager.inviteExists(invite)) {
            inviteManager.addReceivedInvite(invite);
            button.setStyle("-fx-background-color: #1F2D3F");
            button.setTextFill(Color.WHITE);
            box.getChildren().add(button);
            box.getChildren().add(label);
            receivedInvites.getChildren().add(box);
        }
    }

    private void sendAccept(Invite invite) {

        Packet packet = new AcceptPacket(invite.getId(), true, username.getText());
        try {
            System.out.println(invite.getRecallAddress());
            packet.addTarget(new InetSocketAddress(InetAddress.getByName(invite.getRecallAddress()), InviteSender.port));
            inviteSender.sendPacket(packet);
            gameUserCountDown(InetAddress.getByName(invite.getRecallAddress()), invite.getName());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveAccept(AcceptPacket packet) {

        if (packet.isAcceptence()) {
            Invite invite = inviteManager.getInviteById(packet.getId());
            if (invite != null) {
                try {
                    gameServerCountDown(packet.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("No Packet was sent with this ID: " + invite.getId());
            }
        }
    }

    private void updateTimeSentInvites(VBox container) {

        ObservableList<Node> hBoxes = container.getChildren();
        for (Node hBox : hBoxes) {

            HBox box = (HBox) hBox;
            Label label = (Label) box.getChildren().get(1);
            Invite invite = inviteManager.getInviteByContainer(box);
            label.setText(timeConverter.longToString(timeConverter.getSecDif(invite.getDeprecationTime(), invite.getTimeSent())));
        }
    }

    private void gameServerCountDown(String secondUserName) throws IOException {
        String username = this.username.getText();
        Stage stage1 = (Stage) this.username.getScene().getWindow();
        stage1.close();


        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/FXMLGame.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        FXMLGameController controller = fxmlLoader.<FXMLGameController>getController();

        controller.initName(username);
        Server server = new Server();
        Client client = new Client(new InetSocketAddress(InetAddress.getLocalHost(), Client.port));

        controller.initClient(client);
        controller.initServerAddress(InetAddress.getLocalHost());
        controller.initHost(true);
        controller.initNames(username, secondUserName);

        client.addObserver(controller);

        new Thread(server).start();
        new Thread(client).start();

        Scene scene = new Scene(root1);
        stage.setOnCloseRequest((e) -> {
            System.out.println("Shutting down");
            server.setRunning(false);
            client.setRunning(false);
        });

        stage.setTitle("Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        search.setRunning(false);
    }

    private void gameUserCountDown(InetAddress secondPlayer, String secondUserName) throws IOException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String username = this.username.getText();
        System.out.println("Username: " + username);
        Stage stage1 = (Stage) this.username.getScene().getWindow();
        stage1.close();

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/FXMLGame.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        FXMLGameController controller = fxmlLoader.<FXMLGameController>getController();

        Client client = new Client(new InetSocketAddress(secondPlayer, Client.port));

        controller.initClient(client);
        controller.initServerAddress(secondPlayer);
        controller.initHost(false);
        controller.initName(username);
        controller.initNames(secondUserName, username);

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
        timeConverter = new TimeConverter();

        try {
            inviteSender = new InviteSender();
        } catch (IOException e) {
            System.err.print("Failed to initialize InviteSender");
            e.printStackTrace();
        }

        fiveSeconds = new Timeline(new KeyFrame(Duration.millis(50), (e) -> {
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
