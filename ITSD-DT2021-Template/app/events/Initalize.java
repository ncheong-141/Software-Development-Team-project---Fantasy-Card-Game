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
	//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		
		//create board
		Board board = new Board(out);
		board.drawBoard();
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		HumanPlayer p1 = new HumanPlayer();
		ComputerPlayer p2 = new ComputerPlayer();
		//create players (human and computer) --> player instantiation should trigger avatar instantiation 
		//to test placing avatar on board here avatar are instantiated manually
		
		Avatar humanAvatar =  BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, Avatar.class);
		humanAvatar.setOwner(p1, board);//assigning avatar to player and board - this could be done within player's class
		
		
		Avatar computerAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, Avatar.class);
		computerAvatar.setOwner(p2, board);
		
		
		//display avatars on board
		Tile tOne = board.getTile(3, 2);
		Tile tTwo = board.getTile(3, 8);
		
		BasicCommands.drawUnit(out, humanAvatar, tOne);
		
		BasicCommands.drawUnit(out, computerAvatar, tTwo);
		
		BasicCommands.setUnitAttack(out, humanAvatar, 2);
		BasicCommands.setUnitHealth(out, humanAvatar, p1.getHealth());
		
		BasicCommands.setUnitAttack(out, computerAvatar, 2);
		BasicCommands.setUnitHealth(out, computerAvatar, p2.getHealth());
	}

}


