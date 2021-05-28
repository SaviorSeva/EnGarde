package modele;

import java.util.ArrayList;

public class Historique {
	public ArrayList<Action> listAction;
	
	public Historique() {
		this.listAction = new ArrayList<Action>();
	}

	public void addCopy(Action currentAction) {
		Action newAct = new Action(currentAction);
		this.listAction.add(newAct);
	}
	
	@Override
	public String toString() {
		return listAction.toString();
	}
}
