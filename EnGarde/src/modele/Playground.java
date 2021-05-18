package modele;

import java.util.ArrayList;
import java.util.Collections;

import patterns.Observable;

public class Playground extends Observable{
    public Player blanc;
    public Player noir;
    
    ArrayList<Carte> reste;
    ArrayList<Carte> used;
    
    public int tourCourant;
    
    public boolean confirmed;
    int directionDeplace; // 1-Avance, 2-Retrait, 0-Valeur initialise
    
    public ArrayList<Boolean> selected;
    
    public Playground() {
    	this.blanc = new Player(0);
    	this.noir = new Player(22);
    	
    	this.reste = new ArrayList<Carte>();
    	this.used = new ArrayList<Carte>();
    	
    	this.initiliaseCarte();
    	
    	this.tourCourant = 1;
    	this.selected = new ArrayList<Boolean>();
    	this.confirmed = false;
    	
    	this.directionDeplace = 0;
    }
    
    public int getDirectionDeplace() {
		return directionDeplace;
	}
	public void setDirectionDeplace(int directionDeplace) {
		this.directionDeplace = directionDeplace;
	}
	
	public void setConfirmed(boolean b) {
		this.confirmed = b;
	}
	
	public void restartNewRound() {
    	this.blanc.setPlace(10);
    	this.noir.setPlace(15);
    	
    	this.initiliaseCarte();
    	
    	for(int i=0; i<5; i++) {
			this.distribuerCarte(1);
			this.distribuerCarte(2);
		}
    	this.tourCourant = 1;
    	
    	this.roundStart(AttackType.NONE, null, 0);
    }
    
    public void initialiseSelected() {
		this.selected.clear();
		for(int i=0; i<5; i++) {
			selected.add(false);
		}
		//System.out.println(this.selected);
	}
    
    public void resetSelected() {
		for(int i=0; i<this.selected.size(); i++) {
			 this.selected.set(i, false);
		}
		//System.out.println(this.selected);
	}
    
    public void setSelected(ArrayList<Boolean> al) {
    	this.selected = al;
    	//System.out.println(this.selected);
    }
    
    public void setSelected(int i, boolean b) {
    	this.selected.set(i, b);
    	//System.out.println(this.selected);
    }
    
    public boolean isEmptyCase(int i) {
    	return (this.blanc.getPlace() != i) && (this.noir.getPlace() != i);
    }
    
    public int getTourCourant() {
    	return tourCourant;
    }
    
    public void initiliaseCarte() {
    	if(this.reste.size() != 0) this.reste.clear();
    	this.used.clear();
    	this.blanc.clearCartes();
    	this.noir.clearCartes();
    	
    	for(int i=1; i<6; i++) {
    		this.reste.add(Carte.UN);
    		this.reste.add(Carte.DEUX);
    		this.reste.add(Carte.TROIS);
    		this.reste.add(Carte.QUATRE);
    		this.reste.add(Carte.CINQ);
    	}
    	this.shuffleReste();
    }
    
    public void shuffleReste() {
    	Collections.shuffle(reste);
    }
    
    public int distribuerCarte(int player) {
    	if(player != 1 && player != 2) return -1;
    	Carte c = this.reste.remove(0);
    	if(player == 1) this.blanc.addCartes(c);
    	else if(player == 2) this.noir.addCartes(c);
    	return 0;
    }
    
    public void avance(int valeur) {
    	if(tourCourant == 1) this.blanc.setPlace(this.blanc.getPlace() + valeur);
    	else this.noir.setPlace(this.noir.getPlace() - valeur);
    	this.metAJour();
    }
    
    public void retreat(int valeur) {
    	if(tourCourant == 1) this.blanc.setPlace(this.blanc.getPlace() - valeur);
    	else this.noir.setPlace(this.noir.getPlace() + valeur);
    	this.metAJour();
    }
    
    public void changetTour() {
    	if(tourCourant == 1) tourCourant = 2;
    	else tourCourant = 1;
    }
    
    public int getBlancPos() {
    	return this.blanc.getPlace();
    }
    
    public int getNoirPos() {
    	return this.noir.getPlace();
    }
    
    public ArrayList<Carte> getBlancCartes() {
    	return this.blanc.getCartes();
    }
    
    public ArrayList<Carte> getNoirCartes() {
    	return this.noir.getCartes();
    }
    
    public int getResteNb() {
    	return this.reste.size();
    }
    
    public int getDistance() {
    	return this.getNoirPos() - this.getBlancPos();
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("          1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 ");
    	sb.append("\nCases : [ ");
    	for(int i=0; i<23; i++) {
    		if(this.blanc.getPlace() == i) sb.append("B ");
    		else if(this.noir.getPlace() == i) sb.append("N ");
    		else sb.append(". ");
    	}
    	sb.append("]\n");
    	sb.append("Reste : " + this.reste.toString() + "\n");
    	sb.append("Blanc : " + this.blanc.toString() + "\n");
    	sb.append("Noir : " + this.noir.toString() + "\n");
    	
    	return sb.toString();
    }
    
