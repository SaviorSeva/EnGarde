package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdapteurTimerAttente implements ActionListener{
	public ControlCenter cc;
	
	public AdapteurTimerAttente(ControlCenter cc) {
		this.cc = cc;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.cc.IAStep();
	}
	
}
