package events;

import java.util.ArrayList;

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
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.abilities.AbilityToUnitLinkage;
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


		// Initialising ability to unit linkage data to reference whenever loading units. 
		AbilityToUnitLinkage.initialiseUnitAbilityLinkageData();

		twoPlayerMode(out, gameState,message); 
		boardAvatarSetUp(out,gameState,message);
		playerCardSetUp(out, gameState, message);
		
		

		
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
		
		humanAvatar.setAttackValue(2);
		computerAvatar.setAttackValue(2);
		
		
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
		g.getPlayerTwo().setMana(2);
		
		BasicCommands.setPlayer1Health(out, g.getPlayerOne());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setPlayer1Mana(out, g.getPlayerOne());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setPlayer2Health(out, g.getPlayerTwo());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setPlayer2Mana(out, g.getPlayerTwo());
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		int i = 0;
		
		for (Card c : g.getPlayerOne().getHand().getHandList()) {
			BasicCommands.drawCard(out, c, i, 0);
			i++;
		}
	}
	
	private static void twoPlayerMode(ActorRef out, GameState gameState, JsonNode message) {
		

		// Set player2 to a new human player (Need to control the enemy player actions
		gameState.setTwoPlayerMode(new HumanPlayer());
		
		HumanPlayer player1 = gameState.getPlayerOne(); 
		HumanPlayer player2 = gameState.getPlayerTwoHuman(); 
		
		
		/* Card and Hand setting */
		
		// Want to clear hand and get units of choice
		gameState.getPlayerOne().getHand().getHandList().clear();
		
		// Variables to shorten access
		ArrayList<Card> drawDeck1 = gameState.getPlayerOne().getDeck().getCardList();
		ArrayList<Card> drawDeck2 = gameState.getPlayerTwoHuman().getDeck().getCardList();

		
		// Cards you want from deck 1 (max 5)
		int[] cardIDList1 = {0,1,2};
		
		for (int i = 0; i < cardIDList1.length; i++) {
			gameState.getPlayerOne().getHand().getHandList().add(drawDeck1.get(i));
			player1.getDeck().delCard(cardIDList1[i]);
		}
		player1.getHand().setCurr(cardIDList1.length);

		
		// Cards you want to start with from deck 2 (max 5)
		int[] cardIDList2 = {0,1,2};

		for (int i = 0; i < cardIDList2.length; i++) {
			gameState.getPlayerOne().getHand().getHandList().add(drawDeck2.get(i));
			player1.getDeck().delCard(cardIDList2[i]);
		}
		player1.getHand().setCurr(cardIDList2.length);
		
		// Pass control of AI avatar to human player
		gameState.getComputerAvatar().setOwner(player2);
	}
}


