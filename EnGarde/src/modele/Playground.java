package modele;

import java.util.ArrayList;
import patterns.Observable;

public class Playground extends Observable{
    private Player blanc;
    private Player noir;
    
    private ArrayList<Carte> reste;
    private ArrayList<Carte> used;
    
    private Attack lastAttack;
    
    private int tourCourant;
    
    private int directionDeplace; // 1-Avance, 2-Retrait, 3-PlayerPosition(for parry use), 0-Valeur initialise
    
    private ArrayList<Boolean> selected;
    
    private int waitStatus;
    
    /*	
     * startType == 0 two human
     * startType == 1 human play as white
     * startType == 2 human play as black
     */
 
    private int startType;
    
    public Playground() {
    	this.blanc = new Player(0);
    	this.noir = new Player(22);
    	
    	this.reste = new ArrayList<Carte>();
    	this.used = new ArrayList<Carte>();
    	
    	this.tourCourant = 1;
    	this.selected = new ArrayList<Boolean>();
    	
    	this.directionDeplace = 0;
    	this.waitStatus = 0;
    	this.startType = 2;
    }
    
    // Getters et setters basiques
    public int getDirectionDeplace() {
		return directionDeplace;
	}
	public void setDirectionDeplace(int directionDeplace) {
		//System.out.println(directionDeplace);
		this.directionDeplace = directionDeplace;
	}
	public Player getBlanc() {
		return blanc;
	}
	public Player getNoir() {
		return noir;
	}
	public ArrayList<Carte> getReste() {
		return reste;
	}
	public ArrayList<Carte> getUsed() {
		return used;
	}
	public Attack getLastAttack() {
		return lastAttack;
	}
	public ArrayList<Boolean> getSelected() {
		return selected;
	}
	public void setSelected(ArrayList<Boolean> al) {
    	this.selected = al;
    }
    public void setSelected(int i, boolean b) {
    	this.selected.set(i, b);
    }
	public int getWaitStatus() {
		return waitStatus;
	}
	public void setBlanc(Player blanc) {
		this.blanc = blanc;
	}
	public void setNoir(Player noir) {
		this.noir = noir;
	}
	public void setReste(ArrayList<Carte> reste) {
		this.reste = reste;
	}
	public void setUsed(ArrayList<Carte> used) {
		this.used = used;
	}
	public void setLastAttack(Attack lastAttack) {
		this.lastAttack = lastAttack;
	}
	public int getTourCourant() {
	   	return tourCourant;
	}
	public void setTourCourant(int tourCourant) {
		this.tourCourant = tourCourant;
	}
	public void setWaitStatus(int waitStatus) {
		this.waitStatus = waitStatus;
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
 
    public boolean isEmptyCase(int i) {
    	return (this.blanc.getPlace() != i) && (this.noir.getPlace() != i);
    }
    
    public Player getPlayerCourant() {
    	if(getTourCourant() == 1) return this.blanc;
    	else return this.noir;
    }
    
    public Player getEnemyCourant() {
    	if(getTourCourant() == 2) return this.blanc;
    	else return this.noir;
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
    
    public ArrayList<Carte> getCurrentPlayerCards(){
		if(this.getTourCourant() == 1) return this.getBlancCartes();
		else return this.getNoirCartes();
	}
    
    public Carte getSelectedCard() {
		Carte c = null;
		for(int i=0; i<this.getSelected().size(); i++) {
			if(this.getSelected().get(i)) {
				c = this.getCurrentPlayerCards().get(i);
			}
		}
		return c;
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
    
    public int getStartType() {
		return this.startType;
	}
}