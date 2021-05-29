package IAs;

import modele.Carte;
import modele.ExecPlayground;
import modele.Playground;

import java.util.ArrayList;

public class IAProba extends IA{
    private int inconnu[];
    private int nbInconnu;
    private double proba[][];

    public IAProba(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        inconnu = new int[5];
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
        for (int i = 0; i < inconnu.length; i++) nbInconnu += this.inconnu[i];
    }


    public int getNbInconnu() {
        return this.nbInconnu;
    }

    public int factorial(int number) {
        if (number <= 1)
            return 1;
        else
            return number * factorial(number - 1);
    }

    public double calculProba(int i, int k, int n, int N) {
        int K = inconnu[i];
        System.out.println("K " + K);
        double resultat = 0.0;
        int a = (factorial(K) / (factorial(k) * factorial(K - k)));
        int b = (factorial(N - K) / (factorial(n - k) * factorial((N - K) - (n - k))));
        int c = (factorial(N) / (factorial(n) * factorial(N - n)));

        System.out.println("a " + a);
        System.out.println("b " + b);
        System.out.println("c " + c);

        return resultat = (double) (a * b) / c;
    }

    public void setTableauProba() {
        int n = pg.getCurrentEnemyPlayerCards().size();
        int N = getNbInconnu();

        /** i = 0 means carte 1, i = 4 means carte 5
         *  k = 0 means **/

        for (int i = 0; i < 5; i++)
            for (int k = 0; k < 6; k++) {
                if (k == 0 && inconnu[i] == 0) {
                    proba[i][0] = 1.0;
                    continue;
                }else if (k == 0 && inconnu[i] != 0) {
                    proba[i][0] = 0.0;
                    continue;
                }
                proba[i][k] = calculProba(i, k, n, N);
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
                System.out.println("Proba de Inconnu " + (i+1) + " avec " + j + " cartes : " + proba[i][j]);
            }
        }
//        System.out.println("Used : " + pg.getUsed());
//        System.out.println("NbInconnu : " + getNbInconnu());
//        System.out.println("Inconnu1 : " + this.inconnu[0]);
//        System.out.println("Inconnu2 : " + this.inconnu[1]);
//        System.out.println("Inconnu3 : " + this.inconnu[2]);
//        System.out.println("Inconnu4 : " + this.inconnu[3]);
//        System.out.println("Inconnu5 : " + this.inconnu[4]);

    }

    @Override
    public void pickMove() {

    }

    @Override
    public void iaParryPhase() {

    }


}
