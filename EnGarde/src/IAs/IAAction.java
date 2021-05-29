package IAs;

import modele.Attack;
import modele.CarteEtDirection;

import java.util.ArrayList;

public class IAAction implements Comparable<IAAction> {
    public CarteEtDirection move;
    public Attack attack;
    public double probaReussite;
    public IAAction(CarteEtDirection m, Attack a, double p){
        move = m;
        attack = a;
        probaReussite = p;

    }


    @Override
    public int compareTo(IAAction iaAction) {
        if((this.probaReussite - iaAction.probaReussite)<0) return 1;
        else return 0;
    }
}
