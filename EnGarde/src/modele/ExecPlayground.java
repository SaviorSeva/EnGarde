package modele;

import java.util.ArrayList;
import java.util.Collections;

public class ExecPlayground {
	public Playground pg;
	
	public ExecPlayground(Playground pg) {
		this.pg = pg;
	}
	
	public int getDistance() {
		return pg.getDistance();
	}
	
	public int distribuerCarte(int player) {
    	if(player != 1 && player != 2) return -1;
    	Carte c = this.pg.getReste().remove(0);
    	if(player == 1) this.pg.getBlanc().addCartes(c);
    	else if(player == 2) this.pg.getNoir().addCartes(c);
    	return 0;
    }
	
	public void avance(int valeur) {
    	if(this.pg.getTourCourant() == 1) 
    		this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() + valeur);
    	else 
    		this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() - valeur);
    	this.pg.metAJour();
    }
    
    public void retreat(int valeur) {
    	this.avance(-valeur);
    }
	
	public void restartNewRound() {
    	this.pg.getBlanc().setPlace(0);
    	this.pg.getNoir().setPlace(22);
    	
    	this.initiliaseCarte();
    	
    	for(int i=0; i<5; i++) {
			this.distribuerCarte(1);
			this.distribuerCarte(2);
		}
    	this.pg.setTourCourant(1);
    	
    	this.roundStart(new Attack(AttackType.NONE, null, 0));
    }
	
	 public void changetTour() {
    	if(pg.getTourCourant() == 1) pg.setTourCourant(2);
    	else pg.setTourCourant(1);
    }
	
	public void initialiseSelected() {
		this.pg.getSelected().clear();
		for(int i=0; i<5; i++) {
			this.pg.getSelected().add(false);
		}
	}
    
    public void resetSelected() {
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			 this.pg.getSelected().set(i, false);
		}
	}
    
    public void initiliaseCarte() {
    	if(this.pg.getReste().size() != 0) this.pg.getReste().clear();
    	this.pg.getUsed().clear();
    	this.pg.getBlanc().clearCartes();
    	this.pg.getNoir().clearCartes();
    	
    	for(int i=1; i<6; i++) {
    		this.pg.getReste().add(Carte.UN);
    		this.pg.getReste().add(Carte.DEUX);
    		this.pg.getReste().add(Carte.TROIS);
    		this.pg.getReste().add(Carte.QUATRE);
    		this.pg.getReste().add(Carte.CINQ);
    	}
    	this.shuffleReste();
    }
    
    public void shuffleReste() {
    	Collections.shuffle(this.pg.getReste());
    }
	
    
    /*
     *  Return :
     *  	-1 if error,
     *  	0 if cannot parry (which means lose this round),
     *  	1 if has no attack,
     * 		2 if can parry direct attack,
     * 		3 if have to retreat,
     * 		4 if can parry indirect attack, and player can retreat or parry the attack.
     */
	public int phaseParer(Attack dernierAttaque){
		ArrayList<Carte> cartes;
		if(this.pg.getTourCourant() == 1) cartes = this.pg.getBlancCartes();
		else cartes = this.pg.getNoirCartes();
		int nb = 0;
		switch(dernierAttaque.getAt()) {
		case NONE:
			return 1;
		case DIRECT:
			for(int i=0; i<cartes.size(); i++) {
				if(cartes.get(i).getValue() == dernierAttaque.getAttValue().getValue()) nb++;
			}
			if(nb >= dernierAttaque.getAttnb()) {
				for(int i=0; i<cartes.size() && nb>0; i++) {
					if(cartes.get(i).getValue() == dernierAttaque.getAttValue().getValue()) {
						this.pg.getSelected().set(i, true);
						nb--;
					}
				}
				return 2;
			}
			else return 0;
		case INDIRECT:
			// TODO
			for(int i=0; i<cartes.size(); i++) {
				if(cartes.get(i).getValue() == dernierAttaque.getAttValue().getValue()) nb++;
			}
			if(nb >= dernierAttaque.getAttnb()) return 4;
			else {
				return 3;
			}
		}
		this.pg.metAJour();
		return -1;
	}
	
	public void phaseDeplacer(Carte c) {
		
	if(c == null) System.err.println("Error getSelectedCard()");
	
	if(this.pg.getDistance() == c.getValue() && this.pg.getDirectionDeplace() == 1) {
		// Direct Attack
			int nbSelected = this.getNBSelectedCard();
			this.jouerCarte();
			this.roundEnd(new Attack(AttackType.DIRECT, c, nbSelected));	
		}else {
			if(this.pg.getDirectionDeplace() == 1) this.avance(c.getValue());
			else if(pg.getDirectionDeplace() == 2) this.retreat(c.getValue());
			this.jouerCarte();
			if(this.canAttack()) {
				this.pg.setWaitStatus(4);
				
			}else {
				this.roundEnd(new Attack(AttackType.NONE, null, 0));
			}
		}
	}
	
	public ArrayList<Carte> getCurrentPlayerCards(){
		if(pg.getTourCourant() == 1) return this.pg.getBlancCartes();
		else return this.pg.getNoirCartes();
	}
	
	public void jouerCarte() {
		// TODO Valid card
		ArrayList<Carte> cartes = this.getCurrentPlayerCards();
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			if(this.pg.getSelected().get(i)) {
				Carte joue = cartes.remove(i);
				this.pg.getSelected().remove(i);
				i--;
				this.pg.getUsed().add(joue);
			}
		}
		this.pg.metAJour();
	}
	
	public void enterE3() {
		ArrayList<Carte> cs = this.getCurrentPlayerCards();
		boolean unableToMove = true;
		for(int i=0; i<cs.size() && unableToMove; i++) {
			int val = cs.get(i).getValue();
			if(this.pg.getDistance() >= val || this.pg.getPlayerCourant().getDistToStartPoint() > val) unableToMove = false;
		}
		if(unableToMove) {
			System.out.println("You cannot move or attack, therefore you lose !");
			this.pg.getEnemyCourant().incrementPoint();
			this.restartNewRound();
		}else this.pg.setWaitStatus(3);
	}
	
	public void roundStart(Attack att) {
		
		pg.setLastAttack(att);
		
		if(this.pg.getBlancCartes().size() == 0 && this.pg.getNoirCartes().size() == 0) {
			int distBlanc = this.pg.getBlanc().getDistToStartPoint();
			int distNoir = this.pg.getNoir().getDistToStartPoint();
			if(distBlanc > distNoir) this.pg.getBlanc().incrementPoint();
			else if(distNoir > distBlanc) this.pg.getNoir().incrementPoint();
			this.restartNewRound();
		}
		else if(this.getCurrentPlayerCards().size() == 0) this.roundEnd(new Attack(AttackType.NONE, null, 0));
		else {
			int pharerResultat = phaseParer(this.pg.getLastAttack());
	    	switch(pharerResultat) {
	    	case 0:
	    		System.out.println("Case 0 lose");
			if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
			else this.pg.getBlanc().incrementPoint();
			this.restartNewRound();
			break;
		case 1:
			System.out.println("Case 1 noAttack");
			enterE3();
			break;
		case 2:
			System.out.println("Case 2 canParry");
			this.pg.setWaitStatus(1);
			break;
		case 3:
			System.out.println("Case 3 retreat");
			ArrayList<Carte> cs = this.getCurrentPlayerCards();
			boolean unableToRetreat = true;
			for(int i=0; i<cs.size() && unableToRetreat; i++) {
				if(this.pg.getPlayerCourant().getDistToStartPoint() >= cs.get(i).getValue()) unableToRetreat = false;
			}
			if(unableToRetreat) {
				System.out.println("You cannnot retreat. You lose !");
				this.pg.getEnemyCourant().incrementPoint();
				this.restartNewRound();
			}
			this.pg.setWaitStatus(2);
			/*
			this.waitConfirm();
			Carte c = this.getSelectedCard();
			this.jouerCarte();
			this.retreat(c.getValue());
			this.roundEnd(AttackType.NONE, null, 0);
			*/
			break;
		case 4:
			System.out.println("Case 4 retreat or parry");
			this.pg.setWaitStatus(5);
			break;
		default:
			// Should not be executed
			System.err.println("roundStart() Error");
				break;
	    	}
		}
		
	}
	
	public void roundEnd(Attack att) {
		ArrayList<Carte> cartes = this.getCurrentPlayerCards();
		while(this.pg.getReste().size() != 0 && cartes.size() < 5) {
			this.distribuerCarte(pg.getTourCourant());
		}
		this.pg.setWaitStatus(0);
		this.pg.initialiseSelected();
		this.changetTour();
		this.roundStart(att);
	}
	
	public void confirmReceived() {
		Carte c;
		int nbSelected;
		switch(this.pg.getWaitStatus()) {
		case 1:
			c = this.getSelectedCard();
			nbSelected = this.getNBSelectedCard();
			if(c.getValue() == this.pg.getLastAttack().getAttValue().getValue() && nbSelected == this.pg.getLastAttack().getAttnb()) {
				this.jouerCarte();
				enterE3();
			}else {
				System.out.println(	"You cannot parry the direct attack of (" + 
									this.pg.getLastAttack().getAttValue().getValue() + 
									", " + 
									this.pg.getLastAttack().getAttnb() + 
									") with the selection of (" +
									c.getValue() + ", " + nbSelected + ").");
			}
			break;
		case 2:
			c = this.getSelectedCard();
			if(this.pg.getPlayerCourant().getDistToStartPoint() < c.getValue()) System.out.println("You cannot retreat due to the size of the playground.");
			else{
				this.jouerCarte();
				this.retreat(c.getValue());
				this.roundEnd(new Attack(AttackType.NONE, null, 0));
			}
			break;
		case 3:
			c = this.getSelectedCard();
			if(c == null) System.out.println("You must pick a card!");
			else if(c.getValue() > this.pg.getDistance() && this.pg.getDirectionDeplace() == 1)
				System.out.println("You cannot surpass the other player");
			else if(this.pg.getDirectionDeplace() == 2) {
				if(this.pg.getPlayerCourant().getDistToStartPoint() < c.getValue()) System.out.println("You cannot retreat due to the size of the playground.");
				else this.phaseDeplacer(c);
			}else 
				this.phaseDeplacer(c);
				
			
			break;
		case 4:
			// Indirect Attack
			Carte indirectCarte = this.getSelectedCard();
			nbSelected = this.getNBSelectedCard();
			if(indirectCarte.getValue() == this.pg.getDistance()) {
				this.jouerCarte();
				this.roundEnd(new Attack(AttackType.INDIRECT, indirectCarte, nbSelected));
			}else {
				System.out.println("You cannot attack with the selection of (" +
						indirectCarte.getValue() + ", " + nbSelected + 
						") because the distance is " + this.pg.getDistance() + ".");
			}
			break;
		case 5:
			c = this.getSelectedCard();
			nbSelected = this.getNBSelectedCard();
			if(c.getValue() == this.pg.getLastAttack().getAttValue().getValue() && nbSelected == this.pg.getLastAttack().getAttnb()) {
				this.jouerCarte();
				enterE3();
			}else {
				System.out.println(	"You cannot parry the indirect attack of (" + 
									this.pg.getLastAttack().getAttValue().getValue() + 
									", " + 
									this.pg.getLastAttack().getAttnb() + 
									") with the selection of (" +
									c.getValue() + ", " + nbSelected + ").");
			}
			break;
		default:
			// Should not be executed
			System.err.println("Line 323 : confirmReceived() Error");
			break;	
		}
	}
	
	public void addPointByCurrentPlayer() {
		if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
		else this.pg.getBlanc().incrementPoint();
	}
	
	public void cancelReceived() {
		switch(this.pg.getWaitStatus()) {
		case 1:
			System.out.println("Not parrying direct attack. You lose !");
			this.addPointByCurrentPlayer();
			this.restartNewRound();
		break;
		case 2:
			System.out.println("Not parrying indirect attack. You lose !");
			this.addPointByCurrentPlayer();
			this.restartNewRound();
			break;
		case 3:
			System.out.println("You must move at this round !");
			break;
		case 4:
			System.out.println("You've chose not to attack the enemy.");
			this.roundEnd(new Attack(AttackType.NONE, null, 0));
			break;
		case 5:
			Carte c = this.getSelectedCard();
			if(this.pg.getPlayerCourant().getDistToStartPoint() < c.getValue()) System.out.println("You cannot retreat due to the size of the playground.");
			else {
				this.jouerCarte();
	    		this.retreat(c.getValue());
	    		this.roundEnd(new Attack(AttackType.NONE, null, 0));
			}
			
			break;
		}
		this.pg.metAJour();
		}
	
	public Carte getSelectedCard() {
		Carte c = null;
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			if(this.pg.getSelected().get(i)) {
				c = this.getCurrentPlayerCards().get(i);
			}
		}
		return c;
	}
	
	public int getNBSelectedCard() {
		int res = 0;
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			if(this.pg.getSelected().get(i)) res++;
		}
		return res;
	}
	
	public boolean cartesContains(int value) {
		ArrayList<Carte> cartes = this.getCurrentPlayerCards();
		for(int i=0; i<cartes.size(); i++) {
			if(cartes.get(i).getValue() == value) return true;
		}
		return false;
	}
	
	public boolean canAttack() {
		int dist = this.pg.getDistance();
		if(this.cartesContains(dist)) return true;
		return false;
	}
}
