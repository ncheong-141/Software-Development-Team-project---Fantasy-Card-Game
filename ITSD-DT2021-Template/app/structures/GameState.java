package structures;
import events.EndTurnClicked;
import events.tileplaystates.GameplayStates;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.ComputerPlayer;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;
import commands.*;
import commands.GeneralCommandSets;


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

	private Monster trackMonster; 				// YC added
	private Tile monsterLocation; 				// YC added
	
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
		Hand handPlayerOne = new Hand();
		handPlayerOne.drawCard(deckPlayerOne);
		playerOne.setHand(handPlayerOne);

		Deck deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		Hand handPlayerTwo = new Hand();
		handPlayerTwo.drawCard(deckPlayerTwo);
		playerTwo.setHand(handPlayerTwo);


		// Instantiate players 								
		playerOne = new HumanPlayer();
		playerTwo = new ComputerPlayer(deckPlayerTwo);
		
		playerOne.setDeck(deckPlayerOne);


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
		if(this.getTurnOwner().getHand().getSelectedCard() != null) {
			this.getTurnOwner().getHand().setSelectedCard(null);
		}

	}

	/** AI methods **/
	public void computerEnd() {  
		
		e.emptyMana(); //empty mana for player who ends the turn
		e.toCoolDown(); //switch avatars status for current turnOwner
	    deselectAllEntities();
		GeneralCommandSets.boardVisualReset(out, this); 
		deselectAllEntities();	 //current turnOwner Hand is off?

		getTurnOwner().getHand().drawCard(out, this.getTurnOwner().getDeck());

		getTurnOwner().getHand().drawCard(this.getTurnOwner().getDeck());


		turnChange(); // turnOwner exchanged	
		if (e.isDeckEmpty()) {  //check if both players have enought card in deck left for new turn
			gameOver();  // if not, gameover(?)
		}
		e.giveMana(); //give turnCount mana to the player in the beginning of new turn
		e.toCoolDown(); //switch avatars status for new turnOwner in the beginning of new turn
		//getTurnOwner().getHand().setPlayingMode(true); //current turnOwner hand turn on
			
	}
	
	
	// YC add
	public Tile locateMonster(Monster trackMonster) {
		this.monsterLocation = this.getGameBoard().getTile(trackMonster.getPosition().getTilex(), trackMonster.getPosition().getTiley());
		return monsterLocation;
	}
	
	
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

}
