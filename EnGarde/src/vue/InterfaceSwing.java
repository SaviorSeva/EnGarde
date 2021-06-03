package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import controlleur.AdapteurSourisCarte;
import controlleur.AdapteurSourisGrille;
import controlleur.AdapteurMainTimer;
import controlleur.ControlCenter;
import modele.ExecPlayground;
import modele.InterfaceElementPosition;
import modele.Playground;

public class InterfaceSwing implements Runnable{
	public Playground pg;
	public GrilleInterface gi;
	public TitreInterface ti;
	public CarteInterface ci;
	public JFrame frame;
	boolean maximized;
	public ControlCenter cc;
	public JButton confirmer, annulerRound, cancel, annulerAction, pleinEcran, save, load, restart, help;
	public Image pleinEcranImage, saveImage, loadImage, restartImage, helpImage;
	public JTextField infoArea;
	public Timer chorno;
	
	public InterfaceSwing(Playground pg, ExecPlayground epg) {
		this.pg = pg;
		this.gi = new GrilleInterface(this.pg);
		this.ti = new TitreInterface();
		this.ci = new CarteInterface(this.pg);
		this.cc = new ControlCenter(epg);
		this.cc.ajouteInterfaceUtilisateur(this);
		this.chorno = new Timer(16, new AdapteurMainTimer(cc));
		try {
			this.pleinEcranImage = ImageIO.read(new File("./res/images/fullscreen.png"));
			this.pleinEcranImage = this.pleinEcranImage.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
			this.saveImage = ImageIO.read(new File("./res/images/save.png"));
			this.saveImage = this.saveImage.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
			this.loadImage = ImageIO.read(new File("./res/images/load.png"));
			this.loadImage = this.loadImage.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
			this.restartImage = ImageIO.read(new File("./res/images/restart.png"));
			this.restartImage = this.restartImage.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
			this.helpImage = ImageIO.read(new File("./res/images/help.png"));
			this.helpImage = this.helpImage.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void repaintAll() {
		gi.repaint();
		ci.repaint();
	}
	
	@Override
	public void run() {
		this.frame = new JFrame("En Garde !");
		
		//pggraphique.addMouseListener(as);
		//pggraphique.addMouseMotionListener(as);
		
		Box boiteTitre = Box.createHorizontalBox();
		Box boiteInfo = Box.createHorizontalBox();
		Box boiteContenu = Box.createVerticalBox();
		
		AdapteurSourisGrille asg = new AdapteurSourisGrille(this.cc);
		AdapteurSourisCarte asc = new AdapteurSourisCarte(this.cc);
		
		// Info Label
		
		// Bouton confirmer
		this.confirmer = new JButton("Confirmer");
		confirmer.setAlignmentX(Component.LEFT_ALIGNMENT);
		confirmer.setFocusable(false);
		boiteInfo.add(confirmer);
		
		// Bouton cancel
		this.cancel = new JButton("Cancel");
		cancel.setAlignmentX(Component.LEFT_ALIGNMENT);
		cancel.setFocusable(false);
		boiteInfo.add(cancel);
		
		// Bouton annuler tour
		this.annulerRound = new JButton("Annuler tour");
		annulerRound.setAlignmentX(Component.LEFT_ALIGNMENT);
		annulerRound.setFocusable(false);
		boiteInfo.add(annulerRound);
		
		// Bouton annuler action
		this.annulerAction = new JButton("Annuler action");
		annulerAction.setAlignmentX(Component.LEFT_ALIGNMENT);
		annulerAction.setFocusable(false);
		boiteInfo.add(annulerAction);
		
		// Bouton pleinEcran
		ImageIcon pleinEcranIcon = new ImageIcon(pleinEcranImage);
		this.pleinEcran = new JButton(pleinEcranIcon);
		pleinEcran.setAlignmentX(Component.LEFT_ALIGNMENT);
		pleinEcran.setFocusable(false);
		pleinEcran.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		pleinEcran.setContentAreaFilled(false);
		
		// Bouton load
		ImageIcon loadIcon = new ImageIcon(loadImage);
		this.load = new JButton(loadIcon);
		load.setAlignmentX(Component.LEFT_ALIGNMENT);
		load.setFocusable(false);
		load.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		load.setContentAreaFilled(false);
		
		// Bouton save
		ImageIcon saveIcon = new ImageIcon(saveImage);
		this.save = new JButton(saveIcon);
		save.setAlignmentX(Component.LEFT_ALIGNMENT);
		save.setFocusable(false);
		save.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		save.setContentAreaFilled(false);
		
		// Bouton restart
		ImageIcon restartIcon = new ImageIcon(restartImage);
		this.restart = new JButton(restartIcon);
		restart.setAlignmentX(Component.LEFT_ALIGNMENT);
		restart.setFocusable(false);
		restart.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		restart.setContentAreaFilled(false);
		
		// Bouton help
		ImageIcon helpIcon = new ImageIcon(helpImage);
		this.help = new JButton(helpIcon);
		help.setAlignmentX(Component.LEFT_ALIGNMENT);
		help.setFocusable(false);
		help.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		help.setContentAreaFilled(false);
		
		// Creer un boite de text pour afficher des infos utiles
		this.infoArea = new JTextField("Some Text");
		this.infoArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
		boiteInfo.add(infoArea);
		
		// Des actionlisteners pour les boutons
		this.confirmer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cc.confirmReceived();
				cc.initialiseZoom();
			}
		});
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cc.clicCancel();
			}
		});
		this.annulerRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while(cc.epg.currentAction.getNBActionValide() != 0) cc.annulerAction();
				cc.annulerRound();
			}
		});
		this.annulerAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cc.annulerAction();
			}
		});
		this.pleinEcran.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				toggleFullscreen();
			}
		});
		this.save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cc.openSaveGameInterface();
			}
		});
		this.load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cc.openLoadGameInterface();
			}
		});
		this.restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cc.restartButtonAction();
				repaintAll();
			}
		});
		this.help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					File pdfFile = new File("./res/engarde.pdf");
					if(pdfFile.exists()) {
						if(Desktop.isDesktopSupported()) {
			                Desktop.getDesktop().open(pdfFile);
			            }else System.err.println("Awt Desktop is not supported!");
			        }
				} catch (Exception ex) {
					ex.printStackTrace();
		        }
			}
		});
		this.infoArea.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		
		// Des listeners pour les interfaces
		gi.addMouseListener(asg);
		ci.addMouseListener(asc);
		ci.addMouseMotionListener(asc);

		boiteTitre.add(ti);
		boiteTitre.add(pleinEcran);
		boiteTitre.add(load);
		boiteTitre.add(save);
		boiteTitre.add(restart);
		boiteTitre.add(help);

		boiteContenu.add(boiteTitre);
		boiteContenu.add(gi);
		gi.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		boiteContenu.add(ci);
		
		frame.add(boiteContenu);
		
		frame.add(boiteInfo, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setVisible(true);
		
		frame.setMinimumSize(new Dimension(400, 400));
		
		this.chorno.start();
	}
	
	public void toggleFullscreen() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		if (maximized) {
			device.setFullScreenWindow(null);
			maximized = false;
		} else {
			device.setFullScreenWindow(frame);
			maximized = true;
		}
	}
	
	public static void start(Playground j, ExecPlayground epg) {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new InterfaceSwing(j, epg));
	}
	
}
