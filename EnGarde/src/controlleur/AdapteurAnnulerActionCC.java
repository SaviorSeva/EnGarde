package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurAnnulerActionCC implements ActionListener{
	ControlCenter cc;
	
	public AdapteurAnnulerActionCC(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.cc.annulerAction();
	}
}