package controlleur;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class AdapteurSourisGrille extends MouseAdapter {
	public ControlCenter cc;
	
	public AdapteurSourisGrille(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int sourisX = e.getX();
		int sourisY = e.getY();
		if(SwingUtilities.isLeftMouseButton(e))
			this.cc.clicSourisGrille(sourisX, sourisY);
		else if(SwingUtilities.isRightMouseButton(e)){
			cc.clicSourisGrilleDroite(sourisX, sourisY);
		}	
	}
}
