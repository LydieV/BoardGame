package fr.iutlens.mmi.boardgame;

/**
 * Created by dubois on 26/11/2019.
 */

class Coordinate {
    private final int size;
    private final int[][] dir = {{1,0},{0,1},{-1,0},{0,-1}};

    public Coordinate(int size) {
        this.size = size;
    }

    public int getNdx(int xx, int yy) {
        return xx*size+yy;
    }

    public int getNeighbour(int ndx, int direction){
        int[] coord =  {ndx/size,ndx%size};
        for(int i =0; i<2; ++i){
            coord[i]+=dir[direction][i];
            if (coord[i]<0 || coord[i]>=size) return -1;
        }
        return getNdx(coord[0],coord[1]);
    }

    public int getBoardSize() {
        return size * size;
    }

    public int getX(int ndx) {
        return ndx/size;
    }

    public int getY(int ndx) {
        return ndx%size;
    }

}
