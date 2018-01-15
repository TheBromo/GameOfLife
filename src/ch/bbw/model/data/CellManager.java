package ch.bbw.model.data;

import javafx.scene.paint.Color;

import java.util.Random;

import static javafx.scene.paint.Color.rgb;

public class CellManager {
    private Cell[][] cells = new Cell[10][10];
    private Color blue, red;
    private Cell selected;
    private int cellCount = 10;
    private Random random;


    public CellManager() {
        random = new Random();
        blue = rgb(52, 152, 219);
        red = rgb(231, 76, 60);
        //generate();
        mirror();
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
        mirror();
    }

    private void fill() {
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


    public void select(Cell cell) {
        if (selected != null) {
            if (cell.equals(selected)) {
                cell.setSelected(false);
                selected = null;
                return;
            }

            selected.setSelected(false);
        }
        cell.setSelected(true);
        selected = cell;
    }


    public Cell getCellByCoordinates(double x, double y, double canvasWidth) {
        int newX = (int) (x / (canvasWidth / cells.length));
        int newY = (int) (y / (canvasWidth / cells.length));
        Cell cell = cells[(int) (x / (canvasWidth / cells.length))][(int) (y / (canvasWidth / cells.length))];
        System.out.println("Cell [" + newX + "][" + newY + "]: " + cell.getColor() + " alive: " + cell.isAlive() + " Neighbours: " + getNumberOfNeighbours(newX, newY) +
                " Dominant Color: " + getDominantColor(newX, newY));
        return cell;
    }

    private void placeCells(Color color) {
        random = new Random();
        for (int i = 0; i < cellCount; i++) {
            int x, y;
            do {
                x = (random.nextInt() & 0x7fffffff) % cells.length;
                y = (random.nextInt() & 0x7fffffff) % (cells[x].length >> 1);
            } while (cells[x][y] != null);
            cells[x][y] = new Cell(true, color);
            cells[cells.length - x - 1][cells[x].length - y - 1] = new Cell(true, color);
        }
    }

    private void mirror() {
        placeCells(blue);
        placeCells(red);

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (cells[x][y] == null) {
                    cells[x][y] = new Cell(false, null);
                }
            }
        }

        setNextIteration();
    }

    public void iterate() {

        for (Cell[] cell : cells) {
            for (Cell aCell : cell) {
                aCell.setAlive(aCell.isAliveNextTurn());
            }
        }
        setNextIteration();
    }

    public void setNextIteration() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].setAliveNextTurn(getNextCellStage(x, y));
                if (!cells[x][y].isAlive() && cells[x][y].isAliveNextTurn()) {
                    cells[x][y].setColor(getDominantColor(x, y));
                }
            }
        }
    }

    private boolean getNextCellStage(int x, int y) {
        int neighbours = getNumberOfNeighbours(x, y);
        return neighbours >= 2 && neighbours <= 3 && (cells[x][y].isAlive() || neighbours == 3);

    }

    private Color getDominantColor(int x, int y) {
        int blueCount = 0, redCount = 0;
        for (int i = Math.max(0, x - 1); i < Math.min(cells.length, x + 2); i++) {
            for (int k = Math.max(0, y - 1); k < Math.min(cells.length, y + 2); k++) {
                if (!(i == x && k == y)) {
                    if (cells[i][k].isAlive()) {
                        if (cells[i][k].getColor().equals(blue)) {
                            blueCount++;
                        } else {
                            redCount++;
                        }
                    }

                }
            }
        }
        if (blueCount > redCount) {
            return blue;
        } else {
            return red;
        }
    }

    private int getNumberOfNeighbours(int x, int y) {
        int neighbours = 0;
        for (int i = Math.max(0, x - 1); i < Math.min(cells.length, x + 2); i++) {
            for (int k = Math.max(0, y - 1); k < Math.min(cells.length, y + 2); k++) {
                if (!(i == x && k == y)) {
                    if (cells[i][k].isAlive())
                        neighbours++;
                }
            }
        }
        return neighbours;
    }

    public int getRedCount() {
        int reds = 0;
        for (Cell[] cell : cells) {
            for (Cell aCell : cell) {
                if (aCell.isAlive() && aCell.getColor().equals(red)) reds++;
            }
        }
        return reds;
    }

    public int getBlueCount() {
        int blues = 0;
        for (Cell[] cell : cells) {
            for (Cell aCell : cell) {
                if (aCell.isAlive() && aCell.getColor().equals(blue)) blues++;
            }
        }
        return blues;
    }
}
