package fr.iutlens.mmi.boardgame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import fr.iutlens.mmi.boardgame.utils.SpriteSheet;



/**
 * Created by dubois on 26/11/2019.
 */

class Board {


    public static final int HIGHLIGHT_COLOR_OK = 0x8855AADD;
    public static final int HIGHLIGHT_COLOR_ILLEGAL = 0xBBF07733;
    public static final float HOSHI_SIZE = 0.08f;
    private static final float STONE_OFFSET = 0.05f;
    public static final float MIN_PROB = 0.75f;

    private final Paint highlightPaint;
    private final GameState gameState;
    private int highlight_x = -1;
    private int highlight_y = -1;
    private int lastX = -1;
    private int lastY;

    private ScoreEvaluator evaluator = null;

    public void setGameView(View gameView) {
        this.gameView = gameView;
    }

    private View gameView;

    public void evaluate(){
        if (evaluator != null) evaluator.cancelMultiBackgroundEvaluation();
        evaluator = new ScoreEvaluator(gameState);
        evaluator.multiEvaluateInBackground(10,12,gameView);
    }

    public void playIfValid(float x, float y) {
        highlight_x = -1;
        int xx = (int) (x);
        int yy = (int) (y);
        if (!gameState.isValidMove(xx, yy)) return;
        gameState.play(xx, yy);

        evaluate();

        lastX = xx;
        lastY = yy;
    }

    private Paint linePaint;

    public Board(int size) {
        this.gameState = new GameState(size);

        linePaint = new Paint();
        linePaint.setColor(0xff221100);
        linePaint.setStrokeWidth(10);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setAntiAlias(true);

        highlightPaint = new Paint();
        highlightPaint.setColor(HIGHLIGHT_COLOR_OK);
        highlightPaint.setStrokeWidth(80);
        highlightPaint.setAntiAlias(true);


    }

    public float getSize() {
        return gameState.getSize();
    }

    public void paint(Canvas canvas) {
        SpriteSheet spriteSheet = SpriteSheet.get(R.mipmap.goban);

// Background
        for(int i = 0; i < gameState.getSize(); ++i)
            for(int j = 0; j< gameState.getSize(); ++j) {
                spriteSheet.paint(canvas, GameState.EMPTY, i * spriteSheet.w, j * spriteSheet.h);
                if ((i == 2 || i == 6) && (j == 2 || j == 6)){
                    canvas.drawCircle((i+0.5f)*spriteSheet.w,
                            (j+0.5f)*spriteSheet.h,
                            HOSHI_SIZE *spriteSheet.w,
                    linePaint);

                }
            }

// Lines
        for(int i = 0; i < gameState.getSize(); ++i){
            canvas.drawLine( // H line
                    (i+0.5f)*spriteSheet.w,0.5f*spriteSheet.h,
                    (i+0.5f)*spriteSheet.w, (gameState.getSize() -0.5f)*spriteSheet.h,
                    linePaint
            );
            canvas.drawLine( // V line
                    (0.5f)*spriteSheet.w,(i+0.5f)*spriteSheet.h,
                    (gameState.getSize() -0.5f)*spriteSheet.w, (i+0.5f)*spriteSheet.h,
                    linePaint
            );

        }

// Stones
        for(int i = 0; i < gameState.getSize(); ++i)
            for(int j = 0; j< gameState.getSize(); ++j){
                int c = gameState.get(i, j);
                if (c != GameState.EMPTY) {
                    spriteSheet.paint(canvas, c,(i+STONE_OFFSET)*spriteSheet.w,(j+STONE_OFFSET)*spriteSheet.h);
                }
            }

// Last move
        if (lastX != -1){
            spriteSheet.paint(canvas, GameState.LAST, lastX *spriteSheet.w, lastY *spriteSheet.h);
        }

// KO
        if (gameState.isKo()){
            int x = gameState.coordinate.getX(gameState.last_capture_ndx);
            int y = gameState.coordinate.getY(gameState.last_capture_ndx);
            spriteSheet.paint(canvas, GameState.KO, x *spriteSheet.w, y *spriteSheet.h);

        }
// current selection
        if (highlight_x != -1){
            canvas.drawLine(
                    (highlight_x+0.5f)*spriteSheet.w,0f*spriteSheet.h,
                    (highlight_x+0.5f)*spriteSheet.w, (gameState.getSize())*spriteSheet.h,
                    highlightPaint
            );
            canvas.drawLine(
                    (0f)*spriteSheet.w,(highlight_y+0.5f)*spriteSheet.h,
                    (gameState.getSize())*spriteSheet.w, (highlight_y+0.5f)*spriteSheet.h,
                    highlightPaint
            );
        }

// evaluation
        if (evaluator != null && evaluator.tryout> 10){
            for(int i = 0; i < gameState.getSize(); ++i)
                for(int j = 0; j< gameState.getSize(); ++j){
                    int c = GameState.EMPTY;
                    if (evaluator.get(i,j)> MIN_PROB) c = GameState.BLACK_TERRITORY;
                    else if (evaluator.get(i,j) < 1-MIN_PROB) c = GameState.WHITE_TERRITORY;
                    if (c != GameState.EMPTY) {
                        spriteSheet.paint(canvas, c,(i)*spriteSheet.w,(j)*spriteSheet.h);
                    }

                }

        }

    }

    public void highlight(float x, float y) {
        highlight_x = (int) x;
        highlight_y = (int) y;

        highlightPaint.setColor(
                gameState.isValidMove(highlight_x, highlight_y) ? HIGHLIGHT_COLOR_OK : HIGHLIGHT_COLOR_ILLEGAL);
    }

}
