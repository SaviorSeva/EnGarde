package IAs;

import controlleur.ControlCenter;
import modele.Carte;
import modele.CarteEtDirection;
import modele.ExecPlayground;
import modele.Playground;

import java.util.ArrayList;

public abstract class IA {
    ExecPlayground epg;
    Playground pg;
    ArrayList<Carte> iaCartes;
    int iaPlayer;
    ArrayList<Boolean> choisir;
    ArrayList<CarteEtDirection> ceds;
    int direction;
    ControlCenter c;
    public IA(ExecPlayground epg, Playground pg, ControlCenter cc){
        this.epg = epg;
        this.pg = pg;
        this.c = cc;
        direction = 0;
        ceds = new ArrayList<>();
        iaPlayer = epg.humanPlayer%2+1;
        if(iaPlayer == 1) iaCartes = pg.getBlancCartes();
        else if (iaPlayer == 2) iaCartes = pg.getNoirCartes();
        choisir = new ArrayList<>();
        for (int i = 0; i < 5; i++) choisir.add(false);
    }

    public IA(){

    }

    public void resetChoisir(){
        for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++) {
            choisir.set(i, false);
        }
    }

    public void choisirParryOrAttackCartes(int nb, int attValue){
        for (int i = 0; i < iaCartes.size() && nb > 0 ; i++)
            if (iaCartes.get(i).getValue() == attValue){
                choisir.set(i, true);
                nb--;
            }
    }

    public void jouerCarte(int direction, ArrayList<Boolean> choisir){
        pg.setDirectionDeplace(direction);
        pg.setSelected(choisir);
        
        int choseCase;
        if((this.pg.getTourCourant() == 1 && this.pg.getDirectionDeplace() == 1) || (this.pg.getTourCourant() == 2 && this.pg.getDirectionDeplace() == 2)) {
            choseCase = this.pg.getPlayerCourant().getPlace() + this.epg.getSelectedCard().getValue();
        }else if((this.pg.getTourCourant() == 2 && this.pg.getDirectionDeplace() == 1) || (this.pg.getTourCourant() == 2 && this.pg.getDirectionDeplace() == 1)){
            choseCase = this.pg.getPlayerCourant().getPlace() - this.epg.getSelectedCard().getValue();
        }else choseCase = this.pg.getPlayerCourant().getPlace();
        //c.tapezSourisGrille(choseCase);
        c.interSwing.gi.setChoseCase(choseCase);
        System.out.println("Etat : " + this.pg.getWaitStatus());
        c.confirmReceived();
        resetChoisir();
    }

    public int nbCarteI(int valeur){
        int n = 0;
        for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++)
            if(pg.getCurrentPlayerCards().get(i).getValue() == valeur) n++;
        return n;
    }
    public void resetAllPossible(boolean needAvance){
        ceds.clear();
        for(int i=0;i<iaCartes.size();i++) {
            if (needAvance){
                if (iaCartes.get(i).getValue() <= pg.getDistance())
                    ceds.add(new CarteEtDirection(1, iaCartes.get(i), i));
            }
            if (iaCartes.get(i).getValue() <= this.pg.getPlayerCourant().getDistToStartPlace())
                ceds.add(new CarteEtDirection(2, iaCartes.get(i), i));
        }
    }


    public abstract boolean pickMove();
    
    public abstract void iaParryPhase();

    public abstract void iaStep();


}
