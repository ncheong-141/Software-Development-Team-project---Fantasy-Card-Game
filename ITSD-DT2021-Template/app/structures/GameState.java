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
	
	// Need to remove
	EndTurnClicked e; 
	
	/* Debug/two player mode */
	private boolean 		twoPlayerMode;



	/** Constructor **/
	public GameState() {
		
		/* Set attributes */ 
		turnCount = 1;
		playerDead = false;
		
		tileAdjustedRangeContainer = new ArrayList<Tile>(); 
		

		/* two player mode (comment or uncomment */
		twoPlayerMode(); 
		
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
		

		// Chiara's Avatar fix
//		humanAvatar = (Avatar) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Avatar.class);
//		humanAvatar.avatarSetUp();
//		humanAvatar.setOwner(playerOne);
//		
//		computerAvatar = (Avatar) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Avatar.class);
//		computerAvatar.avatarSetUp();
//		computerAvatar.setOwner(playerTwo);
//		
//		System.out.println("board: " + this.getBoard());
//		System.out.println();
//		System.out.println("human avatar owner : " + this.humanAvatar.getOwner());
//		System.out.println();
//		System.out.println("Computer avatar owner : " + this.computerAvatar.getOwner() );

		// Avatar instantiation
		humanAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, playerOne, Avatar.class);
		computerAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, playerTwo, Avatar.class);

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
		else {
			turnOwner = playerOne;
		}

		turnCount++;
	}

	public Player getPlayerOne() {
		return  playerOne;
	}

	public Player getPlayerTwo() {
		return  playerTwo;
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

	public boolean isTwoPlayerMode() {
		return twoPlayerMode;
	}

	public void setTwoPlayerMode(boolean twoPlayerMode) {
		this.twoPlayerMode = twoPlayerMode;
	}
	
	
	
	
	/** Two player mode methods (used for debugging) **/

		
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
		int[] cardIDList1 = {0,1,2};
		
		for (int i = 0; i < cardIDList1.length; i++) {
			this.getPlayerOne().getHand().getHandList().add(drawDeck1.get(i));
			playerOne.getDeck().delCard(cardIDList1[i]);
		}
		playerOne.getHand().setCurr(cardIDList1.length);

		
		// Cards you want to start with from deck 2 (max 5)
		int[] cardIDList2 = {0,1,2};

		for (int i = 0; i < cardIDList2.length; i++) {
			this.getPlayerTwo().getHand().getHandList().add(drawDeck2.get(i));
			playerTwo.getDeck().delCard(cardIDList2[i]);
		}
		playerTwo.getHand().setCurr(cardIDList2.length);
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
		
		// Clear the temp TileRange container between actions 
		tileAdjustedRangeContainer.clear(); 
	}
	
	
	/** Methods to change GameState data when EndTurn**/
	public void endTurnStateChange(ActorRef out) {  

		//emptyMana(); //empty mana for player who ends the turn
		//	e.toCoolDown(this); //switch avatars status for current turnOwner
		emptyMana(); 	//empty mana for player who ends the turn

	    deselectAllEntities();
		GeneralCommandSets.boardVisualReset(out, this);  	//visual
		
		if (isDeckEmpty()) {  //check if current player has enough card in deck left to be added into hand
			gameOver();  // if not, gameover
		} else {
			
			getTurnOwner().getHand().drawCard(this.getTurnOwner().getDeck());  //if holds enough card, get card from deck
			
			Card card = turnOwner.getDeck().getCardList().get(0);
			int handPos = (turnOwner.getHand().getHandList().size())-1;
			BasicCommands.drawCard(out, card, handPos, 0);
			GeneralCommandSets.threadSleepLong();
		}
		
		turnChange(); // turnOwner exchanged	
		giveMana(); //give turnCount mana to the player in the beginning of new turn
		toCoolDown(); //switch avatars status for current turnOwner

		if (twoPlayerMode) {
			// redraw hand to humanplayer
			int oldCardListSize = this.getEnemyPlayer().getHand().getHandList().size(); 
			
			GeneralCommandSets.drawCardsInHand(out, this, oldCardListSize, this.getTurnOwner().getHand().getHandList());
		}
		
	}
	
	
	//give turnCount mana to the player just in the beginning of new turn	
	public void giveMana() {  

			getTurnOwner().setMana(getTurnCount()+1);  

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
		ArrayList<Monster> toCool = getBoard().friendlyUnitList(this.getTurnOwner());			
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
	
	public void computerEnd() {
		
	}
	
	/** methods to change GameState data when EndTurn**/
	
	
	
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
						}
					}

					// Execute the rest 
					for (Ability ability : abilityContainer) {
						ability.execute(targetMonster, this);
					}
				}

			}

		}
		
		return abilityFound; 
	}
	
	
	/** 
	 * Method for obtaining the enemy player reference 
	 */
	public Player getEnemyPlayer() {
		
		// Check if the turn owner is instance of human player, if so return the computer player
		if (this.getTurnOwner() == this.playerOne) {
			return this.getPlayerTwo(); 
		}
		else {
			return this.getPlayerOne(); 
		}
	}

	
	
			// To do:
			// Move deck player-setting and instantiation into the (separate Human/Computer-) Player constructor
			// Move hand instantiation/set up from gamestate into Player constructor
			// Move AbilityUnitLinkage call into GameState

//	public static void computerEnd() {
//		// TODO Auto-generated method stub
//		
//	}
	
}
