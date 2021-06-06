package IAs;

import modele.*;

import java.util.ArrayList;

import static IAs.IAProba.factorial;

public class IAConfiguration{
    ArrayList<Carte> carteJouer;
    ArrayList<Carte> cartesMain;
    ArrayList<Carte> reste;//adverse
    ArrayList<Carte> carteCurrant;//ia
    ArrayList<Carte> carteEnemyCurrant;//
    ArrayList<IAConfiguration> tousFils;
    int tourCourrant;
    int owner;
    int positionNoir;
    int positionBlanc;
    IAConfiguration pere;
    Attack parry;
    IAAction action;
    int typeGagne;
    double gagnerProba;
    int branchGagne;
    int branchPerdu;
    int couche;

    public IAConfiguration(Playground pg){
        carteCurrant = new ArrayList<>();
        reste = new ArrayList<>();
        for(int i=1; i<6; i++) {
            reste.add(Carte.UN);
            reste.add(Carte.DEUX);
            reste.add(Carte.TROIS);
            reste.add(Carte.QUATRE);
            reste.add(Carte.CINQ);
        }

        cartesMain = new ArrayList<>();
        for (Carte c: pg.getPlayerCourant().getCartes()) {
            cartesMain.add(c);
            reste.remove(c);
        }
        for (Carte c: pg.getUsed()) {
            reste.remove(c);
        }
        carteCurrant.addAll(cartesMain);
        if(carteCurrant.size()!=5 && reste.size()>5) carteCurrant.addAll(reste);
        tousFils = new ArrayList<>();
        positionBlanc = pg.getBlancPos();
        positionNoir = pg.getNoirPos();
        pere = null;
        tourCourrant = pg.getTourCourant()%2+1;
        this.owner = pg.getTourCourant();
        typeGagne = 2;
        action = new IAAction(null, null, 0);
    }


    public IAConfiguration(Attack p, IAAction a, IAConfiguration pere){
        int n = 0;
        this.pere = pere;
        owner = pere.owner;
        tourCourrant = pere.tourCourrant%2+1;
        parry = p;
        action = new IAAction(a);
        reste = new ArrayList<>();
        carteCurrant = new ArrayList<>();
        tousFils = new ArrayList<>();
        pere.tousFils.add(this);
        carteJouer =new ArrayList<>();
        cartesMain = new ArrayList<>();
        cartesMain.addAll(pere.cartesMain);
        positionBlanc = pere.positionBlanc;
        positionNoir = pere.positionNoir;
        typeGagne = 2;
        couche++;

        //Deepcopy
        reste.addAll(pere.reste);
        //Parry phase
        if(p!=null){
            for (int i = 0; i < reste.size() && n<=parry.getAttnb(); i++) {
                carteJouer.add(parry.getAttValue());
            }
        }
        //Move phase
        if (action.move.getC() != null) {
            carteJouer.add(action.move.getC());
        }
        //Attack phase
        if (action.attack != null && action.attack.getAttnb() > 0) {
            for (int i = 0; i < action.attack.getAttnb(); i++) {
                carteJouer.add(action.attack.getAttValue());
            }
        }
        //Verifier la carte a la main d'abord
        for (Carte c : carteJouer) {
            //System.out.println(cartesMain.size());
            if (cartesMain != null && cartesMain.contains(c) && tourCourrant == owner) {
//                System.out.println("Carte M  : " + c);
//                System.out.println("remove M : " + cartesMain.remove(c));
                cartesMain.remove(c);
            } else {
//                System.out.println("Carte  : " + c);
//                System.out.println("remove : " + reste.remove(c));
                reste.remove(c);
            }
        }

        System.out.println("PPPPPPPPPPPP :"+ (reste.size() + cartesMain.size()));


        if (tourCourrant == 1 && action.move.getDirection() == 1) {
            positionBlanc += action.move.getC().getValue();
        } else if (tourCourrant == 1 && action.move.getDirection() == 2) {
            positionBlanc -= action.move.getC().getValue();
        } else if (tourCourrant == 2 && action.move.getDirection() == 1) {
            positionNoir -= action.move.getC().getValue();
        } else if (tourCourrant == 2 && action.move.getDirection() == 2) {
            positionNoir += action.move.getC().getValue();
        }
        System.out.println("Couche : " + couche);
        if(this.tourCourrant== this.owner){
            carteCurrant.addAll(pere.reste);
        }
        else {
            carteCurrant.addAll(pere.cartesMain);
            if(reste.size()>5 && cartesMain.size()<5){
                carteCurrant.addAll(pere.reste);
            }
        }
        if(p!=null){
            for (int i = 0; i < p.getAttnb(); i++) {
                carteCurrant.remove(p.getAttValue());
            }
        }
    }

    public IAConfiguration(int gagne, IAConfiguration pere, Attack attack){
        owner = pere.owner;
        tourCourrant = pere.tourCourrant%2+1;
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
    public void setTourCourrant(){
        tourCourrant = tourCourrant%2+1;
    }

    
}
