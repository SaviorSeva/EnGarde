package controlleur;

import java.awt.Point;
import java.util.ArrayList;

import IAs.IA;
import IAs.IAAleatoire;
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
import vue.InterfaceSwing;

public class ControlCenter implements Observateur{
	Playground pg;
	ExecPlayground epg;
	InterfaceSwing interSwing;
	ArrayList<Point> elementPos;
	IA ia;
	
	public ControlCenter(ExecPlayground epg) {
		this.epg = epg;
		this.pg = this.epg.getPg();
		this.epg.ajouteObservateur(this);
		switch(this.epg.getIAType()) {
		case 0:
			this.ia = null;
			break;
		case 1:
			this.ia = new IAAleatoire(this.epg, this.pg);
			break;
		}
	}
	
	public void ajouteInterfaceUtilisateur(InterfaceSwing ifs) {
		this.interSwing = ifs;
	}
	
	public void IAStep() {
		if (epg.isIaRound()) {
			ia.iaParryPhase();
			
			ia.pickMove();
			pg.setDirectionDeplace(ia.getDirection());
			pg.setSelected(ia.getIaCartes());
			this.epg.confirmReceived();
			ia.resetChoisir();
		}else ia = new IAAleatoire(epg,pg);
			//iaAleatoire.setParry(false);
	}
	
	public void resetZoom(){
		this.interSwing.ci.resetZoom();
	}
	
	public void initialiseZoom() {
		this.interSwing.ci.initialiseZoom();
	}
	
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
	
	public void clicSourisGrille(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCaseByClick(sourisX, sourisY);
		if(this.epg.getSelectedCard() != null) {
			if(iep.getEle() == InterfaceElementType.CASE && this.interSwing.gi.equalToHighlighted(iep.getNombre())) {
				if(epg.pg.getTourCourant() == 1) {
					int place = epg.pg.getBlancPos();
					if(iep.getNombre() > place) epg.pg.setDirectionDeplace(1);
					else if(iep.getNombre() < place) epg.pg.setDirectionDeplace(2);
					else epg.pg.setDirectionDeplace(3);
				}else {
					int place = epg.pg.getNoirPos();
					if(iep.getNombre() > place) epg.pg.setDirectionDeplace(2);
					else if(iep.getNombre() < place) epg.pg.setDirectionDeplace(1);
					else epg.pg.setDirectionDeplace(3);
				}
				this.interSwing.gi.setChoseCase(iep.getNombre());
			}
			this.interSwing.repaintGrille();
		}
	}
	
	public void clicSourisGrilleDroite(int sourisX, int sourisY) {
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
				if(this.epg.getSelectedCard().getValue() == this.pg.getDistance()) this.interSwing.gi.setAttackCaseColor();
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
	
	public void resetCartes() {
		for(int i=0; i<interSwing.ci.zoomCarte.size(); i++) {
			interSwing.ci.changeZoomTo(i, LockedBoolean.FALSE);
		}
	}
	
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
	}

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
	
	public void confirmReceived() {
		this.epg.confirmReceived();
		this.interSwing.ci.initialiseZoom();
		this.interSwing.gi.resetCaseColor();
		this.interSwing.gi.resetChoseCase();
		this.interSwing.gi.resetParryCase();
	}
	
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
	
	public void annulerRound() {
		if(this.epg.hist.listAction.size() == 0) {
			System.out.println("There is no more round to recover.");
		}else {
			Action lastAction = this.epg.hist.removeLastAction();
			String lastActions[] = lastAction.getActionString().split(",");
			// for(int i=0; i<actions.length; i++) System.out.println(actions[i]);
			System.out.println(this.pg.getEnemyCourant().getCartes().toString());
			this.returnCardToPool(lastActions[3]);
			System.out.println(this.pg.getEnemyCourant().getCartes().toString());
			this.resetAttack(lastActions[2]);
			System.out.println(this.pg.getEnemyCourant().getCartes().toString());
			this.resetMove(lastActions[1]);
			System.out.println(this.pg.getEnemyCourant().getCartes().toString());
			this.resetParry(lastActions[0]);
			System.out.println(this.pg.getEnemyCourant().getCartes().toString());
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
				System.err.println("Error resetMove() in ControlCenter.java at Line 311");
				break;
			}
		}
	}
	
	public void resetParry(String s) {
		switch(s.charAt(0)) {
		case 'P':
		case 'R':
			int parryVal = Character.getNumericValue(s.charAt(1));
			int parryNb = s.length() - 1;
			this.poolToCurrentPlayerCard(parryVal, parryNb);
			break;
		default:
			break;
		}
	}

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
			}
			this.epg.currentAction.deleteAction();
			System.out.println(this.epg.currentAction.toString());
			this.pg.setWaitStatus(3);
			break;
		case 1:
			if(actions[0] == null || actions[0].equals("N0")) System.out.println("Reached round start.");
			else if(actions[0].charAt(0) == 'P') {
				int parryVal = Character.getNumericValue(actions[0].charAt(1));
				int parryNB = actions[0].length() - 1;
				this.poolToCurrentPlayerCard(parryVal, parryNB);
				this.epg.currentAction.deleteAction();
				this.epg.roundStart(this.pg.getLastAttack());
			}
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
}
