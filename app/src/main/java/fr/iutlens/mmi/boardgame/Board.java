package fr.iutlens.mmi.boardgame;

import android.content.Intent;
import android.graphics.Canvas;
import android.view.View;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import fr.iutlens.mmi.boardgame.utils.SpriteSheet;



/**
 * Created by dubois on 26/11/2019.
 */

class Board {
    final HexagonalCoordinate coordinate;
    final int[] game; //tableau du jeu
    final int size;

    private final int spriteSheetId;

    private View gameView;
    private int selection;
    private int select;
    public boolean perdu;
    private int score;

    public void playIfValid(float x, float y) {
        int ndx = coordinate.getIndexXY(x, y);
        if (isValidMove(ndx)) play(ndx);
    }

    public Board(int id, int size) {
        this.spriteSheetId = id;
        coordinate = new HexagonalCoordinate(id, size,size);
        this.size = size;
        game = new int[coordinate.getNM()];
        initPlateau();

    }

    private void initPlateau() {
        selection =-1;
        for(int i = 0; i < 9; ++i)
            game[i]= (int) (Math.random()*9+1);

        for(int i = 0; i < 100; ++i){
            int a =  (int) (Math.random()*game.length);
            int b =  (int) (Math.random()*game.length);
            int tmp = game[a];
            game[a] = game[b];
            game[b] = tmp;
        }

        select = -1;
        perdu = false;
    }


    public void paint(Canvas canvas) {
        SpriteSheet spriteSheet = SpriteSheet.get(spriteSheetId);

        for(int i = 0; i < size; ++i)
            for(int j = 0; j< size; ++j){
                int ndx = coordinate.getIndexIJ(i,j);
                int c = get(i, j);
                spriteSheet.paint(canvas, c,coordinate.getX(ndx), coordinate.getY(ndx));
            }

    }


    void play(int ndx) {

       if(!isEmpty(ndx)){
           if(select != ndx && select != -1){
               isSelected(select);
               isSelected(ndx);
           } else {
               isSelected(ndx);
           }
       }
       if(isEmpty(ndx) && select != -1){
           int tmp = game[ndx];
           game[ndx] = game[select];
           game[select] = tmp;
           isSelected(ndx);
           if(!checkComposante(ndx)){
               ajoutPion();
               ajoutPion();
               ajoutPion();
           }
       }
    }

    private void ajoutPion() {
        //parcours tableau
        Vector<Integer> resultat = new Vector<>();

            for(int i = 0; i < game.length; i++){
                if(isEmpty(i)){
                    resultat.add(i);
                }
            }

            // vérifie qu'il y a des cases vides
            if(resultat.size() > 0){
                int nbAletoire = (int) (Math.random()*resultat.size());
                int index = resultat.get(nbAletoire);
                game[index] = (int) (Math.random()*9+1);
                checkComposante(index);
            }

            perdu = resultat.size()<= 1;
    }

    private boolean checkComposante(int ndx) {
        Set<Integer> comp = composante(ndx);
        if (comp.size()>=4){
            int value = game[ndx]+2;
            score += 1 << (value-1);
            if(value >= 10){
                value = 0;
            }
            for(Integer v : comp){
                game[v]= 0;
            }

            game[ndx] = value;
            if(value != 0){
                checkComposante(ndx);
            }
            return true;
        }
        return false;
    }

    private Set<Integer> composante(int ndx) {
        Set<Integer> resultat = new HashSet<>();
        Set<Integer> voisin = new HashSet<>();
        Set<Integer> prochainVoisin = new HashSet<>();

        resultat.add(ndx);
        int value = game[ndx];

        voisin = getVoisin(ndx,value);

        //cherche voisin, tant qu'il y a des voisins il continue de chercher
        while (!voisin.isEmpty()){
            resultat.addAll(voisin);
            for(Integer v : voisin){
                Set<Integer> tmp = getVoisin(v,value);
                tmp.removeAll(resultat);
                prochainVoisin.addAll(tmp);
            }
            Set<Integer> tmp = prochainVoisin;
            prochainVoisin = voisin;
            voisin = tmp;
            prochainVoisin.clear();
        }
        return resultat;
    }

    private Set<Integer> getVoisin(int ndx, int value) {
        Set<Integer> resultat = new HashSet<>();

        for(int i=0; i < 6; ++i){
            int ndx_voisin = coordinate.next(ndx,i);
            if (ndx_voisin !=  -1 && game[ndx_voisin] == value) resultat.add(ndx_voisin);
        }

        return resultat;
    }

    private void isSelected(int ndx) {
        if(game[ndx] <= 10){
            game[ndx] = game[ndx]+10;
            select = ndx;
        } else {
            game[ndx] = game[ndx]-10;
            select = -1;
        }
    }

    public boolean isValidMove(int ndx) {
        if  (ndx == -1) return false;


        return true;
    }

    private boolean isEmpty(int ndx) {
        return game[ndx] ==0;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
