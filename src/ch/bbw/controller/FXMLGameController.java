package ch.bbw.controller;

import ch.bbw.model.network.Client;
import ch.bbw.model.network.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLGameController implements Initializable {
    @FXML
    private Label blueName, redName, blueBlocks, redBlocks;
    @FXML
    private Canvas canvas;
    private Client network;
    private Server server;


    @FXML
    private void handleTurnEnd(ActionEvent event) {

    }

    @FXML
    private void handleCanvasClick(MouseEvent event) {

    }


    public void initClient(Client network) {
        this.network = network;
    }

    public void initServer(Server server){
        this.server= server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
