package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

public class Board {
	private Tile [][] gameBoard;
	//protected GameActor out;
	private final int width;
	private final int heigth;
	private ActorRef out;
	
	public Board(ActorRef o) {
		width =5;
		heigth = 9;
		this.out = o;
		gameBoard = new Tile[heigth][width];
		for (int i = 0; i<gameBoard.length; i++) {
			for (int k = 0; k<gameBoard[0].length; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(i, k);
						
			}
		}
	}
	
	public void drawBoard() {
		for (int i = 0; i<heigth; i++) {
			for (int k = 0; k<width; k++) {
				BasicCommands.drawTile(out, gameBoard [i][k],2);
						
			}
		}
	}
	public Tile[][] getGameBoard() {
		return gameBoard;
	}

}
