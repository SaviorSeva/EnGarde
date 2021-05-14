package controlleur;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdapteurSouris extends MouseAdapter {
	public ControleMediateur cm;
	
	public AdapteurSouris(ControleMediateur cm) {
		this.cm = cm;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int sourisX = e.getX();
		int sourisY = e.getY();
		System.out.println("(" + sourisX + ", " + sourisY + ")");
		cm.clicSouris(sourisX, sourisY);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseMoved(e);
	}
}
