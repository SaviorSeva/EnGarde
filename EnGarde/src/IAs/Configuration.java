package IAs;

import modele.Action;
import modele.Attack;
import modele.Carte;
import modele.Player;

import java.util.ArrayList;

public class Configuration {
    ArrayList<Carte> carteJouer;
    ArrayList<Carte> cartesMain;
    ArrayList<Carte> reste;
    int tourCourrant;
    int owner;
    int positionNoir;
    int positionBlanc;
    Configuration pere;
    Attack parry;
    IAAction action;
    int typeGagne;

    public Configuration(Player p1, int owner){
        for(int i=1; i<6; i++) {
            reste.add(Carte.UN);
            reste.add(Carte.DEUX);
            reste.add(Carte.TROIS);
            reste.add(Carte.QUATRE);
            reste.add(Carte.CINQ);
        }
        for (Carte c: p1.getCartes()) {
            cartesMain.add(c);
            reste.remove(c);
        }
        positionBlanc = 0;
        positionNoir = 22;
        pere = null;
        tourCourrant = owner;
        this.owner = owner;
        typeGagne = 2;
    }


    public Configuration(Attack p, IAAction a, Configuration pere){
        int n = 0;
        this.pere = pere;
        tourCourrant = pere.tourCourrant%2+1;
        parry = p;
        action = a;
        reste = new ArrayList<>();
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
        if(action.attack.getAttnb()>0){
            for (int i = 0; i < action.attack.getAttnb(); i++) {
                carteJouer.add(action.attack.getAttValue());
            }
        }
        //Verifier la carte a la main d'abord
        for (Carte c:carteJouer) {
            if(cartesMain.contains(c)){
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
    public Configuration(int gagne, Configuration pere){
        if(gagne == owner) typeGagne = 1;
        else typeGagne = 0;
        this.pere = pere;
    }

    public int getDistance(){
        return this.positionNoir-this.positionBlanc;
    }

    public int disToDebut(){
        if(tourCourrant==1) return positionBlanc;
        else return 22-positionNoir;
    }
    
}
