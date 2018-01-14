package ch.bbw.model.data;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Cell {

    private boolean alive,aliveNextTurn,selected,bornNextTurn;
    private Color color;
    private ArrayList<Cell>parents= new ArrayList<>();

    public Cell(boolean alive, boolean aliveNextTurn, boolean selected, Color color) {
        this.alive = alive;
        this.aliveNextTurn = aliveNextTurn;
        this.selected = selected;
        this.color = color;
    }

    public Cell(boolean alive, Color color) {
        this.alive = alive;
        this.color = color;

    }

    public Cell copy(){
        return new Cell(alive,aliveNextTurn,selected,color);
    }

    public void setBornNextTurn(boolean bornNextTurn) {
        this.bornNextTurn = bornNextTurn;
    }

    public ArrayList<Cell> getParents() {
        return parents;
    }

    public void addParent(Cell cell){
        parents.add(cell);
    }

    public void removeParent(Cell cell){
        parents.remove(cell);
    }

    public void clearParents(){
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
