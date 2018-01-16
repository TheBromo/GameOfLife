package ch.bbw.model.data;

import ch.bbw.model.network.packets.ActionPacket;
import ch.bbw.model.utils.CellCoordinates;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

import static javafx.scene.paint.Color.rgb;

public class CellManager {
    private ArrayList<Field> fields = new ArrayList<>();
    private Cell[][] cells;
    private Color blue, red;
    private Cell selected;
    private int cellCount = 10, index;
    private Random random;


    public CellManager() {
        index = 0;
        cells = new Cell[10][10];
        random = new Random();
        blue = rgb(52, 152, 219);
        red = rgb(231, 76, 60);
        //generate();
        mirror();
    }

    public Cell[][] getView() {
        return fields.get(index).getCells();
    }

    public boolean isViewingNewestField() {
        return index == fields.size() - 1;
    }

    public void goForward() {
        if (index + 1 != fields.size()) {
            index++;
        }
    }

    public void goBackward() {
        if (index - 1 != -1) {
            index--;
        }
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
        reset();
        mirror();
    }

    private void reset() {

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y] = null;
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

    public CellCoordinates getCellCoor(Cell cell) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (cells[x][y].equals(cell)) {
                    return new CellCoordinates(cell.isAlive(), x, y);
                }

            }
        }
        return null;
    }

    public void processEnemyAction(ActionPacket packet, boolean isHost) {
        if (packet.hasParents()) {
            Color color;
            if (isHost) {
                color = blue;
            } else {
                color = red;
            }
            Cell mainCell = cells[packet.getMainCell().getX()][packet.getMainCell().getY()];
            mainCell.setAlive(packet.getMainCell().isAlive());
            mainCell.setColor(color);
            System.out.println("Cell born: " + packet.getMainCell().getX() + " " + packet.getMainCell().getY());

            for (CellCoordinates coordinates : packet.getParents()) {
                Cell parent = cells[coordinates.getX()][coordinates.getY()];
                parent.setAlive(coordinates.isAlive());
                System.out.println("Parent set alive: " + coordinates.isAlive());
            }
        } else {
            Cell cell = cells[packet.getMainCell().getX()][packet.getMainCell().getY()];
            cell.setAlive(packet.getMainCell().isAlive());
            System.out.println("Cell killed: " + packet.getMainCell().getX() + " " + packet.getMainCell().getY());
        }
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

        for (int i = 0; i < cellCount; i++) {
            int x, y;
            do {
                x = (random.nextInt() & 0x7fffffff) % cells.length;
                y = (random.nextInt() & 0x7fffffff) % (cells[x].length >> 1);
            } while (cells[x][y] != null);
            cells[x][y] = new Cell(true, color);
            if (color == red) {
                cells[cells.length - x - 1][cells[x].length - y - 1] = new Cell(true, blue);
            } else {
                cells[cells.length - x - 1][cells[x].length - y - 1] = new Cell(true, red);
            }
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
        addNewField();
    }

    private void addNewField() {
        Field field = new Field();
        field.setCells(cells);
        fields.add(field);
        index = fields.size() - 1;
    }

    public void iterate() {
        for (Cell[] cell : cells) {
            for (Cell aCell : cell) {
                aCell.setAlive(aCell.isAliveNextTurn());
            }
        }
        setNextIteration();
        addNewField();
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
