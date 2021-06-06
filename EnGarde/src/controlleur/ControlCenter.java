package controlleur;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import IAs.IA;
import IAs.IAAleatoire;
import IAs.IAMinmax;
import IAs.IAProba;
import animations.Animation;
import animations.AnimationJoueur;
import animations.AnimationTriangle;
import modele.Action;
import modele.Attack;
import modele.AttackType;
import modele.Carte;
import modele.ExecPlayground;
import modele.InterfaceElementPosition;
import modele.InterfaceElementType;
import modele.LockedBoolean;
import modele.Playground;
import patterns.Observateur;
import vue.InterfaceInitialise;
import vue.InterfaceSwing;
import vue.LoadInterface;
import vue.SaveInterface;
import vue.WinInterface;

public class ControlCenter implements Observateur{
	public Playground pg;
	public ExecPlayground epg;
	public InterfaceSwing interSwing;
	public InterfaceInitialise interIni;
	public WinInterface wi;
	ArrayList<Point> elementPos;
	public boolean clicable;
	IA ia;
	IA iaAlea;
	IA iaProba;
	LoadInterface li;
	SaveInterface si;
	AnimationTriangle at;
	ArrayList<AnimationJoueur> anims;
	
	public ControlCenter(ExecPlayground epg) {
		this.epg = epg;
		this.pg = this.epg.getPg();
		this.epg.ajouteObservateur(this);
//		switch(this.epg.getIAType()) {
//		case 0:
//			this.ia = null;
//			break;
//		case 1:
//			this.ia = new IAAleatoire(this.epg, this.pg);
//			break;
//		case 2:
//			this.ia = new IAProba(this.epg, this.pg);
//			break;
//		case 4:
//			this.ia = new IAMinmax(this.epg, this.pg);
//			break;
//		}
		this.anims = new ArrayList<AnimationJoueur>();
		this.clicable = true;
		this.at = new AnimationTriangle(30, this);
		this.wi = new WinInterface(this);
	}
	
	public void ajouteInterfaceUtilisateur(InterfaceSwing ifs) {
		this.interSwing = ifs;
	}
	
	// Faire un step d'IA
	public void IAStep() {
		switch(this.epg.getIAType()) {
			case 0:
				this.ia = null;
				break;
			case 1:
				this.ia = new IAAleatoire(this.epg, this.pg, this);
				break;
			case 2:
				this.ia = new IAProba(this.epg, this.pg, this);
				break;
			case 3:
				this.iaAlea = new IAAleatoire(this.epg, this.pg, this);
				this.iaProba = new IAProba(this.epg, this.pg, this);
				break;
			case 4:
				this.ia = new IAMinmax(this.epg, this.pg, this);
				break;
		}
		if(this.epg.getIAType()==3) {
			if(epg.isIaAleatoireRound()){
				System.out.println("iaAlea");
				System.out.println("");
				iaAlea.iaStep();
				System.out.println("iaAlea fin");
			}else if(epg.isIaProbaRound()){
				System.out.println("iaProba");
				System.out.println("");
				iaProba.iaStep();
				System.out.println("iaProba fin");
			}
		}else ia.iaStep();

	}
	
	public void resetZoom(){
		this.interSwing.ci.resetZoom();
	}
	
	public void initialiseZoom() {
		this.interSwing.ci.initialiseZoom();
	}
	
	// Retourne l'info sur le case cliqué
	public InterfaceElementPosition getCaseByClick(int x, int y) {
		InterfaceElementPosition res = null; 
		boolean changed = false;
		for(InterfaceElementPosition iep : this.interSwing.gi.grillePos) {
			if((iep.getP1().getX() < x) && (iep.getP1().getY() < y) && (iep.getP2().getX() >= x) && (iep.getP2().getY() >= y)) {
				res = iep;
				changed = true;
				break;
			}
		}
		if(!changed) return new InterfaceElementPosition(InterfaceElementType.BACKGROUND, x, x, y, y, 0);
		return res;
	}
	
	public InterfaceElementPosition getCardByClick(int x, int y) {
		InterfaceElementPosition res = null; 
		boolean changed = false;
		for(InterfaceElementPosition iep : this.interSwing.ci.cartePosition) {
			if((iep.getP1().getX() < x) && (iep.getP1().getY() < y) && (iep.getP2().getX() >= x) && (iep.getP2().getY() >= y)) {
				res = iep;
				changed = true;
				break;
			}
		}
		if(!changed) return new InterfaceElementPosition(InterfaceElementType.BACKGROUND, x, x, y, y, 0);
		return res;
	}
	
