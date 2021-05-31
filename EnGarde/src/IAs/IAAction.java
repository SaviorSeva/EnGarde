package IAs;

import modele.Attack;
import modele.CarteEtDirection;

import java.util.ArrayList;

public class IAAction  {
    public CarteEtDirection move;
    public Attack attack;
    public double probaReussite;
    public IAAction(CarteEtDirection m, Attack a, double p){
        move = m;
        attack = a;
        probaReussite = p;

    }

}
