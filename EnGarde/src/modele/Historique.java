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
	
	public int size(){
		return this.listAction.size();
	}
	
	public Action removeLastAction() {
		return this.listAction.remove(this.listAction.size() - 1);
	}
	
	@Override
	public String toString() {
		return listAction.toString();
	}
	
	public String generateHistString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<this.listAction.size(); i++) {
			sb.append(this.listAction.get(i).getActionString() + " ");
		}
		return sb.toString();
	}
	
}