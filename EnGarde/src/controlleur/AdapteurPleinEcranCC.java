package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurPleinEcranCC implements ActionListener{
	ControlCenter cc;
	
	public AdapteurPleinEcranCC(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		cc.interSwing.toggleFullscreen();
	}
}
