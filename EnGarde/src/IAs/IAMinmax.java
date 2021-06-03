package IAs;

import modele.*;

import java.util.ArrayList;

public class IAMinmax extends IA{


    int noirGagne;
    int blancGagne;
    ArrayList<IAAction> iaAction;
    public IAMinmax(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        iaAction = new ArrayList<>();
        iaCartes = new ArrayList<>();
        noirGagne = 0;
        blancGagne =0;

    }

    public void resetAllPossible(boolean needAvance, Configuration config){
        if(ceds.size()>0) ceds = new ArrayList<>();
        for(int i=1;i<6;i++) {
            if(!iaCartes.contains(Carte.generateCarteFromInt(i))) continue;
            if (needAvance){
                if (i <= config.getDistance()){
                    ceds.add(new CarteEtDirection(1, Carte.generateCarteFromInt(i), i));
                }
            }
            if ( i<= config.disToDebut()){
                ceds.add(new CarteEtDirection(2, Carte.generateCarteFromInt(i), i));
            }
        }
    }
    public int nbCarteI(int valeur, ArrayList<Carte> reste){
        int n = 0;
        for (int i = 0; i < reste.size(); i++)
            if(reste.get(i).getValue() == valeur) n++;
        return n;
    }

    public void iaCanAttack(Configuration config){
        iaAction = new ArrayList<>();
        if(config.tourCourrant== config.owner) iaCartes = config.iaCartes;
        else iaCartes = config.reste;
        boolean []vue = new boolean[5];
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
                    if(dis-i != dis){
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
        if(config.typeGagne!=2){
            //System.out.println("Config : " + config.typeGagne);
            if(config.typeGagne==1){noirGagne++;}
            else blancGagne++;
            if(config.pere.reste.size()>10){
                System.out.println("Entre : " + config.pere.reste.size() + " ");
                System.out.println("NoirGagne : " + noirGagne);
                System.out.println("BlancGagne : " + blancGagne);
            }
            return config;
        }
        //System.out.println("Entre : " + config.reste.size() + " " + config.positionNoir + " " + config.positionBlanc);

        for (Carte c:config.reste) {
            iaCartes.add(c);
        }
        if(config.action.attack==null){
            iaCanAttack(config);

            for (IAAction act: iaAction) {
                allPossible(new Configuration(null, new IAAction(act.move, act.attack,0), config));
            }
        }else{
            Attack lastAttack = config.action.attack;
            //parry
            if(nbCarteI(lastAttack.getAttValue().getValue())>=lastAttack.getAttnb()){
                for (int i = 0; i < lastAttack.getAttnb(); i++) {
                    iaAction.remove(lastAttack.getAttValue());
                }
                iaCanAttack(config);
                for (IAAction act: iaAction) {
                   allPossible(new Configuration(lastAttack, new IAAction(act.move, act.attack,0), config));
                }
            }
            //No parry
            if(lastAttack.getAt()==AttackType.INDIRECT){
                //如果能撤,把所有撤的情况列出来
                for (int i = 1; i < 6; i++) {
                    if(config.disToDebut()>i && nbCarteI(i)>0){
                        allPossible(new Configuration(null, new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0), config));
                    }
                }
                if(config.disToDebut()<4){
                    allPossible(new Configuration(config.tourCourrant, config, lastAttack));
                }
            }
            //如果不能撤，new config 输的
            if(lastAttack.getAt()==AttackType.DIRECT){
                allPossible(new Configuration(config.tourCourrant, config, lastAttack));
            }
        }
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
        Configuration config = new Configuration(pg.getPlayerCourant(), pg.getTourCourant());
        allPossible(config);
        System.out.println("NoirGagne ******: " + noirGagne);
        System.out.println("BlancGagne ******: " + blancGagne);
        //System.out.println(allPossible(config).tousFils.size());
    }

}
