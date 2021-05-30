package vue;

import javax.swing.SwingUtilities;

import global.Configuration;
import modele.ExecPlayground;
import modele.Playground;

public class Starter {
	public static void main(String[] args) {
		/*
		Playground pg = new Playground();
		ExecPlayground epg = new ExecPlayground(pg);
		
		epg.shuffleReste();

		PGInterface.start(pg);
		
		epg.restartNewRound();
		
		System.out.println(pg.toString());
		*/
		//InterfaceInitialise.start();
		String iaActive = Configuration.instance().lis("IA");
		Playground pg = new Playground();
		ExecPlayground epg;
		switch (iaActive){
		case "IAAleatoire" :
			System.out.println("RandomIA activated !");
			
			epg = new ExecPlayground(pg, 1);
			
			break;	
		
		default :
				epg = new ExecPlayground(pg, 0);
				
				break;
			
			
		}

		epg.shuffleReste();
		InterfaceSwing.start(pg, epg);
		epg.restartNewRound();
		
	}
}
