package ch.bbw.model.utils;

import ch.bbw.model.data.Cell;
import ch.bbw.model.data.CellManager;
import ch.bbw.model.network.packets.ActionPacket;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static javafx.scene.paint.Color.rgb;

public class ActionHandler {
    private Color color;
    private boolean cellKilled, canEndTurn;
    private Action lastAction;
    private CellManager cellManager;

    public ActionHandler(boolean isHost, CellManager cellManager) {
        this.cellManager = cellManager;
        cellKilled = false;
        canEndTurn = false;
        if (isHost) {
            color = rgb(231, 76, 60);
            System.out.println("Is host, has color: " + color);
        } else {
            color = rgb(52, 152, 219);
        }
    }

    /**
     * this class interprets a
     * @param clickedCell the cell that has been pressed
     */
    public void handleAction(Cell clickedCell) {
        //only one action can be taken
        if (clickedCell.isAlive() && lastAction != Action.CREATE_NEW_CELL && !cellKilled) {
            //when the user presses on a cell the first time he kills a cell
            clickedCell.setAlive(false);
            lastAction = Action.KILL_CELL;
            lastAction.setCell(clickedCell);
            lastAction.setOldColor(clickedCell.getColor());
            cellKilled = true;
            canEndTurn = true;
            System.out.println("Cell killed");

        } else if (!clickedCell.isAlive() && cellKilled && lastAction.getCell().equals(clickedCell)) {
            //when a killed cell is pressed on again it gets revived
            clickedCell.setAlive(true);
            clickedCell.setColor(lastAction.getOldColor());
            lastAction.setCell(null);
            lastAction = null;
            cellKilled = false;
            canEndTurn = false;
            System.out.println("Cell revived");

        } else if (!clickedCell.isAlive() && !cellKilled && lastAction != Action.CREATE_NEW_CELL) {
            //user clicked on a dead cell, this cell will be born, still needs 2 parents
            clickedCell.setAlive(true);
            lastAction = Action.CREATE_NEW_CELL;
            lastAction.setCell(clickedCell);
            lastAction.setOldColor(clickedCell.getColor());
            clickedCell.setColor(color);
            System.out.println("Color:" + color);
            clickedCell.setBornNextTurn(true);
            System.out.println("Cell born");

        } else if (clickedCell.isAlive() && !cellKilled && lastAction == Action.CREATE_NEW_CELL && clickedCell.getColor().equals(color)) {
            if (lastAction.getCell().equals(clickedCell)) {
                //to stop the birthing of a cell and restart the action process
                clickedCell.setColor(lastAction.getOldColor());
                clickedCell.setAlive(false);
                clickedCell.setBornNextTurn(false);
                lastAction = null;
                System.out.println("Cell deborn");
                for (Cell parent : clickedCell.getParents()) {
                    parent.setAlive(true);
                    parent.setColor(color);
                }
                canEndTurn = false;
                clickedCell.clearParents();
            } else {
                if (lastAction.getCell().getParents().size() < 2) {
                    //cell gets sacrificed that the new cell can be born
                    clickedCell.setAlive(false);
                    lastAction.getCell().addParent(clickedCell);
                    System.out.println("Cell sacrificed");
                    if (lastAction.getCell().getParents().size() == 2) {
                        canEndTurn = true;
                        System.out.println("Can end turn");
                    }
                } else {
                    System.out.println("Already has 2 Parents");
                }
            }
        }


    }

    public boolean canEndTurn() {
        return canEndTurn;
    }

    /**
     * Creates a packet for the last action taken
     * @return the ActionPacket
     */
    public ActionPacket getAction() {
        if (lastAction == Action.CREATE_NEW_CELL) {
            ArrayList<CellCoordinates> parents = new ArrayList<>();
            for (Cell cell : lastAction.getCell().getParents()) {
                parents.add(cellManager.getCellCoor(cell));
            }
            return new ActionPacket(lastAction.getCell().hasParents(), cellManager.getCellCoor(lastAction.getCell()), parents);
        } else {
            return new ActionPacket(false, cellManager.getCellCoor(lastAction.getCell()), null);
        }
    }

    /**
     * resets everything so new actions can be taken
     */
    public void newTurn() {
        if (lastAction == Action.CREATE_NEW_CELL) {
            lastAction.getCell().clearParents();
            lastAction.getCell().setBornNextTurn(false);
        }
        lastAction = null;
        cellKilled = false;
        canEndTurn = false;
    }

    /**
     * undoes las action
     */
    public void undoAction() {
        if (lastAction != null) {
            handleAction(lastAction.getCell());
        }
    }

    /**
     * is used for saving what has been changed in the last action and what kind of action it was
     */
    enum Action {

        CREATE_NEW_CELL, KILL_CELL;

        private Cell cell;
        private Color oldColor;

        public Cell getCell() {
            return cell;
        }

        public void setCell(Cell cell) {
            this.cell = cell;
        }

        public Color getOldColor() {
            return oldColor;
        }

        public void setOldColor(Color oldColor) {
            this.oldColor = oldColor;
        }


    }
}
