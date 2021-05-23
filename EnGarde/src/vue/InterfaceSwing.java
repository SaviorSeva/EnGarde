package vue;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import controlleur.ControleMediateur;
import modele.ExecPlayground;
import modele.Playground;

public class InterfaceSwing implements Runnable{
	public Playground pg;
	public GrilleInterface gi;
	public TitreInterface ti;
	public CarteInterface ci;
	public JFrame frame;
	public ControleMediateur cm;
	public JToggleButton confirmer, annuler, cancel;
	public JTextField infoArea;
	
	public InterfaceSwing(Playground pg) {
		this.pg = pg;
		this.gi = new GrilleInterface(this.pg);
		this.ti = new TitreInterface();
		this.ci = new CarteInterface(this.pg);
		this.cm = new ControleMediateur(new ExecPlayground(pg));
		// this.cm.ajouteInterfaceUtilisateur(this);
	}

	@Override
	public void run() {
		this.frame = new JFrame("En Garde !");
		
		AdapteurSouris as = new AdapteurSouris(this.cm);
		
		//pggraphique.addMouseListener(as);
		//pggraphique.addMouseMotionListener(as);
	
		Box boiteInfo = Box.createHorizontalBox();
		Box boiteContenu = Box.createVerticalBox();
		
		// Info Label
		
		// Bouton confirmer
		this.confirmer = new JToggleButton("Confirmer");
		confirmer.setAlignmentX(Component.LEFT_ALIGNMENT);
		confirmer.setFocusable(false);
		boiteInfo.add(confirmer);
		
		// Bouton cancel
		this.cancel = new JToggleButton("Cancel");
		cancel.setAlignmentX(Component.LEFT_ALIGNMENT);
		cancel.setFocusable(false);
		boiteInfo.add(cancel);
		
		this.infoArea = new JTextField("Some Text");
		this.infoArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
		boiteInfo.add(infoArea);
		
		Timer chrono = new Timer(16, new AdapteurTimerAttente(this.cm));
		this.confirmer.addActionListener(new AdapteurConfirmer(this.cm));
		this.cancel.addActionListener(new AdapteurCancel(this.cm));
		
		boiteContenu.add(ti);
		boiteContenu.add(gi);
		gi.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		boiteContenu.add(ci);
		

		frame.add(boiteContenu);
		
		frame.add(boiteInfo, BorderLayout.SOUTH);
		
		chrono.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setVisible(true);
	}
	
	public static void start(Playground j) {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new InterfaceSwing(j));
	}
	
}
