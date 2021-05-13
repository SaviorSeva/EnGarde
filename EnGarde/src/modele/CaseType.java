package modele;

public enum CaseType {
	VIDE(0), BLANC(1), NOIR(2);
	
	private int type;
	
	private CaseType(int i) {
		this.type = i;
	}
	
	public int getType() {
		return this.type;
	}
}
