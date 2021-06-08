package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

public class Board {
	private Tile [][] gameBoard;
	private final int Y;
	private final int X;
	private Tile humanStart;
	private Tile computerStart;
	private ActorRef out;
	
	public Board(ActorRef o) {
		X = 9;
		Y = 5;
		this.out = o;
		gameBoard = new Tile[Y][X];
		for (int i = 0; i<Y; i++) {
			for (int k = 0; k<X; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(k, i);
						
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

	public void drawBoard() {
		for (int i = 0; i<Y; i++) {
			for (int k = 0; k<X; k++) {
				BasicCommands.drawTile(out, gameBoard [i][k],0);
						
			}
		}
	}
	
	
	public Tile[][] getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(Tile[][] gameBoard) {
		this.gameBoard = gameBoard;
	}
	
	public Tile getTile(int h, int w) {
		return gameBoard[h][w];
	}
	

}
