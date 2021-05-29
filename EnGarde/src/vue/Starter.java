package vue;

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
		String iaActive = Configuration.instance().lis("IA");
		Playground pg = new Playground();
		ExecPlayground epg;
		switch (iaActive){
			case "None" :
				epg = new ExecPlayground(pg, 0);
				
				epg.shuffleReste();

				InterfaceSwing.start(pg, epg);
				
				epg.restartNewRound();
				break;
			case "IAAleatoire" :
				System.out.println("RandomIA activated !");
				
				epg = new ExecPlayground(pg, 1);
				
				epg.shuffleReste();

				InterfaceSwing.start(pg, epg);
				
				epg.restartNewRound();
				break;
			case "IAProba" :
				System.out.println("ProbaIA activated !");

				epg = new ExecPlayground(pg, 2);

				epg.shuffleReste();

				InterfaceSwing.start(pg, epg);

				epg.restartNewRound();
				break;
		}
		
		
	}
}
