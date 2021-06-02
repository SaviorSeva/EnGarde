package vue;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import controlleur.ControlCenter;

public class LoadInterface implements Runnable{
	public TitreInterface ti;
	public ControlCenter cc;
	public JFrame frame;
	
	public LoadInterface(ControlCenter cc) {
		this.ti = new TitreInterface();
		this.cc = cc;
	}
	
	public String[] getAllFileNames() {
		File folder = new File("./res/savefile");
		File[] listOfFiles = folder.listFiles();

		ArrayList<String> al = new ArrayList<String>();
		for(int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()) al.add(listOfFiles[i].getName());
		}
		return al.toArray(new String[al.size()]);
	}
	
	@Override
	public void run() {
		this.frame = new JFrame("Charger !");
		
		JButton confirm = new JButton("Charger");
		JButton cancel = new JButton("Quitter");
		
		JLabel infoLabel = new JLabel("Choisissez un fichier à charger :");
		infoLabel.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(confirm);
		buttonBox.add(cancel);
		
		String items[] = this.getAllFileNames();
		
		JComboBox<String> fileComboBox = new JComboBox<String>(items);
		fileComboBox.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		
		Box contentBox = Box.createVerticalBox();
		contentBox.add(infoLabel);
		contentBox.add(fileComboBox);
		
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cc.loadGame(fileComboBox.getSelectedItem().toString());
			}
		});
		
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		
		frame.setLayout(new BorderLayout(20, 20));
		 
		frame.add(this.ti, BorderLayout.NORTH);
		frame.add(contentBox, BorderLayout.CENTER);
		frame.add(buttonBox, BorderLayout.SOUTH);
		
		frame.setSize(600, 500);
		frame.setVisible(true);
	}
	
	
}
