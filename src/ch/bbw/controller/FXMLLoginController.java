package ch.bbw.controller;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ch.bbw.model.network.NetToolsSearch;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author TheBromo
 */
public class FXMLLoginController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private Button login;

    /**
     * checks is the name has been set
     * and closes the old window
     */
    @FXML
    private void handleButtonLogin() {
        if (!name.getText().equals("")) {
            //Opens Lobby Window
            openLobbyWindow(name.getText());

            //Closes login Window
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Opens the Lobby window
     * @param username is the name the user will have
     */
    private void openLobbyWindow(String username) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ch/bbw/view/FXMLLobby.fxml"));
            Parent root1 = fxmlLoader.load();

            //initializes name in Lobby controller
            FXMLLobbyController controller = fxmlLoader.getController();
            controller.initUserName(username);

            NetToolsSearch search = new NetToolsSearch();
            //is added so if a new user is found a new button will be added
            search.addObserver(controller);

            //starts the search for other users
            new Thread(search).start();
            //is set so that it can be closed when a game is started
            controller.initNetThread(search);


            Scene scene = new Scene(root1);
            //if the users closes the lobby all threads will stop
            stage.setOnCloseRequest((e) -> search.setRunning(false));

            stage.setTitle("Lobby");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }


}
