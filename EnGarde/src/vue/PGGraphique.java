package vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.*;

import javax.imageio.ImageIO;

import modele.Carte;
import modele.Playground;
import patterns.Observateur;

public class PGGraphique extends JComponent implements Observateur{
	public Playground pg;
	public int caseSize;
	public Image[] imgCarte;
	public double proportionCarte;
	public double proportionTitre;
	public double proportionCase;
	public Image imgTitle;
	
	public int distYEnd;
	public int caseYStart;
	public int carteYStart;
	
	public PGGraphique(Playground pg) {
		this.pg = pg;
		imgCarte = new Image[6];
		caseSize = 50;
		pg.ajouteObservateur(this);
		for(int i=0; i<6; i++) {
			File imgFile = new File("./EnGarde/res/images/carte" + i + ".png");
			try {
				imgCarte[i] = ImageIO.read(imgFile);
				imgTitle = ImageIO.read(new File("./EnGarde/res/images/title.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.proportionCarte = 1.25;
		this.proportionTitre = 1.0;
		this.proportionCase = 1.0;
		caseYStart = 0;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D drawable = (Graphics2D) g;
		int width = getSize().width;
		int height = getSize().height;
		
		this.caseSize = (width-20)/23;
		
		this.proportionCarte = 1.25 * Math.min(width/1200.0, height/800.0);
		this.proportionTitre = 1.0 * Math.min(width/1200.0, height/800.0);
		this.proportionCase = 1.0 * Math.min(width/1200.0, height/800.0);
		
		drawable.clearRect(0, 0, width, height);
		
		// Titre 
		drawable.drawImage(imgTitle, (int)(width/2 - (200*proportionTitre)),  0, (int)(400*proportionTitre), (int)(200*proportionTitre), null);
		
		this.caseYStart = (int)(200*proportionTitre);
		
		int caseLength = (int)(200*this.proportionCase);
		
		for(int i=0; i<23; i++) {
			// Orange background
			drawable.setColor(Color.ORANGE);
			drawable.fillRect(10+i*caseSize, caseYStart, caseSize, caseLength);
			
			// Black line
			drawable.setColor(Color.BLACK);
			drawable.drawRect(10+i*caseSize, caseYStart, caseSize, caseLength);
			
			// Number of cases
			drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(15*proportionCase)));
			drawable.drawString((i+1) + "", 10+i*caseSize+(int)(caseSize*0.4), caseYStart+(int)(caseLength * 0.9));
		}
		
		int b = pg.getBlancPos();
		int n = pg.getNoirPos();
		
		// Joueur Blanc
		drawable.setColor(Color.WHITE);
		drawable.fillOval(10+b*caseSize, caseYStart + (int)(100*this.proportionCase - caseSize/2), caseSize, caseSize);
		
		drawable.setColor(Color.BLACK);
		drawable.fillOval(10+n*caseSize, caseYStart + (int)(100*this.proportionCase - caseSize/2), caseSize, caseSize);
		
		// String Distance
		this.distYEnd = caseYStart + (int)(caseLength * 1.15);
		drawable.setColor(Color.BLACK);
		String text = "Distance entre 2 joueurs : " + pg.getDistance();
		Font font = new Font("TimesRoman", Font.PLAIN, (int)(20*this.proportionCase));
		FontMetrics metrics = g.getFontMetrics(font);
		
		int strX = width/2 - metrics.stringWidth(text) / 2;

		drawable.setFont(font);
		drawable.drawString(text, strX, distYEnd);
		
		// Cartes
		this.carteYStart = caseYStart + (int)(caseLength * 1.3);
		int carteLengthTotal = (int)(680 * this.proportionCarte);
		int carteXStart = (width - carteLengthTotal) / 2;
		
		int tour = pg.getTourCourant();
		
		String s = "";
		ArrayList<Carte> cartes;
		if(tour == 1) cartes = pg.getBlancCartes();
		else cartes = pg.getNoirCartes();
		
		// Paint card border
		drawable.setColor(Color.GREEN);
		drawable.setStroke(new BasicStroke(5));
		drawable.drawRect(carteXStart, carteYStart, (int)(500*this.proportionCarte), (int)(130*this.proportionCarte));
		
		// Paint card
		for(int i=0; i<cartes.size(); i++) {
			int picSelected = cartes.get(i).getValue();
			drawable.drawImage(imgCarte[picSelected], carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i), 2 + carteYStart, (int)(80*this.proportionCarte), (int)(126*this.proportionCarte), null);
		}
		
		// Paint reste
		int resteXStart = carteXStart + (int)(500*this.proportionCarte*1.2);
		drawable.drawImage(imgCarte[0], (int)resteXStart, carteYStart, (int)(80*this.proportionCarte), (int)(126*this.proportionCarte), null);
		
		// Paint nb carte reste
		int carteEndX = resteXStart + (int)(80*this.proportionCarte*1.1);
		
		drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(30*proportionCarte)));
		drawable.setColor(Color.BLACK);
		String nbReste = this.pg.getResteNb() + "";
		int nbResteXStart = resteXStart + (int)(40*this.proportionCarte) - (int)(0.5*g.getFontMetrics(font).stringWidth(nbReste));
		drawable.drawString(nbReste, nbResteXStart, carteYStart+(int)(120*this.proportionCarte));
		//drawable.drawString(this.pg.getResteNb() + "", carteEndX, carteYStart + (int)(63*this.proportionCarte));
	}

	@Override
	public void miseAJour() {
		this.repaint();
	}
}
