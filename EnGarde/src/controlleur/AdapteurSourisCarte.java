package controlleur;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class AdapteurSourisCarte extends MouseAdapter {
	public ControlCenter cc;
	
	public AdapteurSourisCarte(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int sourisX = e.getX();
		int sourisY = e.getY();
		cc.deplaceSourisCarte(sourisX, sourisY);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(cc.clicable) {
			int sourisX = e.getX();
			int sourisY = e.getY();
			if(SwingUtilities.isLeftMouseButton(e))
				cc.clicSourisCarte(sourisX, sourisY);
			else if(SwingUtilities.isRightMouseButton(e)){
				cc.clicSourisCarteDroite(sourisX, sourisY);
			}
		}
	}

}
