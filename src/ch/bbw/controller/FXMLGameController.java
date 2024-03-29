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
import javafx.scene.control.Alert;
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
@SuppressWarnings("deprecation")
public class FXMLGameController implements Initializable, Observer {
    @FXML
    private Label blueName, redName, blueBlocks, redBlocks, index;
    @FXML
    private Canvas canvas, color;
    private GraphicsContext gc, colorGc;
    private Client client;
    private TurnHandler turnHandler;
    private InetAddress serverAddress;
    private CellManager cellManager;
    private ActionHandler actionHandler;
    private double xOffset, yOffset, zoom, recZoom, lastDragY, lastDragX, size;
    private boolean dragInProgress, zoomed, host, finished;

    /**
     * handles the click of the end turn button
     * sends the turn
     *
     */
    @FXML
    private void handleTurnEnd() {
        if (actionHandler.canEndTurn() && turnHandler.canPlay() && !finished) {
            System.out.println("Ending turn");
            ActionPacket actionPacket = actionHandler.getAction();
            actionPacket.addTarget(new InetSocketAddress(serverAddress, Client.port));
            client.queuePacket(actionPacket);

            //updates the data
            cellManager.iterate();
            actionHandler.newTurn();
            updateCellCount();
            draw();
            turnHandler.newTurn();
            paintActivePlayer();
            checkWinner();
            index.setText("" + cellManager.getHumanIndex());
        }
    }

    /**
     * changes the view to an older state
     * @param event
     */
    @FXML
    public void handleSkipBack(ActionEvent event) {
        cellManager.goBackward();
        index.setText("" + cellManager.getHumanIndex());
        draw();
    }

    /**
     * changes the view to a newer view
     * @param event
     */
    @FXML
    public void handleSkipForwards(ActionEvent event) {
        cellManager.goForward();
        index.setText("" + cellManager.getHumanIndex());
        draw();
    }

    /**
     * interprets the click onto the canvas
     * @param event
     */
    @FXML
    private void handleCanvasClick(MouseEvent event) {
        System.out.println("Click...");
        if (dragInProgress) {
            dragInProgress = false;
        } else {
            double x = event.getX() / recZoom - xOffset;
            double y = event.getY() / recZoom - yOffset;
            System.out.println("y = " + y);
            System.out.println("x = " + x);
            if (y > 0 && y < size && x > 0 && x < size) {
                Cell cell = cellManager.getCellByCoordinates(x, y, size);
                System.out.println("Viewing newset field?" + cellManager.isViewingNewestField());
                if (turnHandler.canPlay() && !finished && cellManager.isViewingNewestField()) {
                    actionHandler.handleAction(cell);
                    cellManager.setNextIteration();
                }
                draw();
            }
        }

    }

    /**
     * undoes the last action
     */
    @FXML
    private void handleActionUndo() {
        if (turnHandler.canPlay()) {
            actionHandler.undoAction();
            cellManager.setNextIteration();
            draw();
        }
    }

