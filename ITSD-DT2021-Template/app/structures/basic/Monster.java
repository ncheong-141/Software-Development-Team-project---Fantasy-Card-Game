package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

public class Board {
	private Tile [][] gameBoard;
	private final int width;
	private final int heigth;
	private Tile humanStart;
	private Tile computerStart;
	private ActorRef out;
	
	public Board(ActorRef o) {
		width = 9;
		heigth = 5;
		this.out = o;
		gameBoard = new Tile[heigth][width];
		for (int i = 0; i<gameBoard.length; i++) {
			for (int k = 0; k<gameBoard[0].length; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(i, k);
				gameBoard [i][k].free = true; 	
			}
		}
		
		humanStart = gameBoard[3][2];
		computerStart = gameBoard[3][8];
	}

	public Tile getHumanStart() {
		return humanStart;
	}

	public Tile getComputerStart() {
		return computerStart;
	}

	public void drawBoard() {
		for (int i = 0; i<heigth; i++) {
			for (int k = 0; k<width; k++) {
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
