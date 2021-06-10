package IAs;

import modele.*;
import java.util.ArrayList;

public class IAConfiguration{
    ArrayList<Carte> carteJouer;
    ArrayList<Carte> reste;//adverse
    ArrayList<Carte> carteForNext;//ia
    ArrayList<Carte> carteEnemyNext;//
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
    ArrayList<Carte> cartesIA;
    ArrayList<Carte> cartesHuman;
    ArrayList<Carte> cartesReste;
    int playerPickLastCard;
    //minmax:true => max
    boolean minmax, cut;

    public IAConfiguration(Playground pg){
        parry = pg.getLastAttack();
        cartesIA = new ArrayList<>(pg.getCurrentPlayerCards());
        cartesHuman = new ArrayList<>(pg.getCurrentEnemyPlayerCards());
        cartesReste = new ArrayList<>(pg.getReste());

        carteForNext = cartesIA;
        playerPickLastCard = 0;

        tousFils = new ArrayList<>();
        positionBlanc = pg.getBlancPos();
        positionNoir = pg.getNoirPos();
        pere = null;
        minmax = true;
        tourCourrant = pg.getTourCourant() % 2 + 1; /** Pour mettre le premier "AllPossible" est le tour de IA **/
        this.owner = pg.getTourCourant(); //owner = AI
        typeGagne = -1;
        action = new IAAction(null, pg.getLastAttack(), 0);
    }

    public IAConfiguration(Attack p, IAAction a, IAConfiguration pere){
//        if (pere.pere == null) minmax = pere.minmax;
//        else
        minmax = !pere.minmax;

        this.pere = pere;
        owner = pere.owner;

//        if(pere.pere==null) tourCourrant = pere.tourCourrant;
//        else
        tourCourrant = pere.tourCourrant % 2 + 1;

        action = new IAAction(a);
        playerPickLastCard = 0;

        cartesIA = new ArrayList<>(pere.cartesIA);
        cartesHuman = new ArrayList<>(pere.cartesHuman);
        cartesReste = new ArrayList<>(pere.cartesReste);

        carteForNext = new ArrayList<>();
        carteJouer = new ArrayList<>();
        tousFils = new ArrayList<>();
        pere.tousFils.add(this);

        positionBlanc = pere.positionBlanc;
        positionNoir = pere.positionNoir;

        typeGagne = -1;
        couche = pere.couche + 1;
        ArrayList<Carte> cartesCourant;
        if (tourCourrant == owner) {
            cartesCourant = cartesIA;
            carteForNext = cartesHuman;
            carteEnemyNext = cartesIA;
        }
        else {
            cartesCourant = cartesHuman;
            carteForNext = cartesIA;
            carteEnemyNext = cartesHuman;
        }

        //Parry phase
        if(p!=null){
            parry = new Attack(p.getAt(),p.getAttValue(),p.getAttnb());
            //Déjà sûr que le jouer a assez cartes pour "parry" cet attaque.
            for (int i = 0; i < parry.getAttnb(); i++) {
                carteJouer.add(parry.getAttValue());
            }
        }
        //Move phase
        if (action.move.getC().getValue() != 0) {
            carteJouer.add(action.move.getC());
        }
        //Attack phase
        if (action.attack != null) {
            for (int i = 0; i < action.attack.getAttnb(); i++) {
                carteJouer.add(action.attack.getAttValue());
            }
        }

        //Verifier la carte a la main d'abord
        for (int i = 0; i < carteJouer.size(); i++) {
            cartesCourant.remove(carteJouer.get(i));
            carteJouer.get(i);
        }

        //System.out.println("Cartes reste arpès jouer :"+ cartesCourant.size());
        if (cartesReste.size() == 0) playerPickLastCard = tourCourrant % 2 + 1;
        else {
            for (int i = 0; i < carteJouer.size(); i++) {
                if (cartesReste.size() == 0) playerPickLastCard = tourCourrant;
                else cartesCourant.add(cartesReste.remove(0));
            }
            if (cartesReste.size() == 0) playerPickLastCard = tourCourrant;
        }

//        System.out.println("Cartes reste :"+ cartesReste.size());
//        System.out.println("Cartes complété :"+ cartesCourant.size());

        if (tourCourrant == 1 && action.move.getDirection() == 1) {
            positionBlanc += action.move.getC().getValue();
        } else if (tourCourrant == 1 && action.move.getDirection() == 2) {
            positionBlanc -= action.move.getC().getValue();
        } else if (tourCourrant == 2 && action.move.getDirection() == 1) {
            positionNoir -= action.move.getC().getValue();
        } else if (tourCourrant == 2 && action.move.getDirection() == 2) {
            positionNoir += action.move.getC().getValue();
        } //else System.out.println("No move this time ");

    }

    public IAConfiguration(int gagne, IAConfiguration pere){
        this.pere = pere;
        couche = pere.couche + 1;
        owner = pere.owner;
        playerPickLastCard = 0;
        //System.out.println("Couche : " + couche);
        tourCourrant = pere.tourCourrant%2+1;
        pere.tousFils.add(this);
        if(gagne == owner) {
            typeGagne = 1;
        } else if (gagne == -1){
            typeGagne = -2;
            return;
        }else {
            typeGagne = 0;
        }

        if (pere.action.attack != null) parry = pere.action.attack;
    }

    public int getDistance(){
        return this.positionNoir-this.positionBlanc;
    }

    public int disToDebut(int tour){
        if(tour == 1) return positionBlanc;
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

}
