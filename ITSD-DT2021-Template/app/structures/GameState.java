package structures;

import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.ComputerPlayer;
import structures.basic.Deck;
import structures.basic.HumanPlayer;
import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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

	
	/** Constructor **/
	public GameState() {
	
		/* Set attributes */ 
		turnCount = 0;
		playerDead = false;
		turnOwner = playerOne;
				

		// Deck instantiations 
		Deck deckPlayerOne = new Deck(); 
		deckPlayerOne.deckOne();
		// playerOne.setDeck(deckPlayerOne);
				
		Deck deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		//playerTwo.setDeck(deckPlayerTwo);
		
		// Instantiate players 								// GOING TO NEED TO REMOVE DECKS SOON 
		playerOne = new HumanPlayer(deckPlayerOne);
		playerTwo = new ComputerPlayer(deckPlayerTwo);
		

		// Board instantiation (Change Avatars to be instantiated in initialise methods and remove Avatar from gameState) 
		gameBoard = new Board();
		humanAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, Avatar.class);
		humanAvatar.setOwner(playerOne, gameBoard);//assigning avatar to player and board - this could be done within player's class
		
		computerAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, Avatar.class);
		computerAvatar.setOwner(playerTwo, gameBoard);
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
	
	
	public Board getGameBoard() {
		return gameBoard;
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
		
		// call method to finish game
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
		if(this.getTurnOwner().getHand().isPlayingMode()) {
			this.getTurnOwner().getHand().setSelectedCard(null);
			this.getTurnOwner().getHand().setPlayingMode(false);
		}
		
	}

}