	// les procédures à faire après clique de souris sur les grilles
	public void tapezSourisGrille(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCaseByClick(sourisX, sourisY);
		if(this.epg.getSelectedCard() != null) {
			if(iep.getEle() == InterfaceElementType.CASE && this.interSwing.gi.equalToHighlighted(iep.getNombre())) {
				System.out.println("Exe");
				if(epg.pg.getTourCourant() == 1) {
					// si tour blanc, et si on clique sur droite de place, on avance
					// sinon on retraite
					int place = epg.pg.getBlancPos();
					if(iep.getNombre() > place) epg.pg.setDirectionDeplace(1);
					else if(iep.getNombre() < place) epg.pg.setDirectionDeplace(2);
					else epg.pg.setDirectionDeplace(3);
				}else {
					// si tour noir, et si on clique sur droite de place, on retraite
					// sinon on avance
					int place = epg.pg.getNoirPos();
					if(iep.getNombre() > place) epg.pg.setDirectionDeplace(2);
					else if(iep.getNombre() < place) epg.pg.setDirectionDeplace(1);
					else epg.pg.setDirectionDeplace(3);
				}
				this.interSwing.gi.setChoseCase(iep.getNombre());
			}
		}else {
			if(iep.getEle() == InterfaceElementType.CASE && this.interSwing.gi.equalStayCase(iep.getNombre())) {
				epg.pg.setDirectionDeplace(3);
				this.interSwing.gi.setChoseCase(iep.getNombre());
			}
		}
		this.interSwing.repaintGrille();
	}
	public void tapezSourisGrille(int nombre) {
		if(this.epg.getSelectedCard() != null) {
			if(nombre == this.pg.getPlayerCourant().getPlace()) {
				System.out.println("Exe");
				if(epg.pg.getTourCourant() == 1) {
					// si tour blanc, et si on clique sur droite de place, on avance
					// sinon on retraite
					int place = epg.pg.getBlancPos();
					if(nombre > place) epg.pg.setDirectionDeplace(1);
					else if(nombre < place) epg.pg.setDirectionDeplace(2);
					else epg.pg.setDirectionDeplace(3);
				}else {
					// si tour noir, et si on clique sur droite de place, on retraite
					// sinon on avance
					int place = epg.pg.getNoirPos();
					if(nombre > place) epg.pg.setDirectionDeplace(2);
					else if(nombre < place) epg.pg.setDirectionDeplace(1);
					else epg.pg.setDirectionDeplace(3);
				}
				this.interSwing.gi.setChoseCase(nombre);
			}
		}else {
			epg.pg.setDirectionDeplace(3);
			this.interSwing.gi.setChoseCase(nombre);
		}
		this.interSwing.repaintGrille();
	}

	// Deselectionner un grille
	public void tapezSourisGrilleDroite(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCaseByClick(sourisX, sourisY);
		if(iep.getEle() == InterfaceElementType.CASE) {
			this.interSwing.gi.resetChoseCase();
		}else {
			this.resetGrille();
			this.resetCartes();
			this.interSwing.repaintAll();
		}
	}

	public void resetGrille() {
		this.interSwing.gi.resetCaseColor();
		this.interSwing.gi.resetChoseCase();
		this.interSwing.gi.resetParryCase();
	}

