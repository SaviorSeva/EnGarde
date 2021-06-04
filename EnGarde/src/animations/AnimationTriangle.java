package animations;

import controlleur.ControlCenter;

public class AnimationTriangle extends Animation {
	ControlCenter cc;
	
	public AnimationTriangle(int vitesse, ControlCenter cc) {
		super(15);
		this.cc = cc;
	}
	
	@Override
	public void tictac() {
		compteur += vitesse;
		if(this.compteur >= MAXCOMPTEUR) {
			this.compteur = 0;
		}
		miseAJour();
	}
	
	@Override
	public boolean estTerminee() {
		return true;
	}

	@Override
	public void miseAJour() {
		cc.interSwing.gi.updateIndicateurPosition(this.progress(), this.cc.pg.getPlayerCourant().getPlace(), this.cc.pg.getTourCourant());
	}

}
