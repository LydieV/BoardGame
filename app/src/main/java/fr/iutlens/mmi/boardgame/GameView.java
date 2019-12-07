package fr.iutlens.mmi.boardgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.Console;

import fr.iutlens.mmi.boardgame.utils.RefreshHandler;
import fr.iutlens.mmi.boardgame.utils.SpriteSheet;
import fr.iutlens.mmi.boardgame.utils.TimerAction;


public class GameView extends View implements TimerAction, View.OnTouchListener {
    private RefreshHandler timer;
    private Board board;
    private Matrix matrix;
    private Matrix inverse;

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initialisation de la vue
     *
     * Tous les constructeurs (au-dessus) renvoient ici.
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {

        // Chargement des feuilles de sprites
        SpriteSheet.register(R.mipmap.goban,2,4,this.getContext());
//        SpriteSheet.register(R.drawable.car,3,1,this.getContext());

        // Création des différents éléments à afficher dans la vue
        board = new Board(9);
        board.setGameView(this);


        matrix = new Matrix();
        inverse = new Matrix();

        // Gestion du rafraichissement de la vue. La méthode update (juste en dessous)
        // sera appelée toutes les 30 ms
        timer = new RefreshHandler(this);

        this.setOnTouchListener(this);

        // Un clic sur la vue lance (ou relance) l'animation
/*        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timer.isRunning()) timer.scheduleRefresh(30);
            }
        });*/
    }

    /**
     * Mise à jour (faite toutes les 30 ms)
     */
    @Override
    public void update() {
        if (this.isShown()) { // Si la vue est visible
            timer.scheduleRefresh(30); // programme le prochain rafraichissement
//            car.update(); // mise à jour de la position de la voiture
            invalidate(); // demande à rafraichir la vue
        }
    }

    /**
     * Méthode appelée (automatiquement) pour afficher la vue
     * C'est là que l'on dessine le décor et les sprites
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // On met une couleur de fond
        canvas.drawColor(0xff000000);

        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"
        setCamera(canvas);

        // Dessin des différents éléments
        board.paint(canvas);
//        track.paint(canvas);
//        car.paint(canvas,track.getTileWidth(),track.getTileHeight());



    }

    private void setCamera(Canvas canvas) {
        updateTransform();


        canvas.concat(matrix);

    }

    private void updateTransform() {
        // On calcul le facteur de zoom
        float tiles_x = (1.0f*getWidth())/ SpriteSheet.get(R.mipmap.goban).w;
        float tiles_y =  (1.0f*getHeight())/SpriteSheet.get(R.mipmap.goban).h;
        float min_tiles = Math.min(tiles_x,tiles_y);
        float scale = (min_tiles)/board.getSize();


        matrix.setTranslate(getWidth()/2,getHeight()/2);

        // La suite de transfomations est à interpréter "à l'envers"

        // On termine par un centrage de l'origine (le milieu) dans la fenêtre

        // On mets à l'échelle calculée au dessus
        matrix.preScale(scale, scale);

        // On centre sur le milieu du plateau de jeu
        matrix.preTranslate(-SpriteSheet.get(R.mipmap.goban).w*board.getSize()/2.0f,
                -SpriteSheet.get(R.mipmap.goban).h*board.getSize()/2.0f
                );
        matrix.invert(inverse);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.d("GameView",event.toString());
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_MOVE){
            float[] coord = {event.getX(), event.getY()};
            inverse.mapPoints(coord);
            float x =  (coord[0]/SpriteSheet.get(R.mipmap.goban).w);
            float y =  (coord[1]/SpriteSheet.get(R.mipmap.goban).h);

            if (event.getAction() == MotionEvent.ACTION_UP)
                board.playIfValid(x,y);
            else
                board.highlight(x,y);
            this.invalidate();
            return true;
        }
        return false;
    }
}
