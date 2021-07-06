package structures;

import events.EndTurnClicked;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.ComputerPlayer;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.abilities.Ability;
import structures.basic.abilities.Call_IDs;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.*;


/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	/** GameState attributes **/
	private Board 			gameBoard;			// Board object which holds all Unit positions aswell as contains operations to find specific tiles sets. 
	private HumanPlayer 	playerOne;			// Player one, the human player which holds all data for the player such as Hand and Deck for holding current cards. Also holds the control flow for drawing Cards from a Deck etc 
	private ComputerPlayer 	playerTwo;			// Player two, computer player which holds the same as the above + AI logic for ranking combinations of instructions and actioning them. 
	private Avatar 			humanAvatar;		// Do we need avatars in gameState? can it not just be in Board? 
	private Avatar 			computerAvatar;
	private int			 	turnCount;			// Tracker variable for the current number of turns 
	private static boolean 	playerDead;			// Boolean variable which is set when either avatar is defeated
	private Player 			turnOwner;			// The current turn owner of the game, refered to for certain checks such as having permission to click (the human player should not be able to select anything during the AI turn) 
	private EndTurnClicked e;					// 
	private ActorRef out;						// Do we need this?


	
	private Deck deckPlayerOne;
	private Deck deckPlayerTwo;

	/** Constructor **/
	public GameState() {
		
		/* Set attributes */ 
		turnCount = 0;
		playerDead = false;
		turnOwner = playerOne;
		
		// Deck instantiations 
		Deck deckPlayerOne = new Deck(); 
		deckPlayerOne.deckOne();
		

		//Deck deckPlayerTwo = new Deck();
		//deckPlayerTwo.deckTwo();
		
		// Instantiate players 								
		playerOne = new HumanPlayer();
		playerOne.setDeck(deckPlayerOne);
		
		
		
		playerTwo = new ComputerPlayer();
		//playerTwo.setGameBoard(gameBoard);
		//playerTwo.setDeck(deckPlayerTwo);
		//playerTwo.setDummy();
		
		for (Card c : playerOne.getDeck().getCardList()) {
			System.out.println("cardname " +c.getCardname());
		}
		// Set hands
		Hand handPlayerOne = new Hand();
		playerOne.setHand(handPlayerOne);
		handPlayerOne.initialHand(deckPlayerOne);

		//Hand handPlayerTwo = new Hand();
		//playerTwo.setHand(handPlayerTwo);
		//handPlayerTwo.initialHand(deckPlayerTwo);

		
		// Set turn owner
		this.setTurnOwner(playerOne);
		
		// Board instantiation (Change Avatars to be instantiated in initialise methods and remove Avatar from gameState) 
		gameBoard = new Board();
		humanAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, playerOne, gameBoard, Avatar.class);
		//humanAvatar.setOwner(playerOne);

		computerAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, playerTwo, gameBoard, Avatar.class);
		//computerAvatar.setOwner(playerTwo);
		
		System.out.println("board: " + this.getBoard());
		System.out.println();
		System.out.println("human avatar owner : " + this.humanAvatar.getOwner());
		System.out.println();
		System.out.println("Computer avatar owner : " + this.computerAvatar.getOwner());
	}

	/** GameState methods: Getters and setters + some helper methods**/

	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}


	public boolean isPlayerDead() {
		return playerDead;
	}

	public void setPlayerDead(boolean playerDead) {
		this.playerDead = playerDead;
	}

	public Player getTurnOwner() {
		return turnOwner;
	}

	public void setTurnOwner(Player turnOwner) {
		this.turnOwner = turnOwner;
	}

	public void turnChange() {
		if (turnOwner == playerOne) {
			turnOwner = playerTwo;
		}

		else turnOwner = playerOne;

		turnCount++;
	}

	public HumanPlayer getPlayerOne() {
		return playerOne;
	}

	public ComputerPlayer getPlayerTwo() {
		return playerTwo;
	}

	// Potentially remove
	public Avatar getHumanAvatar() {
		return humanAvatar;
	}

	public Avatar getComputerAvatar() {
		return computerAvatar;
	}

	// Is this necessary? 
	public void setPlayers(HumanPlayer h, ComputerPlayer c) {
		playerOne = h;
		playerTwo = c;
	}

	public static void gameOver() {
		playerDead = true;		
	}

	// Errr we have two of these!
	public Board getBoard() {
		return gameBoard; 
	}


	/** Entity selection helper methods **/

	// Deselects Card and Unit (if selected)
	public void deselectAllEntities() { 

		// If there is a selected unit
		if(this.getBoard().getUnitSelected() != null) {
			this.getBoard().setUnitSelected(null);
		}

		// If there is a card selected in turn owner hand
		if(this.getTurnOwner().getHand().getSelectedCard() != null) {
			this.getTurnOwner().getHand().setSelectedCard(null);
		}

	}

	/** methods to change GameState data when EndTurn**/
	public void endTureStateChange() {  
		
		emptyMana(); //empty mana for player who ends the turn
	//	e.toCoolDown(this); //switch avatars status for current turnOwner
	    deselectAllEntities();
		GeneralCommandSets.boardVisualReset(this.out, this);  //visual
		if (isDeckEmpty()) {  //check if current player has enough card in deck left to be added into hand
			gameOver();  // if not, gameover
		} else {
			getTurnOwner().getHand().drawCard(this.getTurnOwner().getDeck());  //if holds enough card, get card from deck
		}
		turnChange(); // turnOwner exchanged	
		giveMana(); //give turnCount mana to the player in the beginning of new turn
	//	e.toCoolDown(this); //switch avatars status for new turnOwner in the beginning of new turn
	}
	
	
	//give turnCount mana to the player just in the beginning of new turn	
	public void giveMana() {  
			getTurnOwner().setMana(getTurnCount());  
	}
	
	//empty mana for player who ends the turn
	public void emptyMana() {
		getTurnOwner().setMana(0);
	}
	
	// check if players decks are are empty 
	public boolean isDeckEmpty() {
		ArrayList<Card> turnOwnerDeck = getTurnOwner().getDeck().getCardList();
		int deckCardLeft = turnOwnerDeck.size();
		if(deckCardLeft < 1) {
			return true;
		}
		return false;
	}
	
	
	//cooldown monsters
	public void toCoolDown() {
		ArrayList<Monster> toCool = getBoard().friendlyUnitList(getTurnOwner());				
		for(Monster m : toCool){
				m.toggleCooldown();				
			}
		}
	
	/** methods to change GameState data when EndTurn**/
	
	
	
	

	
	public void setDeckForStart() {	
		deckPlayerOne = new Deck();
		deckPlayerOne.deckOne();
		playerOne.setDeck(deckPlayerOne);
		
		deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		playerOne.setDeck(deckPlayerTwo);
	
	}
	
	public void setHandForStart() {
		playerOne.getHand().initialHand(deckPlayerOne);
		playerTwo.getHand().initialHand(deckPlayerTwo);
	}
	
	
	/** Generalised method for finding if any monsters require their ability to be executed.
	 * 	Called in relevant places
	 ***/
	public void checkMonsterAbilityActivation(Call_IDs callID, Monster targetMonster) {

		// Loop over all tiles
		for (Tile tile : this.getBoard().getAllTilesList()) {

			// Container for containing all executable abilities
			ArrayList<Ability> abilityContainer = new ArrayList<Ability>(2); 

			// Loop over abilities and get executing ones
			for (Ability ability : tile.getUnitOnTile().getMonsterAbility()) {

				if (ability.getCallID() == callID) {
					abilityContainer.add(ability);
				}
			}

			// Execute all contstruction abilities first
			for (Ability ability : abilityContainer) {

				if (ability.getCallID() == Call_IDs.construction) {
					ability.execute(targetMonster, this); 
					abilityContainer.remove(ability);		// Remove this ability to not execute twice
				}
			}

			// Execute the rest 
			for (Ability ability : abilityContainer) {
				ability.execute(targetMonster, this);
			}
		}
	}
			// To do:
			// Move deck player-setting and instantiation into the (separate Human/Computer-) Player constructor
			// Move hand instantiation/set up from gamestate into Player constructor
			// Move AbilityUnitLinkage call into GameState
	
}
