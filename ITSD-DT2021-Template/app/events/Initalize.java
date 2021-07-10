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
import structures.basic.Monster;
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

		boardAvatarSetUp(out,gameState,message);
		playerCardSetUp(out, gameState, message);
		

		//CommandDemo.executeTileHighlightDemo(out, g);
		//CommandDemo.executeAbilityDemo(out, gameState);
		
		System.out.println("Game set up:  \nplayer one stats: mana " + gameState.getPlayerOne().getMana() +" health: " + gameState.getPlayerOne().getHealth());
		System.out.println("Game set up:  \nplayer two stats: mana " + gameState.getPlayerTwo().getMana() +" health: " + gameState.getPlayerTwo().getHealth()); 
		
		
		//////STUFF FOR TESTING/////
		// Create Card objects to use
		Card cBlazeHound = BasicObjectBuilders.loadCard(StaticConfFiles.c_blaze_hound, 2, Card.class);
		Card cFireSpitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 3, Card.class);
		Card cFireSpitter2 = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 4, Card.class);
				
		// Create Friendly Unit objects to use (sets HP, name, ability, onwer already) 
		Monster[] fmArray = new Monster[4]; 
		fmArray[0] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cFireSpitter, gameState.getPlayerTwo(), Monster.class);
		fmArray[1] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cFireSpitter2, gameState.getPlayerTwo(), Monster.class);
		fmArray[2] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_blaze_hound, cBlazeHound, gameState.getPlayerTwo(), Monster.class);
		fmArray[3] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_blaze_hound, cBlazeHound, gameState.getPlayerTwo(), Monster.class);
		
		
		Board b = gameState.getBoard();
		
		Tile one = b.getTile(6,2);
		Tile two = b.getTile(3, 2);
		Tile three = b.getTile(3, 3);
		Tile four = b.getTile(2, 1);
		
		one.addUnit(fmArray[0]);
		two.addUnit(fmArray[1]);
		three.addUnit(fmArray[2]);
		four.addUnit(fmArray[3]);
		
		BasicCommands.drawUnit(out, fmArray[0], one);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.drawUnit(out, fmArray[1], two);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.drawUnit(out, fmArray[2], three);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.drawUnit(out, fmArray[3], four);
		
				
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

}


