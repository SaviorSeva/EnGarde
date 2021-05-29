package vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.*;

import javax.imageio.ImageIO;

import modele.Carte;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.LockedBoolean;
import modele.Playground;
import patterns.Observateur;

public class PGGraphique extends JComponent implements Observateur{
	Graphics2D drawable;
	Graphics gra;
	
	public Playground pg;
	public int caseWidth;
	public int caseHeight;
	public Image[] imgCarte;
	public BufferedImage [] grayscaledCarte;
	
	int width, height;
	
	public double proportionCarte;
	public double proportionZoomCarte;
	public double proportionTitre;
	public double proportionCase;
	
	public Image imgTitle;
	
	public int distYEnd;
	public int caseXStart;
	public int caseYStart;
	public int carteXStart;
	public int carteYStart;
	
	public ArrayList<InterfaceElementPosition> elePos;
	public ArrayList<LockedBoolean> zoomCarte; 
	
	public PGGraphique(Playground pg) {
		pg.ajouteObservateur(this);
		
		this.pg = pg;
		imgCarte = new Image[6];
		this.grayscaledCarte = new BufferedImage[6];
		caseWidth = 50;

		for(int i=0; i<6; i++) {
			File imgFile = new File("./res/images/carte" + i + ".png");
			try {
				imgCarte[i] = ImageIO.read(imgFile);
				imgTitle = ImageIO.read(new File("./res/images/title.png"));
				this.grayscaledCarte[i] = ImageIO.read(imgFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.toGrayscale();
		
		this.caseXStart = 10;
		this.proportionCarte = 1.25;
		this.proportionTitre = 1.0;
		this.proportionCase = 1.0;
		this.proportionZoomCarte = 1.25 * proportionCarte;
		this.zoomCarte = new ArrayList<LockedBoolean>(); 
		initialiseZoom();
		caseYStart = 0;
	}
	
	public void toGrayscale() {
		for(int m=0; m<this.grayscaledCarte.length; m++) {
			BufferedImage image = this.grayscaledCarte[m];
			width = image.getWidth();
			height = image.getHeight();
			for(int i=0; i<height; i++) {
				for(int j=0; j<width; j++) {
					Color c = new Color(image.getRGB(j, i));
					int red = (int)(c.getRed() * 0.299);
					int green = (int)(c.getGreen() * 0.587);
					int blue = (int)(c.getBlue() *0.114);
					Color newColor = new Color(red+green+blue,
												red+green+blue,red+green+blue);
					image.setRGB(j,i,newColor.getRGB());
				}
			}
		}	
	}
	
	public void initialiseZoom() {
		this.zoomCarte.clear();
		for(int i=0; i<5; i++) {
			zoomCarte.add(LockedBoolean.FALSE);
		}
		this.pg.initialiseSelected();
	}
	
	public void resetZoom() {
		for(int i=0; i<this.zoomCarte.size(); i++) {
			LockedBoolean status = this.zoomCarte.get(i);
			if(!status.isLocked()) {
				if(!status.isInvalid()) this.zoomCarte.set(i, LockedBoolean.FALSE);
			}else {
				if(!status.isTrue()) this.zoomCarte.set(i, LockedBoolean.FALSE);
			}
		}
		this.pg.setSelected(this.lockedBooleanListToBooleanList());
	}
	
	public void changeZoomTo(int i, LockedBoolean lb) {
		this.zoomCarte.set(i, lb);
		this.pg.setSelected(i, lb.isLocked() && lb.isTrue());
	}
	
	public void tracerTitre() {
		drawable.drawImage(imgTitle, (int)(width/2 - (200*proportionTitre)),  0, (int)(400*proportionTitre), (int)(200*proportionTitre), null);
	}
	
	public void tracerGrille() {
		for(int i=0; i<23; i++) {
			// Orange background
			drawable.setColor(Color.ORANGE);
			drawable.fillRect(caseXStart+i*caseWidth, caseYStart, caseWidth, caseHeight);
			
			// Black line
			drawable.setColor(Color.BLACK);
			drawable.drawRect(caseXStart+i*caseWidth, caseYStart, caseWidth, caseHeight);
			
			// Number of cases
			drawable.setFont(new Font("TimesRoman", Font.BOLD, (int)(15*proportionCase)));
			drawable.drawString((i+1) + "", caseXStart+i*caseWidth+(int)(caseWidth*0.4), caseYStart+(int)(caseHeight * 0.9));
		}
	}
	
	public void tracerJoueur(int b, int n) {
		// Joueur Blanc
		drawable.setColor(Color.WHITE);
		drawable.fillOval(caseXStart+b*caseWidth, caseYStart + (int)(100*this.proportionCase - caseWidth/2), caseWidth, caseWidth);
		if(pg.getTourCourant() == 1) {
			int xpoints[] = {(int)(caseXStart+(b+0.25)*caseWidth), (int)(caseXStart+(b+0.75)*caseWidth), (int)(caseXStart+(b+0.5)*caseWidth)};
			int triangleStart = caseYStart + (this.caseHeight - this.caseWidth) / 6;
			int triangleEnd = caseYStart + (this.caseHeight - this.caseWidth) / 3;
		    int ypoints[] = {triangleStart, triangleStart, triangleEnd};
		    int npoints = 3;
		    drawable.fillPolygon(xpoints, ypoints, npoints);
		}
			
		
		// Joueur Noir
		drawable.setColor(Color.BLACK);
		drawable.fillOval(caseXStart+n*caseWidth, caseYStart + (int)(100*this.proportionCase - caseWidth/2), caseWidth, caseWidth);
		if(pg.getTourCourant() == 2) {
			int xpoints[] = {(int)(caseXStart+(n+0.25)*caseWidth), (int)(caseXStart+(n+0.75)*caseWidth), (int)(caseXStart+(n+0.5)*caseWidth)};
			int triangleStart = caseYStart + (this.caseHeight - this.caseWidth) / 6;
			int triangleEnd = caseYStart + (this.caseHeight - this.caseWidth) / 3;
		    int ypoints[] = {triangleStart, triangleStart, triangleEnd};
		    int npoints = 3;
		    drawable.fillPolygon(xpoints, ypoints, npoints);
		}
	
		// Distance
		this.distYEnd = caseYStart + (int)(caseHeight * 1.15);
		drawable.setColor(Color.BLACK);
		String text = "Distance entre 2 joueurs : " + pg.getDistance();
		Font font = new Font("TimesRoman", Font.PLAIN, (int)(20*this.proportionCase));
		FontMetrics metrics = gra.getFontMetrics(font);
		int strX = width/2 - metrics.stringWidth(text) / 2;
		drawable.setFont(font);
		drawable.drawString(text, strX, distYEnd);
	}
	
	public void tracerCarte() {
		// Cartes
		this.carteYStart = caseYStart + (int)(caseHeight * 1.3);
		int carteLengthTotal = (int)(680 * this.proportionCarte);
		this.carteXStart = (width - carteLengthTotal) / 2;
		
		int tour = pg.getTourCourant();
		
		String s = "";
		
		ArrayList<Carte> cartes;
		if(tour == 1) cartes = pg.getBlancCartes();
		else cartes = pg.getNoirCartes();
		
		// Paint card border
		drawable.setColor(Color.GREEN);
		drawable.setStroke(new BasicStroke(5));
		drawable.drawRect(carteXStart, carteYStart, (int)(500*this.proportionCarte), (int)(130*this.proportionCarte));
		
		// Paint player's 5 cards
		for(int i=0; i<cartes.size(); i++) {
			int picSelected = cartes.get(i).getValue();
			if(zoomCarte.get(i).isTrue()) {
				drawable.drawImage(	imgCarte[picSelected], 
						carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i) - (int)(40*(this.proportionZoomCarte-this.proportionCarte)), 
						(int)(2 + carteYStart - (63*(this.proportionZoomCarte-this.proportionCarte))), 
						(int)(80*this.proportionZoomCarte), 
						(int)(126*this.proportionZoomCarte), 
						null);
			}else if(!zoomCarte.get(i).isInvalid()){
				drawable.drawImage(	imgCarte[picSelected], 
									carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i), 
									2 + carteYStart, 
									(int)(80*this.proportionCarte), 
									(int)(126*this.proportionCarte), 
									null);
			}else {
				drawable.drawImage(	this.grayscaledCarte[picSelected], 
						carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i), 
						2 + carteYStart, 
						(int)(80*this.proportionCarte), 
						(int)(126*this.proportionCarte), 
						null);
			}
		}
	}
	
