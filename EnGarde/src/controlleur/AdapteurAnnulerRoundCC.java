package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurAnnulerRoundCC implements ActionListener{
	ControlCenter cc;
	
	public AdapteurAnnulerRoundCC(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.cc.annulerAction();
		this.cc.annulerRound();
	}
}
