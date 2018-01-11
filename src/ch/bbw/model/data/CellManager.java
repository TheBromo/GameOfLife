package ch.bbw.model.data;

public class CellManager {
    private Cell[][] cells = new Cell[20][20];

    public CellManager(){
        generate();
    }

    public void generate() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                Cell cell = new Cell();
                cells[x][y] = cell;
            }
        }
    }
}
