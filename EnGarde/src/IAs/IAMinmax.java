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
    ArrayList<Carte> iaCartesMinmax;
    public IAMinmax(ExecPlayground epg, Playground pg, ControlCenter c) {
        super(epg, pg, c);

        iaCartesMinmax = new ArrayList<>();
        noirGagne = 0;
        blancGagne =0;
        fils = 0;
    }

    public int nbCarteI(int valeur, ArrayList<Carte> reste){
        int n = 0;
        for (int i = 0; i < reste.size(); i++)
            if(reste.get(i).getValue() == valeur) n++;
        return n;
    }

    public ArrayList<IAAction> iaCanAttack(IAConfiguration config, Attack lastAttack){

        ArrayList<IAAction> iaAction = new ArrayList<>();
        iaCartesMinmax = new ArrayList<>();
        iaCartesMinmax.addAll(config.carteCurrant);
        if(lastAttack!=null) for (int i = 0; i < lastAttack.getAttnb(); i++) iaCartesMinmax.remove(lastAttack.getAttValue());
        int dis = config.getDistance();
        //Direct attack possible
//        for (Carte c: iaCartesMinmax) {
//            System.out.println("000" + c);
//        }
        for (int i = 1; i < 6; i++) {
            if(!iaCartesMinmax.contains(Carte.generateCarteFromInt(i))) continue;
            if (dis == i) {
                for (int j = 1; j <=  nbCarteI(i, iaCartesMinmax); j++) {
                    iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, Carte.generateCarteFromInt(i), j), 0));
                }
            }
            if(dis>i){
                iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), null, 0));
                if(config.carteCurrant.contains(Carte.generateCarteFromInt(dis+i))){
                    int k = nbCarteI(dis-i, iaCartesMinmax);
                    if(dis-i == dis){
                        k--;
                    }
                    for (int j = 0; j < k; j++) {
                        iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), new Attack(AttackType.INDIRECT,Carte.generateCarteFromInt(dis-i), j),0));
                    }
                }
            }
            if(config.disToDebut()>i){
                //System.out.println("disToDebut : " + config.disToDebut());
                iaAction.add(new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0));
                if(config.carteCurrant.contains(Carte.generateCarteFromInt(dis+i))){
                    int k = nbCarteI(dis-i, iaCartesMinmax);
                    if(dis+i != dis){
                        k--;
                    }
                    for (int j = 0; j < k; j++) {
                        iaAction.add(new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), new Attack(AttackType.INDIRECT,Carte.generateCarteFromInt(dis-i), j),0));
                    }
                }
            }
        }
