package vue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controlleur.ControleMediateur;

public class AdapteurCancel implements ActionListener{
	ControleMediateur cm;
	
	public AdapteurCancel(ControleMediateur cm) {
		this.cm = cm;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.cm.clicCancel();
	}
}