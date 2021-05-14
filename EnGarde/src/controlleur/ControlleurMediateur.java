package controlleur;

import modele.Player;
import modele.Playground;
import vue.CollecteurEvenements;
import vue.PGGraphique;

public class ControlleurMediateur implements CollecteurEvenements {
    Playground p;

    public ControlleurMediateur(Playground pgg) {
        p = pgg;
    }

    public void clicSouris(int l, int c) {
        int dl = l;
        int dc = c;
        p.avance(dc);
    }
}
