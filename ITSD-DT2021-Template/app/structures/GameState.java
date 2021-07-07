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
	private ActorRef out;						// Do we need this?
	
	private ArrayList<Tile> tileAdjustedRangeContainer;		// Container array of tiles which store tiles to be highlight due to Abilities or anything else that requires distinct highlighting

	private Monster trackMonster; 				// YC added
	private Tile monsterLocation; 				// YC added
	
	private Deck deckPlayerOne;
	private Deck deckPlayerTwo;
	
	// Need to remove
	EndTurnClicked e; 
	
	/** DEBUG/TESTING VARIABLES (initialised in own method called externally) */
	private HumanPlayer playerTwoHuman; 
	

	/** Constructor **/
	public GameState() {

		/* Set attributes */ 
		turnCount = 0;
		playerDead = false;
		turnOwner = playerOne;
		
		tileAdjustedRangeContainer = new ArrayList<Tile>(); 
		
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
		
		for (Card c : playerOne.getDeck().getCardList()) {
			System.out.println("cardname " +c.getCardname());
		}
		// Set hands
		Hand handPlayerOne = new Hand();
		playerOne.setHand(handPlayerOne);
		handPlayerOne.initialHand(deckPlayerOne);

		Hand handPlayerTwo = new Hand();
		playerTwo.setHand(handPlayerTwo);
		handPlayerTwo.initialHand(deckPlayerTwo);

		
		// Set turn owner
		this.setTurnOwner(playerOne);
		
		// Board instantiation (Change Avatars to be instantiated in initialise methods and remove Avatar from gameState) 
		gameBoard = new Board();
		
		// Avatar instantiation
		humanAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, playerOne, Avatar.class);
		computerAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, playerTwo, Avatar.class);
		
		//playerOne.setAvatar(humanAvatar);
		//playerTwo.setAvatar(computerAvatar);
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
	
	public void setTwoPlayerMode(HumanPlayer h1) {
		playerTwoHuman = h1; 

		Deck deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		playerTwoHuman.setDeck(deckPlayerTwo);
		
		Hand handPlayerTwo = new Hand();
		playerTwoHuman.setHand(handPlayerTwo);
	}
	
	public HumanPlayer getPlayerTwoHuman() {
		return playerTwoHuman;
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

	/** AI methods **/
	public void computerEnd() {  
		
		e.emptyMana(this); //empty mana for player who ends the turn
		e.toCoolDown(this); //switch avatars status for current turnOwner
	    deselectAllEntities();
		GeneralCommandSets.boardVisualReset(this.out, this); 
		deselectAllEntities();	 //current turnOwner Hand is off?

		getTurnOwner().getHand().drawCard(this.getTurnOwner().getDeck());

		turnChange(); // turnOwner exchanged	
		if (e.isDeckEmpty(this)) {  //check if both players have enought card in deck left for new turn
			gameOver();  // if not, gameover(?)
		}
		e.giveMana(this); //give turnCount mana to the player in the beginning of new turn
		e.toCoolDown(this); //switch avatars status for new turnOwner in the beginning of new turn
		//getTurnOwner().getHand().setPlayingMode(true); //current turnOwner hand turn on
	}
	
	
	// YC add
	public Tile locateMonster(Monster trackMonster) {
		this.monsterLocation = this.getBoard().getTile(trackMonster.getPosition().getTilex(), trackMonster.getPosition().getTiley());
		return monsterLocation;
	}
	
	
	public void setDeckForStart() {	
		deckPlayerOne = new Deck();
		deckPlayerOne.deckOne();
		playerOne.setDeck(deckPlayerOne);
		
		deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		playerTwo.setDeck(deckPlayerTwo);
	
	}
	
	public void setHandForStart() {
		playerOne.getHand().initialHand(deckPlayerOne);
		playerTwo.getHand().initialHand(deckPlayerTwo);
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
		if (this.getTurnOwner() instanceof HumanPlayer) {
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
	
}
