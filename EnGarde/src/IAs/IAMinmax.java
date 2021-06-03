package IAs;

import modele.*;

import java.util.ArrayList;

public class IAMinmax extends IA{


    final double [][]proba;
    ArrayList<IAAction> iaAction;
    public IAMinmax(ExecPlayground epg, Playground pg) {
        super(epg, pg);
        this.proba = new double[5][6];
        iaAction = new ArrayList<>();

    }

    public void iaCanAttack(Configuration config){
        boolean []vue = new boolean[5];
        int dis = epg.getDistance();
        //Direct attack possible
        for (int i = 1; i < 6; i++) {
            if(nbCarteI(i)==0) continue;
            if (dis == i) {
                for (int j = 0; j <  nbCarteI(i); j++) {
                    iaAction.add(new IAAction(new CarteEtDirection(), new Attack(AttackType.DIRECT, Carte.generateCarteFromInt(i), j), 1 - proba[dis-1][nbCarteI(dis)]));
                }
            }
            if(dis>i){
                iaAction.add(new IAAction(new CarteEtDirection(1, Carte.generateCarteFromInt(i), -1), null, 0));
            }
            if(config.disToDebut()>i){
                iaAction.add(new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0));
            }
        }
        //Indirect attack et move possible
        resetAllPossible(true);
        for (CarteEtDirection ced : ceds) {
            int index = ced.getIndex();
            for (int j = 0; j < iaCartes.size(); j++) {
                dis = epg.getDistance();
                if (vue[iaCartes.get(index).getValue()-1]) break;
                if (ced.getDirection() == 1)
                    dis = dis - iaCartes.get(index).getValue();
                else if (ced.getDirection() == 2)
                    dis = dis + iaCartes.get(index).getValue();
                //System.out.println("Dis : " + dis);
                //move et attack(Indirect attack)
                if (j != ced.getIndex() && dis > 0) {
                    if (dis == iaCartes.get(j).getValue()){
                        iaAction.add(new IAAction(ced, new Attack(AttackType.INDIRECT, iaCartes.get(j), nbCarteI(iaCartes.get(j).getValue())), 1 - proba[dis-1][nbCarteI(dis)]));
                        vue[ced.getC().getValue()-1] = true;
                    }
                }
            }

        }
    }

    public Configuration allPossible(Configuration config){
        iaCartes = new ArrayList<>();
        System.out.println("Entre : ");
        if(config.typeGagne!=2) return config;
        for (Carte c:config.reste) {
            iaCartes.add(c);
        }
        if(config.action.attack==null){
            iaCanAttack(config);
            for (IAAction act: iaAction) {
                return allPossible(new Configuration(null, new IAAction(act.move, act.attack,0), config));
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
                   return allPossible(new Configuration(lastAttack, new IAAction(act.move, act.attack,0), config));
                }
            }
            //No parry
            if(lastAttack.getAt()==AttackType.INDIRECT){
                //如果能撤,把所有撤的情况列出来
                for (int i = 1; i < 6; i++) {
                    if(config.disToDebut()>i && nbCarteI(i)>0){
                        return allPossible(new Configuration(null, new IAAction(new CarteEtDirection(2, Carte.generateCarteFromInt(i), -1), null, 0), config));
                    }
                }
            }
            //如果不能撤，new config 输的
            return allPossible(new Configuration(config.tourCourrant, config, lastAttack));
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
        System.out.println(allPossible(config).tousFils.size());
    }

}
