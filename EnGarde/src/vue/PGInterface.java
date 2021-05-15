package vue;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import controlleur.AdapteurSouris;
import controlleur.ControleMediateur;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.Playground;
import patterns.Observateur;

public class PGInterface implements Runnable{
	public Playground pg;
	public PGGraphique pggraphique;
	public JFrame frame;
	public ControleMediateur cm;
	
	
	public PGInterface(Playground pg) {
		this.pg = pg;
		this.pggraphique = new PGGraphique(this.pg);
		this.cm = new ControleMediateur(this.pg);
		this.cm.ajouteInterfaceUtilisateur(this);
	}
	
	@Override
	public void run() {
		pggraphique.addMouseListener(new AdapteurSouris(this.cm));
		
		this.frame = new JFrame("En Garde !");
		frame.add(pggraphique);
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setVisible(true);
	}
	
	public InterfaceElementPosition getElementByClick(int x, int y) {
		return pggraphique.getElementByClick(x, y);
	}
	
	public static void start(Playground j) {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new PGInterface(j));
	}

}
