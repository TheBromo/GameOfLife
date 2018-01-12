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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;

import java.net.InetAddress;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.rgb;

public class FXMLGameController implements Initializable, Observer {
    @FXML
    private Label blueName, redName, blueBlocks, redBlocks;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private Client network;
    private Server server;
    private InetAddress serverAdress;
    private CellManager cellManager;
    private double xOffset, yOffset, zoom, lastDragY, lastDragX;
    private boolean dragInProgress, zoomed;
    private Scale norm;


    @FXML
    private void handleTurnEnd(ActionEvent event) {
        Packet packet = new TextPacket("Hello");
        packet.addTarget(serverAdress);
        network.queuePacket(packet);
    }

    @FXML
    private void handleCanvasClick(MouseEvent event) {
        System.out.println("Click...");
        if (dragInProgress) {
            dragInProgress = false;
            return;
        }
    }


    @FXML
    private void handleMouseDrag(MouseEvent event) {
        System.out.println("Draging...");
        if (dragInProgress) {
            if (xOffset + event.getX() - lastDragX <= 380 && xOffset + event.getX() - lastDragX >= -380 && yOffset + event.getY() - lastDragY <= 380 && yOffset + event.getY() - lastDragY >= -380) {
                xOffset += event.getX() - lastDragX;
                yOffset += event.getY() - lastDragY;
            }
        } else {
            dragInProgress = true;
        }
        lastDragX = event.getX();
        lastDragY = event.getY();
        drawBackground(0, 0);
    }

    @FXML
    private void handleScroll(ScrollEvent event) {
        System.out.println("Scrolling..");
        double zoomFactor = 1.05;
        double deltaY = event.getDeltaY();
        if (deltaY < 0) {
            zoomFactor = 2.0 - zoomFactor;
        }
        zoom = zoomFactor;
        drawBackground(event.getX(), event.getY());
        zoomed = true;
    }

    private void drawBackground(double xM, double yM) {
        gc.setFill(rgb(44, 62, 80));
        gc.fillRect(0, 0, 100000, 10000);
        gc.setFill(rgb(52, 73, 94));
        if (zoomed) {
            gc.scale(zoom, zoom);
            zoomed = false;
        }
        for (double x = 0; x < canvas.getWidth(); x = x + 20) {
            for (double y = 0; y < canvas.getHeight(); y = y + 20) {
                gc.fillRect(x + 2 + xOffset, y + 2 + yOffset, 16, 16);
            }
        }

    }


    public void initClient(Client network) {
        this.network = network;
    }

    public void initServer(Server server) {
        this.server = server;
    }

    public void initName(String redName, String blueName) {
        this.redName.setText(redName);
        this.blueName.setText(blueName);
    }

    public void initServerAddress(InetAddress serverAdress) {
        this.serverAdress = serverAdress;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomed = false;
        zoom = 1;
        cellManager = new CellManager();
        gc = canvas.getGraphicsContext2D();

        drawBackground(0, 0);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
