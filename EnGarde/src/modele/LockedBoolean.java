package modele;

public enum LockedBoolean {
	INVALID(-1), FALSE(0), TRUE(1), LOCKEDFALSE(2), LOCKEDTRUE(3);
	
	private int type;
	
	private LockedBoolean(int i) {
		this.type = i;
	}
	
	public int getValue() {
		return this.type;
	}
	
	public boolean isTrue() {
		return this.type == 1 || this.type == 3;
	}
	
	public boolean isLocked() {
		return this.type >= 2;
	}
	
	public boolean isInvalid() {
		return this.type == -1;
	}
}