	// les procédure à faire après clique de souris sur carte
	public void clicSourisCarte(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCardByClick(sourisX, sourisY);
		if(iep.getEle() == InterfaceElementType.CARTE && !this.interSwing.ci.zoomCarte.get(iep.getNombre()).isInvalid()) {
			if(iep.getNombre() < this.pg.getPlayerCourant().getCartes().size()) this.interSwing.ci.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDTRUE);
			
			int dist = epg.getDistance();
			ArrayList<Carte> cartes = this.epg.getCurrentPlayerCards();
			for(int i=0; i<cartes.size(); i++) {
				Carte selected = this.epg.getSelectedCard();
				if(selected != null) {
					if(i != iep.getNombre()) {
						if(dist != this.epg.getSelectedCard().getValue())
							this.interSwing.ci.changeZoomTo(i, LockedBoolean.INVALID);
						else {
							if(cartes.get(i).getValue() != cartes.get(iep.getNombre()).getValue())
								this.interSwing.ci.changeZoomTo(i, LockedBoolean.INVALID);
						}
					}
				}
			}
			switch(this.pg.getWaitStatus()) {
			case 1:
				// Parry direct attack
				boolean valEQ = this.epg.getSelectedCard().getValue() == this.pg.getLastAttack().getAttValue().getValue();
				boolean nbEQ = this.epg.getNBSelectedCard() == this.pg.getLastAttack().getAttnb();
				if(nbEQ) this.interSwing.gi.setParryCase();
				break;
			case 2:
				// Retreat only
				this.interSwing.gi.setRetreatCaseColor();
				break;
			case 3:
				// Move or direct attack
				if(this.epg.getNBSelectedCard() == 1) this.interSwing.gi.setMoveCaseColor();
				else {
					Carte selected = this.epg.getSelectedCard();
					if(selected != null) this.interSwing.gi.setAttackCaseColor();
				}
				break;
			case 4:
				// Indirect attack only
				if(this.epg.getSelectedCard() != null && this.epg.getSelectedCard().getValue() == this.pg.getDistance()) {
					this.interSwing.gi.resetStayCase();
					this.interSwing.gi.setAttackCaseColor();
				}
				break;
			case 5:
				// Parry indirect attack or retreat
				boolean nbEQ1 = this.epg.getNBSelectedCard() == 1;
				valEQ = this.epg.getSelectedCard().getValue() == this.pg.getLastAttack().getAttValue().getValue();
				nbEQ = this.epg.getNBSelectedCard() == this.pg.getLastAttack().getAttnb();
				this.interSwing.gi.resetCaseColor();
				this.interSwing.gi.resetChoseCase();
				if(nbEQ1) {
					if(valEQ && nbEQ) this.interSwing.gi.setPRCaseColor();
					else this.interSwing.gi.setRetreatCaseColor();
				}else if(valEQ && nbEQ) {
					this.interSwing.gi.setParryCase();
				}
				break;
			default:
				System.err.println("Clic Carte Error Line 136");
				break;
			}
		}
		this.interSwing.repaintCarte();
	}
	
	// Reinitialiser le zoom
	public void resetCartes() {
		for(int i=0; i<interSwing.ci.zoomCarte.size(); i++) {
			interSwing.ci.changeZoomTo(i, LockedBoolean.FALSE);
		}
	}

	// les procédure après clique de souris droite sur la carte
	public void clicSourisCarteDroite(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCardByClick(sourisX, sourisY);
		if(iep.getEle() == InterfaceElementType.CARTE) {
			
			for(int i=0; i<interSwing.ci.zoomCarte.size(); i++) {
				if(i != iep.getNombre()) interSwing.ci.changeZoomTo(i, LockedBoolean.FALSE);
				else this.interSwing.ci.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDFALSE);
			}
			this.interSwing.gi.resetCaseColor();
			this.interSwing.gi.resetChoseCase();
		}else {
			this.resetGrille();
			this.resetCartes();
			this.interSwing.repaintAll();
		}
		this.interSwing.repaintCarte();
		if(this.pg.getWaitStatus() == 4) this.interSwing.gi.setStayCaseColor();
	}

	// les procédures quand le souris est sur la carte ou sur background
	public void deplaceSourisCarte(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCardByClick(sourisX, sourisY);
		
		if(iep.getEle() == InterfaceElementType.CARTE) {
			LockedBoolean status = interSwing.ci.zoomCarte.get(iep.getNombre());
			if(!status.isLocked() && !status.isInvalid())
				interSwing.ci.changeZoomTo(iep.getNombre(), LockedBoolean.TRUE);
		}
		else if (iep.getEle() == InterfaceElementType.BACKGROUND) {
			interSwing.ci.resetZoom();
		}
		this.interSwing.repaintCarte();
	} 
	
	// Clic sur bouton confirmer
	public void confirmReceived() {
		switch (this.pg.getWaitStatus()){
		case 2:
			// Retreat
			addAnimationJoueur();
			break;
		case 3:
			if(this.epg.getSelectedCard().getValue() != this.pg.getDistance() || this.pg.getDirectionDeplace() != 1) {
				// Movement
				addAnimationJoueur();
			}
			break;
		case 5:
			// Retreat
			if(this.interSwing.gi.choseCase != this.pg.getPlayerCourant().getPlace())
				addAnimationJoueur();
			break;
		}
		
		this.epg.confirmReceived();
		
		if(!this.epg.isIaRound()) {
			this.interSwing.ci.initialiseZoom();
			this.interSwing.gi.resetCaseColor();
			this.interSwing.gi.resetChoseCase();
			this.interSwing.gi.resetParryCase();
			if(this.pg.getWaitStatus() == 4)this.interSwing.gi.setStayCaseColor();
			this.interSwing.repaintAll();
		}
		
	}
	
	public void addAnimationJoueur() {
		int startPos = this.pg.getPlayerCourant().getPlace();
		int endPos = this.interSwing.gi.choseCase;
		AnimationJoueur aj = new AnimationJoueur(this.pg.getTourCourant() ,startPos, endPos, this);
		System.out.println("joueur = " + this.pg.getTourCourant() +", startPos = " + startPos + ", endPos = " + endPos);
		this.anims.add(aj);
		if(this.anims.size() == 1) {
			if(this.pg.getTourCourant() == 1) this.interSwing.gi.setInBlancAnimation(true);
			else this.interSwing.gi.setInNoirAnimation(true);
		}
	}
	
	// Clic sur bouton cancel
	public void clicCancel() {
		this.epg.cancelReceived();
		this.interSwing.ci.initialiseZoom();
		this.interSwing.gi.resetCaseColor();
		this.interSwing.gi.resetChoseCase();
		this.interSwing.gi.resetParryCase();
	}

	@Override
	public void miseAJour() {
		this.IAStep();
	}

	// Clic sur bouton annuler tour
	public void annulerRound() {
		if(this.epg.hist.listAction.size() == 0) {
			System.out.println("There is no more round to recover.");
		}else {
			Action lastAction = this.epg.hist.removeLastAction();
			String lastActions[] = lastAction.getActionString().split(",");
			this.returnCardToPool(lastActions[3]);
			this.resetAttack(lastActions[2]);
			this.resetMove(lastActions[1]);
			this.resetParry(lastActions[0]);
			Attack attBL;
			if(this.epg.hist.listAction.size() != 0) {
				Action actBeforeLast = this.epg.hist.listAction.get(this.epg.hist.listAction.size() - 1);
				String actionsBL[] = actBeforeLast.getActionString().split(",");
				attBL = this.getAttackBeforeLast(actionsBL[2]);
			}else attBL = new Attack(AttackType.NONE, null, 0);
			this.epg.changetTour();
			this.pg.setWaitStatus(0);
			this.pg.initialiseSelected();
			this.pg.setDirectionDeplace(0);
			this.epg.currentAction.clear();
			this.epg.roundStart(attBL);
			this.interSwing.repaintAll();
		}

	}

	// Prendre des carte de joueur et le remettre au pile
	public void returnCardToPool(String s) {
		if(s.substring(0, 2).equals("GE")) {
			for(int i=s.length()-1; i>=2; i--) {
				// Gernerate a new card
				int val = Character.getNumericValue(s.charAt(i));
				Carte c = Carte.generateCarteFromInt(val);
				// Add the card to pool
				this.pg.getReste().add(0, c);
				// Remove player's card
				this.pg.getEnemyCourant().getCartes().remove(this.pg.getEnemyCourant().getCartes().size() - 1);
			}
		}
	}

	// Annuler le dernier etape d'attaquer
	public void resetAttack(String s) {
		switch(s.substring(0, 2)) {
		case "NA" :
			break;
		default :
			int attVal = Character.getNumericValue(s.charAt(2));
			int attNb = s.length() - 2;
			this.poolToEnemyPlayerCard(attVal, attNb);
		}
	}

	// Annuler le dernier mouvement
	public void resetMove(String s) {
		if(s.charAt(2) != '0') {
			int moveVal;
			switch(s.charAt(1)) {
			case 'F':
				// Undo the last move by the player
				moveVal = Character.getNumericValue(s.charAt(2));
				if(pg.getTourCourant() == 1) this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() + moveVal);
				else this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() - moveVal);
				this.poolToEnemyPlayerCard(moveVal, 1);
				break;
			case 'B':
				// Undo the last move by the player
				moveVal = Character.getNumericValue(s.charAt(2));
				if(pg.getTourCourant() == 1) this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() - moveVal);
				else this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() + moveVal);
				this.poolToEnemyPlayerCard(moveVal, 1);
			default:
				// Shouldn't be executed
				System.err.println("Error resetMove() in ControlCenter.java at Line 345");
				break;
			}
		}
	}

	// Annuler le dernier action de parer
	public void resetParry(String s) {
		switch(s.charAt(0)) {
		case 'P':
		case 'R':
			int parryVal = Character.getNumericValue(s.charAt(1));
			int parryNb = s.length() - 1;
			System.out.println("parryNb = " + parryNb);
			this.poolToEnemyPlayerCard(parryVal, parryNb);
			if(s.charAt(0) == 'R') {
				if(pg.getTourCourant() == 1) this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() - parryVal);
				else this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() + parryVal);
			}
			break;
		default:
			break;
		}
	}

	// Prendre l'attaque avant precedent
	public Attack getAttackBeforeLast(String s) {
		switch(s.substring(0, 2)) {
		case "NA":
			// No attack
			return new Attack(AttackType.NONE, null, 0);
		default:
			int attVal = Character.getNumericValue(s.charAt(2));
			int attNb = s.length() - 2;
			if(s.substring(0, 2).equals("DA")) {
				return new Attack(AttackType.DIRECT, Carte.generateCarteFromInt(attVal), attNb);
			}else {
				return new Attack(AttackType.INDIRECT, Carte.generateCarteFromInt(attVal), attNb);
			}
		}
	}

	// Annuler une seul action
	public void annulerAction() {
		String actionString = this.epg.currentAction.getActionString();
		String actions[] = actionString.split(",");
		switch(actions.length) {
		case 2:
			if(actions[1].charAt(2) != '0') {
				int moveVal;
				switch(actions[1].charAt(1)) {
				case 'F':
					// Undo the last move by the player
					moveVal = Character.getNumericValue(actions[1].charAt(2));
					if(pg.getTourCourant() == 2) this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() + moveVal);
					else this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() - moveVal);
					this.poolToCurrentPlayerCard(moveVal, 1);
					break;
				case 'B':
					// Undo the last move by the player
					moveVal = Character.getNumericValue(actions[1].charAt(2));
					if(pg.getTourCourant() == 2) this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() - moveVal);
					else this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() + moveVal);
					this.poolToCurrentPlayerCard(moveVal, 1);
					break;
				default:
					// Shouldn't be executed
					System.err.println("Error annulerAction() in ControlCenter.java at Line 417");
					break;
				}
				this.interSwing.gi.resetCaseColor();
				this.interSwing.gi.resetChoseCase();
			}
			this.epg.currentAction.deleteAction();
			System.out.println(this.epg.currentAction.toString());
			this.pg.setWaitStatus(3);
			break;
		case 1:
			if(actions[0].equals("N0") || actions[0].equals("")) {
				System.out.println("Reached round start.");
			}
			else if(actions[0].charAt(0) == 'P') {
				int parryVal = Character.getNumericValue(actions[0].charAt(1));
				int parryNB = actions[0].length() - 1;
				this.poolToCurrentPlayerCard(parryVal, parryNB);
				this.epg.currentAction.deleteAction();
				this.epg.roundStart(this.pg.getLastAttack());
			}
			break;
		case 0:
			System.out.println("Reached round start.");
			break;
		default:
			// Shouldn't be executed
			System.err.println("Error annulerAction() in ControlCenter.java at Line 430");
		}
		this.interSwing.repaintAll();
		this.pg.initialiseSelected();
		this.interSwing.ci.initialiseZoom();
	}

	public void poolToCurrentPlayerCard(int val, int nb) {
		for(int i=0; i<nb; i++) {
			// Give the card back to player
			Carte c = Carte.generateCarteFromInt(val);
			this.pg.getPlayerCourant().getCartes().add(c);
			// Remove the card from the pool of used cards
			this.pg.getUsed().remove(this.pg.getUsed().size() - 1);
		}
	}

	public void poolToEnemyPlayerCard(int val, int nb) {
		for(int i=0; i<nb; i++) {
			// Give the card back to player
			Carte c = Carte.generateCarteFromInt(val);
			this.pg.getEnemyCourant().getCartes().add(c);
			// Remove the card from the pool of used cards
			this.pg.getUsed().remove(this.pg.getUsed().size() - 1);
		}
	}

	public void openSaveGameInterface() {
		this.si = new SaveInterface(this);
		si.run();
	}

	public void openLoadGameInterface() {
		this.li = new LoadInterface(this);
		li.run();
	}

	public void generateSaveGame(String text) {
		String s = this.epg.generateSaveString();
		boolean fileExist = false;

	    try {
	    	File f = new File("./res/savefile/" + text);
	    	if (f.createNewFile()) {
	    		System.out.println("File created: " + f.getName());
	    	} else {
	    		System.out.println("File already exists.");
	    		fileExist = true;
	    	}
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }

	    if(!fileExist) {
	    	try {
				PrintWriter out = new PrintWriter("./res/savefile/" + text);
				out.print(s);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	    }
	    this.si.frame.setVisible(false);
	}
	
	public String removeAllNewLine(String s) {
		String newString = "";
		for(int i=0; i<s.length(); i++) if(s.charAt(i) != '\n') newString += s.charAt(i);
		return newString;
	}

	public void loadGame(String string) {
		String content = "";
		boolean loaded = false;
		try {
			content = new String (Files.readAllBytes(Paths.get("./res/savefile/" + string)));
			loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		if(loaded) {
			content = removeAllNewLine(content);
			String[] playgroundStrings = content.split(";");
			for(int i=0; i<playgroundStrings.length; i++) {
				System.out.println(i + " : " + playgroundStrings[i]);
				switch(i) {
				case 0:
					// Charger Joueur Blanc
					this.pg.setJoueurByString(1, playgroundStrings[0]);
					break;
				case 1:
					// Charger Joueur Noir
					this.pg.setJoueurByString(2, playgroundStrings[1]);
					break;
				case 2:
					// Charger Pile Reste
					this.pg.setPileByString(1, playgroundStrings[2]);
					break;
				case 3:
					// Charger Pile Used
					this.pg.setPileByString(2, playgroundStrings[3]);
					for(int m=0; m<this.pg.getUsed().size(); m++) System.out.println(this.pg.getUsed().get(m));
					break;
				case 4:
					// Charger Dernier Attaque
					this.pg.setAttackByString(playgroundStrings[4]);
					break;
				case 5:
				case 6:
				case 7:
					/* 
					 * 5 - Changer Tour Courant
					 * 6 - Changer WaitStatus
					 * 7 - Changer RoundCount
					 */
					this.pg.setParamByString(i, playgroundStrings[i]);
					break;
				case 8:
					// 8 - Changer StartType
					this.pg.setParamByString(i, playgroundStrings[i]);
					String params[] = playgroundStrings[i].split(":");
					this.epg.humanPlayer = Integer.parseInt(params[1]);
				case 9:
					// Changer current action string
					this.epg.setCurrentActionByString(playgroundStrings[9]);
					break;
				case 10:
					// Changer historique
					this.epg.setHistoriqueByString(playgroundStrings[10]);
					break;
				case 11:
					this.epg.setAITypeByString(playgroundStrings[11]);
					break;
				default :
					// Shouldn't be executed
					System.err.println(i + " : loadGame() Error in ContorlCenter.java");
					break;		
				}
					
			}
			this.interSwing.repaintAll();
			this.li.frame.setVisible(false);
		}
		
	}

	@Override
	public void changeText(String s) {
		//if(s != null) this.interSwing.infoArea.setText(s);
		//this.interSwing.infoArea.setText("New Text");
	}
	
	public void restartButtonAction() {
		this.pg.setRoundCount(0);
		this.epg.restartNewRound();
		this.pg.getBlanc().setPoint(0);
		this.pg.getNoir().setPoint(0);
	}
	
	public boolean hasCaseSelected() {
		return this.interSwing.gi.choseCase != -1;
	}

	public void updateAnimations() {
		this.at.tictac();
		this.interSwing.gi.updateAnimationGrille();
		if(this.anims.size() != 0) {
			if(this.anims.get(0).estTerminee()) {
				AnimationJoueur aj = this.anims.remove(0);
				if(aj.joueur == 1) {
					this.interSwing.gi.setInBlancAnimation(false);
					this.interSwing.gi.calculNewBlancPos(aj.getStartCase(), aj.getTargetCase(), 0);
				}
				else {
					this.interSwing.gi.setInNoirAnimation(false);
					this.interSwing.gi.noirAnimationPos = this.pg.getNoirPos();
				}
				
				if(this.anims.size() != 0) {
					if(this.anims.get(0).joueur == 1) this.interSwing.gi.setInBlancAnimation(true);
					else this.interSwing.gi.setInNoirAnimation(true);
					System.out.println(this.anims.get(0));
				}
			}
			if(this.anims.size() != 0) this.anims.get(0).tictac();
		}
	}
	
	public boolean allAnimationTermined() {
		for(Animation a : this.anims) {
			if(!a.estTerminee()) return false;
		}
		return true;
	}

	@Override
	public void receiveLoseSignal(int i, String s) {
		this.clicable = false;
		this.wi.run();
		if(i != -1) this.wi.changeText("Player " + i + " lose because " + s);
		else this.wi.changeText("Game draw because " + s);
		this.wi.setVisible(true);
	}
}
