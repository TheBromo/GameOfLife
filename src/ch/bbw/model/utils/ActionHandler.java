package ch.bbw.model.utils;

import ch.bbw.model.data.Cell;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.rgb;

public class ActionHandler {
    private Color color;
    private boolean cellKilled, canEndTurn;
    private Action lastAction;

    public ActionHandler(boolean isHost) {
        cellKilled = false;
        canEndTurn = false;
        if (isHost) {
            color = rgb(52, 152, 219);
        } else {
            color = rgb(231, 76, 60);
        }
    }

    public void handleAction(Cell clickedCell) {
        if (clickedCell.isAlive() && lastAction != Action.CREATE_NEW_CELL && !cellKilled) {

            clickedCell.setAlive(false);
            lastAction = Action.KILL_CELL;
            lastAction.setCell(clickedCell);
            lastAction.setOldColor(clickedCell.getColor());
            cellKilled = true;
            canEndTurn = true;
            System.out.println("Cell killed");

        } else if (!clickedCell.isAlive() && cellKilled && lastAction.getCell().equals(clickedCell)) {

            clickedCell.setAlive(true);
            clickedCell.setColor(lastAction.getOldColor());
            lastAction.setCell(null);
            lastAction = null;
            cellKilled = false;
            canEndTurn = false;
            System.out.println("Cell revived");

        } else if (!clickedCell.isAlive() && !cellKilled && lastAction != Action.CREATE_NEW_CELL) {

            clickedCell.setAlive(true);
            lastAction = Action.CREATE_NEW_CELL;
            lastAction.setCell(clickedCell);
            lastAction.setOldColor(clickedCell.getColor());
            clickedCell.setColor(color);
            clickedCell.setBornNextTurn(true);
            System.out.println("Cell born");

        } else if (clickedCell.isAlive() && !cellKilled && lastAction == Action.CREATE_NEW_CELL && clickedCell.getColor().equals(color)) {
            if (lastAction.getCell().equals(clickedCell)) {
                clickedCell.setColor(lastAction.getOldColor());
                clickedCell.setAlive(false);
                clickedCell.setBornNextTurn(false);
                lastAction = null;
                System.out.println("Cell deborn");
                for (Cell parent:clickedCell.getParents()){
                    parent.setAlive(true);
                    parent.setColor(color);
                }
                canEndTurn = false;
                clickedCell.clearParents();
            }else {
                if (lastAction.getCell().getParents().size()<2) {
                    clickedCell.setAlive(false);
                    lastAction.getCell().addParent(clickedCell);
                    System.out.println("Cell sacrificed");
                    if (lastAction.getCell().getParents().size() == 2) {
                        canEndTurn = true;
                        System.out.println("Can end turn");
                    }
                }else {
                    System.out.println("Already has 2 Parents");
                }
            }
        }


    }

    public boolean canEndTurn() {
        return canEndTurn;
    }

    public void newTurn() {
        if (lastAction == Action.CREATE_NEW_CELL) {
            lastAction.getCell().clearParents();
            lastAction.getCell().setBornNextTurn(false);
        }
        lastAction = null;
        cellKilled = false;
        canEndTurn = false;
    }

    public void undoAction() {
        if (lastAction != null) {
            handleAction(lastAction.getCell());
        }
    }

    enum Action {

        CREATE_NEW_CELL, KILL_CELL;

        private Cell cell;
        private Color oldColor;


        public void setCell(Cell cell) {
            this.cell = cell;
        }

        public Cell getCell() {
            return cell;
        }


        public void setOldColor(Color oldColor) {
            this.oldColor = oldColor;
        }

        public Color getOldColor() {
            return oldColor;
        }


    }
}
