package modele;

import java.util.ArrayList;
import java.util.Collections;

public class Playground {
    Player blanc;
    Player noir;
    
    ArrayList<Carte> reste;
    ArrayList<Carte> used;
    
    int tourCourant;
    
    public Playground() {
    	this.blanc = new Player(0);
    	this.noir = new Player(22);
    	
    	this.initiliaseCarte();
    	this.tourCourant = 1;
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
    }
    
    public void changetTour() {
    	if(tourCourant == 1) tourCourant = 2;
    	else tourCourant = 1;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Reste : " + this.reste.toString() + "\n");
    	sb.append("Blanc : " + this.blanc.toString() + "\n");
    	sb.append("Noir : " + this.noir.toString() + "\n");
    	
    	return sb.toString();
    }
    
    
}
