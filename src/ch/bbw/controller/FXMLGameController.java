package ch.bbw.controller;

import ch.bbw.model.data.CellManager;
import ch.bbw.model.network.Client;
import ch.bbw.model.network.Server;
import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.TextPacket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.InetAddress;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class FXMLGameController implements Initializable,Observer {
    @FXML
    private Label blueName, redName, blueBlocks, redBlocks;
    @FXML
    private Canvas canvas;
    private Client network;
    private Server server;
    private InetAddress serverAdress;
    private CellManager cellManager;


    @FXML
    private void handleTurnEnd(ActionEvent event) {
        Packet packet = new TextPacket("Hello");
        packet.addTarget(serverAdress);
        network.queuePacket(packet);
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

    public void initName(String redName,String blueName){
        this.redName.setText(redName);
        this.blueName.setText(blueName);
    }

    public void initServerAddress(InetAddress serverAdress) {
        this.serverAdress = serverAdress;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cellManager = new CellManager();

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
