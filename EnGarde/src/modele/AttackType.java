package modele;

public enum AttackType {
	NONE(0), INDIRECT(1), DIRECT(2);
	
	private int type;
	
	private AttackType(int i) {
		this.type = i;
	}
	
	public int getValue() {
		return this.type;
	}
	
	public boolean noAttack() {
		return this.type == 0;
	}
	
	public boolean isDirect() {
		return this.type == 2;
	}
	
	public boolean isIndirect() {
		return this.type == 1;
	}
	
	public static AttackType generatedAttackByValue(int val) {
		switch(val) {
		case 0:
			return NONE;
		case 1:
			return DIRECT;
		case 2:
			return INDIRECT;
		default:
			return null;
		}
	}
}
