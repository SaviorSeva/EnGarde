package vue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import modele.Carte;
import modele.Playground;
import patterns.Observateur;

public class PGGraphique extends JComponent implements Observateur{
	public Playground pg;
	public int caseSize;
	
	public PGGraphique(Playground pg) {
		this.pg = pg;
		pg.ajouteObservateur(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D drawable = (Graphics2D) g;
		int width = getSize().width;
		int height = getSize().height;
		
		this.caseSize = 40;
		
		drawable.clearRect(0, 0, width, height);
		
		for(int i=0; i<23; i++) {
			drawable.setColor(Color.ORANGE);
			drawable.fillRect(i*caseSize, 0, caseSize, 200);
			
			drawable.setColor(Color.BLACK);
			drawable.drawRect(i*caseSize, 0, caseSize, 200);
		}
		
		int b = pg.getBlancPos();
		int n = pg.getNoirPos();
		
		drawable.setColor(Color.WHITE);
		drawable.fillOval(b*40, 80, 40, 40);
		
		drawable.setColor(Color.BLACK);
		drawable.fillOval(n*40, 80, 40, 40);
		
		int tour = pg.getTourCourant();
		
		String s = "";
		ArrayList<Carte> cartes;
		if(tour == 1) cartes = pg.getBlancCartes();
		else cartes = pg.getNoirCartes();
		
		for(int i=0; i<cartes.size(); i++) {
			s = s + cartes.get(i).getValue() + " ";
		}
		
		drawable.setColor(Color.BLACK);
		drawable.drawString(s, 20, 300);
		
	}

	@Override
	public void miseAJour() {
		this.repaint();
	}
}
