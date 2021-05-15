package controlleur;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class AdapteurSouris extends MouseAdapter {
	public ControleMediateur cm;
	
	public AdapteurSouris(ControleMediateur cm) {
		this.cm = cm;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int sourisX = e.getX();
		int sourisY = e.getY();
		if(SwingUtilities.isLeftMouseButton(e))
			cm.clicSouris(sourisX, sourisY);
		else if(SwingUtilities.isRightMouseButton(e)){
			cm.clicSourisDroite(sourisX, sourisY);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int sourisX = e.getX();
		int sourisY = e.getY();
		//System.out.println("(" + sourisX + ", " + sourisY + ")");
		cm.deplaceSouris(sourisX, sourisY);
	}
	
	
}
