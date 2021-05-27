package IAs;

import modele.*;

import java.util.ArrayList;
import java.util.Random;

public class IAAleatoire extends IA{
    Random random;

    ArrayList<Carte> canPlay;
    ArrayList<CarteEtDirection> ceds;
    int direction;
    ArrayList<Boolean> choisir;
    private boolean parry;

    public IAAleatoire(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        random = new Random();
        ceds = new ArrayList<>();
        direction = 0;
        choisir = new ArrayList<>();
        parry = false;
        for (int i = 0; i < 5; i++) {
            choisir.add(false);
        }
    }

    public void resetAllPossible(boolean needAvance){
        ceds.clear();
        if(ceds.size() == 0){
            for(int i=0;i<iaCartes.size();i++) {
                if (needAvance){
                    if (iaCartes.get(i).getValue() <= pg.getDistance())
                        ceds.add(new CarteEtDirection(1, iaCartes.get(i), i));
                }
                if (iaCartes.get(i).getValue() <= this.pg.getPlayerCourant().getDistToStartPoint())
                    ceds.add(new CarteEtDirection(2, iaCartes.get(i), i));
            }
        }
    }

    public boolean iaCanAttack(){
        //int n = 0;
        int r = random.nextInt(2);
        //Carte c = null;
        if(epg.canAttack()) {
            for (int i = 0; i < iaCartes.size(); i++) {
                if (iaCartes.get(i).getValue() == epg.getDistance()) {
                    direction = 1;
                    choisir.set(i, true);
                    if (r == 0) break;
                }
            }
            //epg.roundEnd(new Attack(AttackType.DIRECT, c, n));
            return true;
        }
        return false;
    }

    @Override
    public void iaParryPhase(){
        if(parry==false) {
            Attack etreAtt = pg.getLastAttack();
            int nb = 0;
            switch (etreAtt.getAt()) {
                case NONE:
                    resetChoisir();
                    break;
                case DIRECT:
                    for (int i = 0; i < iaCartes.size() && nb < etreAtt.getAttnb(); i++)
                        if (iaCartes.get(i).getValue() == pg.getLastAttack().getAttValue().getValue()){
                            choisir.set(i, true);
                            nb++;
                        }
                    pg.setSelected(this.getIaCartes());
                    epg.confirmReceived();
                    break;
                case INDIRECT:
                    resetAllPossible(false);
                    nb = 0;
                    for(int i=0; i<iaCartes.size(); i++) {
                        if(iaCartes.get(i).getValue() == etreAtt.getAttValue().getValue()) nb++;
                    }
                    if(nb >= etreAtt.getAttnb()) {
                        for (int i = 0; i < iaCartes.size(); i++) {
                            if (iaCartes.get(i).getValue() == etreAtt.getAttValue().getValue()) choisir.set(i, true);
                        }
                        System.out.println("IA parry");
                        pg.setSelected(this.getIaCartes());
                        epg.confirmReceived();
                    }else if(ceds.size()>0){
                        int r = random.nextInt(ceds.size());
                        int index = ceds.get(r).getIndex();
                        choisir.set(index, true);
                        direction = ceds.get(r).getDirection();
                        System.out.println("IA retreat" + ceds.get(r).getC());
                        epg.pg.setDirectionDeplace(direction);
                        pg.setSelected(choisir);
                        epg.confirmReceived();
                    }else{
                        System.err.println("Probleme");
                    }
                    break;
                default:
                    break;
            }
            parry = true;
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
            }else {
                System.out.println("AI has no card! OvO");
            }
        }
    }

    public int getDirection() {
        return direction;
    }

    public ArrayList<Boolean> getIaCartes() {
        return choisir;
    }

    public boolean getParry(){
        return parry;
    }

    public void setParry(Boolean bool){
        parry = bool;
    }

    @Override
    public void resetChoisir(){
        for (int i = 0; i < iaCartes.size(); i++) {
            choisir.set(i, false);
        }
    }

}