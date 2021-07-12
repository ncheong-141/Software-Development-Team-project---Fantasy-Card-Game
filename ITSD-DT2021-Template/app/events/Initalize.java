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
		
		/**===========================**/
		gameState.userinteractionUnlock();
		/**===========================**/			
	}
	
	private static void boardAvatarSetUp(ActorRef out, GameState gameState, JsonNode message) {

		// Set board reference
		Board board = gameState.getBoard();
		
		// Draw the board
		for (int i = 0; i<board.getGameBoard().length; i++) {
			for (int k = 0; k<board.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, board.getGameBoard()[i][k], 0);
			}
			GeneralCommandSets.threadSleep();
		}
		GeneralCommandSets.threadSleep();
		
		// Set up avatar references
		Avatar humanAvatar = gameState.getHumanAvatar();
		Avatar computerAvatar = gameState.getComputerAvatar();
		GeneralCommandSets.threadSleep();
		
	
		// Setting avatars' starting position
		Tile tOne = gameState.getBoard().getTile(1, 2);
		Tile tTwo = gameState.getBoard().getTile(7, 2);
		humanAvatar.setPositionByTile(tOne);
		computerAvatar.setPositionByTile(tTwo);

		// Drawing avatarts on the board
		BasicCommands.drawUnit(out, humanAvatar, tOne);
		tOne.addUnit(humanAvatar);
		GeneralCommandSets.threadSleep();
		BasicCommands.setUnitAttack(out, humanAvatar, humanAvatar.getAttackValue());
		BasicCommands.setUnitHealth(out, humanAvatar, humanAvatar.getHP());
		GeneralCommandSets.threadSleep();
				
		BasicCommands.drawUnit(out, computerAvatar, tTwo);	
		tTwo.addUnit(computerAvatar);
		GeneralCommandSets.threadSleep();
		BasicCommands.setUnitAttack(out, computerAvatar, computerAvatar.getAttackValue());
		BasicCommands.setUnitHealth(out, computerAvatar, computerAvatar.getHP());
		GeneralCommandSets.threadSleep();		
	}
	
	private static void playerCardSetUp(ActorRef out, GameState gameState, JsonNode message) {
	
		// Set mana for first turn
		gameState.giveMana();
		
		// Set player stats
		BasicCommands.setPlayer1Health(out, gameState.getPlayerOne());
		GeneralCommandSets.threadSleep();
		BasicCommands.setPlayer1Mana(out, gameState.getPlayerOne());
		GeneralCommandSets.threadSleep();
		
		BasicCommands.setPlayer2Health(out, gameState.getPlayerTwo());
		GeneralCommandSets.threadSleep();	
		BasicCommands.setPlayer2Mana(out, gameState.getPlayerTwo());
		GeneralCommandSets.threadSleep();
		
		int i = 0;
		//showing human player's hand
		for (Card c : gameState.getTurnOwner().getHand().getHandList()) {
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