    /**
     * shifts the displayed field
     * @param event
     */
    @FXML
    private void handleMouseDrag(MouseEvent event) {
        if (dragInProgress) {
            if (xOffset + event.getX() - lastDragX <= size && xOffset + event.getX() - lastDragX >= -size && yOffset + event.getY() - lastDragY <= size && yOffset + event.getY() - lastDragY >= -size) {
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

    /**
     * zooms in and out
     * @param event
     */
    @FXML
    private void handleScroll(ScrollEvent event) {
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

    /**
     * draw all the cells and background
     */
    private void draw() {
        gc.setFill(rgb(44, 62, 80));
        gc.fillRect(0, 0, 100000, 10000);
        gc.setFill(rgb(52, 73, 94));
        if (zoomed) {
            gc.scale(zoom, zoom);
            zoomed = false;
        }
        Cell[][] cells = cellManager.getView();
        System.out.println("Width: " + size);
        for (double x = 0; x < size; x = x + (size / cellManager.getCells().length)) {
            for (double y = 0; y < 40 * cells.length; y = y + (size / cellManager.getCells().length)) {
                gc.fillRect(x + 4 + xOffset, y + 4 + yOffset, 32, 32);
                Cell cell = cells[(int) (x / (size / cellManager.getCells().length))][(int) (y / (size / cellManager.getCells().length))];

                if (cell.isBornNextTurn() && cellManager.isViewingNewestField()) {
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

                    }
                    if (!cell.isAliveNextTurn()) {
                        gc.setFill(rgb(52, 73, 94));
                        gc.fillRect(x + 15 + xOffset, y + 15 + yOffset, 10, 10);
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

    /**
     * shows the active players color
     */
    private void paintActivePlayer() {
        if ((turnHandler.canPlay() && host) || (!turnHandler.canPlay() && !host)) {
            colorGc.setFill(rgb(231, 76, 60));
        } else {
            colorGc.setFill(rgb(52, 152, 219));
        }
        colorGc.fillRect(0, 0, color.getWidth(), color.getHeight());
    }

    /**
     * updates the cell count of each player
     */
    private void updateCellCount() {
        redBlocks.setText("x " + cellManager.getRedCount());
        blueBlocks.setText("x " + cellManager.getBlueCount());
    }

    /**
     * checks if somebody won
     */
    private void checkWinner() {

        if (cellManager.getRedCount() == 0 && cellManager.getBlueCount() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tie");
            alert.setHeaderText("Congratulations! \n You both lost!");
            alert.setContentText("All cells of " + redName.getText() + " & " + blueName.getText() + "  have been eliminated");
            alert.showAndWait();
            finished = true;
        } else if (cellManager.getRedCount() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("The Game has been won");
            if (host) {
                alert.setHeaderText("You lost!");
            } else {
                alert.setHeaderText("You won!");
            }
            alert.setContentText("All cells of " + redName.getText() + "  have been eliminated");
            alert.showAndWait();
            finished = true;
        } else if (cellManager.getBlueCount() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("The Game has been won");
            if (host) {
                alert.setHeaderText("You won!");
            } else {
                alert.setHeaderText("You lost!");
            }
            alert.setContentText("All cells of " + blueName.getText() + "  have been eliminated");
            alert.showAndWait();
            finished = true;
        }
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


    }

    public void initName(String name) {
        if (host) {
            redName.setText(name);
        } else {
            blueName.setText(name);
        }
    }

    public void initCellManager(int w, int h) {
        cellManager = new CellManager(w, h);
        size = cellManager.getCells().length * 40;
        turnHandler = new TurnHandler(host);
        actionHandler = new ActionHandler(host, cellManager);
        paintActivePlayer();
        updateCellCount();
        draw();
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
        index.setText("1");
        finished = false;
        zoomed = false;
        zoom = 1;
        recZoom = 1;
        gc = canvas.getGraphicsContext2D();
        colorGc = color.getGraphicsContext2D();


    }

    /**
     * interprets received packets
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            //interprets Packets
            Packet packet = (Packet) arg;
            if (packet instanceof NamePacket) {

                NamePacket pm = (NamePacket) packet;
                System.out.println(pm.getText());

            } else if (packet instanceof SeedPacket) {

                SeedPacket pm = (SeedPacket) packet;
                System.out.println(pm.getSeed());
                cellManager.setSeed(pm.getSeed());
                System.out.println("User: updating cell count");
                updateCellCount();
                draw();

            } else if (packet instanceof ActionPacket) {

                ActionPacket pm = (ActionPacket) packet;
                cellManager.processEnemyAction(pm, host);
                cellManager.setNextIteration();

                cellManager.iterate();
                actionHandler.newTurn();
                updateCellCount();
                cellManager.setNextIteration();
                draw();
                turnHandler.newTurn();
                paintActivePlayer();
                checkWinner();
                index.setText("" + cellManager.getHumanIndex());
            }
        });
    }

}
