package vue;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import controlleur.ControlCenter;


public class SaveInterface implements Runnable{
	public TitreInterface ti;
	public ControlCenter cc;
	public JFrame frame;
	
	public SaveInterface(ControlCenter cc) {
		this.ti = new TitreInterface();
		this.cc = cc;
	}
	
	@Override
	public void run() {
		this.frame = new JFrame("Sauvgarder !");
		
		JButton confirm = new JButton("Sauvgarder");
		JButton cancel = new JButton("Quitter");
		
		JLabel infoLabel = new JLabel("Saissisez le nom du fichier à sauvgarder");
		infoLabel.setFont(new Font("TimesRoman", Font.PLAIN, 25));

		JTextField nameField = new JTextField("defautSaveFile");
		nameField.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(confirm);
		buttonBox.add(cancel);
		
		Box contentBox = Box.createVerticalBox();
		contentBox.add(infoLabel);
		contentBox.add(nameField);
		
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cc.generateSaveGame(nameField.getText());
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
