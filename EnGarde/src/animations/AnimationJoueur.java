package animations;

import controlleur.ControlCenter;

public class AnimationJoueur extends Animation {
	ControlCenter cc;
	int startCase;
	int targetCase;
	
	public AnimationJoueur(int startCase, int targetCase, ControlCenter cc) {
		super(120);
		this.startCase = startCase;
		this.targetCase = targetCase;
		this.cc = cc;
	}
	
	@Override
	public void tictac() {
		super.compteur += super.vitesse;
		if(super.compteur >= MAXCOMPTEUR) {
			super.compteur = MAXCOMPTEUR;
		}
		System.out.println("Compteur = "+ super.compteur);
		miseAJour();
	}

	@Override
	public void miseAJour() {
		this.cc.interSwing.gi.calculNewJoueurPos(startCase, targetCase, super.progress());
	}
	
	@Override
	public boolean estTerminee() {
		return super.compteur == MAXCOMPTEUR;
	}
}
