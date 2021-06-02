package vue;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import modele.ExecPlayground;
import modele.Playground;

public class InterfaceInitialise implements Runnable{
	public TitreInterface ti;
	public JFrame frame;
	public JToggleButton startHuman, startAI;
	
	public InterfaceInitialise() {
		this.ti = new TitreInterface();
	}
	
	@Override
	public void run() {
		this.frame = new JFrame("En Garde !");
		JRadioButton aiOption1 = new JRadioButton("AI Random");
        JRadioButton aiOption2 = new JRadioButton("AI Probabilité");
        JRadioButton aiOption3 = new JRadioButton("AI Arbre MinMax");
        
        JRadioButton aiOption4 = new JRadioButton("Play as white");
        JRadioButton aiOption5 = new JRadioButton("Play as black");

        this.startHuman = new JToggleButton("Start with human");
        this.startAI = new JToggleButton("Start with an AI");
		
        ButtonGroup aiDiffGroup = new ButtonGroup();
        aiDiffGroup.add(aiOption1);
        aiDiffGroup.add(aiOption2);
        aiDiffGroup.add(aiOption3);
        aiOption1.setSelected(true);
        
        ButtonGroup aiPlayerGroup = new ButtonGroup();
        aiPlayerGroup.add(aiOption4);
        aiPlayerGroup.add(aiOption5);
        aiOption4.setSelected(true);
        
        frame.setLayout(new FlowLayout());
        
        Box frameBox = Box.createVerticalBox();
        Box titleBox =  Box.createHorizontalBox(); 
        Box settingBox = Box.createHorizontalBox();
        Box humanBox = Box.createVerticalBox();
        Box aiBox = Box.createVerticalBox();
        Box aiSettingBox = Box.createHorizontalBox();
        Box aiDiffBox = Box.createVerticalBox();
        Box aiPlayerBox = Box.createVerticalBox();
        Box POneBox = Box.createHorizontalBox();
        Box PTwoBox = Box.createHorizontalBox();
        Box aiPBox = Box.createHorizontalBox();
        
        JLabel POneL = new JLabel("1P Name : ");
        JLabel PTwoL = new JLabel("2P Name : ");
        JLabel aiPL = new JLabel("Player Name : ");
        
        JTextField POneText = new JTextField();
        JTextField PTwoText = new JTextField();
        JTextField aiPText = new JTextField();
        
        POneBox.add(POneL);
        POneBox.add(POneText);
        
        PTwoBox.add(PTwoL);
        PTwoBox.add(PTwoText);
        
        aiPBox.add(aiPL);
        aiPBox.add(aiPText);
    
        aiDiffBox.add(aiOption1);
        aiDiffBox.add(aiOption2);
        aiDiffBox.add(aiOption3);
        
        aiPlayerBox.add(aiOption4);
        aiPlayerBox.add(aiOption5);
        
        aiSettingBox.add(aiDiffBox);
        aiSettingBox.add(aiPlayerBox);
        
        aiBox.add(startAI);
        startAI.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        aiBox.add(aiSettingBox);
        aiBox.add(aiPBox);
        
        humanBox.add(startHuman);
        startHuman.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        humanBox.add(POneBox);
        humanBox.add(PTwoBox);
        
        settingBox.add(humanBox);
        settingBox.add(aiBox);
        
        frameBox.add(ti);
        frameBox.add(settingBox);
        
        frame.add(frameBox);
        
        frame.setSize(800, 450);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void start() {
		// Swing s'exécute dans un thread séparé. En aucun cas il ne faut accéder directement
		// aux composants graphiques depuis le thread principal. Swing fournit la méthode
		// invokeLater pour demander au thread de Swing d'exécuter la méthode run d'un Runnable.
		SwingUtilities.invokeLater(new InterfaceInitialise());
	}
}
