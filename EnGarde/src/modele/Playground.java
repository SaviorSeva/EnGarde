package modele;

import java.util.ArrayList;
import java.util.Collections;

import patterns.Observable;

public class Playground extends Observable{
    Player blanc;
    Player noir;
    
    ArrayList<Carte> reste;
    ArrayList<Carte> used;
    
    int tourCourant;
    
    public ArrayList<Boolean> selected;
    
    public Playground() {
    	this.blanc = new Player(0);
    	this.noir = new Player(22);
    	
    	this.initiliaseCarte();
    	this.tourCourant = 1;
    	this.selected = new ArrayList<Boolean>();
    }
    
    public void initialiseSelected() {
		this.selected.clear();
		for(int i=0; i<5; i++) {
			selected.add(false);
		}
	}
    
    public void resetSelected() {
		for(int i=0; i<this.selected.size(); i++) {
			 this.selected.set(i, false);
		}
	}
    
    public int getTourCourant() {
    	return tourCourant;
    }
    
    public void initiliaseCarte() {
    	this.reste = new ArrayList<Carte>();
    	for(int i=1; i<6; i++) {
    		this.reste.add(Carte.UN);
    		this.reste.add(Carte.DEUX);
    		this.reste.add(Carte.TROIS);
    		this.reste.add(Carte.QUATRE);
    		this.reste.add(Carte.CINQ);
    	}
    	this.used = new ArrayList<Carte>();
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
    	StringBuilder sb = new StringBuilder();
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
    
    public void retreat(int valeur) {
    	if(tourCourant == 1) this.blanc.setPlace(this.blanc.getPlace() - valeur);
    	else this.noir.setPlace(this.noir.getPlace() + valeur);
    	this.metAJour();
    }
}
