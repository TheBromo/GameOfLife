package ch.bbw.controller;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ch.bbw.model.NetToolsSearch;
import ch.bbw.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.InetAddress;
import java.net.URL;
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
    private ArrayList<User> usersList = new ArrayList<>();
    private User activeOpponent;
    private NetToolsSearch search;


    public void addUser(InetAddress address) {
        Button button = new Button(address.getHostAddress());
        button.setOnAction(this::handleUser);
        users.getChildren().add(button);
        usersList.add(new User(address, button));
    }

    @FXML
    private void handleUser(ActionEvent event) {
        Button button = (Button) event.getSource();
        for (User user : usersList) {
            if (user.getButton().equals(button)) {
                activeOpponent = user;
            }
        }
    }

    private void sendinvite() {

    }

    public void receiveInvite() {

    }


    public void initUserName(String username) {
        this.username.setText(username);
    }

    public void initNettools(NetToolsSearch search){
        this.search=search;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
