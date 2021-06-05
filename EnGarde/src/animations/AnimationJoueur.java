package animations;

import controlleur.ControlCenter;

public class AnimationJoueur extends Animation {
	ControlCenter cc;
	public int joueur;
	int startCase;
	int targetCase;
	
	public AnimationJoueur(int joueur, int startCase, int targetCase, ControlCenter cc) {
		super(75);
		this.joueur = joueur;
		this.startCase = startCase;
		this.targetCase = targetCase;
		this.cc = cc;
	}
	public int getStartCase() {
		return startCase;
	}
	public int getTargetCase() {
		return targetCase;
	}



	public void setStartCase(int startCase) {
		this.startCase = startCase;
	}



	public void setTargetCase(int targetCase) {
		this.targetCase = targetCase;
	}



	@Override
	public void tictac() {
		super.compteur += super.vitesse;
		if(super.compteur >= MAXCOMPTEUR) {
			super.compteur = MAXCOMPTEUR;
		}
		// System.out.println("Compteur = "+ super.compteur);
		miseAJour();
	}

	@Override
	public void miseAJour() {
		if(this.joueur == 1) this.cc.interSwing.gi.calculNewBlancPos(startCase, targetCase, super.progress());
		else this.cc.interSwing.gi.calculNewNoirPos(startCase, targetCase, super.progress());
	}
	
	@Override
	public boolean estTerminee() {
		return super.compteur == MAXCOMPTEUR;
	}

	@Override
	public String toString() {
		return "AnimationJoueur [joueur=" + joueur + ", startCase=" + startCase + ", targetCase=" + targetCase + "]";
	}
}
