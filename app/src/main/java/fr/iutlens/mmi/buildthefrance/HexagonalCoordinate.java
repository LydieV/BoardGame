package fr.iutlens.mmi.buildthefrance;


import fr.iutlens.mmi.buildthefrance.utils.SpriteSheet;

/**
 * Gestion des coordonnées sur une grille hexagonale
 *
 *
 * On utilise les noms suivants pour les coordonnées :
 *
 * i, j : coordonnées entières dans la grille (colone et ligne)
 * x, y : coordonnées à l'écran (réels)
 * ndx : un entier indiquant le numéro (unique) d'une case
 *
 * exemple de numéroation des cases pour une grille 4x4 (n=m=4)
 *
 *  i  0  1   2  3
 * j
 * 0   0      2
 * 0      1      3
 * 1   4      6
 * 1      5      7
 * 2   8     10
 * 2      9     11
 * 3  12     14
 * 3     13     15
 */
public class HexagonalCoordinate {


    /* directions possibles
         4
       3   5
         x
       2   0
         1
     */
	private static final int[] direction = {
		1, 1,		
		0, 1,
		-1, 1,
		-1, 0,
		0,-1,
		1, 0
	}; 

	final int n,m; // n sur m cases
	float c,h; // taille des hexagones : inscrits dans un rectangle 4h sur 2c

    /**
     * Création du système de coordonnées
     *
     * @param id Feuille de sprite utilisée pour les cases (définit leur taille)
     * @param n nombre de colonnes
     * @param m nombre de lignes
     */
	public HexagonalCoordinate(int id, int n, int m) {
		super();
		this.n = n;
		this.m = m;
		SpriteSheet spriteSheet = SpriteSheet.get(id);
		this.c = spriteSheet.w /4;
		this.h = spriteSheet.h /2;
	}


	public int nextI(int i, int j, int d){
		return i+direction[d*2];
	}
	public int nextJ(int i, int j, int d){
		int result = j+direction[d*2+1];
		if (direction[d*2] == 0) return result;
		if (i%2 == 0) --result;
		return result;
	}

    /**
     * Calcul de l'index de la case suivante dans une direction donnée
     * @param index
     * @param dir
     * @return -1 si en dehors de la grille
     */
	public int next(int index, int dir){
		int i = getI(index);
		int j = getJ(index);
		return getIndexIJ(nextI(i,j,dir),nextJ(i,j,dir));
	}

	public int getIndexIJ(int i, int j){
		if (i<0 || j <0 || i>= n || j>=m) return -1;
		return i+j*n;
	}

	public int getI(int index){
		return index%n;
	}

	public int getJ(int index){
		return index/n;
	}

	public int getN(){
		return n;
	}

	public int getM(){
		return m;
	}

	public int getNM(){
		return n*m;
	}

	public float getXCenter(int index){
		return c*(3*getI(index)+2);
	}

	public float getYCenter(int index){
		return h*(2*getJ(index)+getI(index)%2+1);
	}

	public float getX(int index){
		return 3*c*getI(index);
	}

	public float getY(int index){
		return h*(2*getJ(index)+getI(index)%2);
	}

	public float getXSize(){
		return 4*c;
	}

	public float getYSize(){
		return 2*h;
	}

	public float getXTotalSize(){
		return (3*n+1)*c;
	}

	public float getYTotalSize(){
		return (2*m+1)*h;
	}


	public int getIndexXY(float x, float y){
		if (x<0 || y <0) return -1;
		int i = (int) (x /c);
		int j = (int) (y /h);
		x = x -  i*c;
		y = y -  j*h;
		if (i%3 ==0){
			if ((i+j)%2 == 0){
				if (x*h + y*c < c*h) --i;
			} else {
				if (x*h < y*c) --i;
			}
		}
		if (i<0) return -1;
		i = i /3;
		j = (j-i%2);
		if (j <0) return -1;
		j = j/2;
		return getIndexIJ(i,j);
	}





	public float getC() {
		return c;
	}
	public float getH() {
		return h;
	}

	public void setCH(float c, float h) {
		this.c = c;
		this.h = h;
	}

}
