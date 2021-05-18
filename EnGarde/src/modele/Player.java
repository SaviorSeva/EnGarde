package modele;

import java.util.ArrayList;

public class Player {
	public int place;
	int point;
	ArrayList<Carte> cartes;
	
	public Player(int i) {
		this.place = i;
		this.point = 0;
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
	
	public void clearCartes() {
		this.cartes.clear();
	}
	
	public void addCartes(Carte c) {
		this.cartes.add(c);
	}
	
	public void incrementPoint() {
		this.point++;
	}
	
	public int getPoint() {
		return this.point;
	}

	@Override
	public String toString() {
		return "place=" + place + ", cartes=" + cartes.toString();
	}
	
	
}
