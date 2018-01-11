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
import ch.bbw.model.network.Inviter;
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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author TheBromo
 */
public class FXMLLobbyController implements Initializable, Observer {

    long inviteTime = 30000;
    boolean inviteSent, inviteAccepted;
    @FXML
    private Label username;
    @FXML
    private VBox users, sentInvites, receivedInvites;
    @FXML
    private TextField ipadress;
    private TimeConverter timeConverter;
    private UserManager userManager;
    private InviteManager inviteManager;
    private Inviter inviter;
    private Timeline fiveSeconds;

    public void addUser(InetAddress address) {
        Button button = new Button(address.getHostAddress());
        button.setOnAction(this::handleUser);
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
    private void handleInviteButton(ActionEvent event) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ipadress.getText());
            sendInvite(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void disableAllInvites() {
        for (Node button : users.getChildren()) {
            Button edit = (Button) button;
            edit.setDisable(true);
        }
    }


    private void enableAllInvites() {
        for (Node button : users.getChildren()) {
            Button edit = (Button) button;
            edit.setDisable(false);
        }
    }

    private void sendInvite(InetAddress address) {
        Random random = new Random();
        Invite invite = new Invite(System.currentTimeMillis(), System.currentTimeMillis() + inviteTime, address.toString(), random.nextInt());
        if (!inviteManager.inviteExists(invite)) {
            inviteManager.addReceivedInvite(invite);

            Packet packet = new InvitePacket(username.getText(), invite.getDeprecationTime(), invite.getId());
            packet.addTarget(address);

            HBox field = new HBox();

            Label timeLeft = new Label(timeConverter.longToString(timeConverter.getSecDif(invite.getDeprecationTime(), invite.getTimeSent())));
            Label name = new Label(invite.getTarget());
            field.getChildren().add(name);
            field.getChildren().add(timeLeft);
            invite.setContainer(field);

            sentInvites.getChildren().add(field);

            try {
                inviter.sendPacket(packet);
                inviteSent = true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to send Packet");
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


    private void gameCountDown() {
        //TODO
    }


    public void receivedInvite(InvitePacket packet) throws IOException {

        HBox box = new HBox();
        Button button = new Button(packet.getName());
        Label label = new Label(timeConverter.longToString(timeConverter.getSecDif(packet.getStartTime(), System.currentTimeMillis())));

        Invite invite = new Invite(packet.getStartTime(), packet.getId(), box);
        if (!inviteManager.inviteExists(invite)) {
            inviteManager.addReceivedInvite(invite);

            box.getChildren().add(button);
            box.getChildren().add(label);
            receivedInvites.getChildren().add(box);
        }
    }

    public void initUserName(String username) {
        this.username.setText(username);
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inviteManager = new InviteManager();
        userManager = new UserManager();
        timeConverter = new TimeConverter();

        try {
            inviter = new Inviter(8888);
        } catch (IOException e) {
            System.out.print("Failed to initialize Inviter");
            e.printStackTrace();
        }

        fiveSeconds = new Timeline(new KeyFrame(Duration.millis(50), (e) -> {
            try {
                ArrayList<Packet> packets = inviter.readReceivedPacket();
                if (packets.size() != 0) {
                    for (Packet packet : packets) {
                        if (packet instanceof InvitePacket) {
                            InvitePacket invitePacket = (InvitePacket) packet;
                            receivedInvite(invitePacket);
                        } else if (packet instanceof AcceptPacket) {
                            AcceptPacket acceptPacket = (AcceptPacket) packet;
                            for (Invite invite : inviteManager.getSentInvites()) {
                                if (invite.getId() == acceptPacket.getId() && acceptPacket.isAcceptence()) {
                                    gameCountDown();
                                }
                            }
                        }
                    }
                }

                updateTimeSentInvites(sentInvites);
                updateTimeSentInvites(receivedInvites);

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
