package ch.bbw.model.data;
public class Field {
    private Cell[][] cells ;

    public Field(int width,int height) {
        cells =new Cell[width][height];
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells.length; y++) {
                this.cells[x][y] = cells[x][y].copy();
            }
        }
    }
}
