package controlleur;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.Timer;

import modele.Carte;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.LockedBoolean;
import modele.Playground;
import vue.PGInterface;

public class ControleMediateur {
	Playground pg;
	PGInterface pginter;
	ArrayList<Point> elementPos; 
	
	public ControleMediateur(Playground pg) {
		this.pg = pg;
	}
	
	public void ajouteInterfaceUtilisateur(PGInterface pgi) {
		this.pginter = pgi;
	}

	public void clicSouris(int x, int y) {
		InterfaceElementPosition iep = pginter.getElementByClick(x, y);
		if(iep.getEle() == InterfaceElementType.CARTE && !pginter.getZoomCarte().get(iep.getNombre()).isInvalid()) {
			this.pginter.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDTRUE);
			
			int dist = pg.getDistance();
			ArrayList<Carte> cartes = this.pg.getCurrentPlayerCards();
			for(int i=0; i<cartes.size(); i++) {
				if(i != iep.getNombre()) {
					if(dist != this.pg.getSelectedCard().getValue())
						this.pginter.changeZoomTo(i, LockedBoolean.INVALID);
					else {
						if(cartes.get(i).getValue() != cartes.get(iep.getNombre()).getValue())
							this.pginter.changeZoomTo(i, LockedBoolean.INVALID);
					}
				}
			}
			
			
		}else if(iep.getEle() == InterfaceElementType.CASE) {
			if(pg.getTourCourant() == 1) {
				int place = pg.getBlancPos();
				if(iep.getNombre() > place) pg.setDirectionDeplace(1);
				else pg.setDirectionDeplace(2);
			}else {
				int place = pg.getNoirPos();
				if(iep.getNombre() > place) pg.setDirectionDeplace(2);
				else pg.setDirectionDeplace(1);
			}
			System.out.println(this.pg.getDirectionDeplace());
		}
		System.out.println(iep.toString());
		this.pginter.repaint();
	}

	public void deplaceSouris(int sourisX, int sourisY) {
		InterfaceElementPosition iep = pginter.getElementByClick(sourisX, sourisY);
		
		if(iep.getEle() == InterfaceElementType.CARTE) {
			LockedBoolean status = pginter.getZoomCarte().get(iep.getNombre());
			if(!status.isLocked() && !status.isInvalid())
				this.pginter.changeZoomTo(iep.getNombre(), LockedBoolean.TRUE);
		}
		else if (iep.getEle() == InterfaceElementType.BACKGROUND) {
			this.pginter.resetZoom();
		}
		this.pginter.repaint();
	}

	public void clicSourisDroite(int sourisX, int sourisY) {
		InterfaceElementPosition iep = pginter.getElementByClick(sourisX, sourisY);
		if(iep.getEle() == InterfaceElementType.CARTE) {
			for(int i=0; i<pginter.getZoomCarte().size(); i++) {
				if(i != iep.getNombre()) pginter.changeZoomTo(i, LockedBoolean.FALSE);
				else this.pginter.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDFALSE);
			}
			
		}
		this.pginter.repaint();
	}
	
	public void confirmReceived() {
		this.pg.confirmReceived();
	}
	
	public void resetZoom(){
		this.pginter.resetZoom();
	}
	
	public void initialiseZoom() {
		this.pginter.initialiseZoom();
	}
	
	public void clicCancel() {
		this.pg.cancelReceived();
	}
}
