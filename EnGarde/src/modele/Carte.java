package modele;

public enum Carte {
	NULL(0), UN(1), DEUX(2), TROIS(3), QUATRE(4), CINQ(5);
	
	private int type;
	
	private Carte(int i) {
		this.type = i;
	}
	
	public int getValue() {
		return this.type;
	}
	
	public static Carte generateCarteFromInt(int i) {
		switch(i) {
			case 0:
				return Carte.NULL;
		case 1 :
			return Carte.UN;
		case 2:
			return Carte.DEUX;
		case 3:
			return Carte.TROIS;
		case 4:
			return Carte.QUATRE;
		case 5:
			return Carte.CINQ;
		default:
			return null;
		}
	}
}
