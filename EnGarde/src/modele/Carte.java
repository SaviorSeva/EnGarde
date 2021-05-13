package modele;

public enum Carte {
	UN(1), DEUX(2), TROIS(3), QUATRE(4), CINQ(5);
	
	private int type;
	
	private Carte(int i) {
		this.type = i;
	}
	
	public int getValue() {
		return this.type;
	}
}
