package vue;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdaptateurSouris extends MouseAdapter {
    CollecteurEvenements controle;
    PGGraphique pgg;

    AdaptateurSouris(PGGraphique pg, CollecteurEvenements c) {
        controle = c;
        pgg = pg;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int c = e.getX() / 100;
        int l = e.getY() / 100;
        System.out.println(l);
        System.out.println(c);
        controle.clicSouris(l, c);
    }
}
