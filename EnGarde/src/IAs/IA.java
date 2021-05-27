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
    
    public IA(ExecPlayground epg, Playground pg){
        this.epg = epg;
        this.pg = pg;
        iaPlayer = epg.humanPlayer%2+1;
        if(iaPlayer == 1) iaCartes = pg.getBlancCartes();
        else if (iaPlayer == 2) iaCartes = pg.getNoirCartes();
    }

    public abstract void pickMove();
    
    public abstract void iaParryPhase();
    
    public abstract void resetChoisir();

	public abstract boolean getParry();

	public abstract int getDirection();

	public abstract ArrayList<Boolean> getIaCartes();


}
