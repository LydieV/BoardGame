package fr.iutlens.mmi.boardgame;

import android.graphics.Canvas;
import android.view.View;

import fr.iutlens.mmi.boardgame.utils.SpriteSheet;



/**
 * Created by dubois on 26/11/2019.
 */

class Board {
    final HexagonalCoordinate coordinate;
    final int[] game;
    final int size;

    private final int spriteSheetId;

    private View gameView;

    public void playIfValid(float x, float y) {
        play(x, y);
    }

    public Board(int id, int size) {
        this.spriteSheetId = id;
        coordinate = new HexagonalCoordinate(id, size,size);
        this.size = size;
        game = new int[coordinate.getNM()];
        for(int i = 0; i < game.length; ++i)
            game[i]= 0;

    }


    public void paint(Canvas canvas) {
        SpriteSheet spriteSheet = SpriteSheet.get(spriteSheetId);

        for(int i = 0; i < size; ++i)
            for(int j = 0; j< size; ++j){
                int ndx = coordinate.getIndexIJ(i,j);
                int c = get(i, j);
                spriteSheet.paint(canvas, 4+c,coordinate.getX(ndx), coordinate.getY(ndx));
            }

    }


    void play(float xx, float yy) {
        int ndx = coordinate.getIndexXY(xx, yy);
        play(ndx);
    }

    void play(int ndx) {
        game[ndx] =1- game[ndx];

    }
    public boolean isValidMove(int ndx) {
        return true;
    }

    int get(int xx, int yy) {
        return game[coordinate.getIndexIJ(xx, yy)];
    }

    public float getSize() {
        return size;
    }

    
    public void setGameView(View gameView) {
        this.gameView = gameView;
    }



}
