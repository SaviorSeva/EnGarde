package IAs;


import controlleur.ControlCenter;
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
    final int []inconnu;
    private int nbInconnu;
    final double [][]proba;
    Random random;
    private PriorityQueue<IAAction> iaAction;
    private CarteEtDirection move;
    final double seuilIntension;
    final int dynamique = 8;
    private boolean isRetreat;

    public IAProba() {
        random = new Random();
        isRetreat = false;
        inconnu = new int[5];
        iaAction = new PriorityQueue<>(new IAActionComparator());
        for (int i = 0; i <5 ; i++) {
            inconnu[i] = 5;
        }
        nbInconnu = 0;
        proba = new double[5][6];
        seuilIntension = 0.5; /* 攻击阈值 */

    }
    public IAProba(ExecPlayground epg, Playground pg, ControlCenter cc) {
        super(epg, pg, cc);
        random = new Random();
        isRetreat = false;
        inconnu = new int[5];
        iaAction = new PriorityQueue<>(new IAActionComparator());
        for (int i = 0; i <5 ; i++) {
            inconnu[i] = 5;
        }
        nbInconnu = 0;
        proba = new double[5][6];
        seuilIntension = 0.5; /* 攻击阈值 */
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

    public static long factorial(long number) {
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

        return (double) (a * b) / c;
    }

    public void setTableauProba() {
        int n = pg.getCurrentEnemyPlayerCards().size();
        int N = getNbInconnu();

        /* i = 0 means carte 1, i = 4 means carte 5 */

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
        if(epg.isIaRound() || epg.isIaProbaRound()){
            setCarteInconnu();
            setTableauProba();
            System.out.println("IA Proba Cartes : " + pg.getCurrentPlayerCards());
            this.iaParryPhase();
            if(!isRetreat) this.pickMove();
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.println("Proba de Inconnu " + (i+1) + " au moin avec " + j + " cartes : " + proba[i][j]);
            }
        }
    }

    public void iaCanAttack(){
        boolean []vue = new boolean[5];
        int dis = epg.getDistance();
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
                //System.out.println("Dis : " + dis);
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
            for (IAAction i : iaAction) {
                System.out.println("proba : " + i.probaReussite);
                if (i.attack.getAt() == AttackType.INDIRECT) {
                    System.out.println("move : " + i.move.getC().getValue());
                }
                System.out.println("Type att : " + i.attack.getAt());
                System.out.println("Att value : " + i.attack.getAttValue());
                System.out.println("Att nb : " + i.attack.getAttnb());
                System.out.println(" ");

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
        /* Mouvement */
        resetAllPossible(true);
        movement(pg.getDistance(), ceds);
        choisir.set(move.getIndex(), true);
        jouerCarte(move.getDirection(),choisir);
        if (epg.cartesContains(epg.getDistance())){
            resetChoisir();
            jouerCarte(3, choisir);
        }
        return false;
        //选概率最高的打，如果概率都不高return false, 在iastep里执行movement + cancel
    }

    public void movement(int dis, ArrayList<CarteEtDirection> ceds){
        int min = 10, in = 0, dir = 0, max = 0;
        if(pg.getEnemyCourant().getCartes().size()==0){
            for(CarteEtDirection ced : ceds){
                //可改进，对面无法移动的情况，手牌可以任意组合
                dir = ced.getDirection();
                in = ced.getIndex();
                if(ced.getDirection()==1) break;
            }
        }
        else if(dis>10) {
            for (int j = 0; j < iaCartes.size(); j++){
                if((pg.getEnemyCourant().getDistToStartPlace() - pg.getPlayerCourant().getDistToStartPlace())<=0) {
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
                 //5+Math.round(5*((23-pg.getPlayerCourant().getDistToStartPoint())/23));
                //目前撤退到dis为8的附近，可改进，8 可动态，在确定对方手牌时可增加判断条件，或离出发点过近时
                if(ced.getDirection()==1) val = Math.abs(dynamique - (dis - ced.getC().getValue()));
                else if(ced.getDirection()==2) val = Math.abs(dynamique - (dis + ced.getC().getValue()));
                if(min>val){
                    min = val;
                    in = ced.getIndex();
                    dir = ced.getDirection();
                }
            }
            if(dir == 2 && dis + iaCartes.get(in).getValue() <=5){
                double m = 1.0;
                resetAllPossible(false);
                for(CarteEtDirection ced : ceds){
                    //如果不得不撤退到5以内的位置，选择一个对手直接攻击我成功率最低的位置
                    int disApres = dis + ced.getC().getValue();
                    dir = 2;
                    if(nbCarteI(disApres)>3){
                        in = ced.getIndex();
                        break;
                    }
                    if(m>proba[Math.min(disApres-1, 4)][nbCarteI(disApres)+1]){
                        in = ced.getIndex();
                        m=proba[Math.min(disApres-1, 4)][nbCarteI(disApres)+1];
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
        iaCartes = pg.getCurrentPlayerCards();
        switch (etreAtt.getAt()) {
            case NONE:
                resetChoisir();
                break;
            case DIRECT:
                choisirParryOrAttackCartes(etreAtt.getAttnb(), etreAtt.getAttValue().getValue());
                System.out.println("AI Proba choose to parry direct attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                jouerCarte(3, choisir);
                iaCartes = pg.getCurrentPlayerCards();
                break;
            case INDIRECT:
                resetAllPossible(false);
                //Parry indirect attack
                if(nbCarteI(etreAtt.getAttValue().getValue()) >= etreAtt.getAttnb()) {
                    choisirParryOrAttackCartes(etreAtt.getAttnb(), etreAtt.getAttValue().getValue());
                    System.out.println("AI Proba choose to parry indirect attack of " + pg.getLastAttack().getAttValue().getValue() + "with " + etreAtt.getAttnb() + "cards");
                    for (Boolean aBoolean : choisir) {
                        System.out.println("choisir : " + aBoolean);
                    }
                    jouerCarte(3, choisir);
                    iaCartes = pg.getCurrentPlayerCards();
                    //retreat
                }else if(ceds.size()>0){
                    isRetreat = true;
                    movement(pg.getDistance(),ceds);
                    choisir.set(move.getIndex(), true);
                    direction = move.getDirection();
                    System.out.println("IA Proba retreat " + move.getC());
                    jouerCarte(direction, choisir);
                } else System.err.println("Probleme");
                break;
            default:
                break;
        }
    }
}
