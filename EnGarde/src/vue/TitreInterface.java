package vue;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import modele.Playground;
import patterns.Observateur;

public class TitreInterface extends JComponent{
	Graphics2D drawable;
	
	public Image imgTitle;
	private double proportionTitre;
	private int width;
	
	public TitreInterface() {
		try {
			imgTitle = ImageIO.read(new File("./res/images/title.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setPreferredSize(new Dimension(400, 200));
	}
	
	public void tracerTitre() {
		drawable.drawImage(imgTitle, (int)(width/2 - (200*proportionTitre)),  0, (int)(400*proportionTitre), (int)(200*proportionTitre), null);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		this.width = getSize().width;
		int height = getSize().height;
	
		this.proportionTitre = 1.0 * Math.min(width/400.0, height/200.0);
		this.drawable = (Graphics2D) g;
		this.tracerTitre();
	}
}
