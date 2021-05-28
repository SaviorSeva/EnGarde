package IAs;

import modele.Carte;
import modele.ExecPlayground;
import modele.Playground;

import java.util.ArrayList;

public abstract class IA {
    ExecPlayground epg;
    Playground pg;
    ArrayList<Carte> iaCartes;
    int iaPlayer;
    ArrayList<Boolean> choisir;
    
    public IA(ExecPlayground epg, Playground pg){
        this.epg = epg;
        this.pg = pg;
        iaPlayer = epg.humanPlayer%2+1;
        if(iaPlayer == 1) iaCartes = pg.getBlancCartes();
        else if (iaPlayer == 2) iaCartes = pg.getNoirCartes();
        choisir = new ArrayList<>();
        for (int i = 0; i < 5; i++) choisir.add(false);
    }

    public void resetChoisir(){
        for (int i = 0; i < iaCartes.size(); i++) {
            choisir.set(i, false);
        }
    }

    public void choisirParryCartes(int nb, int attValue){
        for (int i = 0; i < iaCartes.size() && nb > 0 ; i++)
            if (iaCartes.get(i).getValue() == attValue){
                choisir.set(i, true);
                nb--;
            }
    }

    public void jouerCarte(int direction, ArrayList<Boolean> choisir){
        pg.setDirectionDeplace(direction);
        pg.setSelected(choisir);
        epg.confirmReceived();
        resetChoisir();
    }



    public abstract void pickMove();
    
    public abstract void iaParryPhase();

    public abstract void iaStep();


}
