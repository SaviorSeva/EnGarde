package modele;

import java.util.ArrayList;
import java.util.Collections;

import patterns.Observable;

public class ExecPlayground extends Observable{
	public Playground pg;
	public Action currentAction;
	public Historique hist;
	boolean gameStart;
	String infoString;
	
	public int getLastCardPlayer;
	
	public int IAType; // 0-Sans IA, 1-IA Aleatoire, 2-IA Proba, 3-IA Minmax
	public int humanPlayer;
	
	public boolean gameStopped;
	
	public ExecPlayground(Playground pg, int IAType) {
		this.pg = pg;
		this.currentAction = new Action();
		this.hist = new Historique();
		this.IAType = IAType;
		humanPlayer = pg.getStartType();
		gameStopped = false;
		gameStart = true;
		this.infoString = "";
		getLastCardPlayer = 0;
	}
	
	public int getIAType() {
		return IAType;
	}

	public boolean isIaRound() {
		if (pg.getTourCourant() != humanPlayer && (IAType != 0)) return true;
		else return false;
	}

	public boolean isIaAleatoireRound() {
		if(IAType == 3 && pg.getTourCourant()==1) return true;
		else return false;
	}

	public boolean isIaProbaRound() {
		if(IAType == 3 && pg.getTourCourant()==2) return true;
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
		this.getLastCardPlayer = 0;
		this.gameStopped = false;
		
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
    	
    	this.currentAction = new Action();
    	this.hist = new Historique();
    	
    	if(this.gameStart) {
	    	if(this.pg.getStartType() == 0)
	    		this.infoString = "Bienvenue au jeu En Garde, " + this.pg.getBlanc().getName()  + " et " + this.pg.getNoir().getName() + "!\n";
	    	else if(this.pg.getStartType() == 1)
	    		this.infoString = "Bienvenue au jeu En Garde, " + this.pg.getBlanc().getName()  + "!\n" ;
	    	else if(this.pg.getStartType() == 2)
	    		this.infoString = "Bienvenue au jeu En Garde, " + this.pg.getNoir().getName()  + "!\n" ;
	    	
    	}
    	
    	this.sendRestartSignal();
    	
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
					this.infoString = "Vous avez faire un mouvement, mais vous pouvez faire une attaque indirecte ou terminer votre tour.";
					this.pg.setWaitStatus(4);
					this.metAJour(infoString);
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
			if(this.pg.getDistance() >= val || this.pg.getPlayerCourant().getDistToStartPlace() >= val) unableToMove = false;
		}
		// si on ne peut pas déplacer ou attaquer, on perde le Round courant puis recommencer un nouvelle Round
		if(unableToMove) {
			System.out.println("You cannot move or attack, therefore " + pg.getTourCourant() + " lose !");
			this.pg.getEnemyCourant().incrementPoint();
			
			if(this.IAType != 3) {
				this.sendLoseSignal(pg.getTourCourant(), "he is unable to move or attack with his cards.");
				
			}else this.restartNewRound();
		}else {
			// si oui on rentre dans l'etat 3, c'est à dire on attend le joueur choisi un / des carte(s) puis confirmer
			this.pg.setWaitStatus(3);
			if(this.gameStart)
				this.infoString += "Vous pouvez choisir un carte pour faire un mouvement ou attaquer votre ennemi.";
			else this.infoString = "Vous pouvez choisir un carte pour faire un mouvement ou attaquer votre ennemi.";
			this.gameStart = false;
			this.metAJour(infoString);
		}
	}
	
	public void roundStart(Attack att) {
		pg.setLastAttack(att);
		if(pg.getPlayerCourant().getPoint()+pg.getEnemyCourant().getPoint()==30) {
			if(pg.getPlayerCourant().getPoint()>pg.getEnemyCourant().getPoint()){
				System.out.println("Winner est : " + pg.getTourCourant() + " Point: " + pg.getPlayerCourant().getPoint());
				System.out.println("Loser est : " + pg.getEnemyCourant().startPlace +" Point: " + pg.getEnemyCourant().getPoint());
			}else{
				System.out.println("Winner est : " + pg.getEnemyCourant().startPlace + "  Point: " + pg.getEnemyCourant().getPoint());
				System.out.println("Loser : " + "  " + pg.getTourCourant()+ "  Point: " + pg.getPlayerCourant().getPoint());
			}
			System.exit(0);
		}
		
		/*
		// 1. si les deux joueurs n'ont plus de carte, passez au règlement
		if(this.pg.getBlancCartes().size() == 0 && this.pg.getNoirCartes().size() == 0) {
			int distBlanc = this.pg.getBlanc().getDistToStartPlace();
			int distNoir = this.pg.getNoir().getDistToStartPlace();
			// la joueur dont la distance entre joueur et départ est la plus grand obtient un point
			// puis recommencer le prochain Round
			int i=-1;
			if(distBlanc > distNoir) {
				this.pg.getBlanc().incrementPoint();
				i=2;
			}
			else if(distNoir > distBlanc) {
				this.pg.getNoir().incrementPoint();
				i=1;
			}
			if(i!=-1) {
				if(this.IAType != 3) {
					this.sendLoseSignal(i, "the other player is further from his start place.");
					this.gameStopped = true;
				}
				else this.restartNewRound();
			}
			else {
				if(this.IAType != 3) this.sendLoseSignal(i, "both player travel the same distance.");
				else this.restartNewRound();
			}
		}
		
		
		// 2. si joueur courant n'a plus de carte
		else if(this.getCurrentPlayerCards().size() == 0) {
			// Verifier s'il y a un attaque, si oui, il perde
			if(pg.getLastAttack().getAt() != AttackType.NONE) {
				if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
				else this.pg.getBlanc().incrementPoint();
				
				if(this.IAType != 3) {
					this.sendLoseSignal(this.pg.getTourCourant(), "he has no card to parry an attack.");
					
				}else this.restartNewRound();
			}else {
				// Sinon il fait aucun action
				this.currentAction.appendNoAction();
				this.roundEnd(new Attack(AttackType.NONE, null, 0));
			}
		}
		
		*/
		
		//3. si joueur courant contient des cartes
		// on rentre dans différent état selon valeur retour par la fonction phaseParer
		int parerResultat = phaseParer(this.pg.getLastAttack());
    	switch(parerResultat) {
    	case 0:
    		// incrémenter points d'ennemis puis recommencer nouvel round
    		System.out.println("Case 0 lose, lose player: " + pg.getTourCourant());
    		this.getLastCardPlayer = 0;
			if(this.pg.getTourCourant() == 1) this.pg.getNoir().incrementPoint();
			else this.pg.getBlanc().incrementPoint();
			if(this.IAType != 3) {
				this.sendLoseSignal(pg.getTourCourant(), "Unparryable direct attack.");
				
			}else this.restartNewRound();
			
			break;
		case 1:
			// 1 - Pas d'attaque
			System.out.println("Case 1 noAttack");
			// Rentre dans l'état 3: attendre le joueur choisit le carte puis confirmer
			this.currentAction.appendNoParryAction();
			System.out.println("Make sure the getLastCardPlayer have been reset: " + this.getLastCardPlayer);
			if(this.getLastCardPlayer != this.pg.getTourCourant()) enterE3();
			else this.endProcedure();
			break;
		case 2:
			// 2 - attaque direct et le joueur courant a des cartes pour défendre cette attaque
			System.out.println("Case 2 canParry");
			// on rentre dans l'état 1: choisir la carte pour défendre puis attendre le joueur clic confirmer
			this.pg.setWaitStatus(1);
			if(this.pg.getLastAttack().getAttnb() == 1)
				this.infoString = "Vous subissez un attaque direct de 1 carte avec un valeur de " + 
									this.pg.getLastAttack().getAttValue().getValue() + " , \n";
			else 
				this.infoString = "Vous subissez un attaque direct de " + this.pg.getLastAttack().getAttnb() +
									" cartes avec un valeur de " + 
									this.pg.getLastAttack().getAttValue().getValue() + " , \n";
			this.infoString += "Parer cette attaque avec la(les) catre(s) avec le même valeur et nombre.";
			this.metAJour(this.infoString);
			break;
		case 3:
			// 3 - attaque indirect et on ne peut que retraiter
			System.out.println("Case 3 retreat");
			ArrayList<Carte> cs = this.getCurrentPlayerCards();
			boolean unableToRetreat = true;
			for(int i=0; i<cs.size() && unableToRetreat; i++) {
				if(this.pg.getPlayerCourant().getDistToStartPlace() >= cs.get(i).getValue()) unableToRetreat = false;
			}
			// si on ne peut pas retraite, on perd directement
			if(unableToRetreat) {
				System.out.println("You cannnot retreat. You lose !");
				
				this.pg.getEnemyCourant().incrementPoint();
				if(this.IAType != 3) {
					this.sendLoseSignal(this.pg.getTourCourant(), "he is unable to retreat with his cards");
				}else this.restartNewRound();
			//sinon on rentre dans l'état 2: choisir la carte pour défendre
			}else this.pg.setWaitStatus(2);
			
			if(this.pg.getLastAttack().getAttnb() == 1)
				this.infoString = "Vous subissez un attaque indirect de 1 carte avec un valeur de " + 
									this.pg.getLastAttack().getAttValue().getValue() + " , \n";
			else 
				this.infoString = "Vous subissez un attaque indirect de "+ this.pg.getLastAttack().getAttnb() + 
									" cartes avec un valeur de " + 
									this.pg.getLastAttack().getAttValue().getValue() + " , \n";
			this.infoString += "Vous n'avez pas de carte pour le parer, mais vous pouvez retraiter avec un carte et terminer votre tour.";
			this.metAJour(infoString);
			break;
		case 4:
			// 4 - attaque indirect avec carte possible pour défendre ou juste retraite
			System.out.println("Case 4 retreat or parry");
			// on rentre dans l'état 5: on peut soit défendre soit retraite
			this.pg.setWaitStatus(5);
			if(this.pg.getLastAttack().getAttnb() == 1)
				this.infoString = "Vous subissez un attaque indirect de 1 carte avec un valeur de " + 
									this.pg.getLastAttack().getAttValue().getValue() + " , \n";
			else 
				this.infoString = "Vous subissez un attaque indirect de "+ this.pg.getLastAttack().getAttnb() + 
									"cartes avec un valeur de " + 
									this.pg.getLastAttack().getAttValue().getValue() + " , \n";
			this.infoString += "Vous pouvez parer cette attaque avec les propres cartes et continuer votre tour, \n"
								+ "ou retraiter avec un carte et terminer votre tour.";
			this.metAJour(infoString);
			break;
		default:
			// Should not be executed
			System.err.println("roundStart() Error");
			break;
    	}

		if(!this.gameStopped) {
			if(this.IAType==3) this.metAJour();
			else if (this.IAType != 0 && this.isIaRound()) {
				this.metAJour();
			}
		}
	}

	private void endProcedure() {
		System.out.println("Enter the endProcedure");
		this.getLastCardPlayer = 0;
		// if distance <= 5, comparer player's card of who has the most card of the distance
		if(this.pg.getDistance() <= 5) {
			int blancNb = 0, noirNb = 0;
			for(Carte c : this.pg.getBlancCartes()) {
				if(c.getValue() == this.pg.getDistance()) blancNb++;
			}
			for(Carte c : this.pg.getNoirCartes()) {
				if(c.getValue() == this.pg.getDistance()) noirNb++;
			}
			if(blancNb < noirNb) {
				this.pg.getNoir().incrementPoint();
				this.sendLoseSignal(1, "he has less card of value " + this.pg.getDistance());
			}
			else if(blancNb > noirNb) {
				this.pg.getBlanc().incrementPoint();
				this.sendLoseSignal(2, "he has less card of value " + this.pg.getDistance());
			}
			else {
				compareDistance();
			}
		}else {
			compareDistance();
		}
	}

	private void compareDistance() {
		int distBlanc = this.pg.getBlanc().getDistToStartPlace();
		int distNoir = this.pg.getNoir().getDistToStartPlace();
		// la joueur dont la distance entre joueur et départ est la plus grand obtient un point
		// puis recommencer le prochain Round
		int i=-1;
		if(distBlanc > distNoir) {
			this.pg.getBlanc().incrementPoint();
			i=2;
		}
		else if(distNoir > distBlanc) {
			this.pg.getNoir().incrementPoint();
			i=1;
		}
		if(i!=-1) {
			if(this.IAType != 3) this.sendLoseSignal(i, "the other player travelled further from his start place.");
			else this.restartNewRound();
		}
		else {
			if(this.IAType != 3) this.sendLoseSignal(i, "both player travelled the same distance.");
			else this.restartNewRound();
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
		
		System.out.println("Reste = " + this.pg.getReste().size());
		
		if(this.pg.getReste().size() == 0 && this.getLastCardPlayer == 0) {
			System.out.println("Someone gets the last card.");
			this.getLastCardPlayer = new Integer(this.pg.getTourCourant());
			//this.getLastCardPlayer = this.pg.getTourCourant();
		}
		// reinitialiser l'état courant et les cartes sélectionnées enfin changer le tour

		this.pg.setWaitStatus(0);
		this.pg.initialiseSelected();
		this.pg.setDirectionDeplace(0);
		this.changetTour();
		// on ajoute l'action courant dans l'historique
		hist.addCopy(this.currentAction);
		System.out.println(this.currentAction.toString());
		this.metAJour(this.currentAction.toString());
		System.out.println(this.hist.toString());
		this.currentAction.clear();
		// commencer le tour de joueur suivant
		this.roundStart(att);
	}
	
	public boolean confirmReceived() {
		Carte c;
		int nbSelected;
		switch(this.pg.getWaitStatus()) {
		case 1:
			c = this.getSelectedCard();
			nbSelected = this.getNBSelectedCard();
			// si la valeur de carte de dernier attaque égale à carte choisi, et le nombre de cartes choisi égale à cel de dernier attaque
			// on peut jouer avec cette carte
			if(c == null) {
				System.out.println("You must pick a card to parry !");
				return false;
			}
			else if(c.getValue() == this.pg.getLastAttack().getAttValue().getValue() && nbSelected == this.pg.getLastAttack().getAttnb()) {
				this.jouerCarte();
				this.currentAction.appendParryAction(c, nbSelected);
				if(this.pg.getTourCourant() != this.getLastCardPlayer) enterE3();
				else endProcedure();
				return true;
			}else {
				//sinon on ne peut pas défendre avec cette carte choisi
				System.out.println(	"You cannot parry the direct attack of (" +
									this.pg.getLastAttack().getAttValue().getValue() + 
									", " + 
									this.pg.getLastAttack().getAttnb() + 
									") with the selection of (" +
									c.getValue() + ", " + nbSelected + ").");
				return false;
			}
		case 2:
			c = this.getSelectedCard();
			// si on ne peut pas retraiter avec cette carte, on affiche des infos pour rechoisir
			if(this.pg.getPlayerCourant().getDistToStartPlace() < c.getValue()) {
				System.out.println("You cannot retreat due to the size of the playground (case 2).");
				return false;
			}
			// sinon on peut jouer avec action retraiter
			else{
				this.jouerCarte();
				this.currentAction.appendRetreatAction(c);
				this.retreat(c.getValue());
				if(this.pg.getTourCourant() != this.getLastCardPlayer) this.roundEnd(new Attack(AttackType.NONE, null, 0));
				else endProcedure();
				return true;
			}
		case 3:
			// 1. si on n'a pas d'attaque à parer , on concatener le string d'action
			if(this.currentAction.getActionString().equals("")) this.currentAction.appendNoParryAction();
			c = this.getSelectedCard();

			// 2. si on n'a pas encore choisi la carte, afficher l'info
			if(c == null) {
				this.infoString += ("\nYou must pick a card!");
				this.metAJour(infoString);
				return false;
			}

			// 3. si on a bien choisi la carte, on vérifie
			else if(c.getValue() > this.pg.getDistance() && this.pg.getDirectionDeplace() == 1) {
				// 3.1 si la valeur de carte est supérieur à distance entre joueurs et avec la direction "avancer"
				// afficher l'info de ne pas dépasser
				this.infoString += ("\nYou cannot surpass the other player");
				this.metAJour(infoString);
				return false;
			}
			else if(this.pg.getDirectionDeplace() == 2) {
				// 3.2 si la direction de movement est "retrait"
				
				if(this.pg.getPlayerCourant().getDistToStartPlace() < c.getValue()) {
					// si la distance depuis point départ est inférieur à la valeur de carte, on ne peut pas retraiter puis afficher l'info
					this.infoString += ("\nYou cannot retreat due to the size of the playground (case 3).");
					this.metAJour(infoString);
					return false;
				}
				// sinon on peut déplacer avec cette carte
				else {
					this.phaseDeplacer(c);
					return true;
				}
			}else {
				// 3.3 on peut soit retraiter soit avancer avec carte inférieur à
				// la distance depuis point départ ou la distance entre deux joueurs
				this.phaseDeplacer(c);
				return true;
			}
		case 4:
			if(this.pg.getDirectionDeplace() == 3) {
				// on annule d'attaquer avec carte admissible, donc rien à faire pour ennemis puis round courant termine
				System.out.println("You've chose not to attack the enemy.");
				this.currentAction.appendNoAttackAction();
				this.roundEnd(new Attack(AttackType.NONE, null, 0));
				return true;
			}else if(this.pg.getDirectionDeplace() == 1) {
				// Indirect Attack
				Carte indirectCarte = this.getSelectedCard();
				nbSelected = this.getNBSelectedCard();
				// si la carte choisi égale à la distance entre deux joueurs,
				// on joue avec cette carte et l'ajoute dans liste d'action enfin finaliser round courant
				if(indirectCarte.getValue() == this.pg.getDistance()) {
					this.jouerCarte();
					this.currentAction.appendIndirectAttackAction(indirectCarte, nbSelected);
					this.roundEnd(new Attack(AttackType.INDIRECT, indirectCarte, nbSelected));
					return true;
				}else {
					// sinon afficher l'info "ne peut pas attaquer avec cette carte car distance différent de celle entre deux joueurs"
					System.out.println("You cannot attack with the selection of (" +
							indirectCarte.getValue() + ", " + nbSelected + 
							") because the distance is " + this.pg.getDistance() + ".");
					return false;
				} 
			}
			return false;
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
					if(this.pg.getTourCourant() != this.getLastCardPlayer) enterE3();
					else endProcedure();
					return true;
				}else {
					// si l'un des deux n'est pas satisfait, on affiche l'info de "ne peut pas défendre attaque indirect avec carte choisi"
					System.out.println(	"You cannot parry the indirect attack of (" +
										this.pg.getLastAttack().getAttValue().getValue() +
										", " +
										this.pg.getLastAttack().getAttnb() +
										") with the selection of (" +
										c.getValue() + ", " + nbSelected + ").");
					return false;
				}
			}else if(this.pg.getDirectionDeplace() == 2){
				// Retreat
				c = this.getSelectedCard();
				if(this.pg.getPlayerCourant().getDistToStartPlace() < c.getValue()) {
					System.out.println("You cannot retreat due to the size of the playground.");
					return true;
				}
				else {
					this.jouerCarte();
					this.currentAction.appendRetreatAction(c);
		    		this.retreat(c.getValue());
		    		if(this.pg.getTourCourant() != this.getLastCardPlayer) this.roundEnd(new Attack(AttackType.NONE, null, 0));
		    		else endProcedure();
		    		return true;
				}
			}else {
				System.out.println("You have to parry the attack or retreat !");
				return false;
			}
	
		default:
			// Should not be executed
			System.err.println("Line 323 : confirmReceived() Error");
			return false;	
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

	public String generateSaveString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.pg.generateSaveString());
		sb.append("CurrentAction:" + this.currentAction.actionString + ";\n");
		sb.append("Historique:" + this.hist.generateHistString() + ";\n");
		sb.append("IAType:" + this.IAType + ";\n");
		sb.append("getLastCardPlayer:" + this.getLastCardPlayer + ";\n");
		return sb.toString();
	}

	public void setCurrentActionByString(String string) {
		String params[] = string.split(":");
		if(params.length == 1) this.currentAction.setActionString("");
		else this.currentAction.setActionString((params[1]));
	}
	
	@Override
	public void sendLoseSignal(int i, String s) {
		this.gameStopped = true;
		super.sendLoseSignal(i, s);
	}
	
	public void setHistoriqueByString(String string) {
		String params[] = string.split(":");
		this.hist = new Historique();
		if(params.length != 1) {
			String actions[] = params[1].split(" ");
			for(int i=0; i<actions.length; i++) this.hist.add(new Action(actions[i]));
		}
	}
	
	public void setAITypeByString(String string) {
		String params[] = string.split(":");
		this.IAType = Integer.parseInt(params[1]);
	}

	public void setgetLastCardPlayerByString(String string) {
		String params[] = string.split(":");
		this.getLastCardPlayer = Integer.parseInt(params[1]);
	}
}
