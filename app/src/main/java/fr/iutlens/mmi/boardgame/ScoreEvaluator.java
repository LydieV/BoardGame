package fr.iutlens.mmi.boardgame;

import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dubois on 27/11/2019.
 */

class ScoreEvaluator {
    private static final int MAX_MOVE = 200;
    private final GameState gameState;
    int tryout, target;
    private View view;
    private int[] territory;
    private List<Integer> list;
    private EvaluateTask task = null;
    private MultiEvaluateTask[] tasks = null;


    private class MultiEvaluateTask extends AsyncTask<Integer,ScoreEvaluator,ScoreEvaluator>{

        @Override
        protected ScoreEvaluator doInBackground(Integer... integers) {
            final ScoreEvaluator scoreEvaluator = new ScoreEvaluator(gameState);
            for (int i = 0; i<integers[0]; ++i) {
                scoreEvaluator.fillRandom();
            }
            return scoreEvaluator;
        }

        @Override
        protected void onPostExecute(ScoreEvaluator scoreEvaluator) {
            super.onPostExecute(scoreEvaluator);

            aggregate(scoreEvaluator);
        }
    }

    private void aggregate(ScoreEvaluator scoreEvaluator) {
        for(int i = 0; i< territory.length; ++i){
            territory[i] += scoreEvaluator.territory[i];
        }
        tryout += scoreEvaluator.tryout;

        if (view != null && tryout>= target){
            view.invalidate();
        }
    }

    private class EvaluateTask extends AsyncTask<Integer,Integer,Integer> {
        View view;

        public EvaluateTask(View view) {
            this.view = view;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            for (int i = 0; i<integers[0]; ++i) {
                fillRandom();
                if (i%10==0) this.publishProgress(i);
            }
            return integers[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            view.invalidate();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            view.invalidate();
        }
    }

    public void evaluateInBackground(int n, View view){
        if (task != null) task.cancel(true);
        task = new EvaluateTask(view);
        task.execute(n);
    }

    public void cancelBackgroundEvaluation(){
        if (task != null) task.cancel(true);
        task = null;
    }

    public void multiEvaluateInBackground(int n, int nb, View view){
        this.view = view;
        if (tasks != null) for(MultiEvaluateTask t: tasks){
            if (t != null) t.cancel(true);
        }
        tasks = new MultiEvaluateTask[nb];
        target += nb*n;
        for(MultiEvaluateTask t : tasks){
            t = new MultiEvaluateTask();
            t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,n);
        }
    }

    public void cancelMultiBackgroundEvaluation(){
        if (tasks != null) for(MultiEvaluateTask t: tasks){
            if (t != null) t.cancel(true);
        }
        tasks = null;
        view = null;
    }


    public ScoreEvaluator(GameState gameState) {
        this.gameState = new GameState(gameState);
        tryout = 0;
        territory = new int[gameState.coordinate.getBoardSize()];
    }

    public void fillRandom(){
        GameState game = new GameState(gameState);
        list = new ArrayList<>();
        int consecutivePass = 0;
        int count = 0;
        while (consecutivePass <2 && count < MAX_MOVE){
            consecutivePass =  (game.playRandom(list)) ? 0 : (consecutivePass+1);
            ++ count;
        }
        score(game);
    }

    public void fillRandom(int n){
        for (int i = 0; i<n; ++i) fillRandom();
    }

    private void score(GameState game) {
        ++tryout;
        for(int ndx = 0 ; ndx<territory.length;++ndx){
            int c = game.game[ndx];
            if (c == GameState.EMPTY){
                int black=0;
                int white=0;
                for(int i = 0; i<4; ++i){
                    GameState.Chain chain = game.chain.get(game.coordinate.getNeighbour(ndx,i));
                    if (chain != null){
                        if (chain.color == GameState.BLACK) ++black;
                        else if (chain.color == GameState.WHITE) ++white;
                    }
                }
                if (black == 0 && white >=1) c = GameState.WHITE;
                else if (black >= 1 && white ==0) c = GameState.BLACK;
            }
            if (c == GameState.BLACK) territory[ndx] +=2;
            else if (c == GameState.EMPTY) territory[ndx] +=1;
        }
    }

    public float get(int ndx){
        return (territory[ndx]*0.5f)/tryout;
    }

    public float get(int x, int y){
        return  get(gameState.coordinate.getNdx(x,y));
    }


}
