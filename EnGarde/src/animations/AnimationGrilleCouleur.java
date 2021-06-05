package animations;

import java.awt.Color;

import controlleur.ControlCenter;

public class AnimationGrilleCouleur extends Animation {
	public Color startColor;
	public Color endColor;
	public Color currentColor;
	public int caseNB;
	
	public AnimationGrilleCouleur(Color startColor, Color endColor, int caseNB) {
		super(120);
		this.startColor = startColor;
		this.endColor = endColor;
		this.caseNB = new Integer(caseNB);
		this.currentColor = startColor;
	}
	
	@Override
	public void tictac() {
		compteur += vitesse;
		if(this.compteur >= MAXCOMPTEUR) {
			this.compteur = MAXCOMPTEUR;
		}
		System.out.println("Compteur = " + this.compteur);
		miseAJour();
	}
	
	@Override
	public boolean estTerminee() {
		return super.compteur == MAXCOMPTEUR;
	}

	@Override
	public void miseAJour() {
		int newR = (int)((double)(startColor.getRed() * (100-this.progress())) / 100.0 + endColor.getRed() * this.progress() / 100.0);
		int newG = (int)((double)(startColor.getGreen() * (100-this.progress())) / 100.0 + endColor.getGreen() * this.progress() / 100.0);
		int newB = (int)((double)(startColor.getBlue() * (100-this.progress())) / 100.0 + endColor.getBlue() * this.progress() / 100.0);
		System.out.println("R=" + newR + " G=" + newG + " B=" + newB + " pos=" + this.caseNB);
		this.currentColor = new Color(newR, newG, newB);
	}
}
