package modele;

import java.util.ArrayList;
import java.util.Collections;

import patterns.Observable;

public class ExecPlayground extends Observable{
	public Playground pg;
	public Action currentAction;
	public Historique hist;
	
	public int IAType;
	public int humanPlayer;
	
	public ExecPlayground(Playground pg, int IAType) {
		this.pg = pg;
		this.currentAction = new Action();
		this.hist = new Historique();
		this.IAType = IAType;
		humanPlayer = pg.getStartType();
	}
	
	public int getIAType() {
		return IAType;
	}

	public boolean isIaRound() {
		if (pg.getTourCourant() != humanPlayer && (IAType != 0)) return true;
		else return false;
	}

	public Playground getPg() {
		return this.pg;
	}
	
	// Le distance entre 2 joueurs
	public int getDistance() {
		return pg.getDistance();
	}
	
	
	public int distribuerCarte(int player) {
		// si ni blanc ni blanc joueur, erreur
    	if(player != 1 && player != 2) return -1;
    	// on dépile la première carte de carte non utilisé
    	Carte c = this.pg.getReste().remove(0);
    	// si c'est joueur blanc, on le distribue
    	if(player == 1) this.pg.getBlanc().addCartes(c);
    	// sinon, on distribue la carte à joueur noir
    	else if(player == 2) this.pg.getNoir().addCartes(c);
    	// Retouner la valeur de carte qu'il distribue
    	return c.getValue();
    }
	
	// le joueur blanc ou noir avance valeur de cases dans le zone de playground
	public void avance(int valeur) {
    	if(this.pg.getTourCourant() == 1) 
    		this.pg.getBlanc().setPlace(this.pg.getBlanc().getPlace() + valeur);
    	else 
    		this.pg.getNoir().setPlace(this.pg.getNoir().getPlace() - valeur);
    	this.pg.metAJour();
    }
    
	// retraitement de l'avance avec valeur négative de nombre de cases
    public void retreat(int valeur) {
    	this.avance(-valeur);
    }
	
    // on remet le jeu comme le début
	public void restartNewRound() {
    	this.pg.getBlanc().setPlace(0);
    	this.pg.getNoir().setPlace(22);
    	
    	this.initiliaseCarte();
    	
    	for(int i=0; i<5; i++) {
			this.distribuerCarte(1);
			this.distribuerCarte(2);
		}
    	
    	this.pg.incrementRoundCount();
    	if(this.pg.getRoundCount() % 2 == 1) this.pg.setTourCourant(1);
    	else this.pg.setTourCourant(2);
    	
    	this.roundStart(new Attack(AttackType.NONE, null, 0));
    }
	
	// échanger tour courant entre les deux joueurs
	 public void changetTour() {
    	if(pg.getTourCourant() == 1) pg.setTourCourant(2);
    	else pg.setTourCourant(1);
    }
	
	// Initialiser la selection des cartes
	public void initialiseSelected() {
		this.pg.getSelected().clear();
		for(int i=0; i<5; i++) {
			this.pg.getSelected().add(false);
		}
	}
    
