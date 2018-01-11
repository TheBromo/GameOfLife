package ch.bbw.controller;

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
    private Label blueName,redName,blueBlocks,redBlocks;
    @FXML
    private Canvas canvas;


    @FXML
    private void handleTurnEnd(ActionEvent event){

    }

    @FXML
    private  void handleCanvasClick(MouseEvent event){

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
