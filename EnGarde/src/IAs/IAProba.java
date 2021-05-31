package IAs;


import modele.*;

import java.util.*;
class IAActionComparator implements Comparator<IAAction> {
    public int compare(IAAction iaAction, IAAction t1) {
        if((iaAction.probaReussite - t1.probaReussite)>0) return -1;
        else if((iaAction.probaReussite - t1.probaReussite)==0 && t1.attack.getAt()==AttackType.DIRECT) return 1;
        else return 1;
    }
}

public class IAProba extends IA{
    private int inconnu[];
    private int nbInconnu;
    private double proba[][];
    Random random;
    private PriorityQueue<IAAction> iaAction;
    private CarteEtDirection move;
    private double seuilIntension;
    private boolean isRetreat;

    public IAProba(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        random = new Random();
        isRetreat = false;
        inconnu = new int[5];
        iaAction = new PriorityQueue<>(new IAActionComparator());
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
        if(epg.isIaRound()){
            setCarteInconnu();
            setTableauProba();
            System.out.println("IA Cartes : " + pg.getCurrentPlayerCards());
            this.iaParryPhase();
            if(!isRetreat) this.pickMove();
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.println("Proba de Inconnu " + (i+1) + " au moin avec " + j + " cartes : " + proba[i][j]);
            }
        }
//        Iterator<IAAction> it = iaAction.iterator();
//        while (it.hasNext()) {
//            IAAction i = it.next();
//            System.out.println("Direction :" + i.move.getDirection());
//            System.out.println("ATT V: " + i.attack.getAttValue());
//            System.out.println("ATT nb: " + i.attack.getAttnb());
//            System.out.println("Att type" + i.attack.getAt());
//            System.out.println("Proba : " + i.probaReussite);
//        }

    }

    public void iaCanAttack(){
        boolean vue[] = new boolean[5];
        int dis = epg.getDistance();
        int n = 0;
        //Direct attack possible
        for (Carte iaCarte : iaCartes) {
            if (dis == iaCarte.getValue()) {
                iaAction.offer(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, iaCarte, nbCarteI(iaCarte.getValue())), 1 - proba[dis-1][nbCarteI(dis)]));
                break;
            }
        }
        //Indirect attack et move possible
        resetAllPossible(true);
        for (CarteEtDirection ced : ceds) {
            int index = ced.getIndex();
            for (int j = 0; j < iaCartes.size(); j++) {
                dis = epg.getDistance();
                if (vue[iaCartes.get(index).getValue()-1]) break;
                if (ced.getDirection() == 1)
                    dis = dis - iaCartes.get(index).getValue();
                else if (ced.getDirection() == 2)
                    dis = dis + iaCartes.get(index).getValue();
                System.out.println("Dis : " + dis);
                //move et attack(Indirect attack)
                if (j != ced.getIndex() && dis > 0) {
                    if (dis == iaCartes.get(j).getValue()){
                        iaAction.offer(new IAAction(ced, new Attack(AttackType.INDIRECT, iaCartes.get(j), nbCarteI(iaCartes.get(j).getValue())), 1 - proba[dis-1][nbCarteI(dis)]));
                        vue[ced.getC().getValue()-1] = true;
                    }
                }
            }

        }
    }

    public boolean pickMove() {
        iaCanAttack();
        if (iaAction.size() != 0) {
            Iterator it = iaAction.iterator();
            while(it.hasNext()){
                IAAction i = (IAAction) it.next();
                System.out.println("proba : " + i.probaReussite);
                if(i.attack.getAt()==AttackType.INDIRECT){
                    System.out.println("move : " + i.move.getC().getValue());
                }
                System.out.println("Type att : " + i.attack.getAt());
                System.out.println("Att value : " + i.attack.getAttValue());
                System.out.println("Att nb : " + i.attack.getAttnb());
                System.out.println("");

            }
            IAAction pickAttack = iaAction.remove();
            System.out.println("ProbaReussite !!! : " + pickAttack.probaReussite);
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
        System.out.println("Mouvement！！！");
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
                //目前撤退到dis为8的附近，可改进，8 可动态，在确定对方手牌时可增加判断条件，或离出发点过近时
                if(ced.getDirection()==1) val = Math.abs(8 - (dis - ced.getC().getValue()));
                else if(ced.getDirection()==2) val = Math.abs(8 - (dis + ced.getC().getValue()));
                if(min>val){
                    min = val;
                    in = ced.getIndex();
                    dir = ced.getDirection();
                }
            }
            if(dir == 2 && dis + iaCartes.get(in).getValue() <=5){
                double m = 1.0;
                for (int i = 0; i < iaCartes.size(); i++) {
                    //如果不得不撤退到5以内的位置，选择一个对手直接攻击我成功率最低的位置
                    int disApres = dis + iaCartes.get(i).getValue();
                    dir = 2;
                    if(nbCarteI(disApres)>3){
                        in = i;
                        break;
                    }
                    if(m>proba[disApres-1][nbCarteI(disApres)+1]){
                        in = i;
                        m=proba[disApres-1][nbCarteI(disApres)+1];
                    }
                }
            }
        }

        move = new CarteEtDirection(dir, iaCartes.get(in), in);
        System.out.println("Movement direction: " + move.getDirection());
        System.out.println("Movement value: " + move.getC().getValue());
    }


    @Override
    public void iaParryPhase(){
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
                    isRetreat = true;
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
