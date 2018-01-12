package ch.bbw.model.data;

import javafx.scene.paint.Color;

import java.util.Random;

import static javafx.scene.paint.Color.rgb;

public class CellManager {
    private Cell[][] cells = new Cell[20][20];


    public CellManager() {
        fill();
        generate();
    }

    public void fill() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                Cell cell = new Cell(false, Color.WHITE);
                cells[x][y] = cell;
            }
        }
    }

    public void generate() {

        Random random = new Random();
        for (int i = 0; i < 75; i++) {
            int x = random.nextInt(10), y = random.nextInt(10);
            while (cells[x][y].getColor().equals(Color.WHITE) || cells[x][y].getColor().equals(rgb(52, 152, 219))) {
                x = random.nextInt(10);
                y = random.nextInt(10);
            }
            Cell cell = cells[x][y];
            cell.setColor(rgb(231, 76, 60));
            cell.setAlive(true);
        }
        for (int i = 0; i < 75; i++) {
            int x = random.nextInt(10), y = random.nextInt(10);
            while (cells[x][y].getColor().equals(Color.WHITE) || cells[x][y].getColor().equals(rgb(231, 76, 60))) {
                x = random.nextInt(10);
                y = random.nextInt(10);
            }
            Cell cell = cells[x][y];
            cell.setColor(rgb(52, 152, 219));
            cell.setAlive(true);
        }

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[cells.length - x][cells[cells.length - x].length - y].setColor(cells[x][y].getColor());
                cells[cells.length - x][cells[cells.length - x].length - y].setAlive(cells[x][y].isAlive());
            }
        }
    }
}
