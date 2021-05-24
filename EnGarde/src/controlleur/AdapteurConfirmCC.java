package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurConfirmCC implements ActionListener{
	ControlCenter cc;
	
	public AdapteurConfirmCC(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		cc.confirmReceived();
		cc.initialiseZoom();
	}
}
