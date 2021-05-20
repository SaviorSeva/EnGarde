package vue;

import modele.Playground;

public class Starter {
	public static void main(String[] args) {
		Playground pg = new Playground();
		
		pg.shuffleReste();

		PGInterface.start(pg);
		
		pg.restartNewRound();
		
		System.out.println(pg.toString());
		
		
	}
}
