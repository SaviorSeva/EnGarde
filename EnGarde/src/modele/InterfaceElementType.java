package modele;

public enum InterfaceElementType {
	CASE(1), CARTE(2), BACKGROUND(3);
	
	private int type;
	
	private InterfaceElementType(int i) {
		this.type = i;
	}
	
	public int getValue() {
		return this.type;
	}
}
