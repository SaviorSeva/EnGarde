package vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import modele.Carte;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.LockedBoolean;
import modele.Playground;
import patterns.Observateur;

public class CarteInterface extends JComponent implements Observateur{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Graphics2D drawable;
	Graphics gra;
	
	public BufferedImage[] imgCarte;
	public BufferedImage[] grayscaledCarte;
	
	public Playground pg;
	public int carteXStart;
	
	public double proportionCarte;
	public double proportionZoomCarte;
	
	public ArrayList<InterfaceElementPosition> cartePosition;
	public ArrayList<LockedBoolean> zoomCarte; 
	
	public CarteInterface(Playground pg) {
		this.pg = pg;
		pg.ajouteObservateur(this);
		this.imgCarte = new BufferedImage[6];
		this.grayscaledCarte = new BufferedImage[6];
		
		// on introduit 6 images de cartes dont 5 recto et 1 verso de carte
		for(int i=0; i<6; i++) {
			File imgFile = new File("./res/images/carte" + i + ".png");
			try {
				imgCarte[i] = ImageIO.read(imgFile);
				this.grayscaledCarte[i] = ImageIO.read(imgFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.generateGrayscaledCards();
		this.zoomCarte = new ArrayList<LockedBoolean>(); 
		initialiseZoom();
		this.setPreferredSize(new Dimension(800, 200));
	}
	
	public void initialiseZoom() {
		this.zoomCarte.clear();
		for(int i=0; i<5; i++) {
			zoomCarte.add(LockedBoolean.FALSE);
		}
		this.pg.initialiseSelected();
	}
	
	public void changeZoomTo(int i, LockedBoolean lb) {
		this.zoomCarte.set(i, lb);
		this.pg.setSelected(i, lb.isLocked() && lb.isTrue());
	}
	
	public void resetZoom() {
		for(int i=0; i<this.zoomCarte.size(); i++) {
			LockedBoolean status = this.zoomCarte.get(i);
			// si c'est une case non bloqu?? et valide(soit FALSE et TRUE), on remet le case i de liste zoomCarte ?? False
			if(!status.isLocked()) {
				if(!status.isInvalid()) this.zoomCarte.set(i, LockedBoolean.FALSE);
			}else {
				// si c'est une case bloqu?? et contient False(soit LOCKEDFALSE et LOCKEDTRUE), on remet ?? False
				if(!status.isTrue()) this.zoomCarte.set(i, LockedBoolean.FALSE);
			}
		}
		this.pg.setSelected(this.lockedBooleanListToBooleanList());
	}
	
	public ArrayList<Boolean> lockedBooleanListToBooleanList(){
		ArrayList<Boolean> al = new ArrayList<Boolean>();
		// il copie zoomCarte ?? al puis le renvoie 
		for(int i=0; i<this.zoomCarte.size(); i++){
			al.add(this.zoomCarte.get(i).isTrue());
		}
		return al;
	}
	
	// g??n??rer des cartes ?? ??chelle grise
	public void generateGrayscaledCards() {
		for(int m=0; m<this.grayscaledCarte.length; m++) {
			BufferedImage image = this.grayscaledCarte[m];
			int w = image.getWidth();
			int h = image.getHeight();
			for(int i=0; i<h; i++) {
				for(int j=0; j<w; j++) {
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
	
	public void tracerCarte(int width, int height) {
		// Cartes
		int carteLengthTotal = (int)(680 * this.proportionCarte);
		this.carteXStart = (width - carteLengthTotal) / 2;
		
		int tour = pg.getTourCourant();
		
		ArrayList<Carte> cartes;
		if(tour == 1) cartes = pg.getBlancCartes();
		else cartes = pg.getNoirCartes();
		
		// Paint card border
		if(this.pg.getTourCourant() == 1) drawable.setColor(Color.GREEN);
		else drawable.setColor(Color.PINK);
		drawable.setStroke(new BasicStroke(5));
		drawable.drawRect(carteXStart, 2, (int)(500*this.proportionCarte), (int)(126*this.proportionZoomCarte));
		
		// Paint player's 5 cards
		for(int i=0; i<cartes.size(); i++) {
			// Mettre les cartes impossible de choisir en INVALID si on est en etape 1 ou 4 (demande de parer un attaque)
			if(pg.getWaitStatus() == 1) {
				int attVal = this.pg.getLastAttack().getAttValue().getValue();
				for(int m=0; m<this.pg.getCurrentPlayerCards().size(); m++) if(cartes.get(m).getValue() != attVal) this.changeZoomTo(m, LockedBoolean.INVALID);
			}
			if(pg.getWaitStatus() == 4) {
				for(int m=0; m<this.pg.getCurrentPlayerCards().size(); m++) if(cartes.get(m).getValue() != this.pg.getDistance()) this.changeZoomTo(m, LockedBoolean.INVALID);
			}
			int picSelected = cartes.get(i).getValue();
			// on affiche les cartes s??lectionn??es 
			if(zoomCarte.get(i).isTrue()) {
				drawable.drawImage(	imgCarte[picSelected], 
						carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i) - (int)(40*(this.proportionZoomCarte-this.proportionCarte)), 
						2, 
						(int)(80*this.proportionZoomCarte), 
						(int)(126*this.proportionZoomCarte), 
						null);
			}else if(!zoomCarte.get(i).isInvalid()){
				// afficher les cartes avec couleurs
				drawable.drawImage(	imgCarte[picSelected], 
									carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i), 
									2 + (int)(63*(this.proportionZoomCarte-this.proportionCarte)), 
									(int)(80*this.proportionCarte), 
									(int)(126*this.proportionCarte), 
									null);
			}else {
				// afficher les cartes gris
				drawable.drawImage(	this.grayscaledCarte[picSelected], 
						carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i), 
						2 + (int)(63*(this.proportionZoomCarte-this.proportionCarte)), 
						(int)(80*this.proportionCarte), 
						(int)(126*this.proportionCarte), 
						null);
			}
		}
	}
	
	public void tracerReste() {
		// Tracer la carte
		int resteXStart = carteXStart + (int)(500*this.proportionCarte*1.2);
		drawable.drawImage(imgCarte[0], resteXStart, (int)(63*(this.proportionZoomCarte-this.proportionCarte)), (int)(80*this.proportionCarte), (int)(126*this.proportionCarte), null);
		
		// Tracer nb de carte dans le pile
		Font f2 = new Font("TimesRoman", Font.BOLD, (int)(30*proportionCarte));
		drawable.setFont(f2);
		drawable.setColor(Color.BLACK);
		String nbReste = this.pg.getResteNb() + "";
		int nbResteXStart = resteXStart + (int)(40*this.proportionCarte) - (int)(0.5*gra.getFontMetrics(f2).stringWidth(nbReste));
		drawable.drawString(nbReste, nbResteXStart, (int)(120*this.proportionCarte));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		this.drawable = (Graphics2D) g;
		this.gra = g;
		
		int width = getSize().width;
		int height = getSize().height;
		
		// initialiser la porportion ?? 1.1
		this.proportionCarte = 1.1 * Math.min(width/800.0, height/200.0);
		this.proportionZoomCarte = 1.25 * proportionCarte;
		
		this.cartePosition = this.getCartePosition();
		this.tracerCarte(width, height);
		this.tracerReste();
	}
	
	// Retourne les positions de cartes et stocker dans cartePosition
	public ArrayList<InterfaceElementPosition> getCartePosition() {
		ArrayList<InterfaceElementPosition> res = new ArrayList<InterfaceElementPosition>();
		// x1 y1 est le point de gauche en haute, x2 y2 est le point de droite en bas
		for(int i=0; i<5; i++) {
			InterfaceElementType iet = InterfaceElementType.CARTE;
			int x1 = carteXStart + (int)(10*this.proportionCarte) + (int)(100*this.proportionCarte*i);
			int y1 = 2 + (int)(63*(this.proportionZoomCarte-this.proportionCarte));
			int x2 = x1 + (int)(80*this.proportionCarte);
			int y2 = y1 + (int)(126*this.proportionCarte);
			InterfaceElementPosition iep = new InterfaceElementPosition(iet, x1, x2, y1, y2, i);
			res.add(iep);
		}
		return res;
	}

	@Override
	public void miseAJour() {
		this.repaint();
	}

	@Override
	public void changeText(String s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void receiveLoseSignal(int i, String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveRestartSignal() {
		// TODO Auto-generated method stub
		
	}

}
