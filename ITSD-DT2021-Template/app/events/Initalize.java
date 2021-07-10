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


		// Initialising ability to unit linkage data to reference whenever loading units. 
		AbilityToUnitLinkage.initialiseUnitAbilityLinkageData();

		boardAvatarSetUp(out,gameState,message);
		playerCardSetUp(out, gameState, message);
		
		//boardPrintAllMethods(out, gameState);

		Card rPulv = BasicObjectBuilders.loadCard(StaticConfFiles.c_rock_pulveriser, StaticConfFiles.u_rock_pulveriser, 151, Card.class);
		Monster u_rPulv = BasicObjectBuilders.loadMonsterUnit(rPulv.getConfigFile(), rPulv, (Player) gameState.getPlayerOne(), Monster.class);
		u_rPulv.toggleCooldown();
		
		u_rPulv.setPositionByTile(gameState.getBoard().getTile(3, 1));
		gameState.getBoard().getTile(3, 1).addUnit(u_rPulv);
		GeneralCommandSets.drawUnitWithStats(out, u_rPulv, (gameState.getBoard().getTile(3, 1)));
		
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
		g.getPlayerOne().setMana(10);
		g.getPlayerTwo().setMana(10);
		
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
		
		
		Board board = gameState.getBoard();
		
		for (int i = 0; i<board.getGameBoard().length; i++) {
			for (int k = 0; k<board.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, board.getGameBoard()[i][k], 0);
			}
			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		
		Tile currentTile = gameState.getBoard().getTile(5, 3);
		
		Card como = BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, StaticConfFiles.u_comodo_charger, 154, Card.class);
		Monster u_como = BasicObjectBuilders.loadMonsterUnit(como.getConfigFile(), como, (Player) gameState.getPlayerOne(), Monster.class);
		
		u_como.toggleCooldown();
		currentTile.addUnit(u_como);
		u_como.setPositionByTile(currentTile);
		
		
		BasicCommands.addPlayer1Notification(out, "All free tiles", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().allFreeTiles(), 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);
		
		BasicCommands.addPlayer1Notification(out, "adjTiles", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().adjTiles(currentTile) , 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);

		
		BasicCommands.addPlayer1Notification(out, "cardinally adjacent", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().cardinallyAdjTiles(currentTile) , 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);

		
		BasicCommands.addPlayer1Notification(out, "Actionable tiles", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().unitAllActionableTiles(5, 3, u_como.getAttackRange(), u_como.getMovesLeft()) , 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);

		
		BasicCommands.addPlayer1Notification(out, "Attackable tiles", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().unitAttackableTiles(5, 3, u_como.getAttackRange(), u_como.getMovesLeft()) , 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);

		
		BasicCommands.addPlayer1Notification(out, "Moveable tiles", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().unitMovableTiles(5, 3, u_como.getMovesLeft()), 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);
		
		BasicCommands.addPlayer1Notification(out, "Reachable tiles", 4);
		GeneralCommandSets.drawBoardTiles(out,gameState.getBoard().reachableTiles(5, 3, u_como.getMovesLeft()), 2);
		GeneralCommandSets.threadSleepOverride(4000);
		GeneralCommandSets.boardVisualReset(out, gameState);
				
	}
	
	
	private static void attackMoveDemo(ActorRef out, GameState g, JsonNode message) {
		
		AbilityToUnitLinkage.initialiseUnitAbilityLinkageData();
		
		ArrayList <Monster> monsToLink = new ArrayList <Monster> ();
		
		// Load some enemy cards
		Card blood = BasicObjectBuilders.loadCard(StaticConfFiles.c_bloodshard_golem, StaticConfFiles.u_bloodshard_golem, 148, Card.class);
		Card planSco = BasicObjectBuilders.loadCard(StaticConfFiles.c_planar_scout, StaticConfFiles.u_planar_scout, 149, Card.class);
		// Flying/Draw card on death
		Card windshrike = BasicObjectBuilders.loadCard(StaticConfFiles.c_windshrike, StaticConfFiles.u_windshrike, 150, Card.class);
		// Provoke
		Card rPulv = BasicObjectBuilders.loadCard(StaticConfFiles.c_rock_pulveriser, StaticConfFiles.u_rock_pulveriser, 151, Card.class);
		// Ranged
		Card fires = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, StaticConfFiles.u_fire_spitter, 152, Card.class);
		// Double attack
		Card azureL = BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion, StaticConfFiles.u_azurite_lion, 153, Card.class);
		
		// Load some friendly cards
		Card como = BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, StaticConfFiles.u_comodo_charger, 154, Card.class);
		Card hail = BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, StaticConfFiles.u_hailstone_golem, 155, Card.class);
		// Buff - ^attack when Av takes damage
		Card silverg = BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight,StaticConfFiles.u_silverguard_knight, 156, Card.class);
		// Double attack
		Card frazureL = BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion, StaticConfFiles.u_azurite_lion, 157, Card.class);
		// Ranged attack
		Card frfires = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, StaticConfFiles.u_fire_spitter, 158, Card.class);
		
		// Load enemy units
		Monster u_rPulv = BasicObjectBuilders.loadMonsterUnit(rPulv.getConfigFile(), rPulv, (Player) g.getPlayerTwo(), Monster.class);
			u_rPulv.toggleCooldown();
			
			u_rPulv.setPositionByTile(g.getBoard().getTile(3, 1));
			g.getBoard().getTile(3, 1).addUnit(u_rPulv);
			GeneralCommandSets.drawUnitWithStats(out, u_rPulv, (g.getBoard().getTile(3, 1)));
		Monster u_fires = BasicObjectBuilders.loadMonsterUnit(fires.getConfigFile(), fires, (Player) g.getPlayerTwo(), Monster.class);
			u_fires.toggleCooldown();
		Monster u_azureL = BasicObjectBuilders.loadMonsterUnit(azureL.getConfigFile(), azureL, (Player) g.getPlayerTwo(), Monster.class);
			u_azureL.toggleCooldown();
		
		// Load friendly units
		Monster u_como = BasicObjectBuilders.loadMonsterUnit(como.getConfigFile(), como, (Player) g.getPlayerOne(), Monster.class);
			u_como.toggleCooldown();
		Monster u_frazureL = BasicObjectBuilders.loadMonsterUnit(frazureL.getConfigFile(), frazureL, (Player) g.getPlayerOne(), Monster.class); 
			u_frazureL.toggleCooldown();
		Monster u_frfires = BasicObjectBuilders.loadMonsterUnit(frfires.getConfigFile(), frfires, (Player) g.getPlayerOne(), Monster.class);
			u_frfires.toggleCooldown();
		
		// Enemy units
		u_rPulv.setPositionByTile(g.getBoard().getTile(3, 1));
		g.getBoard().getTile(3, 1).addUnit(u_rPulv);
		GeneralCommandSets.drawUnitWithStats(out, u_rPulv, (g.getBoard().getTile(3, 1)));
		monsToLink.add(u_rPulv);
		
		u_fires.setPositionByTile(g.getBoard().getTile(3, 3));
		g.getBoard().getTile(3, 3).addUnit(u_fires);
		GeneralCommandSets.drawUnitWithStats(out, u_fires, (g.getBoard().getTile(3, 3)));
		monsToLink.add(u_fires);
		
		u_azureL.setPositionByTile(g.getBoard().getTile(6, 1));
		g.getBoard().getTile(6, 1).addUnit(u_azureL);
		GeneralCommandSets.drawUnitWithStats(out, u_azureL, (g.getBoard().getTile(6, 1)));
		monsToLink.add(u_azureL);
		
		// Friendly units

		monsToLink.add(u_como);

		u_frazureL.setPositionByTile(g.getBoard().getTile(5, 2));
		g.getBoard().getTile(5, 2).addUnit(u_frazureL);
		GeneralCommandSets.drawUnitWithStats(out, u_frazureL, g.getBoard().getTile(5, 2));
		monsToLink.add(u_frazureL);
					
		u_frfires.setPositionByTile(g.getBoard().getTile(5, 4));
		g.getBoard().getTile(5, 4).addUnit(u_frfires);
		GeneralCommandSets.drawUnitWithStats(out, u_frfires, g.getBoard().getTile(5, 4));
		monsToLink.add(u_frfires);
		
		/***		Ability linkage since this usually happens after summon	***/
		
		for(Monster m : monsToLink) {
			if(m.getMonsterAbility() == null) { continue;	}
			for(Ability a : m.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.construction) {
					a.execute(m, g);
				}
			}
		}
		
	}
}


