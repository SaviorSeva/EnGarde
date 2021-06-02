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

	// 1. Parry Phase
	// 1.1 pas de défence
	public void appendNoParryAction() {
		this.actionString += "N0,";
	}
	// 1.2 retrait: pas d'action de movement ni attaque
	public void appendRetreatAction(Carte c) {
		this.actionString += "R" + c.getValue() + ",MN0,NA0,";
	}
	// 1.3 action défendre
	public void appendParryAction(Carte c, int nb) {
		this.actionString += "P";
		for(int i=0; i<nb; i++) this.actionString += c.getValue() + "";
		this.actionString += ",";
	}
	
	// 2. Move Phase
	// 2.1 action movement en avance
	public void appendMoveForwardAction(Carte c) {
		this.actionString += "MF" + c.getValue() + ",";
	}
	// 2.2 action movement en arrière
	public void appendMoveBackwardAction(Carte c) {
		this.actionString += "MB" + c.getValue() + ",";
	}
	// 2.3 pas d'action movement
	public void appendNoMovementAction() {
		this.actionString += "MN0,";
	}

	// 3. Attack Phase
	// 3.1 on choisi nb carte pour réaliser attaque direct, pas d'action movement
	public void appendDirectAttackAction(Carte c, int nb) {
		appendNoMovementAction();
		this.actionString += "DA";
		for(int i=0; i<nb; i++) this.actionString += c.getValue() + "";
		this.actionString += ",";
	}
	// 3.2 on choisi nb carte pour réaliset attaique indirect, avancer puis attaquer
	public void appendIndirectAttackAction(Carte c, int nb) {
		this.actionString += "IA";
		for(int i=0; i<nb; i++) this.actionString += c.getValue() + "";
		this.actionString += ",";
	}
	// 3.3 pas d'action attaquer
	public void appendNoAttackAction() {
		this.actionString += "NA0,";
	}
	
	// 4. Get cards
	// 4.1 On ne pioche aucun carte
	public void appendNoGetCardAction() {
		this.actionString += "NG0";
	}
	// 4.2 On pioche quelque(s) carte(s)
	public void appendStartGetCardAction() {
		this.actionString += "GE";
	}
	// 4.3 Notez les cartes qu'on pioche
	public void appendGetCardAction(int i) {
		this.actionString += i + "";
	}
	
	// pas d'action de défendre, movement ni attaquer.
	// On concatener appendNoGetCardAction() en fin du chaque tour
	public void appendNoAction() {
		this.actionString += "N0,MN0,NA0,";
	}
	
	// Supprimer un action
	public void deleteAction() {
		String actions[] = this.actionString.split(",");
		actions[actions.length - 1] = "";
		this.actionString = String.join(",", actions);
	}

	public int getNBActionValide() {
		if(this.actionString == null || this.actionString.equals("")) return 0;
		else {
			String act[] = this.actionString.split(",");
			int res = 0;
			for(int i=0; i<act.length; i++) {
				switch(i) {
				case 0:
					if(act[i].charAt(0) == 'P' || act[i].charAt(0) == 'R') res++;
					break;
				case 1:
					if(!act[i].equals("MN0")) res++;
					break;
				case 2:
					if(act[i].charAt(0) != 'N') res++;
					break;
				}
			}
			return res;
		}
	}
}
