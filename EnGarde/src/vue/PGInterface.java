package vue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import modele.Playground;

public class PGInterface implements Runnable{
	Playground pg;
	PGGraphique pggraphique;
	JFrame frame;
	
	public PGInterface(Playground pg) {
		this.pg = pg;
		this.pggraphique = new PGGraphique(pg);
	}
		
	@Override
	public void run() {
		this.frame = new JFrame("En Garde !");
		frame.add(pggraphique);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 600);
		frame.setVisible(true);
	}
	
	public static void start(Playground j) {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new PGInterface(j));
	}

}
