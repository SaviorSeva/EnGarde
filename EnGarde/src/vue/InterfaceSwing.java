package vue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import controlleur.AdapteurCancelCC;
import controlleur.AdapteurConfirmCC;
import controlleur.AdapteurSourisCarte;
import controlleur.AdapteurSourisGrille;
import controlleur.AdapteurTimerAttente;
import controlleur.ControlCenter;
import modele.ExecPlayground;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.Playground;

public class InterfaceSwing implements Runnable{
	public Playground pg;
	public GrilleInterface gi;
	public TitreInterface ti;
	public CarteInterface ci;
	public JFrame frame;
	public ControlCenter cc;
	public JToggleButton confirmer, annuler, cancel;
	public JTextField infoArea;
	//public Timer chorno;
	
	public InterfaceSwing(Playground pg, ExecPlayground epg) {
		this.pg = pg;
		this.gi = new GrilleInterface(this.pg);
		this.ti = new TitreInterface();
		this.ci = new CarteInterface(this.pg);
		this.cc = new ControlCenter(epg);
		this.cc.ajouteInterfaceUtilisateur(this);
		//this.chorno = new Timer(1000, new AdapteurTimerAttente(cc));
	}
	
	public ArrayList<InterfaceElementPosition> getGrillePos() {
		return this.gi.getGrillesPositions();
	}
	
	public void repaintGrille() {
		gi.repaint();
	}
	
	public void repaintCarte() {
		ci.repaint();
	}

	@Override
	public void run() {
		this.frame = new JFrame("En Garde !");
		
		//pggraphique.addMouseListener(as);
		//pggraphique.addMouseMotionListener(as);
	
		Box boiteInfo = Box.createHorizontalBox();
		Box boiteContenu = Box.createVerticalBox();
		
		AdapteurSourisGrille asg = new AdapteurSourisGrille(this.cc);
		AdapteurSourisCarte asc = new AdapteurSourisCarte(this.cc);
		
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
		
		this.confirmer.addActionListener(new AdapteurConfirmCC(this.cc));
		this.cancel.addActionListener(new AdapteurCancelCC(this.cc));
		
		gi.addMouseListener(asg);
		ci.addMouseListener(asc);
		ci.addMouseMotionListener(asc);

		boiteContenu.add(ti);
		boiteContenu.add(gi);
		gi.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		boiteContenu.add(ci);
		
		frame.add(boiteContenu);
		
		frame.add(boiteInfo, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setVisible(true);
		
		// this.chorno.start();
	}
	
	
	public static void start(Playground j, ExecPlayground epg) {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new InterfaceSwing(j, epg));
	}
	
}
