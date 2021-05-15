package vue;

import modele.Playground;

public class Starter {
	public static void main(String[] args) {
		Playground pg = new Playground();
		
		pg.shuffleReste();
		
		for(int i=0; i<5; i++) {
			pg.distribuerCarte(1);
			pg.distribuerCarte(2);
		}
		
		pg.avance(18);
		
		System.out.println(pg.toString());
		
		PGInterface.start(pg);
	}
}
