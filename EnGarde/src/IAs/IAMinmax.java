package IAs;

import controlleur.ControlCenter;
import modele.*;

import java.util.ArrayList;

public class IAMinmax extends IA{


    int noirGagne;
    int blancGagne;
    int fils;
    ArrayList<IAAction> iaAction;
    public IAMinmax(ExecPlayground epg, Playground pg, ControlCenter cc) {
        super(epg, pg, cc);
        iaAction = new ArrayList<>();
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

    public void iaCanAttack(Configuration config, Attack lastAttack){
        iaAction = new ArrayList<>();
        iaCartes = new ArrayList<>();
        if(config.tourCourrant== config.owner){
            for (Carte c:config.iaCartes) {
                iaCartes.add(c);
            }
        }
        else {
            for (Carte c:config.reste) {
                iaCartes.add(c);
            }
        }
        if(lastAttack!=null){
            for (int i = 0; i < lastAttack.getAttnb(); i++) {
                iaCartes.remove(lastAttack.getAttValue());
            }
        }
        int dis = config.getDistance();
        //Direct attack possible
//        for (Carte c: iaCartes) {
//            System.out.println("000" + c);
//        }
        for (int i = 1; i < 6; i++) {
            if(!iaCartes.contains(Carte.generateCarteFromInt(i))) continue;
            if (dis == i) {
                for (int j = 0; j <  nbCarteI(i); j++) {
                    iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, Carte.generateCarteFromInt(i), j), 0));
                }
            }
            if(dis>i){
                iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), null, 0));
                if(config.reste.contains(Carte.generateCarteFromInt(dis+i))){
                    int k = nbCarteI(dis-i, config.reste);
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
                if(config.reste.contains(Carte.generateCarteFromInt(dis+i))){
                    int k = nbCarteI(dis-i, config.reste);
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

        }

    public Configuration allPossible(Configuration config){
        iaCartes = new ArrayList<>();
        fils = 0;
        if(config.typeGagne!=2){
            //System.out.println("Config : " + config.typeGagne);
            if(config.typeGagne==1){noirGagne++;}
            else blancGagne++;
            //if(config.pere.reste.size()>3){
                System.out.println("Entre : " + config.pere.reste.size() + " ");
                //System.out.println("Entre : " + config.pere.pere.tousFils.size() + " ");
                System.out.println("NoirGagne : " + noirGagne);
                System.out.println("BlancGagne : " + blancGagne);
            //}
            return config;
        }
        //System.out.println("Entre : " + config.reste.size() + " " + config.positionNoir + " " + config.positionBlanc);

        for (Carte c:config.reste) {
            iaCartes.add(c);
        }
        if(config.action.attack==null){
            iaCanAttack(config, null);
            for (IAAction act: iaAction) {
                fils++;
                allPossible(new Configuration(null, new IAAction(act.move, act.attack,0), config));
            }
            if(iaAction.size()==0){
                allPossible(new Configuration(config.tourCourrant, config, null));
            }
        }else{
            Attack lastAttack = config.action.attack;
            //parry
            if(nbCarteI(lastAttack.getAttValue().getValue())>=lastAttack.getAttnb()){
                iaCanAttack(config, lastAttack);
                for (IAAction act: iaAction) {
                    fils++;
                   allPossible(new Configuration(lastAttack, new IAAction(act.move, act.attack,0), config));
                }
                if(iaAction.size()==0){
                    allPossible(new Configuration(config.tourCourrant, config, lastAttack));
                }
            }
            //No parry
            if(lastAttack.getAt()==AttackType.INDIRECT){
                //如果能撤,把所有撤的情况列出来
                for (int i = 1; i < 6; i++) {
                    if(config.disToDebut()>i && nbCarteI(i)>0){
                        fils++;
                        allPossible(new Configuration(null, new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0), config));
                    }
                }
                if(config.disToDebut()<4){
                    fils++;
                    allPossible(new Configuration(config.tourCourrant, config, lastAttack));
                }
            }
            //如果不能撤，new config 输的
            if(lastAttack.getAt()==AttackType.DIRECT || fils==0){
                fils++;
                allPossible(new Configuration(config.tourCourrant, config, lastAttack));
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
        if(pg.getUsed().size()<15) {
            IA ia = new IAAleatoire(epg, pg, c);
            ia.iaStep();
        }else{
            Configuration config = new Configuration(pg.getPlayerCourant(), pg.getTourCourant(), pg.getUsed());
            allPossible(config);
            System.out.println("NoirGagne ******: " + noirGagne);
            System.out.println("BlancGagne ******: " + blancGagne);
        }

        //System.out.println(allPossible(config).tousFils.size());
    }

}
