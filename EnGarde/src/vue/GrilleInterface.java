package vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.JComponent;

import animations.AnimationGrilleCouleur;
import modele.AttackType;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.Playground;
import patterns.Observateur;

public class GrilleInterface extends JComponent implements Observateur{
	Graphics2D drawable;
	Graphics gra;
	int xpoints[];
	int ypoints[];
	boolean inBlancAnimation;
	boolean inNoirAnimation;
	public int blancAnimationPos;
	public int noirAnimationPos;
	
	int greenCase1 = -1;
	int greenCase2 = -1;
	int redCase = -1;
	int pinkCase = -1;
	int greyCase = -1;
	
	public int oldBlancPos;
	public int oldNoirPos;
	
	int blancCurrentPos;
	int noirCurrentPos;
	
	public Playground pg;
	
	// la taille de grille 
	public int caseWidth;
	public int caseHeight;
	
	public int caseXStart; // la margin en gauche
	public double proportionCaseX; // la proportion de case en largeur
	public double proportionCaseY; // la proportion de case en hauteur
	
	public ArrayList<InterfaceElementPosition> grillePos;
	
	int predictMove1, predictMove2; // Les grilles de mouvement possible - afficher avec couleur vert
	int parryCase; // La grilles pour faire un action de parer - afficher avec couleur rose
	int stayCase; // La grilles pour ne pas poser un attaque indirect - afficher avec couleur gris
	public int choseCase; // Les grilles de mouvement possible - afficher avec border jaune
	
	public  ArrayList<AnimationGrilleCouleur> agcs;
	
	public GrilleInterface(Playground pg) {
		this.pg = pg;
		pg.ajouteObservateur(this);
		this.setPreferredSize(new Dimension(50*23 + 20, 250));
		this.proportionCaseX = 1.0;
		this.proportionCaseY = 1.0;
		this.predictMove1 = -1;
		this.predictMove2 = -1;
		this.stayCase = -1;
		this.parryCase = -1;
		this.choseCase = -1;
		this.xpoints = new int[3];
		this.ypoints = new int[3];
		this.inBlancAnimation = false;
		this.inNoirAnimation = false;
		this.blancCurrentPos = this.pg.getBlancPos();
		this.noirCurrentPos = this.pg.getNoirPos();
		this.oldBlancPos = -1;
		this.oldNoirPos = -1;
		this.blancAnimationPos = 0;
		this.noirAnimationPos = 0;
		this.initialiseIndicateurPosition(this.pg.getPlayerCourant().getPlace(), this.pg.getTourCourant());
		this.agcs = new ArrayList<AnimationGrilleCouleur>();
	}
	
	// Verifier si on peut selectionner un grille
	public boolean equalToHighlighted(int caseNB) {
		return this.predictMove1 == caseNB || this.predictMove2 == caseNB || this.parryCase == caseNB || this.stayCase == caseNB;
	}
	
	
	public boolean isInBlancAnimation() {
		return inBlancAnimation;
	}

	public boolean isInNoirAnimation() {
		return inNoirAnimation;
	}

	public void setInBlancAnimation(boolean inBlancAnimation) {
		this.inBlancAnimation = inBlancAnimation;
	}

	public void setInNoirAnimation(boolean inNoirAnimation) {
		this.inNoirAnimation = inNoirAnimation;
	}

