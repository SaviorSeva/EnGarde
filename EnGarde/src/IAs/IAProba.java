package IAs;


import modele.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

public class IAProba extends IA{
    private int inconnu[];
    private int nbInconnu;
    private double proba[][];
    Random random;
    private boolean parry;
    private PriorityQueue<IAAction> iaAction;
    private CarteEtDirection move;
    private double seuilIntension;

    public IAProba(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        random = new Random();
        parry = false;
        inconnu = new int[5];
        iaAction = new PriorityQueue<>();
        for (int i = 0; i <5 ; i++) {
            inconnu[i] = 5;
        }
        nbInconnu = 0;
        proba = new double[5][6];
        seuilIntension = 0.2; /** 攻击阈值 **/
    }

    public void setCarteInconnu(){
        nbInconnu = 0;
        for (int i = 0; i < pg.getUsed().size(); i++) {
            int k = pg.getUsed().get(i).getValue();
            inconnu[k-1]--;
        }
        for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++) {
            int k = pg.getCurrentPlayerCards().get(i).getValue();
            inconnu[k-1]--;
        }
        for (int j : inconnu) {
            nbInconnu += j;
        }
    }


    public int getNbInconnu() {
        return this.nbInconnu;
    }

    public long factorial(long number) {
        if (number <= 1)
            return 1;
        else
            return number * factorial(number - 1);
    }

    /**
     Formule de loi hypergenetrique
     C(K,k) * C(N-K, n-k)
     f(k,n,K,N) =  ----------------------
     C(N-n)
     **/
    public double calculProba(int i, int k, int n, int N) {
        int K = inconnu[i];
        if(K==0){
            if (k ==0)return 1;
            else return 0;
        }
        long a = (factorial(K) / (factorial(k) * factorial(K - k)));
        long b = (factorial(N - K) / (factorial(n - k) * factorial((N - K) - (n - k))));
        long c = (factorial(N) / (factorial(n) * factorial(N - n)));

        //return resultat = (double) Math.round((a * b) * 10000 / c)/10000;
        return (double) (a * b) / c;
    }

    public void setTableauProba() {
        int n = pg.getCurrentEnemyPlayerCards().size();
        int N = getNbInconnu();

        /** i = 0 means carte 1, i = 4 means carte 5 **/

        for (int i = 0; i < 5; i++)
            for (int k = 0; k < 6; k++) {
                proba[i][k] = calculProba(i, k, n, N);
            }
        for (int i = 0; i <5 ; i++) {
            double cumule = 0;
            for (int j = 5; j > 0; j--) {
                cumule += proba[i][j];
                proba[i][j] = cumule;
            }
        }
    }

    @Override
    public void iaStep() {
        setCarteInconnu();
        setTableauProba();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.println("Proba de Inconnu " + (i+1) + " au moin avec " + j + " cartes : " + proba[i][j]);
            }
        }
        while (epg.isIaRound()){
            if (!this.parry) {
                System.out.println("IA Cartes : " + pg.getCurrentPlayerCards());
                this.iaParryPhase();
            }
            else{
                this.pickMove();
            }
        }

        Iterator<IAAction> it = iaAction.iterator();
        while (it.hasNext()) {
            IAAction i = it.next();
            System.out.println("Direction :" + i.move.getDirection());
            System.out.println("ATT V: " + i.attack.getAttValue());
            System.out.println("ATT nb: " + i.attack.getAttnb());
            System.out.println("Att type" + i.attack.getAt());
            System.out.println("Proba : " + i.probaReussite);
        }

