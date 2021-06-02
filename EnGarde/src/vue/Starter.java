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
		Playground pg;
		ExecPlayground epg;
		switch (iaActive){
			case "None" :
				epg = new ExecPlayground(pg, 0);
				break;
			case "IAAleatoire" :
				System.out.println("RandomIA activated !");
				pg = new Playground(1);
				epg = new ExecPlayground(pg, 1);
				break;
			case "IAProba" :
				System.out.println("ProbaIA activated !");
				pg = new Playground(1);
				epg = new ExecPlayground(pg, 1);
				break;
			case "IAAleatoireVsIAProba" :
				System.out.println("IAale Vs IAprob activated !");
				pg = new Playground(1);
				epg = new ExecPlayground(pg, 1);
				break;
		}

		epg.shuffleReste();
		InterfaceSwing.start(pg, epg);
		epg.restartNewRound();
		
	}
}
