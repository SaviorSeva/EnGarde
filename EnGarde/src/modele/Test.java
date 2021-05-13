package modele;

public class Test {
	public static void main(String args[]) {
		Playground pg = new Playground();
		
		pg.shuffleReste();
		
		for(int i=0; i<5; i++) {
			pg.distribuerCarte(1);
			pg.distribuerCarte(2);
		}
		
		System.out.println(pg.toString());
	}
}
