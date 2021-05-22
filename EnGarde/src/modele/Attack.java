package modele;

public class Attack {
	private AttackType at;
    private Carte attValue;
    private int attnb;
    
    public Attack(AttackType at, Carte attValue, int attnb) {
		this.at = at;
		this.attValue = attValue;
		this.attnb = attnb;
	}

	public AttackType getAt() {
		return at;
	}

	public Carte getAttValue() {
		return attValue;
	}

	public int getAttnb() {
		return attnb;
	}

	public void setAt(AttackType at) {
		this.at = at;
	}

	public void setAttValue(Carte attValue) {
		this.attValue = attValue;
	}

	public void setAttnb(int attnb) {
		this.attnb = attnb;
	}

	@Override
	public String toString() {
		return "Attack [at=" + at + ", attValue=" + attValue + ", attnb=" + attnb + "]";
	}

}
