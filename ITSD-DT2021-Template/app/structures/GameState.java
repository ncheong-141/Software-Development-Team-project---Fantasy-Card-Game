package structures;

import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.ComputerPlayer;
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

	private Board gameBoard;
	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private Avatar humanAvatar;
	private Avatar computerAvatar;
	private int turnCount;
	private boolean playerDead;
	private Player turnOwner;
	
	public GameState() {
		turnCount = 0;
		playerDead = false;
		playerOne = new HumanPlayer();
		playerTwo = new ComputerPlayer();
		turnOwner = playerOne;
		
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
	
	public void gameOver() {
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
	
	Board board; 
	
	public GameState(ActorRef out) {
		
		board = new Board(out); 
	}
	
	
	/* Getters*/ 
	public Board getBoard() {
		return board; 
	}
	
}
