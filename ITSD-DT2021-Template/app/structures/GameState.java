package structures;
import events.EndTurnClicked;
import events.tileplaystates.GameplayStates;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.ComputerPlayer;
import structures.basic.Deck;
import structures.basic.HumanPlayer;
import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
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

	private Board gameBoard;
	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private Avatar humanAvatar;
	private Avatar computerAvatar;
	private int turnCount;
	private static boolean playerDead;
	private Player turnOwner;
	private EndTurnClicked e;
	private ActorRef out;
	private GameState gameState;

	public GameState() {
		
		turnCount = 0;
		playerDead = false;
		turnOwner = playerOne;
		
		
		//decks instantiation 

		Deck deckPlayerOne = new Deck(); 
		deckPlayerOne.deckOne();
		
		for (int i = 0; i < deckPlayerOne.getDeck().size(); i++) {
			System.out.println(deckPlayerOne.getDeck().get(i).getCardname());
		}
		//playerOne.setDeck(deckPlayerOne);
				
		Deck deckPlayerTwo = new Deck();
		deckPlayerTwo.deckTwo();
		//playerTwo.setDeck(deckPlayerTwo);
		
		playerOne = new HumanPlayer(deckPlayerOne);
		playerTwo = new ComputerPlayer(deckPlayerTwo);

		
		gameBoard = new Board();
		humanAvatar =  (Avatar) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Avatar.class);
		humanAvatar.setOwner(playerOne, gameBoard);//assigning avatar to player and board - this could be done within player's class
		
		
		computerAvatar = (Avatar) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Avatar.class);
		computerAvatar.setOwner(playerTwo, gameBoard);
		
	}
	
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

	public Board getGameBoard() {
		return gameBoard;
	}

	public HumanPlayer getPlayerOne() {
		return playerOne;
	}

	public ComputerPlayer getPlayerTwo() {
		return playerTwo;
	}

	public Avatar getHumanAvatar() {
		return humanAvatar;
	}

	public Avatar getComputerAvatar() {
		return computerAvatar;
	}

	public void setPlayers(HumanPlayer h, ComputerPlayer c) {
		playerOne = h;
		playerTwo = c;
	}
	
	public static void gameOver() {
		playerDead = true;
		
		// call method to finish game
	}
	
	public void turnChange() {
		if (turnOwner == playerOne) {
			turnOwner = playerTwo;
		}
		
		else turnOwner = playerOne;		
		turnCount++;
	}

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
	
	
	public void computerEnd() {  
		
		getTurnOwner().drawFromDeck(); //draw a card from deck for current turnOwner
		e.emptyMana(); //empty mana for player who ends the turn
		e.toCoolDown(); //switch avatars status for current turnOwner
	    deselectAllEntities();
		GeneralCommandSets.boardVisualReset(out, gameState); 
		getTurnOwner().getHand().setPlayingMode(false); //current turnOwner Hand is off?
		turnChange(); // turnOwner exchanged	
		e.giveMana(); //give turnCount mana to the player in the beginning of new turn
		e.toCoolDown(); //switch avatars status for new turnOwner in the beginning of new turn
		getTurnOwner().getHand().setPlayingMode(true); //current turnOwner hand turn on
	}
}
