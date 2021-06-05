package controlleur;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import modele.InterfaceElementPosition;

public class AdapteurSourisGrille extends MouseAdapter {
	public ControlCenter cc;
	
	public AdapteurSourisGrille(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(cc.clicable) {
			int sourisX = e.getX();
			int sourisY = e.getY();
			InterfaceElementPosition iep = this.cc.getCaseByClick(sourisX, sourisY);
			if(SwingUtilities.isLeftMouseButton(e))
				
				if(this.cc.hasCaseSelected() && iep.getNombre() == this.cc.interSwing.gi.choseCase) this.cc.confirmReceived();
				else this.cc.tapezSourisGrille(sourisX, sourisY);
			else if(SwingUtilities.isRightMouseButton(e)){
				cc.tapezSourisGrilleDroite(sourisX, sourisY);
			}	
		}
	}
}
