package IAs;

import global.Configuration;
import modele.*;

import java.util.ArrayList;

import static IAs.IAProba.factorial;

public class IAConfiguration{
    ArrayList<Carte> carteJouer;
    ArrayList<Carte> cartesMain;
    ArrayList<Carte> reste;//adverse
    ArrayList<Carte> carteForNext;//ia
    ArrayList<Carte> carteEnemyCurrant;//
    ArrayList<IAConfiguration> tousFils;
    int tourCourrant;
    int owner;
    int positionNoir;
    int positionBlanc;
    IAConfiguration pere;
    IAConfiguration vraiFils;
    Attack parry;
    IAAction action;
    int typeGagne;
    double gagnerProba;
    double branchGagne;
    double branchPerdu;
    int couche;

    public IAConfiguration(Playground pg){
        carteForNext = new ArrayList<>();
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
        carteForNext.addAll(cartesMain);
        if(carteForNext.size()!=5 && reste.size()>5) carteForNext.addAll(reste);
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
        action = new IAAction(a);
        reste = new ArrayList<>();
       carteForNext = new ArrayList<>();
        tousFils = new ArrayList<>();
        pere.tousFils.add(this);
        carteJouer =new ArrayList<>();
        cartesMain = new ArrayList<>();
        cartesMain.addAll(pere.cartesMain);
        positionBlanc = pere.positionBlanc;
        positionNoir = pere.positionNoir;
        typeGagne = 2;
        couche = pere.couche++;

        //Deepcopy
        reste.addAll(pere.reste);
        //Parry phase
        if(p!=null){
            parry = new Attack(p.getAt(),p.getAttValue(),p.getAttnb());
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
            carteForNext.addAll(pere.reste);
        }
        else {
            carteForNext.addAll(pere.cartesMain);
            if(reste.size()>5 && cartesMain.size()<5){
                carteForNext.addAll(pere.reste);
            }
        }
        if(p!=null){
            for (int i = 0; i < p.getAttnb(); i++) {
                carteForNext.remove(p.getAttValue());
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

    public int disToDebut(int t){
        if(t == 1) return positionBlanc;
        else return 22-positionNoir;
    }

    public int tourNext() {
        return tourCourrant % 2 + 1;
    }

    public int getDisToDebutBlanc() {
        return positionBlanc;
    }

    public int getDisToDebutNoir() {
        return positionNoir;
    }

    public void incrementGagne(){
        if(this.pere!=null){
            this.pere.branchGagne++;
            this.pere.incrementGagne();
        }
    }

    public void incrementPerdu(){
        if(this.pere!=null){
            this.pere.branchPerdu++;
            this.pere.incrementPerdu();
        }
    }
    public void setTourCourrant(){
        tourCourrant = tourCourrant%2+1;
    }


    public void setMinmax(IAConfiguration config) {
        if(config.pere!=null){//max
            if(config.tourCourrant==owner){
                if(config.gagnerProba>config.pere.gagnerProba){
                    pere.vraiFils = config;
                    pere.gagnerProba = config.gagnerProba;
                    setMinmax(config.pere);
                }
            }else{
                if(config.gagnerProba<config.pere.gagnerProba){
                    pere.vraiFils = config;
                    pere.gagnerProba = config.gagnerProba;
                    setMinmax(config.pere);
                }
            }
        }
    }
}
