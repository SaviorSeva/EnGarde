package vue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import modele.Carte;
import modele.Playground;
import patterns.Observateur;

public class PGGraphique extends JComponent implements Observateur{
	public Playground pg;
	public int caseSize;
	public Image[] imgCarte;
	public double proportion;
	
	public PGGraphique(Playground pg) {
		this.pg = pg;
		imgCarte = new Image[6];
		caseSize = 50;
		pg.ajouteObservateur(this);
		for(int i=0; i<6; i++) {
			File imgFile = new File("./res/images/kaart" + i + ".gif");
			try {
				imgCarte[i] = ImageIO.read(imgFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.proportion = 1.5;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D drawable = (Graphics2D) g;
		int width = getSize().width;
		int height = getSize().height;
		
		this.caseSize = 50;
		
		drawable.clearRect(0, 0, width, height);
		
		for(int i=0; i<23; i++) {
			drawable.setColor(Color.ORANGE);
			drawable.fillRect(i*caseSize, 0, caseSize, 200);
			
			drawable.setColor(Color.BLACK);
			drawable.drawRect(i*caseSize, 0, caseSize, 200);
			
			drawable.setFont(new Font("TimesRoman", Font.PLAIN, 15));
			
			drawable.drawString((i+1) + "", i*caseSize+(int)(caseSize*0.4), 180);
		}
		
		int b = pg.getBlancPos();
		int n = pg.getNoirPos();
		
		drawable.setColor(Color.WHITE);
		drawable.fillOval(b*caseSize, 80, caseSize, caseSize);
		
		drawable.setColor(Color.BLACK);
		drawable.fillOval(n*caseSize, 80, caseSize, caseSize);
		
		int tour = pg.getTourCourant();
		
		String s = "";
		ArrayList<Carte> cartes;
		if(tour == 1) cartes = pg.getBlancCartes();
		else cartes = pg.getNoirCartes();
		
		// Paint card
		for(int i=0; i<cartes.size(); i++) {
			int picSelected = cartes.get(i).getValue();
			drawable.drawImage(imgCarte[picSelected], 10 + (int)(100*this.proportion*i), 300, (int)(80*this.proportion), (int)(126*this.proportion), null);
		}
		
		// Paint reste
		drawable.drawImage(imgCarte[0], (int)910, 300, (int)(80*this.proportion), (int)(126*this.proportion), null);
		
		drawable.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		drawable.setColor(Color.BLACK);
		drawable.drawString(this.pg.getResteNb() + "", 910 + (int)(80*this.proportion), 300 + (int)(126*this.proportion/2) );
	}

	@Override
	public void miseAJour() {
		this.repaint();
	}
}
