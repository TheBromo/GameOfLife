package ch.bbw.model.data;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Is the object which is used to manage a single cell and it's attributes
 */
public class Cell {
    //used for managing the
    private boolean alive, aliveNextTurn, selected, bornNextTurn;
    //used for managing
    private Color color;
    //used if it gets born
    private ArrayList<Cell> parents = new ArrayList<>();

    public Cell(boolean alive, boolean aliveNextTurn, boolean selected, Color color) {
        this.alive = alive;
        this.aliveNextTurn = aliveNextTurn;
        this.selected = selected;
        this.color = color;
    }

    public Cell(boolean alive, boolean aliveNextTurn, boolean selected, boolean bornNextTurn, Color color, ArrayList<Cell> parents) {
        this.alive = alive;
        this.aliveNextTurn = aliveNextTurn;
        this.selected = selected;
        this.bornNextTurn = bornNextTurn;
        this.color = color;
        this.parents = parents;
    }

    public Cell(boolean alive, Color color) {
        this.alive = alive;
        this.color = color;
    }

    public boolean hasParents() {
        if (parents.size() == 2) {
            return true;
        }
        return false;
    }

    /**
     * is used for copying an object
     * @return is an new instance of the cell
     */
    public Cell copy() {
        return new Cell(alive, aliveNextTurn, selected, bornNextTurn, color, parents);
    }

    public boolean isBornNextTurn() {
        return bornNextTurn;
    }

    public void setBornNextTurn(boolean bornNextTurn) {
        this.bornNextTurn = bornNextTurn;
    }

    public ArrayList<Cell> getParents() {
        return parents;
    }

    public void addParent(Cell cell) {
        parents.add(cell);
    }


    public void clearParents() {
        parents.clear();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAliveNextTurn() {
        return aliveNextTurn;
    }

    public void setAliveNextTurn(boolean aliveNextTrun) {
        this.aliveNextTurn = aliveNextTrun;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
