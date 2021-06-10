package IAs;

import controlleur.ControlCenter;
import modele.*;

import java.util.ArrayList;

public class IAMinmax extends IA{


    int noirGagne;
    int blancGagne;
    IAConfiguration config;
    IAConfiguration configAct;
    boolean minMaxActive;
    int depth, alpha;
    int leger;
    boolean creeArbre;
    boolean cantMoveAgain;

    public IAMinmax(ExecPlayground epg, Playground pg, ControlCenter c) {
        super(epg, pg, c);
        minMaxActive = false;
        noirGagne = 0;
        blancGagne = 0;
        depth = 0;
        cantMoveAgain = false;
    }

    public int nbCarteI(int valeur, ArrayList<Carte> reste){
        int n = 0;
        for (int i = 0; i < reste.size(); i++)
            if(reste.get(i).getValue() == valeur) n++;
        return n;
    }

    public ArrayList<IAAction> iaAttackOrMove(IAConfiguration config, Attack lastAttack){

        ArrayList<IAAction> iaAction = new ArrayList<>();
        ArrayList<Carte> iaCartesMinmax = new ArrayList<>(config.carteForNext);
        if(lastAttack!=null) for (int i = 0; i < lastAttack.getAttnb(); i++) iaCartesMinmax.remove(lastAttack.getAttValue());
        int dis = config.getDistance();

        for (int i = 1; i < 6; i++) {
            if(!iaCartesMinmax.contains(Carte.generateCarteFromInt(i))) continue;
            if (dis == i) {
                for (int j = 1; j <=  nbCarteI(i, iaCartesMinmax); j++) {
                    iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, Carte.generateCarteFromInt(i), j), 0));
                }
            }
            if(dis>i){
                iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), null, 0));
                if(config.carteForNext.contains(Carte.generateCarteFromInt(dis-i))){
                    int k = nbCarteI(dis-i, iaCartesMinmax);
                    if(dis-i == i){
                        k--;
                    }
                    for (int j = 1; j <= k; j++) {
                        iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), new Attack(AttackType.INDIRECT,Carte.generateCarteFromInt(dis-i), j),0));
                    }
                }
            }
            if(config.disToDebut(config.tourNext())>i){
                iaAction.add(new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0));
                if(config.carteForNext.contains(Carte.generateCarteFromInt(dis+i))){
                    int k = nbCarteI(dis+i, iaCartesMinmax);

                    for (int j = 1; j <= k; j++) {
                        iaAction.add(new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), new Attack(AttackType.INDIRECT,Carte.generateCarteFromInt(dis+i), j),0));
                    }
                }
            }
        }
        return iaAction;
    }

    public void allPossible(IAConfiguration config){
        int tourCourant = config.tourCourrant % 2 + 1;
        /** config.carteForNext sont les cartes pour ce noeud **/
        if(config.couche>=leger && config.typeGagne == -1) {
            allPossible(new IAConfiguration(-1, config));
            return;
        }
        if(config.typeGagne!=-1 ){
            //System.out.println("Config : " + config.typeGagne);
            if(config.couche>depth) depth++;
            if(config.typeGagne==1 && pg.getTourCourant()==1){
                blancGagne++;
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);}
            else if(config.typeGagne==0 && pg.getTourCourant()==2){
                blancGagne++;
                config.incrementPerdu();
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);
            }else if(config.typeGagne != -2){
                noirGagne++;
                config.incrementGagne();
                System.out.println(" tourGagne 2:" + config.pere.tourCourrant);
            }

            System.out.println("NoirGagne : " + noirGagne);
            System.out.println("BlancGagne : " + blancGagne);
            return ;
        }
        //if(leger!= 0 && config.couche == leger) return;

        /** Phase finale **/
        if (config.pere != null && config.playerPickLastCard == tourCourant) {
            if (config.action.attack == null) {

                int resultat = nbCarteI(config.getDistance(), config.carteForNext) - nbCarteI(config.getDistance(), config.carteEnemyNext);
                if (resultat > 0) {
                    allPossible(new IAConfiguration(tourCourant, config));
                }else if (resultat == 0) {
                    if (config.disToDebut(tourCourant) < config.disToDebut(config.tourCourrant) ) {
                        allPossible(new IAConfiguration(config.tourCourrant, config));
                    }else {
                        /** Draw include **/
                        allPossible(new IAConfiguration(tourCourant, config));
                    }
                }else{
                    allPossible(new IAConfiguration(config.tourCourrant, config));
                }
            }else if (config.action.attack.getAt() == AttackType.DIRECT || config.action.attack.getAt() == AttackType.INDIRECT) {
                int attValue = config.action.attack.getAttValue().getValue();
                int attNb = config.action.attack.getAttnb();
                int resultat = nbCarteI(attValue, config.carteForNext) - attNb - nbCarteI(attValue, config.carteEnemyNext);
                if (resultat > 0) {
                    allPossible(new IAConfiguration(tourCourant, config));
                }else if (resultat == 0) {
                    if (config.disToDebut(tourCourant) < config.disToDebut(config.tourCourrant) ) {
                        allPossible(new IAConfiguration(config.tourCourrant, config));
                    }else {
                        /** Draw include **/
                        allPossible(new IAConfiguration(tourCourant, config));
                    }
                }else {
                    if (config.action.attack.getAt() == AttackType.DIRECT) {
                        allPossible(new IAConfiguration(config.tourCourrant, config));
                    }else if (config.action.attack.getAt() == AttackType.INDIRECT){
                        boolean vue = false;
                        for (int i = 1; i < 6; i++) {
                            /** disToDebut dépend le tour **/
                            if(config.disToDebut(tourCourant) >= i && nbCarteI(i, config.carteForNext) > 0){
                                /** can retreat **/
                                vue = true;
                                if ((config.disToDebut(tourCourant) - i) < config.disToDebut(config.tourCourrant) ) {
                                    allPossible(new IAConfiguration(config.tourCourrant, config));
                                }else {
                                    /** Draw include **/
                                    allPossible(new IAConfiguration(tourCourant, config));
                                }
                                break;
                            }
                        }
                        if (!vue) allPossible(new IAConfiguration(config.tourCourrant, config));
                    }
                    else System.out.println("Error! 147");
                }
            }
            return;
        }

        /** AllPossible judge que ce tour(noeud) est perdu ou pas, donc config.tourCourant est gagné **/
        if(config.action.attack == null || config.action.attack.getAt() == AttackType.NONE){
            ArrayList<IAAction> iaAction = new ArrayList<>(iaAttackOrMove(config, null));
            if(iaAction.size()!=0) {
                for (int i = 0; i < iaAction.size(); i++) {
                    System.out.println("Entre else 1: ");
                    allPossible(new IAConfiguration(null, new IAAction(iaAction.get(i).move, iaAction.get(i).attack,0), config));
                }
            }else {
                System.out.println("Entre else 2: ");
                allPossible(new IAConfiguration(config.tourCourrant, config));
            }
        }else{
            boolean parry = false;
            boolean canReatreat = false;
            boolean cantReatreat = false;
            System.out.println("Entre else 3: ");
            Attack lastAttack = config.action.attack;

            /** parry **/
            if(nbCarteI(lastAttack.getAttValue().getValue(), config.carteForNext) >= lastAttack.getAttnb()){
                parry = true;

                ArrayList<IAAction> iaAction = new ArrayList<>(iaAttackOrMove(config, lastAttack));
                if(iaAction.size()!=0) {
                    for (int i = 0; i < iaAction.size(); i++) {
                        System.out.println("Entre else 4: ");
                        //if(config.cut) return;
                        allPossible(new IAConfiguration(lastAttack, new IAAction(iaAction.get(i).move, iaAction.get(i).attack, 0), config));
                    }
                }else{
                    /** Après parry (iaAttackOrMove a déjà judgé) mais ne peut pas faire une action **/
                    allPossible(new IAConfiguration(config.tourCourrant, config));
                }
            }

            /** No parry **/
            if(lastAttack.getAt()==AttackType.INDIRECT && !parry){
                int n = 0;
                //如果能撤,把所有撤的情况列出来
                for (int i = 1; i < 6; i++) {
                    /** disToDebut dépend le tour **/
                    //if(config.cut) return;
                    if(config.disToDebut(tourCourant) >= i && nbCarteI(i, config.carteForNext) > 0){
                        canReatreat = true;
                        /** Retreat can use only 1 card, so don't care how many we have, have it or not **/
                        System.out.println("Entre else 5: " );
                        allPossible(new IAConfiguration(null, new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), new Attack(AttackType.NONE, null, 0), 0), config));
                        n++;
                    }
                }
                if (n == 0 && !canReatreat) {
                    cantReatreat = true;
                    System.out.println("Entre else 6: " );
                    allPossible(new IAConfiguration(config.tourCourrant, config));
                }
            }

            /** Si suffit un attaquet directe et ne "parry" pas, c'est perdu **/
            if(lastAttack.getAt()==AttackType.DIRECT && !parry && !cantReatreat){
                System.out.println("Entre else 7: " );
                allPossible(new IAConfiguration(config.tourCourrant, config));
            }
        }
    }



    public boolean pickMove() {
        iaCartes = pg.getCurrentPlayerCards();
        if(configAct == null) return true;
        if(configAct.action!=null){
            if(configAct.action.move.getC().getValue()!=0){
                for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++) {
                    System.out.println(pg.getCurrentPlayerCards().get(i).getValue());
                    System.out.println(configAct.action.move.getC().getValue());
                    if(pg.getCurrentPlayerCards().get(i).getValue()==configAct.action.move.getC().getValue()){
                        choisir.set(i, true);
                        jouerCarte(configAct.action.move.getDirection(),choisir);
                    }
                }
            }
            if(configAct.action.attack!=null && configAct.action.attack.getAttnb()!=0){
                choisirParryOrAttackCartes(configAct.action.attack.getAttnb(), configAct.action.attack.getAttValue().getValue());
                jouerCarte(1, choisir);
            }
            if (configAct.action.attack==null && epg.cartesContains(epg.getDistance())){
                resetChoisir();
                jouerCarte(3, choisir);
            }
        }
        return true;
    }

    @Override
    public void iaParryPhase() {
        System.out.println("MinMax Get parry");
        cantMoveAgain = false;
        iaCartes = pg.getCurrentPlayerCards();
        if (pg.getLastAttack().getAt() == AttackType.NONE) return;
        if(configAct == null){
            choisir.set(0, true);
            jouerCarte(2, choisir);
        }else if (configAct.parry!=null && pg.getLastAttack().getAttValue()!= null &&((nbCarteI(pg.getLastAttack().getAttValue().getValue(), iaCartes)) >= pg.getLastAttack().getAttnb())){
            choisirParryOrAttackCartes(configAct.parry.getAttnb(), configAct.parry.getAttValue().getValue());
            jouerCarte(3, choisir);
        }else if (configAct.action != null && configAct.action.attack == null && configAct.action.move.getDirection() == 2) { /** if there is a Retreat calculed **/
            for (int i = 0; i < iaCartes.size(); i++) {
                if (iaCartes.get(i).getValue() == configAct.action.move.getC().getValue()) {
                    choisir.set(i, true);
                    break;
                }
            }
            jouerCarte(2, choisir);
            cantMoveAgain = true;
        }
        else if(pg.getLastAttack().getAt()!=AttackType.NONE){
            if(pg.getLastAttack().getAttnb() <= nbCarteI(pg.getLastAttack().getAttValue().getValue(), iaCartes)){
                choisirParryOrAttackCartes(pg.getLastAttack().getAttnb(), pg.getLastAttack().getAttValue().getValue());
                jouerCarte(3, choisir);
            }else if(pg.getLastAttack().getAt()==AttackType.INDIRECT){
                int min = Integer.MAX_VALUE;
                int index = 0;
                /** Si'l faut retreat et config n'a pas aucune action,
                 *  choisir une carte de une plus petit valeur pour retreat **/
                for (int i = 0; i < iaCartes.size(); i++) {
                    if (min >= iaCartes.get(i).getValue() ) {
                        min = iaCartes.get(i).getValue();
                        index = i;
                    }
                }
                System.out.println("Retreat ////////////////////////////////////");
                choisir.set(index, true);
                jouerCarte(2, choisir);
                cantMoveAgain = true;
            }
        }
    }


    public void iaStepLeger(){
        leger = 5;
        config = new IAConfiguration(pg);
        allPossible(config);
        System.out.println("Alpha : " + setMinmax(config, leger));
        configAct = config.vraiFils;
        this.iaParryPhase();
        this.pickMove();
    }

    @Override
    public void iaStep() {//IAProba jouer au debut, et puis IAMinmax
            leger = 5;
            config = new IAConfiguration(pg);
            allPossible(config);
            minMaxActive = true;
            creeArbre = true;
            int al = setMinmax(config, leger);
            System.out.println("Depth : " + depth);
            System.out.println("Alpha : " + al);
            if(config.typeGagne != 1 || config.vraiFils == null){
                IAProba ia = new IAProba(epg, pg, c);
                ia.iaStep();
            }else{
                System.out.println("Minmax act!!");
                configAct = config.vraiFils;
                if (epg.isIaRound()|| epg.isIaMinmax()) {
                    this.iaParryPhase();
                    if (epg.getLastCardPlayer == pg.getTourCourant()) System.out.println("IA MinMax picked the last card, it can't move more");
                    else if (!cantMoveAgain) this.pickMove();

                }
            }

        //System.out.println(allPossible(config).tousFils.size());
    }

    public int setMinmax(IAConfiguration cg, int d) {
        if (cg.tousFils == null || d == 0){
            return cg.typeGagne;
        }
        if(cg.minmax){
            alpha= Integer.MIN_VALUE;
            for (int i = 0; i < cg.tousFils.size(); i++) {
                int k;
                if(alpha < (k = Math.max(alpha, setMinmax(cg.tousFils.get(i), d-1))) ){
                    alpha = k;
                    cg.vraiFils = cg.tousFils.get(i);
                    cg.typeGagne = cg.vraiFils.typeGagne;
                }
            }
            if(alpha < -1) alpha = -1;

        }else{
            alpha= Integer.MAX_VALUE;
            for (int i = 0; i < cg.tousFils.size(); i++) {
                int k;
                if(alpha > (k = Math.min(alpha, setMinmax(cg.tousFils.get(i), d-1)))){
                    alpha = k;
                    cg.vraiFils = cg.tousFils.get(i);
                    cg.typeGagne = cg.vraiFils.typeGagne;
                }
            }
            if(alpha >10) alpha = -1;
        }
        return alpha;
    }
    // Annuler le dernier etape d'attaquer
    public CarteEtDirection lireAttackEtMove(String s) {
        CarteEtDirection ced = null;
        if(s.charAt(2) != '0') {
            int moveVal;
            switch(s.charAt(1)) {
                case 'F':
                    // Undo the last move by the player
                    moveVal = Character.getNumericValue(s.charAt(2));
                    ced = new CarteEtDirection(1, Carte.generateCarteFromInt(moveVal), -1);
                    break;
                case 'B':
                    // Undo the last move by the player
                    moveVal = Character.getNumericValue(s.charAt(2));
                    ced = new CarteEtDirection(2, Carte.generateCarteFromInt(moveVal), -1);
                    break;
                default:
                    // Shouldn't be executed
                    System.err.println("Error resetMove() in ControlCenter.java at Line 345");
                    break;
            }
        }else{
            ced = new CarteEtDirection(0, Carte.generateCarteFromInt(0), -1);
        }
        return ced;
    }

}
