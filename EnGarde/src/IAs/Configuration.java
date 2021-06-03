package IAs;

import modele.Action;
import modele.Attack;
import modele.Carte;
import modele.Player;

import java.util.ArrayList;

import static IAs.IAProba.factorial;

public class Configuration extends IAProba{
    ArrayList<Carte> carteJouer;
    ArrayList<Carte> cartesMain;
    ArrayList<Carte> reste;
    ArrayList<Configuration> tousFils;
    int tourCourrant;
    int owner;
    int positionNoir;
    int positionBlanc;
    Configuration pere;
    Attack parry;
    IAAction action;
    int typeGagne;
    double gagnerProba;
    int branchGagne;
    int branchPerdu;

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

    public void setCarteInconnu(){
        for (int i = 0; i < carteJouer.size(); i++) {
            int k = pg.getUsed().get(i).getValue();
            inconnu[k-1]--;
        }
        for (int i = 0; i < cartesMain.size(); i++) {
            int k = pg.getCurrentPlayerCards().get(i).getValue();
            inconnu[k-1]--;
        }
    }


    public void setTableauProba() {
        int n = 5;
        int N = reste.size();
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

    public Configuration(Player p1, int owner){
        reste = new ArrayList<>();
        for(int i=1; i<6; i++) {
            reste.add(Carte.UN);
            reste.add(Carte.DEUX);
            reste.add(Carte.TROIS);
            reste.add(Carte.QUATRE);
            reste.add(Carte.CINQ);
        }
        cartesMain = new ArrayList<>();
        for (Carte c: p1.getCartes()) {
            cartesMain.add(c);
            reste.remove(c);
        }
        tousFils = new ArrayList<>();
        positionBlanc = 0;
        positionNoir = 22;
        pere = null;
        tourCourrant = owner;
        this.owner = owner;
        typeGagne = 2;
        action = new IAAction(null, null, 0);
    }


    public Configuration(Attack p, IAAction a, Configuration pere){
        int n = 0;
        this.pere = pere;
        tourCourrant = pere.tourCourrant%2+1;
        parry = p;
        action = a;
        reste = new ArrayList<>();
        tousFils = new ArrayList<>();
        pere.tousFils.add(this);
        carteJouer =new ArrayList<>();
        typeGagne = 2;
        //Deepcopy
        for (Carte carte:pere.reste) { reste.add(carte);}
        //Parry phase
        if(p!=null){
            for (int i = 0; i < reste.size() && n<=parry.getAttnb(); i++) {
                carteJouer.add(parry.getAttValue());
            }
        }
        //Move phase
        if (action.move.getC()!=null){
            carteJouer.add(action.move.getC());
        }
        //Attack phase
        if(action.attack!=null && action.attack.getAttnb()>0 ){
            for (int i = 0; i < action.attack.getAttnb(); i++) {
                carteJouer.add(action.attack.getAttValue());
            }
        }
        //Verifier la carte a la main d'abord
        for (Carte c:carteJouer) {
            if(cartesMain!=null && cartesMain.contains(c)){
                cartesMain.remove(c);
            }else{
                reste.remove(c);
            }
        }

        if(tourCourrant==1 && action.move.getDirection()==1){
            positionBlanc += action.move.getC().getValue();
        }else if(tourCourrant==1 && action.move.getDirection()==2){
            positionBlanc -= action.move.getC().getValue();
        }else if(tourCourrant==2 && action.move.getDirection()==1){
            positionNoir -= action.move.getC().getValue();
        }else if(tourCourrant==2 && action.move.getDirection()==2){
            positionNoir += action.move.getC().getValue();
        }

    }
    public Configuration(int gagne, Configuration pere, Attack attack){
        if(gagne == owner) {
            typeGagne = 1;
            gagnerProba = 1;
        }
        else {
            typeGagne = 0;
            gagnerProba = 0;
        }
        this.pere = pere;
        tousFils = new ArrayList<>();
        pere.tousFils.add(this);
    }

    public int getDistance(){
        return this.positionNoir-this.positionBlanc;
    }

    public int disToDebut(){
        if(tourCourrant==1) return positionBlanc;
        else return 22-positionNoir;
    }

    public void incrementGagne(){
        this.branchGagne++;
    }

    
}