	public void tracerGrille() {
		for(int i=0; i<23; i++) {
			
			// If can parry, change the color to pink
			if(i == parryCase) drawable.setColor(Color.PINK);
			else if (i == stayCase) {
				drawable.setColor(Color.GRAY);
			}
			else if(i == this.predictMove1) {
				// If the case is moveable but there is an enemy, change the color to red to attack
				if(this.pg.getEnemyCourant().getPlace() == i) drawable.setColor(Color.RED);
				// otherwise change to green
				else drawable.setColor(Color.GREEN);
			}
			else if (i == this.predictMove2) {
				if(this.pg.getEnemyCourant().getPlace() == i) drawable.setColor(Color.RED);
				else drawable.setColor(Color.GREEN);
			}
			// If none, show the default orange color
			else drawable.setColor(Color.ORANGE);
			drawable.fillRect(caseXStart+i*caseWidth, 0, caseWidth, caseHeight);
			
			this.tracerAllGrilleAnimation();
			
			// Black border line
			drawable.setColor(Color.BLACK);
			drawable.drawRect(caseXStart+i*caseWidth, 0, caseWidth, caseHeight);
			drawable.setStroke(new BasicStroke(1));
			
			// String of the number of cases
			// Highlight the selected case with yellow string
			if(i == choseCase) drawable.setColor(Color.YELLOW);
			drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(15*proportionCaseX)));
			drawable.drawString((i+1) + "", caseXStart+i*caseWidth+(int)(caseWidth*0.4), (int)(caseHeight * 0.9));
		}
		
		// Highlight selected case with yellow border and number case string
		if(choseCase != -1) {
			drawable.setColor(Color.YELLOW);
			drawable.setStroke(new BasicStroke(3));
			drawable.drawRect(caseXStart+choseCase*caseWidth, 0, caseWidth, caseHeight);
		}
	}
	
	public void tracerAllGrilleAnimation() {
		for(AnimationGrilleCouleur a : this.agcs) {
			drawable.setColor(a.currentColor);
			drawable.fillRect(caseXStart+a.caseNB*caseWidth, 0, caseWidth, caseHeight);

			drawable.setColor(Color.BLACK);
			drawable.drawRect(caseXStart+a.caseNB*caseWidth, 0, caseWidth, caseHeight);
			drawable.setStroke(new BasicStroke(1));
			
			// String of the number of cases
			// Highlight the selected case with yellow string
			if(a.caseNB == choseCase) drawable.setColor(Color.YELLOW);
			drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(15*proportionCaseX)));
			drawable.drawString((a.caseNB+1) + "", caseXStart+a.caseNB*caseWidth+(int)(caseWidth*0.4), (int)(caseHeight * 0.9));
		}
		
	}
	
	public ArrayList<InterfaceElementPosition> getGrillesPositions(){
		ArrayList<InterfaceElementPosition> res = new ArrayList<InterfaceElementPosition>();
		for(int i=0; i<23; i++) {
			// x1 y1 est le point de gauche en haut, x2 y2 est le point de droite en bas
			InterfaceElementType iet = InterfaceElementType.CASE;
			int x1 = this.caseXStart + this.caseWidth * i;
			int y1 = 0;
			int x2 = this.caseXStart + this.caseWidth * (i+1);
			int y2 = this.caseHeight;
			InterfaceElementPosition iep = new InterfaceElementPosition(iet, x1, x2, y1, y2, i);
			res.add(iep);
		}
		return res;
	}
	
	public void tracerJoueur() {
		// Joueur Blanc
		
		if(this.inBlancAnimation) {
			drawable.setColor(Color.WHITE);
			drawable.fillOval(this.blancAnimationPos, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
			
			drawable.setColor(Color.BLACK);
			if(this.pg.getStartType() == 0) 
				drawable.fillOval(caseXStart+this.pg.getNoirPos()*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
			else 
				drawable.fillOval(caseXStart+this.oldNoirPos*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
		
		}else if(this.inNoirAnimation){
			drawable.setColor(Color.WHITE);
			if(this.pg.getStartType() == 0) 
				drawable.fillOval(caseXStart+this.pg.getBlancPos()*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
			else
				drawable.fillOval(caseXStart+this.oldBlancPos*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
			
			drawable.setColor(Color.BLACK);
			drawable.fillOval(this.noirAnimationPos, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
		
		}else {
			drawable.setColor(Color.WHITE);
			drawable.fillOval(caseXStart+this.blancCurrentPos*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
			
			drawable.setColor(Color.BLACK);
			drawable.fillOval(caseXStart+this.noirCurrentPos*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
		}
	
		// String Distance
		int distYEnd = (int)(caseHeight * 1.15);
		drawable.setColor(Color.BLACK);
		String text = "Distance entre 2 joueurs : " + pg.getDistance();
		Font font = new Font("TimesRoman", Font.PLAIN, (int)(20*this.proportionCaseY));
		FontMetrics metrics = gra.getFontMetrics(font);
		int strX = 23*this.caseWidth/2 - metrics.stringWidth(text) / 2;
		drawable.setFont(font);
		drawable.drawString(text, strX, distYEnd);
	}
	
	// tracer l'indicateur de joueur courant et le string (1P ou 2P) en haut
	public void tracerIndicateur(int place, int tour) {
		if(tour == 1) drawable.setColor(Color.WHITE);
		else drawable.setColor(Color.BLACK);
		
		drawable.fillPolygon(xpoints, ypoints, 3);
	    
	    Font font = new Font("TimesRoman", Font.BOLD + Font.ITALIC, (int)(25*this.proportionCaseY));
	    FontMetrics metrics = gra.getFontMetrics(font);
	    int strX = this.caseWidth/2 - metrics.stringWidth(tour + "P") / 2;
		drawable.setFont(font);
		drawable.drawString(tour + "P", (int)(caseXStart + strX+place*caseWidth), 3 * (this.caseHeight - this.caseWidth) / 16);
	}
	
	public void tracerScore() {
		int yStart = (int)(caseHeight * 1.05);
		drawable.setColor(Color.BLACK);
		drawable.setStroke(new BasicStroke(2));
		int whiteScore = this.pg.getBlanc().getPoint();
		int blackScore = this.pg.getNoir().getPoint();
		int ovalSize = (int)(15 * this.proportionCaseX);
		int ovalMargin = (int)(20 * this.proportionCaseX);
		// dessiner les cercles pour présenter le score de deux joueurs
		// si point obtenue, cercle solide, sinon cercle creux
		for(int i=0; i<5; i++) {
			if(i<whiteScore) drawable.fillOval(10+i*ovalMargin, yStart, ovalSize, ovalSize);
			else drawable.drawOval(10 + i*ovalMargin, yStart, ovalSize, ovalSize);
		}
		for(int i=0; i<5; i++) {
			if(i<blackScore) drawable.fillOval(10 + 23*this.caseWidth - i*ovalMargin - ovalSize, yStart, ovalSize, ovalSize);
			drawable.drawOval(10 + 23*this.caseWidth - i*ovalMargin - ovalSize, yStart, ovalSize, ovalSize);
		}
	}
	
	// Remet la valeur de predictMove1 et predictMove2
	public void setMoveCaseColor() {
		int place = this.pg.getPlayerCourant().getPlace();
		int dist = this.pg.getSelectedCard().getValue();
		this.predictMove1 = place + dist;
		this.predictMove2 = place - dist;
		if(this.pg.getTourCourant() == 1) {
			if(this.predictMove1 > this.pg.getNoirPos() || this.predictMove1 > 22) this.predictMove1 = -1;
			if(this.predictMove2 < 0) this.predictMove2 = -1;
		}else {
			if(this.predictMove2 < this.pg.getBlancPos() || this.predictMove2 < 0) this.predictMove2 = -1;
			if(this.predictMove1 > 22) this.predictMove1 = -1;
		}
		if(this.predictMove1 != -1) {
			if(this.pg.getEnemyCourant().getPlace() != this.predictMove1) {
				this.greenCase1 = new Integer(this.predictMove1);
				this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.GREEN, predictMove1));
			}else {
				this.redCase = new Integer(this.predictMove1);
				this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.RED, predictMove1));
			}
		}
		if(this.predictMove2 != -1) {
			if(this.pg.getEnemyCourant().getPlace() != this.predictMove2) {
				this.greenCase2 = new Integer(this.predictMove2);
				this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.GREEN, predictMove2));
			}else {
				this.redCase = new Integer(this.predictMove2);
				this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.RED, predictMove2));
			}	
		}
		this.repaint();
	}
	
	// Remet la valeur de choseCase
	public void setChoseCase(int i) {
		this.choseCase = i;
		this.repaint();
	}
	
	// Reinitialise la valeur de choseCase
	public void resetChoseCase() {
		this.choseCase = -1;
		this.pg.setDirectionDeplace(0);
		this.repaint();
	}
	
	// Remet la valeur de predictMove1 si on ne peut que retraiter
	public void setRetreatCaseColor() {
		int place = this.pg.getPlayerCourant().getPlace();
		int dist = this.pg.getSelectedCard().getValue();
		if(this.pg.getTourCourant() == 1) this.predictMove1 = place - dist;
		else this.predictMove1 = place + dist;
		if(this.predictMove1 > 22 || this.predictMove1 < 0) this.predictMove1 = -1;
		this.predictMove2 = -1;
		if(this.predictMove1 != -1) 
			this.greenCase1 = new Integer(this.predictMove1);
			this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.GREEN, predictMove1));
		this.repaint();
	}
	
	// Remet la valeur de predictMove1 si on ne peut que attaquer
	public void setAttackCaseColor() {
		int place = this.pg.getEnemyCourant().getPlace();
		this.predictMove1 = place;
		this.predictMove2 = -1;
		this.redCase = new Integer(this.predictMove1);
		this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.RED, place));
		this.repaint();
	}
	
	// Remet la valeur de parryCase si on ne peut que parer
	public void setParryCase() {
		this.parryCase = this.pg.getPlayerCourant().getPlace();
		this.pinkCase = new Integer(this.parryCase);
		this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.PINK, parryCase));
		this.repaint();
	}
	
	// Reinitialiser la valeur de parryCase
	public void resetParryCase() {
		if(this.pinkCase != -1) this.agcs.add(new AnimationGrilleCouleur(Color.PINK, Color.ORANGE, pinkCase));
		this.parryCase = -1;
		this.pinkCase = -1;
		this.repaint();
	}
	
	// Remet la valeur de parryCase et predictMove1 si on peut que parer ou retraiter
	public void setPRCaseColor() {
		int place = this.pg.getPlayerCourant().getPlace();
		int dist = this.pg.getSelectedCard().getValue();
		if(this.pg.getTourCourant() == 1) this.predictMove1 = place - dist;
		else this.predictMove1 = place + dist;
		if(this.predictMove1 > 22 || this.predictMove1 < 0) this.predictMove1 = -1;
		this.parryCase = place;
		if(this.predictMove1 != -1) 
			this.greenCase1 = new Integer(this.predictMove1);
			this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.GREEN, predictMove1));
		this.pinkCase = new Integer(this.parryCase);
		this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.PINK, parryCase));
		this.repaint();
	}
	
	// Remettre la valeur de stayCase
	public void setStayCaseColor() {
		this.stayCase = this.pg.getPlayerCourant().getPlace();
		this.greyCase = new Integer(this.stayCase);
		this.agcs.add(new AnimationGrilleCouleur(Color.ORANGE, Color.GRAY, this.greyCase));
		this.repaint();
	}
	
	// Reinitialiser la valeur de stayCase
	public void resetStayCase() {
		if(this.greyCase != -1) this.agcs.add(new AnimationGrilleCouleur(Color.GRAY, Color.ORANGE, this.greyCase));
		this.stayCase = -1;
		this.greyCase = -1;
		this.repaint();
	}
	
	
	// Reinitialise tous les valeur (sauf selectionne)
	public void resetCaseColor() {
		if(this.greenCase1 != -1 && this.greenCase1 == this.greyCase) {
			this.agcs.add(new AnimationGrilleCouleur(Color.GREEN, Color.GRAY, greenCase1));
			this.greenCase1 = -1;
		}
		if(this.greenCase1 != -1) {
			this.agcs.add(new AnimationGrilleCouleur(Color.GREEN, Color.ORANGE, greenCase1));
			this.greenCase1 = -1;
		}
		if(this.greenCase2 != -1) {
			this.agcs.add(new AnimationGrilleCouleur(Color.GREEN, Color.ORANGE, greenCase2));
			this.greenCase2 = -1;
		}
		if(this.redCase != -1) {
			this.agcs.add(new AnimationGrilleCouleur(Color.RED, Color.ORANGE, redCase));
			this.redCase = -1;
		}
		if(this.pinkCase != -1) {
			this.agcs.add(new AnimationGrilleCouleur(Color.PINK, Color.ORANGE, pinkCase));
			this.pinkCase = -1;
		}
		this.predictMove1 = -1;
		this.predictMove2 = -1;
		this.stayCase = -1;
		this.parryCase = -1;
		this.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int width = getSize().width;
		int height = getSize().height;
		
		this.proportionCaseX = (width-20)/1150.0;
		this.proportionCaseY = height/250.0;
		this.caseXStart = 10;
		
		this.caseHeight = (int)(200*this.proportionCaseY);
		this.caseWidth = (int)(50*this.proportionCaseX);
	
		this.drawable = (Graphics2D) g;
		this.gra = g;
		this.tracerGrille();
		
		if(this.blancCurrentPos != this.pg.getBlancPos()) {
			this.oldBlancPos = this.blancCurrentPos;
			this.blancCurrentPos = this.pg.getBlancPos();
		}
		if(this.noirCurrentPos != this.pg.getNoirPos()) {
			this.oldNoirPos = this.noirCurrentPos;
			this.noirCurrentPos = this.pg.getNoirPos();
		}
		this.tracerJoueur();
		
		// tracer l'indicateur de joueur courant
		if(this.pg.getTourCourant() == 1) tracerIndicateur(this.pg.getBlancPos(), 1);
		else if(this.pg.getTourCourant() == 2) tracerIndicateur(this.pg.getNoirPos(), 2);
		this.tracerScore();
		
		if(	this.pg.getLastAttack().getAt() != AttackType.NONE && 
			(this.pg.getWaitStatus() == 1 || this.pg.getWaitStatus() == 5 || this.pg.getWaitStatus() == 2)) 
			tracerAttackFleche();
		
		this.grillePos = this.getGrillesPositions();
	}
	
	@Override
	public void miseAJour() {
		this.repaint();
	}

	@Override
	public void changeText(String s) {
		// Faire rien s'il recoit ce signal
	}

	public boolean equalStayCase(int nombre) {
		return nombre == this.stayCase;
	}
	
	public void initialiseIndicateurPosition(int place, int player) {
		this.updateIndicateurPosition(0, place, player);
	}

	public void updateIndicateurPosition(int progress, int place, int player) {
		int relativePosition;
		if(progress <= 50) relativePosition = (int)(0.005 * this.caseWidth * progress);
		else relativePosition = (int)(0.5* this.caseWidth - 0.005 * this.caseWidth * progress);

		this.xpoints[0] = (int)(caseXStart+(place+0.25)*caseWidth) + relativePosition;
		this.xpoints[1] = (int)(caseXStart+(place+0.75)*caseWidth) - relativePosition;
		this.xpoints[2] = (int)(caseXStart+(place+0.5)*caseWidth);
		
		// System.out.println("XPoint12 : (" + this.xpoints[0] + ", " + this.xpoints[1] + ")");
		
		int triangleStart = (this.caseHeight - this.caseWidth) / 4;
		int triangleEnd =  7 * (this.caseHeight - this.caseWidth) / 16;
		this.ypoints[0] = triangleStart;
		this.ypoints[1] = triangleStart;
		this.ypoints[2] = triangleEnd;

	    this.repaint();
	}
	
	public void tracerAttackFleche() {
		drawable.setStroke(new BasicStroke(3));
		drawable.setColor(Color.RED);
		int x1, x2;
		int y = this.caseHeight / 2;
		x1 = caseXStart+(int)((this.pg.getBlancPos() + 0.5)*caseWidth);
		x2 = caseXStart+(int)((this.pg.getNoirPos() + 0.5)*caseWidth);
		if(this.pg.getTourCourant() == 1) {
			int triPointsX[] = {x1-5, x1+(int)(0.25*caseWidth), x1+(int)(0.25*caseWidth)};
			int triPointsY[] = {y, (int)(y-0.05*caseHeight), (int)(y+0.05*caseHeight)};
			drawable.fillPolygon(triPointsX, triPointsY, 3);
		}else {
			int triPointsX[] = {x2+5, x2-(int)(0.25*caseWidth), x2-(int)(0.25*caseWidth)};
			int triPointsY[] = {y, (int)(y-0.05*caseHeight), (int)(y+0.05*caseHeight)};
			drawable.fillPolygon(triPointsX, triPointsY, 3);
		}
		drawable.drawLine(x1, y, x2, y);
		
		int lineMiddlePointX = (x1 + x2) / 2;
		String cardValString = this.pg.getLastAttack().getAttValue().getValue() + "";
		String cardNBString = this.pg.getLastAttack().getAttnb() + "";
		
		Font font = new Font("TimesRoman", Font.ITALIC, (int)(25*this.proportionCaseY));
	    FontMetrics metrics = gra.getFontMetrics(font);
	    int valStringPos = lineMiddlePointX - metrics.stringWidth(cardValString) / 2;
	    drawable.drawString(cardValString, valStringPos, y - 10);
	    
	    int nbStringPos = lineMiddlePointX - metrics.stringWidth(cardNBString) / 2;
	    drawable.drawString(cardNBString, nbStringPos, y + (int)(25*this.proportionCaseY));
	    
		drawable.setFont(font);
		
	}
	
	public void calculNewBlancPos(int startCase, int targetCase, int progress) {
		int startCasePos = caseXStart+startCase*caseWidth;
		int	targetCasePos = caseXStart+targetCase*caseWidth;
		this.blancAnimationPos = (int)(startCasePos + (double)((targetCasePos - startCasePos) * progress) / 100.0);
		
		if(progress >= 100) this.oldBlancPos = this.blancCurrentPos;
		this.repaint();
	}
	
	public void calculNewNoirPos(int startCase, int targetCase, int progress) {
		int startCasePos = caseXStart+startCase*caseWidth;
		int	targetCasePos = caseXStart+targetCase*caseWidth;
		this.noirAnimationPos = (int)(startCasePos + (double)((targetCasePos - startCasePos) * progress) / 100.0);
		
		if(progress >= 100) this.oldNoirPos = this.noirCurrentPos;
		this.repaint();
	}
	
	@Override
	public void receiveLoseSignal(int i, String s) {
		
	}

	public void updateAnimationGrille() {
		
		for(int i=0; i<this.agcs.size(); i++) {
			AnimationGrilleCouleur a = this.agcs.get(i);
			a.tictac();
			if(a.estTerminee()) {
				this.agcs.remove(a);
				i--;
			}
		}
		this.repaint();
	}
}