	public void tracerReste() {
		// Tracer la carte
		int resteXStart = carteXStart + (int)(500*this.proportionCarte*1.2);
		drawable.drawImage(imgCarte[0], resteXStart, carteYStart, (int)(80*this.proportionCarte), (int)(126*this.proportionCarte), null);
		
		// Tracer nb de carte dans le pile
		Font f2 = new Font("TimesRoman", Font.BOLD, (int)(30*proportionCarte));
		drawable.setFont(f2);
		drawable.setColor(Color.BLACK);
		String nbReste = this.pg.getResteNb() + "";
		int nbResteXStart = resteXStart + (int)(40*this.proportionCarte) - (int)(0.5*gra.getFontMetrics(f2).stringWidth(nbReste));
		drawable.drawString(nbReste, nbResteXStart, carteYStart+(int)(120*this.proportionCarte));
		//drawable.drawString(this.pg.getResteNb() + "", carteEndX, carteYStart + (int)(63*this.proportionCarte));
	}

	@Override
	public void paintComponent(Graphics g) {
		this.drawable = (Graphics2D) g;
		this.gra = g;
		
		this.width = getSize().width;
		this.height = getSize().height;
		
		this.caseWidth = (width-20)/23;
		
		this.proportionCarte = 1.25 * Math.min(width/1200.0, height/800.0);
		this.proportionTitre = 1.0 * Math.min(width/1200.0, height/800.0);
		this.proportionCase = 1.0 * Math.min(width/1200.0, height/800.0);
		this.proportionZoomCarte = 1.25 * proportionCarte;
		
		drawable.clearRect(0, 0, width, height);
		
		this.caseYStart = (int)(200*proportionTitre);
		this.caseHeight = (int)(200*this.proportionCase);
		
		this.tracerTitre();
		this.tracerGrille();
		this.tracerJoueur(this.pg.getBlancPos(), this.pg.getNoirPos());
		this.tracerCarte();
		this.tracerReste();
		
		this.elePos = this.initialiseElePos();
	}
	
