package fr.iutlens.mmi.boardgame;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class GameState {
    public static final int EMPTY = 0;
    public static final int LAST = 1;
    public static final int BLACK = 3;
    public static final int WHITE = 2;
    public static final int OUTSIDE = 4;
    public static final int KO = 4;
    public static final int BLACK_TERRITORY = 5;
    public static final int WHITE_TERRITORY = 6;

    final Coordinate coordinate;
    final SparseArray<Chain> chain;
    final int[] game;


    int nextColor;
    int last_capture_ndx = -1;
    int last_capture_size = 0;

    final int size;


    public GameState(GameState gameState){
        this.coordinate = gameState.coordinate;
        this.game = gameState.game.clone();
        this.size = gameState.size;

        this.nextColor = gameState.nextColor;
        this.last_capture_ndx = gameState.last_capture_ndx;
        this.last_capture_size = gameState.last_capture_size;

        this.chain = new SparseArray<>();
        HashSet<Chain> chains = new HashSet<>();
        for(int i =0; i< gameState.chain.size(); ++i){
            chains.add(gameState.chain.valueAt(i));
        }

        for (Chain c : chains) new Chain(c);

    }

    public GameState(int size) {
        coordinate = new Coordinate(size);
        this.size = size;
        chain = new SparseArray<>();

        game = new int[coordinate.getBoardSize()];
        for(int i = 0; i < game.length; ++i)
            game[i]= EMPTY;

        nextColor = BLACK;
    }

    void play(int xx, int yy) {
//        Log.d("Board(play)", xx + ":" + yy);
        int ndx = coordinate.getNdx(xx, yy);
        play(ndx);
    }

    void play(int ndx) {
        last_capture_size = 0;

        game[ndx] =nextColor;

        Chain friend = null;
        for (int i = 0; i < 4; ++i) {
            int neighbourNdx = coordinate.getNeighbour(ndx, i);
            if (neighbourNdx != -1) {
                Chain c = chain.get(neighbourNdx);
                if (c != null) {
                    if (c.color == nextColor) {
                        if (friend == null) {
                            friend = c;
                            friend.add(ndx);
                        } else {
                            if (friend.element.size() >= c.element.size()) {
                                friend.merge(c);
                            } else {
                                c.merge(friend);
                                friend = c;
                            }
                        }
                    } else c.removeLiberty(ndx);
                }
            }
        }
        if (friend == null){
            friend = new Chain(nextColor);
            friend.add(ndx);
        } else friend.removeLiberty(ndx);

        nextColor = opponent(nextColor);
    }

    int opponent(int color) {
        return color == BLACK ? WHITE : (color == WHITE ? BLACK : color);
    }

    boolean isValidMove(int xx, int yy) {
        if (xx < 0 || xx >= size) return false;
        if (yy < 0 || yy >= size) return false;
        int ndx = coordinate.getNdx(xx, yy);
        return isValidMove(ndx);
    }

    public boolean isKo(int ndx){
        if (ndx==-1 || game[ndx] != EMPTY) return false; // Not Empty
        if (last_capture_size != 1 || last_capture_ndx != ndx) return false; //Not single capture
        for (int i = 0; i < 4; ++i) {
            int neighbour = getNeighbour(ndx, i);
            Chain c = chain.get(coordinate.getNeighbour(ndx, i));
        //    if (neighbour == EMPTY || neighbour == nextColor) // has liberty or friend
        //        return false; // should be impossible because just captured...
            if (neighbour == opponent(nextColor) && c.isInAtari()) // potential capture
                return true;
        }
        return false; //no potential capture, it would be suicide not ko
    }

    public boolean isValidMove(int ndx) {
        if (game[ndx] != EMPTY) {
//            Log.d("Board:isValidMove", TextUtils.join(",", chain.get(ndx).liberty));
            return false;
        }
        if (last_capture_size == 1 && last_capture_ndx == ndx) return false; // KO !
        for (int i = 0; i < 4; ++i) {
            int neighbour = getNeighbour(ndx, i);
            Chain c = chain.get(coordinate.getNeighbour(ndx, i));
            if (neighbour == EMPTY) return true; //at least one liberty -> legal
            else if (neighbour == nextColor && !c.isInAtari()) // connect to non atari group -> legal
                return true;
            else if (neighbour == opponent(nextColor) && c.isInAtari()) //capture -> legal
                return true;
        }
        return false; //no liberty, no capture and no friendly not ataried neighbour group
    }

    int get(int xx, int yy) {
        return game[coordinate.getNdx(xx, yy)];
    }

    int getNeighbour(int ndx, int dir) {
        ndx = coordinate.getNeighbour(ndx, dir);
        return ndx == -1 ? OUTSIDE : game[ndx];
    }

    public float getSize() {
        return size;
    }

    public boolean playRandom(List<Integer> list) {
        list.clear();
        for(int ndx = 0;ndx<game.length;++ndx){
            if (isValidMove(ndx) && (isCapture(ndx) || (!isSelfAtariOrEyeRemoval(ndx)))) list.add(ndx);
        }
        if (list.isEmpty()) {
            nextColor=opponent((nextColor)); // Pass
            return false;
        }
        play(list.get((int) (Math.random()*list.size())));
        return true;
    }


    private boolean isCapture(int ndx) {
        boolean capture = false;
        for (int i = 0; i < 4; ++i) {
            Chain c = chain.get(coordinate.getNeighbour(ndx, i));
            if (c != null && c.color != nextColor && c.liberty.size()==1) capture = true;
        }
        return capture;
    }

    private boolean isSelfAtariOrEyeRemoval(int ndx) {
        int empty = 0;
        Chain friend = null;
        boolean connect = false;
        boolean hasEnemy = false;
        int liberties = 0;
        for (int i = 0; i < 4; ++i) {
            if (getNeighbour(ndx,i) == EMPTY) ++empty;
            Chain c = chain.get(coordinate.getNeighbour(ndx, i));
            if (c != null && c.color != nextColor) hasEnemy = true;
            else if (c != null && c.color == nextColor) {

                if (friend == null) {
                    friend = c;
                    liberties += (c.liberty.size()-1);
                }
                else {
                    if (friend !=c) {
                        connect = true;
                        liberties += (c.liberty.size()-1);
                        if (c.liberty.size() == 2 && friend.liberty.size()==2 &&
                                    c.liberty.equals(friend.liberty))
                                liberties--;
                        else if (friend.liberty.size()==1) friend = c;
                    }
                }
            }
        }
        if (liberties+empty<=1) return true; //self atari
        if (connect || hasEnemy) return false; // there is no eye to remove
        if (empty == 0) return true; //each neighbour is friend, no connection
        return false;
    }

    public boolean isKo() {
        return isKo(last_capture_ndx);
    }


    class Chain {
        public final int color;
        HashSet<Integer> element;
        HashSet<Integer> liberty;

        public Chain(Chain c){
            this.color= c.color;
            this.element = new HashSet<>(c.element);
            this.liberty = new HashSet<>(c.liberty);
            for (int ndx : element) {
                chain.put(ndx,this);
            }
        }

        public Chain( int color) {
            element = new HashSet<Integer>();
            liberty = new HashSet<Integer>();
            this.color = color;
        }

        public void add(int ndx) {
            element.add(ndx);
            chain.put(ndx,this);
            liberty.remove(ndx);
            for (int i = 0; i < 4; ++i) {
                int neighbourNdx = coordinate.getNeighbour(ndx, i);
                if (neighbourNdx != -1 && game[neighbourNdx]==EMPTY)
                    liberty.add(neighbourNdx); // add potential liberty
            }
        }

        private void removeLiberty(int ndx) {
            liberty.remove(ndx);
            if (liberty.isEmpty()) {
                for (int i : element) {
                    capture(i);
                }
            }
        }

        private void merge(int ndx) {
            Chain target = chain.get(ndx);
            if (target == null) return;
            merge(target);
        }

        public boolean isInAtari() {
            return liberty.size() == 1;
        }

        private void capture(int ndx) {
            Chain removedChain = chain.get(ndx);

            chain.remove(ndx);
            game[ndx] = EMPTY;
            last_capture_size++;
            last_capture_ndx = ndx;

            for (int i = 0; i < 4; ++i) {   // captured stone add one liberty to every neighbour
                int neighbourNdx = coordinate.getNeighbour(ndx, i);
                // get neighbour chain
                Chain neighbourChain = chain.get(neighbourNdx);
                // if none is found, check if current chain fit
//                if (neighbourChain == null && element.contains(neighbourNdx)) neighbourChain =this;
                if (neighbourChain != null && neighbourChain != removedChain) {
                    neighbourChain.liberty.add(ndx);
                }
            }
        }

        public void merge(Chain target) {
            element.addAll(target.element);
            liberty.addAll(target.liberty);
            for (int i : target.element) {
                chain.put(i, this);
            }
        }
    }
}