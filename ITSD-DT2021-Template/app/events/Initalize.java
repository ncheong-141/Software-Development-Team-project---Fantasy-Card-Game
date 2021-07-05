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


		CommandDemo.executeDemoTester(out,gameState); // this executes the command demo, comment out this when implementing your solution
		// CommandDemo.executeDemoUnits(out, gameState);
		//CommandDemo.executeDemoUnitsNicholas(out, gameState); 
		//CommandDemo.executeDemoBoard(out, gameState);
		//CommandDemo.executeDemoDeckHand(out, gameState);
		// CommandDemo.executeDemoSummon(out, gameState);
		//CommandDemo.executeDemoBoard(out, g);
		//CommandDemo.executeDemoDeckHand(out, gameState);
		//CommandDemo.executeDemoSummon(out, gameState);
//		boardAvatarSetUp(out,gameState,message);
//		playerCardSetUp(out, gameState, message);
		//CommandDemo.executeTileHighlightDemo(out, g);
		//CommandDemo.executeAbilityDemo(out, gameState);

	}
	
	private static void boardAvatarSetUp(ActorRef out, GameState g, JsonNode message) {

		ArrayList<Tile> boardTiles = g.getBoard().getAllTilesList();
		
		GeneralCommandSets.drawBoardTiles(out, boardTiles, 0);

		
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		Avatar humanAvatar = g.getHumanAvatar();
		Avatar computerAvatar = g.getComputerAvatar();
		
		
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
		humanAvatar.setAttackValue(2);
		computerAvatar.setAttackValue(2);
		
		
		//display avatars on board
		Tile tOne = g.getGameBoard().getTile(1, 2);
		Tile tTwo = g.getGameBoard().getTile(7, 2);
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
		
		for (Card c : g.getPlayerOne().getHand().getHand()) {
			BasicCommands.drawCard(out, c, i, 0);
			i++;
		}
		
		


	}

}


