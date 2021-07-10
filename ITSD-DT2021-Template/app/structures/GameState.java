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
import structures.basic.abilities.AbilityToUnitLinkage;
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
	private Player 			playerOne;			// Player one, the human player which holds all data for the player such as Hand and Deck for holding current cards. Also holds the control flow for drawing Cards from a Deck etc 
	private Player 			playerTwo;			// Player two, computer player which holds the same as the above + AI logic for ranking combinations of instructions and actioning them.	
	private Avatar 			humanAvatar;		// Do we need avatars in gameState? can it not just be in Board? 
	private Avatar 			computerAvatar;
	private int			 	turnCount;			// Tracker variable for the current number of turns 
	private static boolean 	playerDead;			// Boolean variable which is set when either avatar is defeated
	private Player 			turnOwner;			// The current turn owner of the game, refered to for certain checks such as having permission to click (the human player should not be able to select anything during the AI turn) 
	
	private ArrayList<Tile> tileAdjustedRangeContainer;		// Container array of tiles which store tiles to be highlight due to Abilities or anything else that requires distinct highlighting



	
	private Deck deckPlayerOne;
	private Deck deckPlayerTwo;
	 
	
	/* Debug/two player mode */
	private boolean 		twoPlayerMode;



	/** Constructor **/
	public GameState() {
		
		/* Set attributes */ 
		turnCount = 1;
		playerDead = false;
		
		tileAdjustedRangeContainer = new ArrayList<Tile>(); 
		
		// Initialising ability to unit linkage data to reference whenever loading units. 
		AbilityToUnitLinkage.initialiseUnitAbilityLinkageData();


		/* two player mode (comment or uncomment */
		//twoPlayerMode(); 
		
		if (twoPlayerMode != true) {
			
			// Deck instantiations 
			Deck deckPlayerOne = new Deck(); 
			deckPlayerOne.deckOne();
			
			Deck deckPlayerTwo = new Deck();
			deckPlayerTwo.deckTwo();
					
			// Instantiate players 								
			playerOne = new HumanPlayer();
			playerOne.setDeck(deckPlayerOne);
			playerTwo = new ComputerPlayer();
			playerTwo.setDeck(deckPlayerTwo);
			
			// Set hands
			Hand handPlayerOne = new Hand();
			playerOne.setHand(handPlayerOne);
			handPlayerOne.initialHand(deckPlayerOne);

			Hand handPlayerTwo = new Hand();
			playerTwo.setHand(handPlayerTwo);
			handPlayerTwo.initialHand(deckPlayerTwo);
		}

		
		// Set turn owner
		this.setTurnOwner(playerOne);
		
		// Board instantiation (Change Avatars to be instantiated in initialise methods and remove Avatar from gameState) 
		gameBoard = new Board();
		
		// Avatar instantiation
		humanAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, playerOne, Avatar.class);
		computerAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, playerTwo, Avatar.class);

		System.out.println("board: " + this.getBoard());
		System.out.println();
		System.out.println("human avatar owner : " + this.humanAvatar.getOwner());
		System.out.println();
		System.out.println("Computer avatar owner : " + this.computerAvatar.getOwner() );


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


	public Player getPlayerOne() {
		return  playerOne;
	}

	public Player getPlayerTwo() {
		return  playerTwo;
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
		else {
			turnOwner = playerOne;
		}

		turnCount++;
	}
	
	public Player getEnemyPlayer() {
		
		// Check if the turn owner is instance of human player, if so return the computer player
		if (this.getTurnOwner() == this.playerOne) {
			return this.getPlayerTwo(); 
		}
		else {
			return this.getPlayerOne(); 
		}
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

	public Board getBoard() {
		return gameBoard; 
	}
	
	public ArrayList<Tile> getTileAdjustedRangeContainer() {
		return tileAdjustedRangeContainer; 
	}
	
	public void setTileAdjustedRangeContainer(ArrayList<Tile> tilesToHighlight) {
		tileAdjustedRangeContainer = tilesToHighlight;
	}


	
	
	
	/** Two player mode methods (used for debugging) **/

	public boolean isTwoPlayerMode() {
		return twoPlayerMode;
	}

	public void setTwoPlayerMode(boolean twoPlayerMode) {
		this.twoPlayerMode = twoPlayerMode;
	}
		
	private void twoPlayerMode() {
		
		// Set switch
		twoPlayerMode = true; 
		
		// Instantite players 
		playerOne = new HumanPlayer();
		playerTwo= new HumanPlayer(); 

		// Deck instantiations 
		Deck deckPlayerOne = new Deck(); 
		deckPlayerOne.deckOne();
		
		Deck deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
				
		playerOne.setDeck(deckPlayerOne);
		playerTwo.setDeck(deckPlayerTwo);
		
		playerOne.setHand(new Hand());
		playerTwo.setHand(new Hand());
	
		/* Card and Hand setting */
		// Variables to shorten access
		ArrayList<Card> drawDeck1 = this.getPlayerOne().getDeck().getCardList();
		ArrayList<Card> drawDeck2 = this.getPlayerTwo().getDeck().getCardList();

		
		// Cards you want from deck 1 (max 5)
		int[] cardIDList1 = {6,7,8};
		
		for (int i = 0; i < cardIDList1.length; i++) {
			this.getPlayerOne().getHand().getHandList().add(drawDeck1.get(cardIDList1[i]));
			playerOne.getDeck().delCard(cardIDList1[i]);
		}
		playerOne.getHand().setCurr(cardIDList1.length);

		
		// Cards you want to start with from deck 2 (max 5)
		int[] cardIDList2 = {6,7,8};

		for (int i = 0; i < cardIDList2.length; i++) {
			this.getPlayerTwo().getHand().getHandList().add(drawDeck2.get(cardIDList2[i]));
			playerTwo.getDeck().delCard(cardIDList2[i]);
		}
		playerTwo.getHand().setCurr(cardIDList2.length);
	}
		

	
	/** Entity selection helper methods **/

	// Deselects Card and Unit (if selected)
	public void deselectAllEntities() { 

		// If there is a selected unit
		if(this.getBoard().getUnitSelected() != null) {
			this.getBoard().getUnitSelected().setProvoked(false);
			this.getBoard().setUnitSelected(null);

		}

		// If there is a card selected in turn owner hand
		if(this.getTurnOwner().getHand().getSelectedCard() != null) {
			this.getTurnOwner().getHand().setSelectedCard(null);
		}
		
		// Clear the temp TileRange container between actions 
		tileAdjustedRangeContainer.clear(); 
	}
	


	
	//give turnCount mana to the player just in the beginning of new turn	
	public void giveMana() {  
	    //give turnCount mana to the player just in the beginning of new turn   
	    
	            if ( getTurnCount() >= 9) {                //if it is the 9th turn or more than 9, setMan to 9 for both players
	                    getTurnOwner().setMana(9);
	            }else {       
	                if(getTurnOwner() == playerOne) {    //turncount +1 only when Human player start the new round of game
	                    this.turnCount = getTurnCount()+1;
	                    getTurnOwner().setMana(this.turnCount);
	                }
	                else {
	                    getTurnOwner().setMana(this.turnCount);
	                }
	            }
	   

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
	
	
	// Cooldown monsters
	public void toCoolDown() {
		ArrayList<Monster> toCool = getBoard().friendlyUnitList(this.getTurnOwner());	
		
		// Add avatars 
		if (this.getTurnOwner() == playerOne) {
			toCool.add(this.getHumanAvatar());
		}
		else {
			toCool.add(this.getComputerAvatar());
		}
		
		for(Monster m : toCool){
				m.toggleCooldown();				
		}
	}
	
	public void setMonsterCooldown(boolean value) {
		ArrayList<Monster> toCool = getBoard().friendlyUnitList(this.getTurnOwner());
		
		// Add avatars 
		if (this.getTurnOwner() == playerOne) {
			toCool.add(this.getHumanAvatar());
		}
		else {
			toCool.add(this.getComputerAvatar());
		}
		
		// Set cooldowns
		for(Monster m : toCool){
				m.setCooldown(value);	
		}
	}
	
		
	
	public void setDeckForStart() {	
		deckPlayerOne = new Deck();
		deckPlayerOne.deckOne();
		playerOne.setDeck(deckPlayerOne);
		
		deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		playerOne.setDeck(deckPlayerTwo);
	
	}	
	
	
	/** Generalised method for finding if any monsters require their ability to be executed.
	 * 	Called in relevant places
	 ***/
	public boolean checkMonsterAbilityActivation(Call_IDs callID, Monster targetMonster) {

		boolean abilityFound = false; 
		
		// Loop over all tiles
		for (Tile tile : this.getBoard().getAllTilesList()) {

			// Container for containing all executable abilities
			ArrayList<Ability> abilityContainer = new ArrayList<Ability>(2); 

			//Check if a unit is on the tile
			if (tile.getUnitOnTile() != null) {
				
				// Check if the unit has abilities
				if (tile.getUnitOnTile().getMonsterAbility() != null) {
					
					// Loop over abilities and get executing ones
					for (Ability ability : tile.getUnitOnTile().getMonsterAbility()) {

						if (ability.getCallID() == callID) {
							abilityContainer.add(ability);
							abilityFound = true;
						}
					}

					// Execute all contstruction abilities first
					for (Ability ability : abilityContainer) {

						if (ability.getCallID() == Call_IDs.construction) {
							ability.execute(targetMonster, this); 
							abilityContainer.remove(ability);		// Remove this ability to not execute twice
							System.out.println("Executing ability:" + ability);
						}
					}

					// Execute the rest 
					for (Ability ability : abilityContainer) {
						ability.execute(targetMonster, this);
						System.out.println("Executing ability:" + ability);
					}
				}

			}

		}
		
		return abilityFound; 
	}
	
	public boolean useAdjustedMonsterActRange() {
		return !this.getTileAdjustedRangeContainer().isEmpty();
	}
	

			// To do:
			// Move deck player-setting and instantiation into the (separate Human/Computer-) Player constructor
			// Move hand instantiation/set up from gamestate into Player constructor
			// Move AbilityUnitLinkage call into GameState

	public void computerEnd() {
		
		this.emptyMana(); 										// Empty mana for player who ends the turn
		this.deselectAllEntities();								// Deselect all entities
		

		// Check if the deck is empty, if so then gameover
		if (this.isDeckEmpty()) {  //check if current player has enough card in deck left to be added into hand
			gameOver(); 
		}
		
		this.setMonsterCooldown(true);	// Hard set all monsters on turn enders turn to cooldown
		this.turnChange(); 				// turnOwner exchanged	
		this.giveMana();			 		// Give turnCount mana to the player in the beginning of new turn
		//gameState.toCoolDown(); 				// Switch avatars status for current turnOwner
		this.setMonsterCooldown(false);
	
	
	}
	
}
