package IAs;

import modele.Carte;
import modele.ExecPlayground;
import modele.Playground;

import java.util.ArrayList;
class nbReste{
    int carte;
    int nb;
    public nbReste(int c, int n){
        carte = c;
        nb = n;
    }

    public void dimiNb() {
        this.nb--;
    }
}
public class IAProba extends IA{
    private ArrayList<nbReste> inconnu;
    private int nbInconnu;
    public IAProba(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        inconnu = new ArrayList<>();
        for (int i = 1; i <6 ; i++) inconnu.add(new nbReste(i, 5));
        nbInconnu = 0;
    }

    public void setCarteInconnu(){
        nbInconnu = 0;
        for (int i = 0; i < pg.getUsed().size(); i++) {
            int k = pg.getUsed().get(i).getValue();
            inconnu.get(k-1).dimiNb();
        }
        for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++) {
            int k = pg.getCurrentPlayerCards().get(i).getValue();
            inconnu.get(k-1).dimiNb();
        }
        for (int i = 0; i < inconnu.size(); i++) nbInconnu += this.inconnu.get(i).nb;
    }


    public int getNbInconnu() {
        return this.nbInconnu;
    }

    @Override
    public void iaStep() {
//        setCarteInconnu();
//        System.out.println("Used : " + pg.getUsed());
//        System.out.println("NbInconnu : " + getNbInconnu());
//        System.out.println("Inconnu1 : " + this.inconnu.get(0).nb);
//        System.out.println("Inconnu2 : " + this.inconnu.get(1).nb);
//        System.out.println("Inconnu3 : " + this.inconnu.get(2).nb);
//        System.out.println("Inconnu4 : " + this.inconnu.get(3).nb);
//        System.out.println("Inconnu5 : " + this.inconnu.get(4).nb);

    }

    @Override
    public void pickMove() {

    }

    @Override
    public void iaParryPhase() {

    }


}