//        System.out.println("Used : " + pg.getUsed());
//        System.out.println("NbInconnu : " + getNbInconnu());
//        System.out.println("Inconnu1 : " + this.inconnu[0]);
//        System.out.println("Inconnu2 : " + this.inconnu[1]);
//        System.out.println("Inconnu3 : " + this.inconnu[2]);
//        System.out.println("Inconnu4 : " + this.inconnu[3]);
//        System.out.println("Inconnu5 : " + this.inconnu[4]);
    }

    public void iaCanAttack(){
        boolean vue[] = new boolean[5];
        int dis = epg.getDistance();
        int n = 0;
        //Direct attack possible
        for (Carte iaCarte : iaCartes) {
            if (dis == iaCarte.getValue()) {
                iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, iaCarte, nbCarteI(iaCarte.getValue())), 1 - proba[dis-1][nbCarteI(dis)]));
            }
        }
        //Indirect attack et move possible
        resetAllPossible(true);
        for (CarteEtDirection ced : ceds) {
            int index = ced.getIndex();
            n = 0;
            for (int j = 0; j < iaCartes.size(); j++) {
                if (vue[iaCartes.get(index).getValue()-1]) break;
                if (ced.getDirection() == 1)
                    dis = dis - iaCartes.get(index).getValue();
                else if (ced.getDirection() == 2)
                    dis = dis + iaCartes.get(index).getValue();
                //move et attack(Indirect attack)
                if (j != ced.getIndex() && dis > 0) {
                    if (dis == iaCartes.get(j).getValue())
                        iaAction.add(new IAAction(ced, new Attack(AttackType.INDIRECT, iaCartes.get(j), nbCarteI(iaCartes.get(j).getValue())), 1 - proba[dis-1][nbCarteI(dis)]));
                }
            }
            vue[ced.getC().getValue()-1] = true;
        }
        //jouerCarte(1, choisir);
    }

    public boolean pickMove() {
        iaCanAttack();
        if (iaAction.size() != 0) {
            IAAction pickAttack = iaAction.remove();
            if (pickAttack.probaReussite >= seuilIntension){
                switch (pickAttack.attack.getAt()) {
                    case DIRECT:
                        choisirParryOrAttackCartes(pickAttack.attack.getAttnb(), pickAttack.attack.getAttValue().getValue());
                        jouerCarte(1, choisir);
                        break;
                    case INDIRECT:
                        choisir.set(pickAttack.move.getIndex(), true);
                        jouerCarte(pickAttack.move.getDirection(), choisir);
                        choisirParryOrAttackCartes(pickAttack.attack.getAttnb(), pickAttack.attack.getAttValue().getValue());
                        jouerCarte(1, choisir);
                        break;
                    default:
                        System.out.println("Wrong type of attack! ");
                        break;
                }
                return true;
            }
        }

        /** Mouvement **/
        resetAllPossible(true);
        movement(pg.getDistance(), ceds);
        choisir.set(move.getIndex(), true);
        jouerCarte(move.getDirection(),choisir);
        if (epg.canAttack()){
            epg.cancelReceived();
        }

        return false;
        //选概率最高的打，如果概率都不高return false, 在iastep里执行movement + cancel
    }

    public void movement(int dis, ArrayList<CarteEtDirection> ceds){
        int min = 10, in = 0, dir = 0, max = 0;
        if(pg.getEnemyCourant().getCartes().size()==0){
            dir = 1;
            in = 0;
        }
        else if(dis>10) {
            for (int j = 0; j < iaCartes.size(); j++){
                if((pg.getEnemyCourant().getDistToStartPoint() - pg.getPlayerCourant().getDistToStartPoint())<=0) {
                    if (min > iaCartes.get(j).getValue()) {
                        min = iaCartes.get(j).getValue();
                        dir = 1;
                        in = j;
                    }
                }else {
                    if (max < iaCartes.get(j).getValue()) {
                        max = iaCartes.get(j).getValue();
                        dir = 1;
                        in = j;
                    }
                }
            }
        }else{
            int val = 0;
            for(CarteEtDirection ced : ceds){
                //目前撤退到dis为8的附近，可改进，根据自己手牌和对方手牌来判断哪个dis不会被直接攻击到，或者哪个距离可以格挡
                if(ced.getDirection()==1) val = Math.abs(8 - (dis - ced.getC().getValue()));
                else if(ced.getDirection()==2) val = Math.abs(8 - (dis + ced.getC().getValue()));
                if(min>val){
                    min = val;
                    in = ced.getIndex();
                    dir = ced.getDirection();
                }
            }
        }

        move = new CarteEtDirection(dir, iaCartes.get(in), in);
        System.out.println("Movement direction: " + move.getDirection());
        System.out.println("Movement value: " + move.getC().getValue());
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
                choisirParryOrAttackCartes(etreAtt.getAttnb(), etreAtt.getAttValue().getValue());
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
                    choisirParryOrAttackCartes(nb, etreAtt.getAttValue().getValue());
                    System.out.println("AI choose to parry indirect attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                    jouerCarte(0, choisir);
                    //retreat
                }else if(ceds.size()>0){
                    movement(pg.getDistance(),ceds);
                    choisir.set(move.getIndex(), true);
                    direction = move.getDirection();
                    System.out.println("IA retreat " + move.getC());
                    jouerCarte(direction, choisir);
                } else System.err.println("Probleme");
                break;
            default:
                break;
        }
    }



}
