package vue;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controlleur.AdapteurStarter;

public class InterfaceInitialise implements Runnable{
	public TitreInterface ti;
	public JFrame frame;
	public JButton startHuman, startAI;
	public JTextField POneText;
	public JTextField PTwoText;
	public JTextField aiPText;
	public JRadioButton[] aiPlayerSelections;
	public ArrayList<JRadioButton> aiSelections;
	public ButtonGroup aiDiffGroup;
	
	public InterfaceInitialise() {
		this.ti = new TitreInterface();
		POneText = new JTextField();
        PTwoText = new JTextField();
        aiPText = new JTextField();
        aiPlayerSelections = new JRadioButton[2];
        aiDiffGroup = new ButtonGroup();
        this.aiSelections = new ArrayList<JRadioButton>();
	}
	
	@Override
	public void run() {
		this.frame = new JFrame("En Garde !");
		JRadioButton aiOption1 = new JRadioButton("AI Random");
        JRadioButton aiOption2 = new JRadioButton("AI Probabilité");
        JRadioButton aiOption3 = new JRadioButton("AI Arbre MinMax");
        JRadioButton aiOption6 = new JRadioButton("AI Random VS AI Proba");
        aiSelections.add(aiOption1);
        aiSelections.add(aiOption2);
        aiSelections.add(aiOption3);
        aiSelections.add(aiOption6);
        
        JRadioButton aiOption4 = new JRadioButton("Play as white");
        JRadioButton aiOption5 = new JRadioButton("Play as black");
        
        aiPlayerSelections[0] = aiOption4;
        aiPlayerSelections[1] = aiOption5;
 
        this.startHuman = new JButton("Start with human");
        this.startHuman.addActionListener(new AdapteurStarter(false, this));
        this.startAI = new JButton("Start with an AI");
        this.startAI.addActionListener(new AdapteurStarter(true, this));
        
        aiDiffGroup.add(aiOption1);
        aiDiffGroup.add(aiOption2);
        aiDiffGroup.add(aiOption3);
        aiDiffGroup.add(aiOption6);
        aiOption1.setSelected(true);
        
        ButtonGroup aiPlayerGroup = new ButtonGroup();
        aiPlayerGroup.add(aiOption4);
        aiPlayerGroup.add(aiOption5);
        aiOption4.setSelected(true);
        
        frame.setLayout(new FlowLayout());
        
        Box frameBox = Box.createVerticalBox();
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
        
        POneBox.add(POneL);
        POneBox.add(POneText);
        
        PTwoBox.add(PTwoL);
        PTwoBox.add(PTwoText);
        
        aiPBox.add(aiPL);
        aiPBox.add(aiPText);
    
        aiDiffBox.add(aiOption1);
        aiDiffBox.add(aiOption2);
        aiDiffBox.add(aiOption3);
        aiDiffBox.add(aiOption6);
        
        aiPlayerBox.add(aiOption4);
        aiPlayerBox.add(aiOption5);
        
        aiSettingBox.add(aiDiffBox);
        aiSettingBox.add(aiPlayerBox);
        
        aiBox.add(startAI);
        aiBox.add(Box.createVerticalStrut(20));
        startAI.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        aiBox.add(aiSettingBox);
        aiBox.add(aiPBox);
        
        humanBox.add(startHuman);
        humanBox.add(Box.createVerticalStrut(20));
        startHuman.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        humanBox.add(POneBox);
        humanBox.add(PTwoBox);
        
        settingBox.add(humanBox);
        humanBox.setPreferredSize(new Dimension(200, 100));
        settingBox.add(Box.createHorizontalStrut(100));
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
