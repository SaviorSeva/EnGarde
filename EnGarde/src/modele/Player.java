package modele;

import java.util.ArrayList;

public class Player {
	public int place;
	int startPlace;
	int point;
	ArrayList<Carte> cartes;
	public String name;
	
	public Player(int i, String name) {
		this.place = i;
		this.startPlace = i;
		this.point = 0;
		this.cartes = new ArrayList<Carte>();
		this.name = name;
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
	
	public int getDistToStartPlace() {
		if(this.place > this.startPlace) return this.place - this.startPlace;
		else return this.startPlace - this.place;
	}

	public int getStartPlace() {
		return startPlace;
	}

	public String getName() {
		return name;
	}

	public void setstartPlace(int startPlace) {
		this.startPlace = startPlace;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "place=" + place + ", cartes=" + cartes.toString();
	}
	
	public String generatePlayerString(String player) {
		StringBuilder sb = new StringBuilder();
		sb.append(player + ":");
		sb.append("name=" + this.getName() + ","); 
		sb.append("place=" + this.getPlace() + ",");
		sb.append("startPlace=" + this.getStartPlace() + ",");
		sb.append("point=" + this.getPoint() + ",");
		sb.append("cartes=");
		for(int i=0; i<this.cartes.size(); i++) sb.append(this.cartes.get(i).getValue());
		sb.append(";\n");
		return sb.toString();
	}
}
