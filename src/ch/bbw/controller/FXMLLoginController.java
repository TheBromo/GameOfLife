package ch.bbw.controller;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ch.bbw.model.network.NetToolsSearch;
import javafx.event.ActionEvent;
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
 *
 * @author TheBromo
 */
public class FXMLLoginController implements Initializable {
    
    @FXML
    private TextField name;
    @FXML
    private Button login;
    
    @FXML
    private void handleButtonLogin(ActionEvent event) {
        if (!name.getText().equals("")){
            openLobbyWindow(name.getText());
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();
        }
    }

    private void openLobbyWindow(String username){
        try{
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/FXMLLobby.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();

            FXMLLobbyController controller = fxmlLoader.<FXMLLobbyController>getController();
            controller.initUserName(username);
            NetToolsSearch search = new NetToolsSearch(controller);
            controller.initNettools(search);
            search.addObserver(controller);
            search.run();


            Scene scene = new Scene(root1);
            stage.setOnCloseRequest((e)-> {
                search.setRunning(false);
            });

            stage.setTitle("Test");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
