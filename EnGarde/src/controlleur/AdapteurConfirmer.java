package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurConfirmer implements ActionListener{
	ControleMediateur cm;
	
	public AdapteurConfirmer(ControleMediateur cm) {
		this.cm = cm;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		cm.confirmReceived();
		cm.initialiseZoom();
	}
}
