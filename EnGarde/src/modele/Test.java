package modele;

public class Test {
	public static void main(String args[]) {
		Playground pg = new Playground();
		ExecPlayground epg = new ExecPlayground(pg, 0);
		for(int i=0; i<5; i++) {
			epg.distribuerCarte(1);
			epg.distribuerCarte(2);
		}
		
		
		
		System.out.println(pg.toString());
	}
}
