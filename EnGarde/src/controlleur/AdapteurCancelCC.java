package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controlleur.ControleMediateur;

public class AdapteurCancelCC implements ActionListener{
	ControlCenter cc;
	
	public AdapteurCancelCC(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.cc.clicCancel();
	}
}