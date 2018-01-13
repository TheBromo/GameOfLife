package ch.bbw.model.data;

import javafx.scene.paint.Color;

import java.util.Random;

import static javafx.scene.paint.Color.rgb;

public class CellManager {
    private Cell[][] cells = new Cell[10][10];
    private Color blue = rgb(52, 152, 219), red = rgb(231, 76, 60);


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

    public Cell[][] getCells() {
        return cells;
    }

    public void generate() {

        System.out.println(rgb(52, 152, 219).equals(blue));
        int blueCount = 15, redCount = 15;

        Random random = new Random();
        while (blueCount > 0 && redCount > 0) {
            for (int x = 0; x < cells.length; x++) {
                for (int y = 0; y < cells[x].length; y++) {
                    int num = random.nextInt(3);
                    if (num == 2 && cells[x][y].getColor().equals(Color.WHITE) && redCount > 0) {
                        redCount--;
                        Cell cell = cells[x][y];
                        cell.setColor(rgb(231, 76, 60));
                        cell.setAlive(true);
                        System.out.println("Generated red");
                    } else if (num == 1 && cells[x][y].getColor().equals(Color.WHITE) && blueCount > 0) {
                        blueCount--;
                        Cell cell = cells[x][y];
                        cell.setColor(rgb(52, 152, 219));
                        cell.setAlive(true);
                        System.out.println("Generated blue");
                    }

                }
            }
        }

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (cells[x][y].getColor().equals(rgb(52, 152, 219))) {
                    cells[cells.length - 1 - x][cells[cells.length - 1 - x].length - 1 - y].setColor(rgb(231, 76, 60));
                } else {
                    cells[cells.length - 1 - x][cells[cells.length - 1 - x].length - 1 - y].setColor(rgb(52, 152, 219));
                }
                cells[cells.length - 1 - x][cells[cells.length - 1 - x].length - 1 - y].setAlive(cells[x][y].isAlive());
            }
        }
    }


    public void iterate() {
        Cell[][] nextStage = cells;
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                nextStage[x][y].setAlive(getNextCellStage(x, y));
            }
        }
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].setAlive(nextStage[x][y].isAlive());
            }
        }
        setNextIteration();
    }

    private void setNextIteration() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].setAliveNextTurn(getNextCellStage(x, y));
            }
        }
    }

    private boolean getNextCellStage(int x, int y) {
        int neighbours = getNumberOfNeighbours(x, y);
        if (neighbours < 2 || neighbours > 3) {
            return false;
        }
        return true;

    }

    private int getNumberOfNeighbours(int x, int y) {
        int nb = 0;
        for (int i = Math.max(0, x - 1); i < Math.min(cells.length, x + 2); i++) {
            for (int k = Math.max(0, y - 1); k < Math.min(cells.length, y + 2); k++) {
                if (!(i == x && k == y)) {
                    if (cells[i][k].isAlive())
                        nb++;
                }
            }
        }
        return nb;
    }
}
