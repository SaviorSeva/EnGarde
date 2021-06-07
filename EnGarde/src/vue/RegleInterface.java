package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RegleInterface implements Runnable{
	int currentImageNB;
	BufferedImage[] bi;
	public JButton prev, next, plus;
	public JFrame frame;
	JLabel label;
	
	public RegleInterface() {
		currentImageNB = 0;
		bi = new BufferedImage[5];
		try {
			for(int i=0; i<5; i++)
			bi[i] = ImageIO.read(new File("./res/regleImages/R" + i + ".png"));
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.run();
	}

	@Override
	public void run() {
		this.frame = new JFrame("Règles !");
		
		Box boiteFrame = Box.createVerticalBox();
		Box boiteInfo = Box.createHorizontalBox();
		
		JLabel picLabel = new JLabel(new ImageIcon(bi[0]));
		picLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.label = new JLabel((int)(this.currentImageNB + 1) + " / " + bi.length);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.prev = new JButton("Précedent");
		prev.setAlignmentX(Component.LEFT_ALIGNMENT);
		prev.setFocusable(false);
		boiteInfo.add(prev);
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentImageNB != 0) {
					currentImageNB--;
					picLabel.setIcon(new ImageIcon(bi[currentImageNB]));
					label.setText((currentImageNB + 1) + " / " + bi.length);
				}
			}
		});
		
		this.next = new JButton("Suivant");
		next.setAlignmentX(Component.LEFT_ALIGNMENT);
		next.setFocusable(false);
		boiteInfo.add(next);
		
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentImageNB != 5) {
					currentImageNB++;
					picLabel.setIcon(new ImageIcon(bi[currentImageNB]));
					label.setText((currentImageNB + 1) + " / " + bi.length);
				}
			}
		});
		
		boiteInfo.add(Box.createGlue());

		
		boiteInfo.add(label);
		
		boiteInfo.add(Box.createGlue());
		
		this.plus = new JButton("Plus de règles");
		plus.setAlignmentX(Component.RIGHT_ALIGNMENT);
		plus.setFocusable(false);
		boiteInfo.add(plus);
		
		plus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
		
		
		this.frame.setLayout(new BorderLayout(10, 10));
		
		this.frame.add(picLabel, BorderLayout.CENTER);
		this.frame.add(boiteInfo, BorderLayout.SOUTH);
		
		frame.setSize(950, 950);
		frame.setBackground(Color.WHITE);
		
	}
	
}
