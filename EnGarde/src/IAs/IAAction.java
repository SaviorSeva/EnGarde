package IAs;

import modele.Attack;
import modele.CarteEtDirection;

import java.util.ArrayList;

public class IAAction  {
    public CarteEtDirection move;
    public Attack attack;
    public double probaReussite;
    public int type;
    public IAAction(CarteEtDirection m, Attack a, double p){
        move = m;
        attack = a;
        probaReussite = p;
    }
    //type = 0 DA type = 1 IA, type = 2 move
    public IAAction(CarteEtDirection m, Attack a, double p , int type){
        move = m;
        attack = a;
        probaReussite = p;
        this.type = type;
    }

    public IAAction(IAAction iaAction){
        move = iaAction.move;
        attack = iaAction.attack;
        probaReussite = iaAction.probaReussite;
    }
}
