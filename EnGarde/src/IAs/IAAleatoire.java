package IAs;

import modele.*;

import java.util.ArrayList;
import java.util.Random;

public class IAAleatoire extends IA{
    Random random;

    ArrayList<Carte> canPlay;
    ArrayList<CarteEtDirection> ceds;
    int direction;

    private boolean parry;

    public IAAleatoire(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        random = new Random();
        ceds = new ArrayList<>();
        direction = 0;
        parry = false;
    }

    public void resetAllPossible(boolean needAvance){
        ceds.clear();
        for(int i=0;i<iaCartes.size();i++) {
            if (needAvance){
                if (iaCartes.get(i).getValue() <= pg.getDistance())
                    ceds.add(new CarteEtDirection(1, iaCartes.get(i), i));
            }
            if (iaCartes.get(i).getValue() <= this.pg.getPlayerCourant().getDistToStartPoint())
                ceds.add(new CarteEtDirection(2, iaCartes.get(i), i));
        }
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
        Attack etreAtt = pg.getLastAttack();
        int nb = 0;
        switch (etreAtt.getAt()) {
            case NONE:
                resetChoisir();
                break;
            case DIRECT:
                choisirParryCartes(etreAtt.getAttnb(), etreAtt.getAttValue().getValue());
                System.out.println("AI choose to parry direct attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                jouerCarte(0, choisir);
                break;
            case INDIRECT:
                resetAllPossible(false);
                //Verifier si on peut resister
                for(int i=0; i<iaCartes.size() && nb<etreAtt.getAttnb(); i++)
                    if(iaCartes.get(i).getValue() == etreAtt.getAttValue().getValue()) nb++;
                //Parry indirect attack
                if(nb == etreAtt.getAttnb()) {
                    choisirParryCartes(nb, etreAtt.getAttValue().getValue());
                    System.out.println("AI choose to parry indirect attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                    jouerCarte(0, choisir);
                //retreat
                }else if(ceds.size()>0){
                    int r = random.nextInt(ceds.size());
                    choisir.set(ceds.get(r).getIndex(), true);
                    direction = ceds.get(r).getDirection();
                    System.out.println("IA retreat " + ceds.get(r).getC());
                    jouerCarte(direction, choisir);
                } else System.err.println("Probleme");
                break;
            default:
                break;
        }

    }

    @Override
    public void pickMove(){
        if(!iaCanAttack()) {
            resetAllPossible(true);
            if (ceds.size() > 0) {
                int r = random.nextInt(ceds.size());
                int index = ceds.get(r).getIndex();
                choisir.set(index, true);
                direction = ceds.get(r).getDirection();
                System.out.println("Directiion : " + direction + "   Carte : " + ceds.get(r).getC().getValue());
                jouerCarte(direction, choisir);
            }else System.out.println("AI has no card! OvO");
        }
    }

    @Override
    public void iaStep(){
        while (epg.isIaRound()){
            if (!this.parry) {
                System.out.println("IA Cartes : " + pg.getCurrentPlayerCards());
                this.iaParryPhase();
            }
            else this.pickMove();
        }
    }

    public int nbCarteI(int valeur){
        int n = 0;
        for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++)
            if(pg.getCurrentPlayerCards().get(i).getValue() == valeur) n++;
        return n;
    }
}