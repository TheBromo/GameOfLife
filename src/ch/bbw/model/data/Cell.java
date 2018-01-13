package ch.bbw.model.data;

import javafx.scene.paint.Color;

public class Cell {

    private boolean alive,aliveNextTurn,selected;
    private Color color;

    public Cell(boolean alive, Color color) {
        this.alive = alive;
        this.color = color;
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
