package de.lmu.ifi.pixelfighter.demo.demo2;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by michael on 20.11.17.
 */

public class Board {

    public Cell[][] grid;
    private GameView gameView;
    private int boardSize;

    public Board(GameView gameView, int boardSize) {
        this.grid = new Cell[boardSize][boardSize];
        this.gameView = gameView;

        this.boardSize = boardSize;
    }

    public void draw(Canvas canvas) {
        for(int i = 0; i < this.grid.length; i++) {
            for(int j = 0; j < this.grid.length; j++) {
                this.grid[i][j].onDraw(canvas, 0, 0);
            }
        }
    }

    public void reset() {
        for(int i = 0; i < this.grid.length; i++) {
            for(int j = 0; j < this.grid.length; j++) {
                this.grid[i][j] = new Cell(this.gameView, i, j);
            }
        }
        this.setPositions();
    }

    int bmsize = 150;
    public void setPositions() {
        Log.d("Board", "Width " + gameView.getHolder().getSurfaceFrame().width());
        Log.d("Board", "Height " + gameView.getHolder().getSurfaceFrame().height());
        int horizontalOffset = (gameView.getHolder().getSurfaceFrame().width() - (this.boardSize * bmsize)) / 2;
        for(int i = 0; i < this.grid.length; i++) {
            for(int j = 0; j < this.grid.length; j++) {
                this.grid[i][j].setX(horizontalOffset + i * bmsize);
                this.grid[i][j].setY(90 + j * bmsize);
            }
        }
    }
}
