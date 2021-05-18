package vue;

import modele.Playground;

public class Starter {
	public static void main(String[] args) {
		Playground pg = new Playground();
		
		pg.shuffleReste();
		
		pg.restartNewRound();
		
		pg.avance(18);
		
		System.out.println(pg.toString());
		
		PGInterface.start(pg);
	}
}