    /*
     *  Return :
     *  	-1 if error,
     *  	0 if cannot parry (which means lose this round),
     *  	1 if has no attack,
     * 		2 if can parry attack,
     * 		3 if have to retreat,
     */
    public int phaseParer(AttackType at, Carte attValue, int attnb){
    	ArrayList<Carte> cartes;
    	if(this.tourCourant == 1) cartes = this.getBlancCartes();
    	else cartes = this.getNoirCartes();
    	int nb = 0;
    	switch(at) {
    	case NONE:
    		return 1;
		case DIRECT:
    		for(int i=0; i<cartes.size(); i++) {
    			if(cartes.get(i).getValue() == attValue.getValue()) nb++;
    		}
    		if(nb >= attnb) {
    			for(int i=0; i<cartes.size() && nb>0; i++) {
    				if(cartes.get(i).getValue() == attValue.getValue()) {
    					this.selected.set(i, true);
    					nb--;
    				}
    			}
    			return 2;
    		}
    		else return 0;
		case INDIRECT:
			// TODO
    		for(int i=0; i<cartes.size(); i++) {
    			if(cartes.get(i).getValue() == attValue.getValue()) nb++;
    		}
    		if(nb >= attnb) return 2;
    		else {
    			return 3;
    		}
    	}
    	this.metAJour();
    	return -1;
    }
    
    public void phaseDeplacer() {
    	this.waitConfirm();
    	// 在这里写loop
    	// if (this.confirmed == true)
    	
    	Carte c = this.getSelectedCard();
    	
		if(this.getDistance() != c.getValue()) {
			if(this.directionDeplace == 1) avance(c.getValue());
			else if(this.directionDeplace == 2) retreat(c.getValue());
			this.jouerCarte();
			if(this.canAttack()) {
				// Indirect Attack
				this.waitConfirm();
				Carte indirectCarte = this.getSelectedCard();
				int nbSelected = this.getNBSelectedCard();
				this.jouerCarte();
				this.roundEnd(AttackType.INDIRECT, indirectCarte, nbSelected);
				
			}else {
				this.roundEnd(AttackType.NONE, null, 0);
			}
		}
		else {
			// Direct Attack
			int nbSelected = this.getNBSelectedCard();
			this.jouerCarte();
			this.roundEnd(AttackType.DIRECT, c, nbSelected);
		}
    }
    
    public ArrayList<Carte> getCurrentPlayerCards(){
    	if(tourCourant == 1) return this.getBlancCartes();
		else return this.getNoirCartes();
    }
    
    public void jouerCarte() {
    	// TODO Valid card
    	ArrayList<Carte> cartes = this.getCurrentPlayerCards();
    	for(int i=0; i<this.selected.size(); i++) {
    		if(this.selected.get(i)) {
    			Carte joue = cartes.remove(i);
    			this.selected.remove(i);
    			i--;
    			this.used.add(joue);
    		}
    	}
    	this.metAJour();
    }
    
    public void roundStart(AttackType at, Carte attValue, int attnb) {
    	int pharerResultat = phaseParer(at, attValue, attnb);
    	switch(pharerResultat) {
    	case 0:
    		System.out.println("Case 0 lose");
    		if(this.tourCourant == 1) this.noir.incrementPoint();
    		else this.blanc.incrementPoint();
    		this.restartNewRound();
    		break;
    	case 1:
    		System.out.println("Case 1 noAttack");
    		this.phaseDeplacer();
    		break;
    	case 2:
    		System.out.println("Case 2 canParry");
    		this.waitConfirm();
        	// Carte c = this.getSelectedCard();
        	// int nbSelected = this.getNBSelectedCard();
			this.jouerCarte();
			this.phaseDeplacer();
    		break;
    	case 3:
    		System.out.println("Case 3 retreat");
    		this.waitConfirm();
    		Carte c = this.getSelectedCard();
    		this.jouerCarte();
    		this.retreat(c.getValue());
    		this.roundEnd(AttackType.NONE, null, 0);
    		break;
		default:
			// Should not be executed
			System.err.println("roundStart() Error");
			break;
    	}
    }
    
    public void roundEnd(AttackType at, Carte attValue, int attnb) {
    	ArrayList<Carte> cartes = this.getCurrentPlayerCards();
    	while(this.reste.size() != 0 && cartes.size() < 5) {
    		this.distribuerCarte(tourCourant);
    	}
    	this.changetTour();
    	this.roundStart(at, attValue, attnb);
    }
    
    public void waitConfirm() {
    	while(!confirmed);
    	this.confirmed = false;
    	this.metAJour();
    }
    
    public Carte getSelectedCard() {
    	Carte c = null;
    	for(int i=0; i<this.selected.size(); i++) {
    		if(this.selected.get(i)) {
    			c = this.getCurrentPlayerCards().get(i);
    		}
    	}
    	return c;
    }
    
    public int getNBSelectedCard() {
    	int res = 0;
    	for(int i=0; i<this.selected.size(); i++) {
    		if(this.selected.get(i)) res++;
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
    	int dist = this.getDistance();
    	if(this.cartesContains(dist)) return true;
    	return false;
    }
}
