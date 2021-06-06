package IAs;

import controlleur.ControlCenter;
import modele.*;

import java.util.ArrayList;

public class IAMinmax extends IA{


    int noirGagne;
    int blancGagne;
    int fils;
    public IAMinmax(ExecPlayground epg, Playground pg, ControlCenter c) {
        super(epg, pg, c);

        iaCartes = new ArrayList<>();
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
        iaCartes = config.carteCurrant;

        int dis = config.getDistance();
        //Direct attack possible
//        for (Carte c: iaCartes) {
//            System.out.println("000" + c);
//        }
        for (int i = 1; i < 6; i++) {
            if(!iaCartes.contains(Carte.generateCarteFromInt(i))) continue;
            if (dis == i) {
                for (int j = 0; j <  nbCarteI(i, iaCartes); j++) {
                    iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, Carte.generateCarteFromInt(i), j), 0));
                }
            }
            if(dis>i){
                iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), null, 0));
                if(config.carteCurrant.contains(Carte.generateCarteFromInt(dis+i))){
                    int k = nbCarteI(dis-i, iaCartes);
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
                    int k = nbCarteI(dis-i, iaCartes);
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

    public IAConfiguration allPossible(IAConfiguration config){
        iaCartes = new ArrayList<>();
        fils = 0;
        if(config.typeGagne!=2){
            //System.out.println("Config : " + config.typeGagne);
            if(config.typeGagne==1 && pg.getTourCourant()==1){
                blancGagne++;
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);}
            else if(config.typeGagne==0 && pg.getTourCourant()==2){
                blancGagne++;
                System.out.println(" tourGagne 1:" + config.pere.tourCourrant);
            }else {
                noirGagne++;
                System.out.println(" tourGagne 2:" + config.pere.tourCourrant);
            }
            //if(config.pere.reste.size()>3){
                System.out.println("Entre : " + config.pere.reste.size() + " ");

                //System.out.println("Entre : " + config.pere.pere.tousFils.size() + " ");
                System.out.println("NoirGagne : " + noirGagne);
                System.out.println("BlancGagne : " + blancGagne);
            //}
            return config;
        }
        //System.out.println("Entre : " + config.reste.size() + " " + config.positionNoir + " " + config.positionBlanc);
        iaCartes = config.carteCurrant;
        if(config.action.attack==null){
            ArrayList<IAAction> iaAction = new ArrayList<>();
            iaAction.addAll(iaCanAttack(config, null));
            //System.out.println("00000000000: " + iaAction.size());
            if(iaAction.size()!=0) {
                for (int i = 0; i < iaAction.size(); i++) {
                    fils++;
                    //System.out.println("Entre else1: ");
                    System.out.println("Entre else---: " + iaAction.size());
                    allPossible(new IAConfiguration(null, new IAAction(iaAction.get(i).move, iaAction.get(i).attack,0), config));
                }
            }else {
                if (config.carteCurrant.size() != 0) {
                    System.out.println("Entre else2: ");
                    allPossible(new IAConfiguration(config.tourCourrant%2+1, config, null));
                }else{
                    System.out.println("Entre else999: ");
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
                iaAction.addAll(iaCanAttack(config, null));
                for (IAAction act: iaAction) {
                    fils++;
                    System.out.println("Entre else4: " );
                    allPossible(new IAConfiguration(lastAttack, new IAAction(act.move, act.attack,0), config));
                }
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
                    int minNb = Math.min(5, iaCartes.size());
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
                }
            }

        }
        System.out.println("TousFils : " + fils + " ");
        return null;
    }



    @Override
    public boolean pickMove() {
        return false;
    }

    @Override
    public void iaParryPhase() {

    }

    @Override
    public void iaStep() {
        if(pg.getUsed().size()<12) {
            IA ia = new IAAleatoire(epg, pg, c);
            ia.iaStep();
        }else{
            IAConfiguration config = new IAConfiguration(pg);
            allPossible(config);
            System.out.println("NoirGagne ******: " + noirGagne);
            System.out.println("BlancGagne ******: " + blancGagne);
        }

        //System.out.println(allPossible(config).tousFils.size());
    }

}
