package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurMainTimer implements ActionListener{
	public ControlCenter cc;
	
	public AdapteurMainTimer(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.cc.updateAnimations();
	}
	
}
