package vue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.*;

import controlleur.ControleMediateur;
import modele.ExecPlayground;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.LockedBoolean;
import modele.Playground;
import patterns.Observateur;

public class PGInterface implements Runnable{
	public Playground pg;
	public PGGraphique pggraphique;
	public JFrame frame;
	public ControleMediateur cm;
	public JToggleButton confirmer, annuler, cancel;
	public JTextField infoArea;
	 
	
	public PGInterface(Playground pg) {
		this.pg = pg;
		this.pggraphique = new PGGraphique(this.pg);
		this.cm = new ControleMediateur(new ExecPlayground(pg));
		this.cm.ajouteInterfaceUtilisateur(this);
	}
	
	@Override
	public void run() {

		this.frame = new JFrame("En Garde !");
		
		AdapteurSouris as = new AdapteurSouris(this.cm);
		
		pggraphique.addMouseListener(as);
		pggraphique.addMouseMotionListener(as);
	
		Box boiteInfo = Box.createHorizontalBox();
		
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
		
		frame.add(pggraphique);
		frame.add(boiteInfo, BorderLayout.SOUTH);
		
		chrono.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setVisible(true);
	}
	
	public InterfaceElementPosition getElementByClick(int x, int y) {
		return pggraphique.getElementByClick(x, y);
	}
	
	public void repaint() {
		this.pggraphique.repaint();
	}
	
	public void changeZoomTo(int i, LockedBoolean b) {
		this.pggraphique.changeZoomTo(i, b);
	}
	
	public ArrayList<LockedBoolean> getZoomCarte(){
		return this.pggraphique.zoomCarte;
	}
	
	public void initialiseZoom() {
		this.pggraphique.initialiseZoom();
	}
	
	public void resetZoom() {
		this.pggraphique.resetZoom();
	}
	
	public static void start(Playground j) {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new PGInterface(j));
	}

}
