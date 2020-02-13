package fr.iutlens.mmi.boardgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import fr.iutlens.mmi.boardgame.utils.RefreshHandler;
import fr.iutlens.mmi.boardgame.utils.SpriteSheet;
import fr.iutlens.mmi.boardgame.utils.TimerAction;


public class GameView extends View implements TimerAction, View.OnTouchListener {
    private RefreshHandler timer;
    private Board board;
    private Matrix matrix;
    private Matrix inverse;
    private int id;
    private TextView scoreView;

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
        this.id = R.mipmap.pions_ensemble;
        SpriteSheet.register(id,5,4,this.getContext());

        // Création des différents éléments à afficher dans la vue
        board = new Board(id,5);
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
        canvas.drawColor(0xffffffff);

        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"
        setCamera(canvas);

        // Dessin des différents éléments
        board.paint(canvas);
    }

    private void setCamera(Canvas canvas) {
        updateTransform();
        canvas.concat(matrix);
    }

    private void updateTransform() {
        // On calcule la matrice avec setRectToRect
        // le rectangle drawableRect sera envoyé sur viewRect, centré, et avec une taille maximale

        RectF drawableRect = new RectF(0, 0,
                board.coordinate.getXTotalSize(),
                board.coordinate.getYTotalSize());
        RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        matrix.invert(inverse);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_MOVE){
            float[] coord = {event.getX(), event.getY()};
            inverse.mapPoints(coord);
            float x =  coord[0];
            float y =  coord[1];

            if (event.getAction() == MotionEvent.ACTION_UP) {
                board.playIfValid(x, y);
                this.invalidate();
                if (board.perdu){
                    Intent intent = new Intent(getContext(),PerduActivity.class);
                    intent.putExtra("score",board.getScore());
                    getContext().startActivity(intent);

                }
                if (scoreView != null){
                    scoreView.setText("Score : "+board.getScore());
                }
            }
            return true;
        }
        return false;
    }


    public void setScoreView(TextView scoreView) {
        this.scoreView = scoreView;
    }

    public TextView getScoreView() {
        return scoreView;
    }
}
