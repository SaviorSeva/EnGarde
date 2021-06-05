package controlleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import global.Configuration;
import modele.ExecPlayground;
import modele.Playground;
import vue.InterfaceInitialise;
import vue.InterfaceSwing;

public class AdapteurStarter implements ActionListener{
	boolean isAIGame;
	InterfaceInitialise interIni;
	
	public AdapteurStarter(boolean isAIGame, InterfaceInitialise interIni) {
		this.isAIGame = isAIGame;
		this.interIni = interIni;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		initiailiseParameters();
		loadAndStartMainInterface();
		this.interIni.frame.setVisible(false);
	}

	private void initiailiseParameters() {
		FileWriter fw;
		try {
			fw = new FileWriter("./res/defaut.cfg", false);	
		
			if(!this.isAIGame) {
				// Human vs Human
				fw.write("IA=None\n");
				fw.write("Joueur=Blanc\n");
				String name1P = this.interIni.POneText.getText();
				String name2P = this.interIni.PTwoText.getText();
				String playerName = this.interIni.aiPText.getText();
				if(name1P.equals("")) name1P="1P";
				if(name2P.equals("")) name2P="2P";
				if(playerName.equals("")) playerName="Player";
				fw.write("1PName=" + name1P + "\n");
				fw.write("2PName=" + name2P + "\n");
				fw.write("PlayerName=" + playerName + "\n");
				
				fw.close();
			} else {
				int aiType=-1, playerSide=-1;
				for(int i=0; i<this.interIni.aiSelections.size(); i++) {
					if(this.interIni.aiSelections.get(i).isSelected()) {
						aiType = i;
						break;
					}
				}
				for(int i=0; i<2; i++) {
					if(this.interIni.aiPlayerSelections[i].isSelected()){
						playerSide = i;
						break;
					}
				}
				System.out.println(aiType);
				switch(aiType) {
				case 0:
					fw.write("IA=IAAleatoire\n");
					break;
				case 1:
					fw.write("IA=IAProba\n");
					break;
				case 2:
					// TODO Arbre minmax
					// fw.write("IA=IAMinMax\n");
					break;
				case 3:
					fw.write("IA=IAAleatoireVsIAProba\n");
					break;
				}
				if(playerSide == 0) fw.write("Joueur=Blanc\n");
				else fw.write("Joueur=Noir\n");
				String playerName = this.interIni.aiPText.getText();
				if(playerName.equals("")) playerName="Player";
				
				fw.write("1PName=1P\n");
				fw.write("2PName=2P\n");
				
				fw.write("PlayerName=" + playerName + "\n");
				fw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadAndStartMainInterface() {
		String iaActive = Configuration.instance().lis("IA");
		String joueur = Configuration.instance().lis("Joueur");
		int jou = joueur.equals("Blanc") ? 1 : (joueur.equals("Noir") ? 2 : -1);
		String name1P = Configuration.instance().lis("1PName");
		String name2P = Configuration.instance().lis("2PName");
		String playerName = Configuration.instance().lis("PlayerName");
		
		if (jou == -1 && !iaActive.equals("None")) {
			if(jou == -1) System.err.println("Parametre Joueur Erreur !");
			else System.err.println("Parametre IA Erreur !");
		}else {
			Playground pg;
			ExecPlayground epg;
			switch (iaActive){
				case "IAAleatoire" :
					System.out.println("RandomIA activated !");
					pg = new Playground(jou, playerName, "IAAleatoire");
					epg = new ExecPlayground(pg, 1);

					break;
				case "IAProba" :
					System.out.println("ProbaIA activated !");
					pg = new Playground(jou, playerName, "IAProbabilite");
					epg = new ExecPlayground(pg, 2);

					break;
				case "IAAleatoireVsIAProba" :
					System.out.println("IAale Vs IAprob activated !");
					pg = new Playground(jou, "IAAleatoire", "IAProbabilite");
					epg = new ExecPlayground(pg, 3);

					break;
				default :
					pg = new Playground(0, name1P, name2P);
					epg = new ExecPlayground(pg, 0);

					break;
			}

			epg.shuffleReste();
			InterfaceSwing.start(pg, epg);
			epg.restartNewRound();
		}
	}
}
