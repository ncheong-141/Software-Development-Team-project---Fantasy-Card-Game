package events;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.ComputerPlayer;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.abilities.Ability;
import structures.basic.abilities.AbilityToUnitLinkage;
import structures.basic.abilities.Call_IDs;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = initalize
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		/**===========================**/
		gameState.userinteractionLock();
		/**===========================**/


		// Initialising ability to unit linkage data to reference whenever loading units. 
		AbilityToUnitLinkage.initialiseUnitAbilityLinkageData();

		boardAvatarSetUp(out,gameState,message);
		playerCardSetUp(out, gameState, message);

		ArrayList<Tile> test = gameState.getBoard().moves(1, 2, 2, gameState.getPlayerOne());
		
		for (Tile t : test ) System.out.println(t);
		
		/**===========================**/
		gameState.userinteractionUnlock();
		/**===========================**/			
	}
	
	private static void boardAvatarSetUp(ActorRef out, GameState g, JsonNode message) {

		//ArrayList<Tile> boardTiles = g.getBoard().getAllTilesList();
		
		//GeneralCommandSets.drawBoardTiles(out, boardTiles, 0);
		Board board = g.getBoard();
		
		for (int i = 0; i<board.getGameBoard().length; i++) {
			for (int k = 0; k<board.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, board.getGameBoard()[i][k], 0);
			}
			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		Avatar humanAvatar = g.getHumanAvatar();
		Avatar computerAvatar = g.getComputerAvatar();
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		//display avatars on board

		Tile tOne = g.getBoard().getTile(1, 2);
		Tile tTwo = g.getBoard().getTile(7, 2);
		humanAvatar.setPositionByTile(tOne);
		computerAvatar.setPositionByTile(tTwo);

		BasicCommands.drawUnit(out, humanAvatar, tOne);
		tOne.addUnit(humanAvatar);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, humanAvatar, humanAvatar.getAttackValue());
		BasicCommands.setUnitHealth(out, humanAvatar, humanAvatar.getHP());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}				
				
		BasicCommands.drawUnit(out, computerAvatar, tTwo);	
		tTwo.addUnit(computerAvatar);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, computerAvatar, computerAvatar.getAttackValue());
		BasicCommands.setUnitHealth(out, computerAvatar, computerAvatar.getHP());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}	
		
		//g.getPlayerTwo().setGameBoard(g.getBoard());
		//try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}	
	}
	
	private static void playerCardSetUp(ActorRef out, GameState g, JsonNode message) {

		g.getPlayerOne().setMana(2);
		g.getPlayerTwo().setMana(9);

		
		BasicCommands.setPlayer1Health(out, g.getPlayerOne());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setPlayer1Mana(out, g.getPlayerOne());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setPlayer2Health(out, g.getPlayerTwo());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setPlayer2Mana(out, g.getPlayerTwo());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		int i = 0;
		
		for (Card c : g.getTurnOwner().getHand().getHandList()) {
			BasicCommands.drawCard(out, c, i, 0);
			i++;
		}

	}
	



	private static void boardPrintAllMethods(ActorRef out, GameState gameState) {
//		
//		
//		Board board = gameState.getBoard();
//		
//		for (int i = 0; i<board.getGameBoard().length; i++) {
//			for (int k = 0; k<board.getGameBoard()[0].length; k++) {
//				BasicCommands.drawTile(out, board.getGameBoard()[i][k], 0);
//			}
//			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
//		}
//		
//		
//		Tile currentTile = gameState.getBoard().getTile(5, 3);
//		
//		Card como = BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, StaticConfFiles.u_comodo_charger, 154, Card.class);
//		Monster u_como = BasicObjectBuilders.loadMonsterUnit(como.getConfigFile(), como, (Player) gameState.getPlayerOne(), Monster.class);
//		
//		u_como.toggleCooldown();
//		currentTile.addUnit(u_como);
//		u_como.setPositionByTile(currentTile);
//		
//		
//		BasicCommands.addPlayer1Notification(out, "All free tiles", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().allFreeTiles(), 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//		
//		BasicCommands.addPlayer1Notification(out, "adjTiles", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().adjTiles(currentTile) , 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//
//		
//		BasicCommands.addPlayer1Notification(out, "cardinally adjacent", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().cardinallyAdjTiles(currentTile) , 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//
//		
//		BasicCommands.addPlayer1Notification(out, "Actionable tiles", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().unitAllActionableTiles(5, 3, u_como.getAttackRange(), u_como.getMovesLeft()) , 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//
//		
//		BasicCommands.addPlayer1Notification(out, "Attackable tiles", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().unitAttackableTiles(5, 3, u_como.getAttackRange(), u_como.getMovesLeft()) , 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//
//		
//		BasicCommands.addPlayer1Notification(out, "Moveable tiles", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().unitMovableTiles(5, 3, u_como.getMovesLeft()), 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//		
//		BasicCommands.addPlayer1Notification(out, "Reachable tiles", 4);
//		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().reachableTiles(5, 3, u_como.getMovesLeft()), 2);
//		GeneralCommandSets.threadSleepOverride(4000);
//		GeneralCommandSets.boardVisualReset(out, gameState);
//				
	}

}


