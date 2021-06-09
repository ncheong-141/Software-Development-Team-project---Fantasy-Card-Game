package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.ComputerPlayer;
import structures.basic.HumanPlayer;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 

 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		
		//draw board
		Tile[][] board = gameState.getGameBoard().getGameBoard();
		for (int i = 0; i<board.length; i++) {
			for (int k =0; k<board[0].length; k++) {
				BasicCommands.drawTile(out, board[i][k], 0);;
			}
		}
		

		
		//display avatars on board
		Tile tOne = gameState.getGameBoard().getTile(2, 1);
		Tile tTwo = gameState.getGameBoard().getTile(2, 7);
		
		BasicCommands.drawUnit(out, gameState.getHumanAvatar(), tOne);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.drawUnit(out, gameState.getComputerAvatar(), tTwo);	
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		gameState.getHumanAvatar().setAttack(2);
		gameState.getComputerAvatar().setAttack(2);
		
		BasicCommands.setUnitAttack(out, gameState.getHumanAvatar(), gameState.getHumanAvatar().getAttack());
		BasicCommands.setUnitHealth(out, gameState.getHumanAvatar(), gameState.getHumanAvatar().getHealth());
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		
		BasicCommands.setUnitAttack(out, gameState.getComputerAvatar(), gameState.getComputerAvatar().getAttack());
		BasicCommands.setUnitHealth(out, gameState.getComputerAvatar(), gameState.getComputerAvatar().getHealth());
		
	}

}


