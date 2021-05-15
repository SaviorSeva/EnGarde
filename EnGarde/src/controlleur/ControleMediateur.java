package controlleur;

import java.awt.Point;
import java.util.ArrayList;

import modele.InterfaceElementPosition;
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
		if(iep != null) System.out.println(iep.toString());
	}
}
