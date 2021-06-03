package animations;

public abstract class Animation {
	int compteur;
	final int MAXCOMPTEUR = 600;
	int vitesse;
	
	public Animation(int vitesse) {
		this.compteur = 0;
		this.vitesse = vitesse;
	}

	public void tictac() {
		compteur += vitesse;
		if(this.compteur >= MAXCOMPTEUR) {
			this.compteur = MAXCOMPTEUR;
		}
		miseAJour();
	}
	
	public int progress() { 
		return (int)(((double)this.compteur / (double)MAXCOMPTEUR) * 100); 
	}

	public abstract void miseAJour();

	public boolean estTerminee() {
		return false;
	}
}