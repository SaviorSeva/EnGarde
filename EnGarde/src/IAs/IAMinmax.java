package IAs;

import controlleur.ControlCenter;
import global.Configuration;
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
    public IAMinmax(ExecPlayground epg, Playground pg, ControlCenter c) {
        super(epg, pg, c);
        minMaxActive = false;
        noirGagne = 0;
        blancGagne = 0;
        depth = 0;
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
        if(config.typeGagne!=2 ){
            //System.out.println("Config : " + config.typeGagne);
            if(config.couche>depth) depth++;
            if(config.typeGagne==1 && pg.getTourCourant()==1){
                blancGagne++;
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);}
            else if(config.typeGagne==0 && pg.getTourCourant()==2){
                blancGagne++;
                config.incrementPerdu();
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);
            }else {
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
        iaCartes = pg.getCurrentPlayerCards();
        if(configAct.parry!=null){
            System.out.println("Parry 1: " + configAct.parry.getAt());
            System.out.println("Parry 2: " + configAct.parry.getAttValue().getValue());
            System.out.println("Parry 3: " + configAct.parry.getAttnb());
            choisirParryOrAttackCartes(configAct.parry.getAttnb(), configAct.parry.getAttValue().getValue());
            jouerCarte(3, choisir);
        }else if(pg.getLastAttack().getAt()!=AttackType.NONE){
            if(pg.getLastAttack().getAttnb() <= nbCarteI(pg.getLastAttack().getAttValue().getValue(), iaCartes)){
                choisirParryOrAttackCartes(pg.getLastAttack().getAttnb(), pg.getLastAttack().getAttValue().getValue());
                jouerCarte(3, choisir);
            }else if(pg.getLastAttack().getAt()==AttackType.INDIRECT){
                int k = 0;
                for (int i = 0; i < iaCartes.size(); i++) {
                    if(iaCartes.get(i).getValue()<5){
                        k = i;
                    }
                }
                choisir.set(k, true);
                jouerCarte(2, choisir);
            }
        }else if (configAct.action.attack == null && configAct.action.move.getDirection() == 2) { /** Retreat **/
            for (int i = 0; i < iaCartes.size(); i++) {
                if (iaCartes.get(i).getValue() == configAct.action.move.getC().getValue()) {
                    choisir.set(i, true);
                    break;
                }
            }
            jouerCarte(2, choisir);
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
        if(pg.getUsed().size() < 10 && (!minMaxActive)) {
            IA ia = new IAProba(epg, pg, c);
            ia.iaStep();
        }else{
            if(!creeArbre){
                config = new IAConfiguration(pg);
                allPossible(config);
                minMaxActive = true;
                creeArbre = true;
                System.out.println("Depth : " + depth);
                System.out.println("Alpha : " + setMinmax(config, depth));
                configAct = config.vraiFils;
            }else{
                Action s = epg.hist.listAction.get(epg.hist.listAction.size() - 1);
                System.out.println("Hist : " + s);
                String lastActions[] = s.getActionString().split(",");
                for (int i = 0; i < configAct.tousFils.size(); i++) {
                    System.out.println("1:" + configAct.tousFils.get(i).action.move.getC().getValue());
                    System.out.println("2:" + lireAttackEtMove(lastActions[1]).getC().getValue());
                    System.out.println("  ");
                    if(configAct.tousFils.get(i).action.move.getC().getValue()==lireAttackEtMove(lastActions[1]).getC().getValue()){
                        if(configAct.tousFils.get(i).action.move.getDirection() == lireAttackEtMove(lastActions[1]).getDirection()){
                            System.out.println("1: ***********" );
                            try{
                                if(configAct.tousFils.get(i).action.attack.getAt() ==pg.getLastAttack().getAt()){
                                    if(configAct.tousFils.get(i).action.attack.getAttValue().getValue() == pg.getLastAttack().getAttValue().getValue()){
                                        if (configAct.tousFils.get(i).action.attack.getAttnb() == pg.getLastAttack().getAttnb()){
                                            setMinmax(configAct.tousFils.get(i), depth);
                                            configAct = configAct.tousFils.get(i).vraiFils;
                                            break;
                                        }
                                    }
                                }
                            }catch (Exception e){
                                System.out.println(" 11");
                            }
                        }
                    }
                }
                //configAct = configAct.vraiFils;
            }
            this.iaParryPhase();
            this.pickMove();
            depth--;
        }

        //System.out.println(allPossible(config).tousFils.size());
    }

    public void minMaxActive(){
        config = new IAConfiguration(pg);
        allPossible(config);
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
                }
            }
        }else{
            alpha= Integer.MAX_VALUE;
            for (int i = 0; i < cg.tousFils.size(); i++) {
                int k;
                if(alpha > (k = Math.min(alpha, setMinmax(cg.tousFils.get(i), d-1)))){
                    alpha = k;
                    cg.vraiFils = cg.tousFils.get(i);
                }
            }
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


    public void initialise(){

        noirGagne = 0;
        blancGagne = 0;
        depth = 0;
    }
}
