package ch.bbw.model.utils;

public class CellCoordinates {
    private boolean alive;
    private int x, y;

    public CellCoordinates(boolean alive, int x, int y) {
        this.alive = alive;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAlive() {
        return alive;
    }
}
