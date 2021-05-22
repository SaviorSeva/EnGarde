package vue;

import modele.ExecPlayground;
import modele.Playground;

public class Starter {
	public static void main(String[] args) {
		Playground pg = new Playground();
		ExecPlayground epg = new ExecPlayground(pg);
		
		epg.shuffleReste();

		PGInterface.start(pg);
		
		epg.restartNewRound();
		
		System.out.println(pg.toString());
		
		
	}
}
