package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurTimerAttente implements ActionListener{
	public ControleMediateur cm;
	
	public AdapteurTimerAttente(ControleMediateur cm) {
		this.cm = cm;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//System.out.println("Timer");
	}
	
}
