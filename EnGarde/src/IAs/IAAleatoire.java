package IAs;

import modele.*;

import java.util.ArrayList;
import java.util.Random;

public class IAAleatoire extends IA{
    Random random;


    int direction;

    private boolean parry;

    public IAAleatoire(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        random = new Random();
        ceds = new ArrayList<>();
        direction = 0;
        parry = false;
    }

    public boolean iaCanAttack(){
        if(epg.canAttack()) {
            int nb = 0;
            int r = random.nextInt(nbCarteI(epg.getDistance()));
            for (int i = 0; i < iaCartes.size(); i++)
                if (iaCartes.get(i).getValue() == epg.getDistance()) {
                    choisir.set(i, true);
                    if (r == nb) break;
                    nb++;
                }
            jouerCarte(1, choisir);
            return true;
        }
        return false;
    }

    @Override
    public void iaParryPhase(){
        parry = true;
        iaCartes = pg.getCurrentPlayerCards();
        Attack etreAtt = pg.getLastAttack();
        int nb = 0;
        switch (etreAtt.getAt()) {
            case NONE:
                resetChoisir();
                break;
            case DIRECT:
                choisirParryOrAttackCartes(etreAtt.getAttnb(), etreAtt.getAttValue().getValue());
                System.out.println("AI alea choose to parry direct attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                jouerCarte(3, choisir);
                break;
            case INDIRECT:
                resetAllPossible(false);
                //Verifier si on peut resister
                for(int i=0; i<iaCartes.size() && nb<etreAtt.getAttnb(); i++)
                    if(iaCartes.get(i).getValue() == etreAtt.getAttValue().getValue()) nb++;
                //Parry indirect attack
                if(nb == etreAtt.getAttnb()) {
                    choisirParryOrAttackCartes(nb, etreAtt.getAttValue().getValue());
                    System.out.println("AI alea choose to parry indirect attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                    jouerCarte(3, choisir);
                //retreat
                }else if(ceds.size()>0){
                    int r = random.nextInt(ceds.size());
                    choisir.set(ceds.get(r).getIndex(), true);
                    direction = ceds.get(r).getDirection();
                    System.out.println("IA alea retreat " + ceds.get(r).getC());
                    jouerCarte(direction, choisir);
                } else System.err.println("Probleme");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean pickMove(){
        iaCartes = pg.getCurrentPlayerCards();
        if(!iaCanAttack()) {
            resetAllPossible(true);
            if (ceds.size() > 0) {
                int r = random.nextInt(ceds.size());
                int index = ceds.get(r).getIndex();
                choisir.set(index, true);
                direction = ceds.get(r).getDirection();
                System.out.println("Directiion : " + direction + "   Carte : " + ceds.get(r).getC().getValue());
                jouerCarte(direction, choisir);
            }else System.out.println("AI alea has no card! OvO");
        }
        return true;
    }

    @Override
    public void iaStep(){
        while (epg.isIaRound()||epg.isIaAleatoireRound()){
            if (!this.parry) {
                System.out.println("IA alea Cartes : " + pg.getCurrentPlayerCards());
                this.iaParryPhase();
            }
            else this.pickMove();
        }
    }
}