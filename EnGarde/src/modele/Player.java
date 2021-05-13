package modele;

import java.util.ArrayList;

public class Player {
	int place;
	ArrayList<Carte> cartes;
	
	public Player(int i) {
		this.place = i;
		this.cartes = new ArrayList<Carte>();
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public ArrayList<Carte> getCartes() {
		return cartes;
	}

	public void setCartes(ArrayList<Carte> cartes) {
		this.cartes = cartes;
	}
	
	public void addCartes(Carte c) {
		this.cartes.add(c);
	}

	@Override
	public String toString() {
		return "place=" + place + ", cartes=" + cartes.toString();
	}
	
	
}
