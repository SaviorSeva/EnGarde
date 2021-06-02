package modele;

public class CarteEtDirection {
    private int direction;
    private Carte c;
    private int index;


    public CarteEtDirection(int direction, Carte c, int i){
        this.direction = direction;
        this.c = c;
        this.index = i;
    }

    public CarteEtDirection(){
        this.direction = 0;
        this.c = null;
        this.index = 0;
    }


    public int getDirection() {
        return direction;
    }

    public Carte getC() {
        return c;
    }

    public int getIndex() {
        return index;
    }
}