package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

//=============================Class description =============================//
// this class builds the board that the player will play on
// this class stores a 2D array of Tile objects that represent the board
// this class contains methods to access different tiles on the board
//==========================================================================//

public class Board {

	//class variables
	private Tile [][] gameBoard;
	//since the board for this version of the game is set to given constant values
	//the board lenght on the X and Y axis is represented by constant integer values
	private final int Y;
	private final int X;
	//storing references to the tile where the human and computer avatar will start for ease of access
	//note: still to be implemented, not a fundamental feature
	private Tile humanStart;
	private Tile computerStart;
	
	public Board() {
		X = 9;
		Y = 5;
		gameBoard = new Tile[Y][X];
		for (int i = 0; i<Y; i++) {
			for (int k = 0; k<X; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(k, i);
				gameBoard [i][k].free = true; 		
			}
		}
		
		//humanStart = gameBoard[3][2];
		//computerStart = gameBoard[3][8];
	}


	public Tile getHumanStart() {
		return humanStart;
	}

	public Tile getComputerStart() {
		return computerStart;
	}

	//getter method to access the 2D array of Tiles
	public Tile[][] getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(Tile[][] gameBoard) {
		this.gameBoard = gameBoard;
	}
	
	//Method to access a specific tile on the board given the X and Y coordinates
	public Tile getTile(int x, int y) {
		return gameBoard[y][x];
	}
	

}
