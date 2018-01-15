package ch.bbw.controller;

import ch.bbw.model.data.Cell;
import ch.bbw.model.data.CellManager;
import ch.bbw.model.network.Client;
import ch.bbw.model.network.packets.ActionPacket;
import ch.bbw.model.network.packets.NamePacket;
import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.SeedPacket;
import ch.bbw.model.utils.ActionHandler;
import ch.bbw.model.utils.TurnHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private Client client;
    private TurnHandler turnHandler;
    private InetAddress serverAddress;
    private CellManager cellManager;
    private ActionHandler actionHandler;
    private double xOffset, yOffset, zoom, recZoom, lastDragY, lastDragX;
    private boolean dragInProgress, zoomed, host, nameSent;
    private String username;

    @FXML
    private void handleTurnEnd(ActionEvent event) {
        NamePacket packet = new NamePacket("This is a test");
        packet.addTarget(new InetSocketAddress(serverAddress, Client.port));
        client.queuePacket(packet);
        if (actionHandler.canEndTurn() && turnHandler.canPlay()) {
            cellManager.iterate();
            actionHandler.newTurn();
            updateCellCount();
            draw();
            turnHandler.newTurn();
        }
    }

    @FXML
    private void handleCanvasClick(MouseEvent event) {
        System.out.println("Click...");
        if (dragInProgress) {
            dragInProgress = false;
            return;
        } else {
            double x = event.getX() / recZoom - xOffset;
            double y = event.getY() / recZoom - yOffset;
            System.out.println("y = " + y);
            System.out.println("x = " + x);
            if (y > 0 && y < 400 && x > 0 && x < 400) {
                Cell cell = cellManager.getCellByCoordinates(x, y, canvas.getWidth());
                cellManager.select(cell);
                if (turnHandler.canPlay()) {
                    actionHandler.handleAction(cell);
                    cellManager.setNextIteration();
                }
                draw();

            }
        }

    }

    @FXML
    private void handleActionUndo(ActionEvent event) {
        if (turnHandler.canPlay()) {
            actionHandler.undoAction();
            cellManager.setNextIteration();
            draw();
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
        draw();
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
        recZoom *= zoom;
        draw();
        zoomed = true;
    }

    private void draw() {
        gc.setFill(rgb(44, 62, 80));
        gc.fillRect(0, 0, 100000, 10000);
        gc.setFill(rgb(52, 73, 94));
        if (zoomed) {
            gc.scale(zoom, zoom);
            zoomed = false;
        }
        Cell[][] cells = cellManager.getCells();
        for (double x = 0; x < canvas.getWidth(); x = x + (canvas.getWidth() / cellManager.getCells().length)) {
            for (double y = 0; y < canvas.getHeight(); y = y + (canvas.getWidth() / cellManager.getCells().length)) {
                gc.fillRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                Cell cell = cells[(int) (x / (canvas.getWidth() / cellManager.getCells().length))][(int) (y / (canvas.getWidth() / cellManager.getCells().length))];

                if (cell.isBornNextTurn()) {
                    System.out.println("Born cell: " + cell.getParents().size() + " alive?" + cell.isAliveNextTurn());
                    gc.setFill(cell.getColor());
                    gc.setStroke(cell.getColor());
                    gc.strokeRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                    gc.fillRect(x + 19 + xOffset, y + 5 + yOffset, 2, 30);
                    if (cell.getParents().size() == 1) {
                        gc.fillRect(x + 4 + xOffset, y + 4 + yOffset, 16, 32);
                    } else if (cell.getParents().size() == 2) {
                        gc.fillRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                        System.out.println(cell.isAliveNextTurn());
                        if (!cell.isAliveNextTurn()) {
                            gc.setFill(rgb(52, 73, 94));
                            gc.fillRect(x + 15 + xOffset, y + 15 + yOffset, 10, 10);
                            System.out.println("hey");
                        }
                    }

                    gc.setFill(rgb(52, 73, 94));
                    continue;
                }
                if (cell.isAlive() && cell.isAliveNextTurn()) {
                    gc.setFill(cell.getColor());
                    gc.fillRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                } else if (!cell.isAlive() && cell.isAliveNextTurn()) {
                    gc.setFill(cell.getColor());
                    gc.fillRect(x + 15 + xOffset, y + 15 + yOffset, 10, 10);
                } else if (cell.isAlive() && !cell.isAliveNextTurn()) {
                    gc.setFill(cell.getColor());
                    gc.fillRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                    gc.setFill(rgb(52, 73, 94));
                    gc.fillRect(x + 15 + xOffset, y + 15 + yOffset, 10, 10);
                }
                if (cell.isSelected()) {
                    gc.setStroke(rgb(236, 240, 241, 1.0));
                    gc.strokeRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                }
                gc.setFill(rgb(52, 73, 94));

            }
        }

    }

    private void updateCellCount() {
        redBlocks.setText("x " + cellManager.getRedCount());
        blueBlocks.setText("x " + cellManager.getBlueCount());
    }

    public void initClient(Client network) {
        this.client = network;

    }

    public void setName(String name) {
        if (!host) {
            this.blueName.setText(name);
        } else {
            this.redName.setText(name);
        }


    }

    public void initHost(boolean host) {
        this.host = host;
        turnHandler = new TurnHandler(host);
    }

    public void initName(String name) {
        username = name;
        if (host) {
            redName.setText(name);
        } else {
            blueName.setText(name);
        }

    }

    public void initNames(String host, String client) {
        redName.setText(host);
        blueName.setText(client);
    }

    public void initServerAddress(InetAddress serverAdress) {
        this.serverAddress = serverAdress;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameSent = false;
        zoomed = false;
        zoom = 1;
        recZoom = 1;
        cellManager = new CellManager();
        actionHandler = new ActionHandler(host);
        gc = canvas.getGraphicsContext2D();
        updateCellCount();
        draw();

    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            Packet packet = (Packet) arg;
            if (packet instanceof NamePacket) {
                NamePacket pm = (NamePacket) packet;
                System.out.println(pm.getText());
            } else if (packet instanceof SeedPacket) {
                SeedPacket pm = (SeedPacket) packet;
                System.out.println(pm.getSeed());
                cellManager.setSeed(pm.getSeed());
                System.out.println("updating cell count");
                updateCellCount();
                System.out.println("starting drawing");
                draw();
            } else if (packet instanceof ActionPacket) {
                ActionPacket pm = (ActionPacket) packet;
                cellManager.processEnemyAction(pm);
                updateCellCount();
                draw();
            }
        });
    }
}