	// Remettre la selection des cartes a false
    public void resetSelected() {
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			 this.pg.getSelected().set(i, false);
		}
	}
    
    public void initiliaseCarte() {
    	// vérifier si pile de carte non utilisé est vide, sinon vider
    	if(this.pg.getReste().size() != 0) this.pg.getReste().clear();
    	// vider tous les ArrayList concernant les cartes joués
    	this.pg.getUsed().clear();
    	this.pg.getBlanc().clearCartes();
    	this.pg.getNoir().clearCartes();
    	
    	//on remplit la pile de cartes non utilisés par nombre totale de cartes (5*5)
    	for(int i=1; i<6; i++) {
    		this.pg.getReste().add(Carte.UN);
    		this.pg.getReste().add(Carte.DEUX);
    		this.pg.getReste().add(Carte.TROIS);
    		this.pg.getReste().add(Carte.QUATRE);
    		this.pg.getReste().add(Carte.CINQ);
    	}
    	//mélange aléatoirement
    	this.shuffleReste();
    }
    
    public void shuffleReste() {
    	Collections.shuffle(this.pg.getReste());
    }
	
    
    /*
     *  Return :
     *  	-1 if error,
     *  	0 if cannot parry (which means lose this round),
     *  	1 if has no attack,
     * 		2 if can parry direct attack,
     * 		3 if have to retreat,
     * 		4 if can parry indirect attack, and player can retreat or parry the attack.
     */
	public int phaseParer(Attack dernierAttaque){
		ArrayList<Carte> cartes;
		// si tour courant est blanc, cartes reçoit celles de joueur blanc
		// sinon celle de joueur noir
		if(this.pg.getTourCourant() == 1) cartes = this.pg.getBlancCartes();
		else cartes = this.pg.getNoirCartes();
		
		int nb = 0;
		switch(dernierAttaque.getAt()) {
		// si pas d'action attaque retourne 1
		case NONE:
			return 1;
		case DIRECT:
			// Prendre le nombre de carte de cette attaque
			for(int i=0; i<cartes.size(); i++) {
				if(cartes.get(i).getValue() == dernierAttaque.getAttValue().getValue()) nb++;
			}
			// si les cartes de joueurs courant contient nombre supérieur de cartes identique de dernierAttatque, on peut les sélectionner et retourne valeur 2
			// sinon rien à faire et retourne 0
			if(nb >= dernierAttaque.getAttnb()) {
				for(int i=0; i<cartes.size() && nb>0; i++) {
					if(cartes.get(i).getValue() == dernierAttaque.getAttValue().getValue()) {
						this.pg.getSelected().set(i, true);
						nb--;
					}
				}
				return 2;
			}
			else return 0;
		case INDIRECT:
			// case attaque indirect
			for(int i=0; i<cartes.size(); i++) {
				if(cartes.get(i).getValue() == dernierAttaque.getAttValue().getValue()) nb++;
			}
			// s'il exite de carte pour défendre, c'est à dire on peut soit défendre soit retraite, on retourne 4
			if(nb >= dernierAttaque.getAttnb()) return 4;
			// sinon on ne peut que retraite et retourne 3
			else return 3;
		}
		// Informer les observateurs
		this.pg.metAJour();
		// si pas dans les cases au-dessus, retourne -1
		return -1;
	}
	
	public void phaseDeplacer(Carte c) {
		//si carte null, erreur
		if(c == null) System.err.println("Error getSelectedCard()");
		
		// si valeur de carte est même de distance entre joueurs et direction égale à avance, on applique attaque direct
		if(this.pg.getDistance() == c.getValue() && this.pg.getDirectionDeplace() == 1) {
			// Direct Attack
			int nbSelected = this.getNBSelectedCard();
			this.jouerCarte();
			this.currentAction.appendDirectAttackAction(c, nbSelected);
			this.roundEnd(new Attack(AttackType.DIRECT, c, nbSelected));
		}else if(this.getNBSelectedCard() == 1) {
			// si distance différent et direction non null
			if(this.pg.getDirectionDeplace() != 0){
				// Move Forward
				if(this.pg.getDirectionDeplace() == 1) {
					this.avance(c.getValue());
					this.currentAction.appendMoveForwardAction(c);
				}
				// Move backward
				else if(pg.getDirectionDeplace() == 2) {
					this.retreat(c.getValue());
					this.currentAction.appendMoveBackwardAction(c);
				}
				// Play the card
				this.jouerCarte();
				// Get the possibility of an indirect attack
				if(this.canAttack()) {
					this.pg.setWaitStatus(4);
				}else {
					this.currentAction.appendNoAttackAction();
					this.roundEnd(new Attack(AttackType.NONE, null, 0));
				}
			}else{
				// si aucun direction est selectionne
				System.out.println("You must choose a Direction! ");
			}
			
		}else {
			// s'il chosit plus d'un carte
			System.out.println("You cannot move with more than 1 card ! ");
		}
			
	}
	
	// obtenir les cartes de joueur courant
	public ArrayList<Carte> getCurrentPlayerCards(){
		if(pg.getTourCourant() == 1) return this.pg.getBlancCartes();
		else return this.pg.getNoirCartes();
	}
	
	// jouer les cartes sélectionnées
	public void jouerCarte() {
		// TODO Valid card
		ArrayList<Carte> cartes = this.getCurrentPlayerCards();
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			if(this.pg.getSelected().get(i)) {
				Carte joue = cartes.remove(i);
				this.pg.getSelected().remove(i);
				i--;
				this.pg.getUsed().add(joue);
			}
		}
		this.pg.metAJour();
	}
	
	// Les etapes a faire avant d'entrer l'etat 3
	public void enterE3() {
		ArrayList<Carte> cs = this.getCurrentPlayerCards();
		boolean unableToMove = true;
		
		// on cherche s'il exite de carte pour qu'on puisse déplacer, si oui, 
		// variable unableToMove renvoie False, sinon elle contient True
		for(int i=0; i<cs.size() && unableToMove; i++) {
			int val = cs.get(i).getValue();
			if(this.pg.getDistance() >= val || this.pg.getPlayerCourant().getDistToStartPoint() > val) unableToMove = false;
		}
		// si on ne peut pas déplacer ou attaquer, on perde le Round courant puis recommencer un nouvelle Round
		if(unableToMove) {
			System.out.println("You cannot move or attack, therefore" + pg.getTourCourant() + " lose !");
			this.pg.getEnemyCourant().incrementPoint();
			this.restartNewRound();
		}else {
			// si oui on rentre dans l'etat 3, c'est à dire on attend le joueur choisi un / des carte(s) puis confirmer 
			this.pg.setWaitStatus(3);
		}
	}
	
	public void roundStart(Attack att) {
		pg.setLastAttack(att);
		
		// 1. si les deux joueurs n'ont plus de carte, passez au règlement
		if(this.pg.getBlancCartes().size() == 0 && this.pg.getNoirCartes().size() == 0) {
			int distBlanc = this.pg.getBlanc().getDistToStartPoint();
			int distNoir = this.pg.getNoir().getDistToStartPoint();
			// la joueur dont la distance entre joueur et départ est la plus grand obtient un point
			// puis recommencer le prochain Round
			if(distBlanc > distNoir) this.pg.getBlanc().incrementPoint();
			else if(distNoir > distBlanc) this.pg.getNoir().incrementPoint();
			this.restartNewRound();
		}
		// 2. si joueur courant n'a plus de carte
		else if(this.getCurrentPlayerCards().size() == 0) {
			// Verifier s'il y a un attaque, si oui, il perde
			if(pg.getLastAttack().getAt() != AttackType.NONE) {
				if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
				else this.pg.getBlanc().incrementPoint();
				this.restartNewRound();
			}else {
				// Sinon il fait aucun action
				this.currentAction.appendNoAction();
				this.roundEnd(new Attack(AttackType.NONE, null, 0));
			}
		}
		//3. si joueur courant contient des cartes
		else {
			// on rentre dans différent état selon valeur retour par la fonction phaseParer
			int pharerResultat = phaseParer(this.pg.getLastAttack());
	    	switch(pharerResultat) {
	    	case 0:
	    		// incrémenter points d'ennemis puis recommencer nouvel round
	    		System.out.println("Case 0 lose, lose player: " + pg.getTourCourant());
				if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
				else this.pg.getBlanc().incrementPoint();
				this.restartNewRound();
				break;
			case 1:
				//1 - Pas d'attaque
				System.out.println("Case 1 noAttack");
				// Rentre dans l'état 3: attendre le joueur choisit le carte puis confirmer
				this.currentAction.appendNoParryAction();
				enterE3();
				break;
			case 2:
				// 2 - attaque direct et le joueur courant a des cartes pour défendre cette attaque
				System.out.println("Case 2 canParry");
				// on rentre dans l'état 1: choisir la carte pour défendre puis attendre le joueur clic confirmer
				this.pg.setWaitStatus(1);
				break;
			case 3:
				// 3 - attaque indirect et on ne peut que retraiter
				System.out.println("Case 3 retreat");
				ArrayList<Carte> cs = this.getCurrentPlayerCards();
				boolean unableToRetreat = true;
				for(int i=0; i<cs.size() && unableToRetreat; i++) {
					if(this.pg.getPlayerCourant().getDistToStartPoint() >= cs.get(i).getValue()) unableToRetreat = false;
				}
				// si on ne peut pas retraite, on perd directement
				if(unableToRetreat) {
					System.out.println("You cannnot retreat. You lose !");
					this.pg.getEnemyCourant().incrementPoint();
					this.restartNewRound();
				//sinon on rentre dans l'état 2: choisir la carte pour défendre	
				}else this.pg.setWaitStatus(2);
				break;
			case 4:
				// 4 - attaque indirect avec carte possible pour défendre ou juste retraite
				System.out.println("Case 4 retreat or parry");
				// on rentre dans l'état 5: on peut soit défendre soit retraite
				this.pg.setWaitStatus(5);
				break;
			default:
				// Should not be executed
				System.err.println("roundStart() Error");
				break;
	    	}
		}
		if(this.IAType != 0 && this.isIaRound()) {
			this.metAJour();
		}
	}
	
	public void roundEnd(Attack att) {
		ArrayList<Carte> cartes = this.getCurrentPlayerCards();
		// la pile de carte distribue les carte tant qu'elle soit vide
		if(this.pg.getReste().size() == 0) this.currentAction.appendNoGetCardAction();
		else {
			this.currentAction.appendStartGetCardAction();
			while(this.pg.getReste().size() != 0 && cartes.size() < 5) {
				int val = this.distribuerCarte(pg.getTourCourant());
				this.currentAction.appendGetCardAction(val);
			}
		}
		// reinitialiser l'état courant et les cartes sélectionnées enfin changer le tour
		this.pg.setWaitStatus(0);
		this.pg.initialiseSelected();
		this.pg.setDirectionDeplace(0);
		this.changetTour();
		// on ajoute l'action courant dans l'historique
		hist.addCopy(this.currentAction);
		System.out.println(this.currentAction.toString());
		System.out.println(this.hist.toString());
		this.currentAction.clear();
		// commencer le tour de joueur suivant
		this.roundStart(att);
	}
	
	public void confirmReceived() {
		Carte c;
		int nbSelected;
		switch(this.pg.getWaitStatus()) {
		case 1:
			c = this.getSelectedCard();
			nbSelected = this.getNBSelectedCard();
			// si la valeur de carte de dernier attaque égale à carte choisi, et le nombre de cartes choisi égale à cel de dernier attaque
			// on peut jouer avec cette carte
			if(c == null) System.out.println("You must pick a card to parry !"); 
			else if(c.getValue() == this.pg.getLastAttack().getAttValue().getValue() && nbSelected == this.pg.getLastAttack().getAttnb()) {
				this.jouerCarte();
				this.currentAction.appendParryAction(c, nbSelected);
				enterE3();
			}else {
				//sinon on ne peut pas défendre avec cette carte choisi	
				System.out.println(	"You cannot parry the direct attack of (" + 
									this.pg.getLastAttack().getAttValue().getValue() + 
									", " + 
									this.pg.getLastAttack().getAttnb() + 
									") with the selection of (" +
									c.getValue() + ", " + nbSelected + ").");
			}
			break;
		case 2:
			c = this.getSelectedCard();
			// si on ne peut pas retraiter avec cette carte, on affiche des infos pour rechoisir
			if(this.pg.getPlayerCourant().getDistToStartPoint() < c.getValue()) System.out.println("You cannot retreat due to the size of the playground (case 2).");
			// sinon on peut jouer avec action retraiter
			else{
				this.jouerCarte();
				this.currentAction.appendRetreatAction(c);
				this.retreat(c.getValue());
				this.roundEnd(new Attack(AttackType.NONE, null, 0));
			}
			break;
		case 3:
			// 1. si on n'a pas d'action, rien à faire
			if(this.currentAction.getActionString().equals("")) this.currentAction.appendNoParryAction();
			c = this.getSelectedCard();
			
			// 2. si on n'a pas encore choisi la carte, afficher l'info 
			if(c == null) System.out.println("You must pick a card!");
			
			// 3. si on a bien choisi la carte, on vérifie
			else if(c.getValue() > this.pg.getDistance() && this.pg.getDirectionDeplace() == 1)
				// 3.1 si la valeur de carte est supérieur à distance entre joueurs et avec la direction "avancer"
				// afficher l'info de ne pas dépasser
				System.out.println("You cannot surpass the other player");

			else if(this.pg.getDirectionDeplace() == 2) {
				// 3.2 si la direction de movement est "retrait"
				// si la distance depuis point départ est inférieur à la valeur de carte, on ne peut pas retraiter puis afficher l'info
				if(this.pg.getPlayerCourant().getDistToStartPoint() < c.getValue()) System.out.println("You cannot retreat due to the size of the playground (case 3).");
				// sinon on peut déplacer avec cette carte
				else this.phaseDeplacer(c);
			}else 
				// 3.3 on peut soit retraiter soit avancer avec carte inférieur à 
				// la distance depuis point départ ou la distance entre deux joueurs
				this.phaseDeplacer(c);
			break;
		case 4:
			// Indirect Attack
			Carte indirectCarte = this.getSelectedCard();
			nbSelected = this.getNBSelectedCard();
			// si la carte choisi égale à la distance entre deux joueurs, 
			// on joue avec cette carte et l'ajoute dans liste d'action enfin finaliser round courant
			if(indirectCarte.getValue() == this.pg.getDistance()) {
				this.jouerCarte();
				this.currentAction.appendIndirectAttackAction(indirectCarte, nbSelected);
				this.roundEnd(new Attack(AttackType.INDIRECT, indirectCarte, nbSelected));
			}else {
				// sinon afficher l'info "ne peut pas attaquer avec cette carte car distance différent de celle entre deux joueurs"
				System.out.println("You cannot attack with the selection of (" +
						indirectCarte.getValue() + ", " + nbSelected + 
						") because the distance is " + this.pg.getDistance() + ".");
			}
			break;
		case 5:
			if(this.pg.getDirectionDeplace() == 3) {
				// Parry indirect attack
				c = this.getSelectedCard();
				nbSelected = this.getNBSelectedCard();
				// si la valeur de carte égale à valeur de dernière attaque et nombre de cartes choisi égale à celui de dernier attaque
				// on joue avec cette carte et l'ajoute dans liste d'aciton puis rentre dans l'état 3: attendre joueur choisi une carte puis clique confirmer
				if(c.getValue() == this.pg.getLastAttack().getAttValue().getValue() && nbSelected == this.pg.getLastAttack().getAttnb()) {
					this.jouerCarte();
					this.currentAction.appendParryAction(c, nbSelected);
					enterE3();
				}else {
					// si l'un des deux n'est pas satisfait, on affiche l'info de "ne peut pas défendre attaque indirect avec carte choisi"
					System.out.println(	"You cannot parry the indirect attack of (" + 
										this.pg.getLastAttack().getAttValue().getValue() + 
										", " + 
										this.pg.getLastAttack().getAttnb() + 
										") with the selection of (" +
										c.getValue() + ", " + nbSelected + ").");
				}
			}else if(this.pg.getDirectionDeplace() == 2){
				// Retreat
				c = this.getSelectedCard();
				if(this.pg.getPlayerCourant().getDistToStartPoint() < c.getValue()) System.out.println("You cannot retreat due to the size of the playground.");
				else {
					this.jouerCarte();
					this.currentAction.appendRetreatAction(c);
		    		this.retreat(c.getValue());
		    		this.roundEnd(new Attack(AttackType.NONE, null, 0));
				}
			}else System.out.println("You have to parry the attack or retreat !");
			break;
		default:
			// Should not be executed
			System.err.println("Line 323 : confirmReceived() Error");
			break;	
		}
	}
	
	public void addPointByCurrentPlayer() {
		// on ajoute 1 point à noir si le tour courant est blanc, vice versa
		if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
		else this.pg.getBlanc().incrementPoint();
	}
	
	public void cancelReceived() {
		switch(this.pg.getWaitStatus()) {
		case 1:
			// on mort directement si on ne défend pas puis ajoute point à ennemis enfin recommencer nouvel round
			System.out.println("Not parrying direct attack. You lose !");
			this.addPointByCurrentPlayer();
			this.restartNewRound();
		break;
		case 2:
			// on ne bouge pas donc lose directement puis ajoute 1 point à ennemis et recommencer round suivant
			System.out.println("Not parrying indirect attack. You lose !");
			this.addPointByCurrentPlayer();
			this.restartNewRound();
			break;
		case 3:
			// afficher l'info de "obliger de choisir la carte pour déplacer"
			System.out.println("You must move at this round !");
			break;
		case 4:
			// on annule d'attaquer avec carte admissible, donc rien à faire pour ennemis puis round courant termine
			System.out.println("You've chose not to attack the enemy.");
			this.currentAction.appendNoAttackAction();
			this.roundEnd(new Attack(AttackType.NONE, null, 0));
			break;
		case 5:
			System.out.println("You have to parry the attack or retreat !");
			break;
		}
		this.pg.metAJour();
	}
	
	public Carte getSelectedCard() {
		Carte c = null;
		// on obtient la valeur des cartes choisi pour joueur courant
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			if(this.pg.getSelected().get(i)) {
				if(i < this.pg.getPlayerCourant().getCartes().size()) c = this.getCurrentPlayerCards().get(i);
			}
		}
		return c;
	}
	
	// compter nombre de carte choisi 
	public int getNBSelectedCard() {
		int res = 0;
		for(int i=0; i<this.pg.getSelected().size(); i++) {
			if(this.pg.getSelected().get(i)) res++;
		}
		return res;
	}
	
	// revoie true si les cartes de joueur contient la carte de valeur 'value', false sinon
	public boolean cartesContains(int value) {
		ArrayList<Carte> cartes = this.getCurrentPlayerCards();
		for(int i=0; i<cartes.size(); i++) {
			if(cartes.get(i).getValue() == value) return true;
		}
		return false;
	}
	
	// renvoie vrai s'il existe la carte égale à la distance entre deux joueur, càd on peut attaquer
	// sinon faux
	public boolean canAttack() {
		int dist = this.pg.getDistance();
		if(this.cartesContains(dist)) return true;
		return false;
	}
}
