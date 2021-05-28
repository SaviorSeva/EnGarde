package controlleur;

import java.awt.Point;
import java.util.ArrayList;

import IAs.IA;
import IAs.IAAleatoire;
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
		ia = new IAAleatoire(epg,pg);
		while (epg.isIaRound()){
			if (!ia.getParry()) {
				System.out.println("IA Cartes : " + pg.getCurrentPlayerCards());
				ia.iaParryPhase();
			}
			else ia.pickMove();
		}
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
		if(iep.getEle() == InterfaceElementType.CASE) {
			if(epg.pg.getTourCourant() == 1) {
				int place = epg.pg.getBlancPos();
				if(iep.getNombre() > place) epg.pg.setDirectionDeplace(1);
				else epg.pg.setDirectionDeplace(2);
			}else {
				int place = epg.pg.getNoirPos();
				if(iep.getNombre() > place) epg.pg.setDirectionDeplace(2);
				else epg.pg.setDirectionDeplace(1);
			}
			this.interSwing.gi.setChoseCase(iep.getNombre());
		}
		this.interSwing.repaintGrille();
	}
	
	public void clicSourisCarte(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCardByClick(sourisX, sourisY);
		if(iep.getEle() == InterfaceElementType.CARTE && !this.interSwing.ci.zoomCarte.get(iep.getNombre()).isInvalid()) {
			this.interSwing.ci.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDTRUE);
			
			int dist = epg.getDistance();
			ArrayList<Carte> cartes = this.epg.getCurrentPlayerCards();
			for(int i=0; i<cartes.size(); i++) {
				if(i != iep.getNombre()) {
					if(dist != this.epg.getSelectedCard().getValue())
						this.interSwing.ci.changeZoomTo(i, LockedBoolean.INVALID);
					else {
						if(cartes.get(i).getValue() != cartes.get(iep.getNombre()).getValue())
							this.interSwing.ci.changeZoomTo(i, LockedBoolean.INVALID);
					}
				}
			}
			switch(this.pg.getWaitStatus()) {
			case 1:
				// Parry direct attack
				this.interSwing.gi.setParryCase();
				break;
			case 2:
				// Retreat only
				this.interSwing.gi.setRetreatCaseColor();
				break;
			case 3:
				// Move or direct attack
				this.interSwing.gi.setMoveCaseColor();
				break;
			case 4:
				// Indirect attack only
				this.interSwing.gi.setAttackCaseColor();
				break;
			case 5:
				// Parry indirect attack or retreat
				this.interSwing.gi.setPRCaseColor();
				break;
			default:
				System.err.println("Clic Carte Error Line 136");
				break;
			}
		}
		this.interSwing.repaintCarte();
	}
	
	public void clicSourisCarteDroite(int sourisX, int sourisY) {
		InterfaceElementPosition iep = this.getCardByClick(sourisX, sourisY);
		if(iep.getEle() == InterfaceElementType.CARTE) {
			
			for(int i=0; i<interSwing.ci.zoomCarte.size(); i++) {
				if(i != iep.getNombre()) interSwing.ci.changeZoomTo(i, LockedBoolean.FALSE);
				else this.interSwing.ci.changeZoomTo(iep.getNombre(), LockedBoolean.LOCKEDFALSE);
			}
			this.interSwing.gi.resetCaseColor();
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
		this.interSwing.gi.resetCaseColor();
		this.interSwing.gi.resetChoseCase();
		this.interSwing.gi.resetParryCase();
	}
	
	public void clicCancel() {
		this.epg.cancelReceived();
	}

	@Override
	public void miseAJour() {
		this.IAStep();
	}
}
