package controlleur;

import java.awt.Point;
import java.util.ArrayList;

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
	
	public void removeCard() {
		
	}

	public void clicSouris(int x, int y) {
		InterfaceElementPosition iep = pginter.getElementByClick(x, y);
		if(iep.getEle() == InterfaceElementType.CARTE) {
			this.pginter.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDTRUE);
		}
		System.out.println(iep.toString());
		this.pginter.repaint();
	}

	public void deplaceSouris(int sourisX, int sourisY) {
		InterfaceElementPosition iep = pginter.getElementByClick(sourisX, sourisY);
		
		if(iep.getEle() == InterfaceElementType.CARTE) {
			LockedBoolean status = pginter.getZoomCarte().get(iep.getNombre());
			if(!status.isLocked())
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
			this.pginter.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDFALSE);
		}
		this.pginter.repaint();
	}
}
