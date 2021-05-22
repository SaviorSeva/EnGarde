package modele;

public class Action {
	String actionString;
	
	public Action() {
		this.actionString = "";
	}
	
	public Action(Action a) {
		this.actionString = a.getActionString();
	}
	
	public void clear() {
		this.actionString = "";
	}
	
	@Override
	public String toString() {
		return "Action [" + this.actionString + "]";
	}

	public String getActionString() {
		return actionString;
	}

	public void setActionString(String actionString) {
		this.actionString = actionString;
	}

	// Parry Phase
	public void appendNoParryAction() {
		this.actionString += "N0,";
	}
	public void appendRetreatAction(Carte c) {
		this.actionString += "R" + c.getValue() + ",MN0,NA0;";
	}
	public void appendParryAction(Carte c, int nb) {
		this.actionString += "P";
		for(int i=0; i<nb; i++) this.actionString += c.getValue() + "";
		this.actionString += ",";
	}
	
	// Move Phase
	public void appendMoveForwardAction(Carte c) {
		this.actionString += "MF" + c.getValue() + ",";
	}
	public void appendMoveBackwardAction(Carte c) {
		this.actionString += "MB" + c.getValue() + ",";
	}
	public void appendNoMovementAction() {
		this.actionString += "MN0,";
	}
	
	// Attack Phase
	public void appendDirectAttackAction(Carte c, int nb) {
		appendNoMovementAction();
		this.actionString += "DA";
		for(int i=0; i<nb; i++) this.actionString += c.getValue() + "";
		this.actionString += ";";
	}
	public void appendIndirectAttackAction(Carte c, int nb) {
		this.actionString += "IA";
		for(int i=0; i<nb; i++) this.actionString += c.getValue() + "";
		this.actionString += ";";
	}
	public void appendNoAttackAction() {
		this.actionString += "NA0;";
	}
	
	public void appendNoAction() {
		this.actionString += "N0,MN0,NA0;";
	}

}
