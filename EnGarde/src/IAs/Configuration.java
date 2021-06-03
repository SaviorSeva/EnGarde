package IAs;

import modele.Action;
import modele.Attack;
import modele.Carte;
import modele.Player;

import java.util.ArrayList;

import static IAs.IAProba.factorial;

public class Configuration{
    ArrayList<Carte> carteJouer;
    ArrayList<Carte> cartesMain;
    ArrayList<Carte> reste;//adverse
    ArrayList<Carte> iaCartes;//ia
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


    public Configuration(Player p1, int owner, ArrayList<Carte> used){
        iaCartes = new ArrayList<>();
        reste = new ArrayList<>();
        for(int i=1; i<6; i++) {
            reste.add(Carte.UN);
            reste.add(Carte.DEUX);
            reste.add(Carte.TROIS);
            reste.add(Carte.QUATRE);
            reste.add(Carte.CINQ);
        }
        iaCartes = reste;
        cartesMain = new ArrayList<>();
        for (Carte c: p1.getCartes()) {
            cartesMain.add(c);
            reste.remove(c);
        }
        for (Carte c: used) {
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
        owner = pere.owner;
        tourCourrant = pere.tourCourrant%2+1;
        parry = p;
        action = a;
        reste = new ArrayList<>();
        iaCartes = new ArrayList<>();
        tousFils = new ArrayList<>();
        pere.tousFils.add(this);
        carteJouer =new ArrayList<>();
        cartesMain = new ArrayList<>();
        cartesMain = pere.cartesMain;
        positionBlanc = pere.positionBlanc;
        positionNoir = pere.positionNoir;
        typeGagne = 2;
        //Deepcopy
        for (Carte carte:pere.cartesMain) { iaCartes.add(carte);}
        for (Carte carte:pere.reste) {
            reste.add(carte);
            iaCartes.add(carte);
        }
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
            //System.out.println(cartesMain.size());
            if(cartesMain!=null && cartesMain.contains(c)){
//                System.out.println("Carte M  : " + c);
//                System.out.println("remove M : " + cartesMain.remove(c));
                cartesMain.remove(c);
            }else{
//                System.out.println("Carte  : " + c);
//                System.out.println("remove : " + reste.remove(c));
                reste.remove(c);
            }
        }
        //System.out.println(" :"+ reste.size());

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
        owner = pere.owner;
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
        if(tourCourrant==owner && owner==2) return positionBlanc;
        else return 22-positionNoir;
    }

    public void incrementGagne(){
        this.branchGagne++;
    }


    
}
