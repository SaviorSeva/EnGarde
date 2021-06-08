package IAs;

import controlleur.ControlCenter;
import global.Configuration;
import modele.*;

import java.util.ArrayList;

public class IAMinmax extends IA{


    int noirGagne;
    int blancGagne;
    int fils;
    IAConfiguration config;
    IAConfiguration configAct;
    //ArrayList<Carte> iaCartesMinmax;
    boolean minMaxActive;
    int depth, alpha;

    public IAMinmax(ExecPlayground epg, Playground pg, ControlCenter c) {
        super(epg, pg, c);
        minMaxActive = false;
        //iaCartesMinmax = new ArrayList<>();
        noirGagne = 0;
        blancGagne = 0;
        fils = 0;
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
        if(config.typeGagne!=2){
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

        /** Phase finale **/
        if (config.pere != null && config.pere.playerPickLastCard != 0) {
            if (config.action.attack == null) {
                if (config.disToDebut(tourCourant) < config.disToDebut(config.tourCourrant) ) {
                    allPossible(new IAConfiguration(config.tourCourrant, config));
                }else {
                    /** Draw include **/
                    allPossible(new IAConfiguration(tourCourant, config));
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
        if(config.action.attack == null){
            ArrayList<IAAction> iaAction = new ArrayList<>(iaAttackOrMove(config, null));
            if(iaAction.size()!=0) {
                for (int i = 0; i < iaAction.size(); i++) {
                    System.out.println("Entre else 1: ");
                    if(config.cut) return;
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
                    for (IAAction act : iaAction) {
                        System.out.println("Entre else 4: ");
                        if(config.cut) return;
                        allPossible(new IAConfiguration(lastAttack, new IAAction(act.move, act.attack, 0), config));
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
                    if(config.cut) return;
                    if(config.disToDebut(tourCourant) >= i && nbCarteI(i, config.carteForNext) > 0){
                        canReatreat = true;
                        /** Retreat can use only 1 card, so don't care how many we have, have it or not **/
                        System.out.println("Entre else 5: " );
                        allPossible(new IAConfiguration(null, new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0), config));
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



    @Override
    public boolean pickMove() {
        iaCartes = pg.getCurrentPlayerCards();
        if(configAct.action!=null){
            if(configAct.action.move.getC().getValue()!=0){
                for (int i = 0; i < pg.getCurrentPlayerCards().size(); i++) {
                    if(pg.getCurrentPlayerCards().get(i)==configAct.action.move.getC()){
                        choisir.set(i, true);
                        jouerCarte(configAct.action.move.getDirection(),choisir);
                    }
                }
            }
            if(configAct.action.attack!=null && configAct.action.attack.getAttnb()!=0){
                choisirParryOrAttackCartes(configAct.action.attack.getAttnb(), configAct.action.attack.getAttValue().getValue());
                jouerCarte(1, choisir);
            }
        }
        return true;
    }

    @Override
    public void iaParryPhase() {
        iaCartes = pg.getCurrentPlayerCards();
        if(configAct.parry!=null){
            choisirParryOrAttackCartes(configAct.parry.getAttnb(), configAct.parry.getAttValue().getValue());
            jouerCarte(3, choisir);
        }
    }

    @Override
    public void iaStep() {
        if((pg.getDistance()>10) && (!minMaxActive)) {
            IA ia = new IAAleatoire(epg, pg, c);
            ia.iaStep();
        }else{
            minMaxActive = true;
            config = new IAConfiguration(pg);
            allPossible(config);
            System.out.println("NoirGagne ******: " + noirGagne);
            System.out.println("BlancGagne ******: " + blancGagne);
            System.out.println("Branche gagner :: " + config.branchGagne);
            int i = 0;
            double max = 0;

            for (IAConfiguration conf: config.tousFils) {
                try {
                    if(conf.parry!=null){
                        System.out.println("Parry type: " + conf.parry.getAt());
                        System.out.println("Parry valeur: " + conf.parry.getAttValue().getValue());
                        System.out.println("Parry nb: " + conf.parry.getAttnb());
                    }
                    System.out.println("Action direction: " + conf.action.move.getDirection());
                    System.out.println("Action carte: " + conf.action.move.getC());
                    if(conf.action.attack!=null){
                        System.out.println("Action Att: " + conf.action.attack.getAt());
                        System.out.println("Action carte: " + conf.action.attack.getAttValue().getValue());
                    }
                    System.out.println("Increment branche gagner : " + "Branch i: " + i + "  ng :" + conf.branchGagne);
                    System.out.println("Increment branche gagner : " + "Branch i: " + i + "  np :" + conf.branchPerdu);
                    conf.gagnerProba = conf.branchGagne/(conf.branchGagne+conf.branchPerdu);
                    System.out.println("Increment branche gagner : " + "Branch i: " + i + "  nb :" + conf.gagnerProba);
                    i++;
                }catch (Exception e) {
                    System.out.println("Error");
                }
            }
            System.out.println("Depth : " + depth);
            System.out.println("Alpha : " + setMinmax(config, depth));
//            this.iaParryPhase();
//            this.pickMove();

        }

        //System.out.println(allPossible(config).tousFils.size());
    }

    public int setMinmax(IAConfiguration cg, int d) {
        IAConfiguration iaC = null;
        System.out.println("print typeGagne set: " + d);
        if (cg.tousFils.size()==0 || d == 0){
            System.out.println("print typeGagne : " + cg.typeGagne);
            return cg.typeGagne;
        }
        if(cg.minmax){
            alpha= Integer.MIN_VALUE;
            for (IAConfiguration c: cg.tousFils) {
                int dp = d-1;
                System.out.println("print dp : " + cg.typeGagne);
                alpha = Math.max(alpha, setMinmax(c, dp));
            }
        }else{
            alpha= Integer.MAX_VALUE;
            for (IAConfiguration c: cg.pere.tousFils) {
                int dp = d-1;
                alpha = Math.min(alpha, setMinmax(c, dp));
            }
        }
        return alpha;
    }
}
