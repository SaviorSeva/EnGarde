package vue;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import controlleur.ControlCenter;
import modele.ExecPlayground;

public class WinInterface implements Runnable{
	JLabel info;
	JButton button;
	public JFrame frame;
	ControlCenter cc;
	
	public WinInterface(ControlCenter cc) {
		this.info = new JLabel();
		this.info.setFont(new Font("TimesRoman", Font.BOLD, 15));
		this.button = new JButton("Got it, start new round");
		this.cc = cc;
		
		this.button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				cc.clicable = true;
				frame.setVisible(false);
				cc.epg.restartNewRound();
				cc.interSwing.repaintAll();
				
			}
		});
	}
	
	public void changeText(String s) {
		this.info.setText(s);
	}
	
	public void setVisible(boolean f) {
		this.frame.setVisible(f);
	}
	
	@Override
	public void run() {
		this.frame = new JFrame("Gagne !");

		frame.setLayout(new BorderLayout(20, 20));
		 
		frame.add(info, BorderLayout.CENTER);
		frame.add(button, BorderLayout.SOUTH);
		
		frame.setSize(550, 200);
		frame.setVisible(false);
	}
}