	public ArrayList<InterfaceElementPosition> initialiseElePos(){
		ArrayList<InterfaceElementPosition> res = new ArrayList<InterfaceElementPosition>();
		for(int i=0; i<23; i++) {
			InterfaceElementType iet = InterfaceElementType.CASE;
			int x1 = this.caseXStart + this.caseWidth * i;
			int y1 = this.caseYStart;
			int x2 = this.caseXStart + this.caseWidth * (i+1);
			int y2 = this.caseYStart + this.caseHeight;
			InterfaceElementPosition iep = new InterfaceElementPosition(iet, x1, x2, y1, y2, i);
			res.add(iep);
		}
		for(int i=0; i<5; i++) {
			InterfaceElementType iet = InterfaceElementType.CARTE;
			int x1 = this.carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i);
			int y1 = this.carteYStart;
			int x2 = x1 + (int)(80*this.proportionCarte);
			int y2 = this.carteYStart + (int)(126*this.proportionCarte);
			InterfaceElementPosition iep = new InterfaceElementPosition(iet, x1, x2, y1, y2, i);
			res.add(iep);
		}
		return res;
	}
	
	public InterfaceElementPosition getElementByClick(int x, int y) {
		InterfaceElementPosition res = null; 
		boolean changed = false;
		for(InterfaceElementPosition iep : this.elePos) {
			if((iep.getP1().getX() < x) && (iep.getP1().getY() < y) && (iep.getP2().getX() > x) && (iep.getP2().getY() > y)) {
				res = iep;
				changed = true;
				break;
			}
		}
		if(!changed) return new InterfaceElementPosition(InterfaceElementType.BACKGROUND, x, x, y, y, 0);
		return res;
	}
	
	
	public ArrayList<Boolean> lockedBooleanListToBooleanList(){
		ArrayList<Boolean> al = new ArrayList<Boolean>();
		for(int i=0; i<this.zoomCarte.size(); i++){
			al.add(this.zoomCarte.get(i).isTrue());
		}
		return al;
	}

	@Override
	public void miseAJour() {
		this.repaint();
	}

}