//        for (IAAction iaaction : iaAction) {
//            System.out.println("IAACTIO        N : " + iaaction.move.getC().getValue());
//            System.out.println("IAACTION       D : " + iaaction.move.getDirection());
//        }
        //Indirect attack et move possible
        return iaAction;
        }

    public void allPossible(IAConfiguration config){
        iaCartesMinmax = new ArrayList<>();

        if(config.typeGagne!=2){
            //System.out.println("Config : " + config.typeGagne);
            if(config.typeGagne==1 && pg.getTourCourant()==1){
                blancGagne++;
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);}
            else if(config.typeGagne==0 && pg.getTourCourant()==2){
                blancGagne++;
                //config.incrementPerdu();
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);
            }else {
                noirGagne++;
                //config.incrementGagne();
                System.out.println(" tourGagne 2:" + config.pere.tourCourrant);
            }
            //if(config.pere.reste.size()>3){
                System.out.println("Entre : " + config.pere.reste.size() + " ");

                //System.out.println("Entre : " + config.pere.pere.tousFils.size() + " ");
                System.out.println("NoirGagne : " + noirGagne);
                System.out.println("BlancGagne : " + blancGagne);
            //}
            config.setMinmax(config);
            return ;
        }
        //System.out.println("Entre : " + config.reste.size() + " " + config.positionNoir + " " + config.positionBlanc);
        iaCartesMinmax = config.carteCurrant;
        if(config.action.attack==null){
            ArrayList<IAAction> iaAction = new ArrayList<>();
            iaAction.addAll(iaCanAttack(config, null));
            if(iaAction.size()!=0) {
                for (int i = 0; i < iaAction.size(); i++) {
                    fils++;
                    //System.out.println("Entre else1: ");
                    allPossible(new IAConfiguration(null, new IAAction(iaAction.get(i).move, iaAction.get(i).attack,0), config));
                }
            }else {
                if (config.carteCurrant.size() != 0) {
                    System.out.println("Entre else2: ");
                    allPossible(new IAConfiguration(config.tourCourrant, config, null));
                }else{
                    if(config.disToDebut()>config.pere.disToDebut()) {
                        System.out.println("Entre else***: " + config.disToDebut()+ "***"+config.pere.disToDebut());
                        allPossible(new IAConfiguration(config.tourCourrant, config, null));
                    }else{
                        System.out.println("Entre else###: "+ config.disToDebut()+ "###" + config.pere.disToDebut());
                        allPossible(new IAConfiguration(config.tourCourrant%2+1, config, null));
                    }
                }
            }
        }else{
            System.out.println("Entre else3: ");
            Attack lastAttack = config.action.attack;
            //parry
            if(nbCarteI(lastAttack.getAttValue().getValue(), config.carteCurrant)>=lastAttack.getAttnb()){
                ArrayList<IAAction> iaAction = new ArrayList<>();
                iaAction.addAll(iaCanAttack(config, lastAttack));
                if(iaAction.size()!=0) {
                    for (IAAction act : iaAction) {
                        fils++;
                        System.out.println("Entre else4: ");
                        allPossible(new IAConfiguration(lastAttack, new IAAction(act.move, act.attack, 0), config));
                    }
                }else{
                    if(config.carteCurrant.size()==0){
                        if(config.pere.carteCurrant.size()==0){
                            if(config.disToDebut()>config.pere.disToDebut()){
                                System.out.println("Entre else5: ");
                                allPossible(new IAConfiguration(config.tourCourrant%2+1, config, lastAttack));
                            }else{
                                System.out.println("Entre else6: " );
                                allPossible(new IAConfiguration(config.tourCourrant, config, lastAttack));
                            }
                        }else {
                            if(config.disToDebut()>11){
                                System.out.println("Entre else7: " );
                                allPossible(new IAConfiguration(config.tourCourrant, config, lastAttack));
                            }else{
                                System.out.println("Entre else7///: " );
                                allPossible(new IAConfiguration(config.tourCourrant%2+1, config, lastAttack));

                            }
                        }
                    }else{
                        allPossible(new IAConfiguration(config.tourCourrant, config, lastAttack));
                    }
                }
            }
            //No parry
            if(lastAttack.getAt()==AttackType.INDIRECT){
                //如果能撤,把所有撤的情况列出来
                for (int i = 1; i < 6; i++) {
                    if(config.disToDebut()>i && nbCarteI(i, config.carteCurrant)>0){
                        fils++;
                        System.out.println("Entre else8: " );
                        allPossible(new IAConfiguration(null, new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0), config));
                    }
                }
                if(config.disToDebut()<5){
                    int minNb = Math.min(5, iaCartesMinmax.size());
                    for (int i = 0; i < config.carteCurrant.size(); i++) {
                        if(config.disToDebut()< config.carteCurrant.get(i).getValue()){
                            minNb--;
                        }
                    }
                    if(minNb<=0){
                        System.out.println("Entre else9: " );
                        allPossible(new IAConfiguration(config.tourCourrant, config, lastAttack));
                    }
                }
            }
            //如果不能撤，new config 输的
            if(lastAttack.getAt()==AttackType.DIRECT){
                if(Math.max(5, config.carteCurrant.size())>5){
                    if(!(nbCarteI(lastAttack.getAttValue().getValue(), config.carteCurrant) - (config.carteCurrant.size()-5) >= lastAttack.getAttnb())){
                        System.out.println("Entre else*: " );
                        allPossible(new IAConfiguration(config.tourCourrant, config, lastAttack));
                    }
                }else{
                    if(nbCarteI(lastAttack.getAttValue().getValue(), config.carteCurrant)< lastAttack.getAttnb()){
                        allPossible(new IAConfiguration(config.tourCourrant, config, lastAttack));
                    }
                }
            }

        }
        return;
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
//                choisir.set(config.action.move.getIndex(), true);
//                jouerCarte(config.action.move.getDirection(),choisir);
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
        if(pg.getUsed().size()<15) {
            IA ia = new IAAleatoire(epg, pg, c);
            ia.iaStep();
        }else{
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
                    System.out.println("Increment branche gagner : " + "Branch i: " + i++ + "  ng :" + conf.branchGagne);
                    System.out.println("Increment branche gagner : " + "Branch i: " + i++ + "  np :" + conf.branchPerdu);
                    conf.gagnerProba = conf.branchGagne/(conf.branchGagne+conf.branchPerdu);
                    System.out.println("Increment branche gagner : " + "Branch i: " + i++ + "  nb :" + conf.gagnerProba);
                }catch (Exception e) {
                    System.out.println("Error");
                }
                if(max<conf.gagnerProba){
                    configAct = conf;
                    max = conf.gagnerProba;
                }
            }
            this.iaParryPhase();
            this.pickMove();

        }

        //System.out.println(allPossible(config).tousFils.size());
    }

}
