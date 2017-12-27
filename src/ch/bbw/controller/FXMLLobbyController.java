package ch.bbw.controller;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ch.bbw.model.data.User;
import ch.bbw.model.network.NetToolsSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author TheBromo
 */
public class FXMLLobbyController implements Initializable {

    @FXML
    private Label username;
    @FXML
    private VBox users;
    @FXML
    private TextField ipadress;
    private ArrayList<User> usersList = new ArrayList<>();
    private User activeOpponent;
    private NetToolsSearch search;


    public void addUser(InetAddress address) {
        Button button = new Button(address.getHostAddress());
        button.setOnAction(this::handleUser);
        users.getChildren().add(button);
        usersList.add(new User(new InetSocketAddress(address, 6666), button));
    }

    @FXML
    private void handleUser(ActionEvent event) {
        Button button = (Button) event.getSource();
        for (User user : usersList) {
            if (user.getButton().equals(button)) {
                activeOpponent = user;
                System.out.println("sdas");
            }
        }
    }

    @FXML
    private void handleInviteButton(ActionEvent event) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ipadress.getText());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sendInvite(address);

    }

    @FXML
    private void handleSearch(ActionEvent event){
        ArrayList<InetAddress>addresses= search.getAddresses();
        for (InetAddress address:addresses){
            users.getChildren().clear();
            addUser(address);
        }

    }

    private void sendInvite(InetAddress address) {

    }

    public void receiveInvite() {

    }


    public void initUserName(String username) {
        this.username.setText(username);
    }

    public void initNettools(NetToolsSearch search) {
        this.search = search;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }


}
