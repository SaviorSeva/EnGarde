package vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.border.StrokeBorder;

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
	
	public GrilleInterface(Playground pg) {
		this.pg = pg;
		pg.ajouteObservateur(this);
		this.setPreferredSize(new Dimension(50*23 + 20, 250));
		this.proportionCaseX = 1.0;
		this.proportionCaseY = 1.0;
	}
	
	public void tracerGrille() {
		for(int i=0; i<23; i++) {
			// Orange background
			drawable.setColor(Color.ORANGE);
			drawable.fillRect(caseXStart+i*caseWidth, 0, caseWidth, caseHeight);
			
			// Black line
			drawable.setColor(Color.BLACK);
			drawable.drawRect(caseXStart+i*caseWidth, 0, caseWidth, caseHeight);
			
			// Number of cases
			drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(15*proportionCaseX)));
			drawable.drawString((i+1) + "", caseXStart+i*caseWidth+(int)(caseWidth*0.4), (int)(caseHeight * 0.9));
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
		if(pg.getTourCourant() == 1) {
			int xpoints[] = {(int)(caseXStart+(b+0.25)*caseWidth), (int)(caseXStart+(b+0.75)*caseWidth), (int)(caseXStart+(b+0.5)*caseWidth)};
			int triangleStart = (this.caseHeight - this.caseWidth) / 6;
			int triangleEnd =  (this.caseHeight - this.caseWidth) / 3;
		    int ypoints[] = {triangleStart, triangleStart, triangleEnd};
		    int npoints = 3;
		    drawable.fillPolygon(xpoints, ypoints, npoints);
		}
			
		
		// Joueur Noir
		drawable.setColor(Color.BLACK);
		drawable.fillOval(caseXStart+n*caseWidth, (int)(this.caseHeight - this.caseWidth)/2, caseWidth, caseWidth);
		if(pg.getTourCourant() == 2) {
			int xpoints[] = {(int)(caseXStart+(n+0.25)*caseWidth), (int)(caseXStart+(n+0.75)*caseWidth), (int)(caseXStart+(n+0.5)*caseWidth)};
			int triangleStart = (this.caseHeight - this.caseWidth) / 6;
			int triangleEnd = (this.caseHeight - this.caseWidth) / 3;
		    int ypoints[] = {triangleStart, triangleStart, triangleEnd};
		    int npoints = 3;
		    drawable.fillPolygon(xpoints, ypoints, npoints);
		}
	
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
	
	public void tracerScore() {
		int yStart = (int)(caseHeight * 1.05);
		drawable.setColor(Color.BLACK);
		drawable.setStroke(new BasicStroke(2));
		int whiteScore = this.pg.getBlanc().getPoint();
		int blackScore = this.pg.getNoir().getPoint();
		for(int i=0; i<5; i++) {
			if(i<whiteScore) drawable.fillOval(10+i*20, yStart, 15, 15);
			else drawable.drawOval(10 + i*20, yStart, 15, 15);
		}
		for(int i=0; i<5; i++) {
			if(i<blackScore) drawable.fillOval(23*caseWidth -10 - i*20, yStart, 15, 15);
			else drawable.drawOval(23*caseWidth -10 - i*20, yStart, 15, 15);
		}
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
