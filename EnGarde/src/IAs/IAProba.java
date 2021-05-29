package IAs;


import modele.*;

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
        System.out.println("Used : " + pg.getUsed());
        System.out.println("NbInconnu : " + getNbInconnu());
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.println("Proba de Inconnu " + (i+1) + " au moin avec " + j + " cartes : " + proba[i][j]);
            }
        }
        iaCanAttack();

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
        int n = 0, val = 0;
        //Direct attack possible
        for (Carte iaCarte : iaCartes) {
            if (dis == iaCarte.getValue()) {
                n++;
                iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, iaCarte, n), proba[dis-1][n]));
            }
        }
        //Indirect attack et move possible
        resetAllPossible(true);
        for (CarteEtDirection ced : ceds) {
            int index = ced.getIndex();
            n = 0;
            for (int j = 0; j < iaCartes.size(); j++) {
                if(vue[iaCartes.get(index).getValue()-1]) break;
                if (ced.getDirection() == 1)
                    val = dis - iaCartes.get(index).getValue();
                else if (ced.getDirection() == 2)
                    val = dis + iaCartes.get(index).getValue();
                if (j != ced.getIndex() && val > 0) {
                    if (val == iaCartes.get(j).getValue()) {
                        n++;
                        iaAction.add(new IAAction(ced, new Attack(AttackType.INDIRECT, iaCartes.get(j), n), proba[val-1][n]));
                    }//else iaAction.add(new IAAction(ced, new Attack(AttackType.NONE, null, 0), 0));

                }
            }
            vue[ced.getC().getValue()-1] = true;
        }
        //jouerCarte(1, choisir);

    }
    @Override
    public void pickMove() {

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



}
