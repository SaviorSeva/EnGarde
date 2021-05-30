package vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.Playground;
import patterns.Observateur;

public class GrilleInterface extends JComponent implements Observateur{
	Graphics2D drawable;
	Graphics gra;
	
	public Playground pg;
	
	public int caseWidth;
	public int caseHeight;
	
	public int caseXStart;
	public double proportionCaseX;
	public double proportionCaseY;
	
	public ArrayList<InterfaceElementPosition> grillePos;
	
	int predictMove1, predictMove2;
	int parryCase;
	int choseCase;
	
	public GrilleInterface(Playground pg) {
		this.pg = pg;
		pg.ajouteObservateur(this);
		this.setPreferredSize(new Dimension(50*23 + 20, 250));
		this.proportionCaseX = 1.0;
		this.proportionCaseY = 1.0;
		this.predictMove1 = -1;
		this.predictMove2 = -1;
		this.parryCase = -1;
		this.choseCase = -1;
	}
	
	public boolean equalToHighlighted(int caseNB) {
		return this.predictMove1 == caseNB || this.predictMove2 == caseNB || this.parryCase == caseNB;
	}
	
	public void tracerGrille() {
		for(int i=0; i<23; i++) {
			
			// Orange background

			if(i == parryCase) drawable.setColor(Color.PINK);
			else if(i == this.predictMove1) {
					if(this.pg.getEnemyCourant().getPlace() == i) drawable.setColor(Color.RED);
					else drawable.setColor(Color.GREEN);
			}
			else if (i == this.predictMove2) {
				if(this.pg.getEnemyCourant().getPlace() == i) drawable.setColor(Color.RED);
				else drawable.setColor(Color.GREEN);
			}else drawable.setColor(Color.ORANGE);
			drawable.fillRect(caseXStart+i*caseWidth, 0, caseWidth, caseHeight);
			
			// Black line
			drawable.setColor(Color.BLACK);
			drawable.drawRect(caseXStart+i*caseWidth, 0, caseWidth, caseHeight);
			
			drawable.setStroke(new BasicStroke(1));
			
			// Number of cases
			if(i == choseCase) drawable.setColor(Color.YELLOW);
			drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(15*proportionCaseX)));
			drawable.drawString((i+1) + "", caseXStart+i*caseWidth+(int)(caseWidth*0.4), (int)(caseHeight * 0.9));
		}
		// Highlight selected case
		if(choseCase != -1) {
			drawable.setColor(Color.YELLOW);
			drawable.setStroke(new BasicStroke(3));
			drawable.drawRect(caseXStart+choseCase*caseWidth, 0, caseWidth, caseHeight);
		}
	}
	
	public ArrayList<InterfaceElementPosition> getGrillesPositions(){
		ArrayList<InterfaceElementPosition> res = new ArrayList<InterfaceElementPosition>();
		for(int i=0; i<23; i++) {
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
	
	public void tracerJoueur(int b, int n) {
		// Joueur Blanc
		drawable.setColor(Color.WHITE);
		drawable.fillOval(caseXStart+b*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
		if(this.pg.getTourCourant() == 1) tracerIndicateur(b, 1);
			
		
		// Joueur Noir
		drawable.setColor(Color.BLACK);
		drawable.fillOval(caseXStart+n*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
		if(this.pg.getTourCourant() == 2) tracerIndicateur(n, 2);
	
		// Distance
		int distYEnd = (int)(caseHeight * 1.15);
		drawable.setColor(Color.BLACK);
		String text = "Distance entre 2 joueurs : " + pg.getDistance();
		Font font = new Font("TimesRoman", Font.PLAIN, (int)(20*this.proportionCaseY));
		FontMetrics metrics = gra.getFontMetrics(font);
		int strX = 23*this.caseWidth/2 - metrics.stringWidth(text) / 2;
		drawable.setFont(font);
		drawable.drawString(text, strX, distYEnd);
	}
	
	public void tracerIndicateur(int place, int tour) {
		int xpoints[] = {(int)(caseXStart+(place+0.25)*caseWidth), (int)(caseXStart+(place+0.75)*caseWidth), (int)(caseXStart+(place+0.5)*caseWidth)};
		int triangleStart = (this.caseHeight - this.caseWidth) / 4;
		int triangleEnd =  7 * (this.caseHeight - this.caseWidth) / 16;
	    int ypoints[] = {triangleStart, triangleStart, triangleEnd};
	    int npoints = 3;
	    drawable.fillPolygon(xpoints, ypoints, npoints);
	    
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
		for(int i=0; i<5; i++) {
			if(i<whiteScore) drawable.fillOval(10+i*ovalMargin, yStart, ovalSize, ovalSize);
			else drawable.drawOval(10 + i*ovalMargin, yStart, ovalSize, ovalSize);
		}
		for(int i=0; i<5; i++) {
			if(i<blackScore) drawable.fillOval(10 + 23*this.caseWidth - i*ovalMargin - ovalSize, yStart, ovalSize, ovalSize);
			drawable.drawOval(10 + 23*this.caseWidth - i*ovalMargin - ovalSize, yStart, ovalSize, ovalSize);
		}
	}
	
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
		this.repaint();
	}
	
	public void setChoseCase(int i) {
		this.choseCase = i;
		this.repaint();
	}
	
	public void resetChoseCase() {
		this.choseCase = -1;
		this.pg.setDirectionDeplace(0);
		this.repaint();
	}
	
	public void setRetreatCaseColor() {
		int place = this.pg.getPlayerCourant().getPlace();
		int dist = this.pg.getSelectedCard().getValue();
		if(this.pg.getTourCourant() == 1) this.predictMove1 = place - dist;
		else this.predictMove1 = place + dist;
		if(this.predictMove1 > 22 || this.predictMove1 < 0) this.predictMove1 = -1;
		this.predictMove2 = -1;
		this.repaint();
	}
	
	public void setAttackCaseColor() {
		int place = this.pg.getEnemyCourant().getPlace();
		this.predictMove1 = place;
		this.predictMove2 = -1;
		this.repaint();
	}
	
	public void setParryCase() {
		this.parryCase = this.pg.getPlayerCourant().getPlace();
		this.repaint();
	}
	
	public void resetParryCase() {
		this.parryCase = -1;
		this.repaint();
	}
	
	public void setPRCaseColor() {
		int place = this.pg.getPlayerCourant().getPlace();
		int dist = this.pg.getSelectedCard().getValue();
		if(this.pg.getTourCourant() == 1) this.predictMove1 = place - dist;
		else this.predictMove1 = place + dist;
		if(this.predictMove1 > 22 || this.predictMove1 < 0) this.predictMove1 = -1;
		this.parryCase = place;
		this.repaint();
	}
	
	public void resetCaseColor() {
		this.predictMove1 = -1; 
		this.predictMove2 = -1;
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
		this.tracerJoueur(this.pg.getBlancPos(), this.pg.getNoirPos());
		this.tracerScore();
		
		this.grillePos = this.getGrillesPositions();
	}
	
	@Override
	public void miseAJour() {
		this.repaint();
	}

	
}
