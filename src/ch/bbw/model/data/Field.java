package ch.bbw.model.data;
public class Field {
    private Cell[][] cells ;
    private String turnType;

    public Field(int width, int height, String turnType) {
        cells =new Cell[width][height];
        this.turnType = turnType;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public String getTurnType() {
        return turnType;
    }

    public void setCells(Cell[][] cells) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells.length; y++) {
                this.cells[x][y] = cells[x][y].copy();
            }
        }
    }
}
