package events.tileplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SummonMonsterSubState implements GameplayStates {

	public void execute(GameplayContext context) {
		
		System.out.println("In SummonMonsterSubState.");

		summonMonster(context.getGameStateRef(), context.out, "dont have u_config file yet", context.getLoadedCard(), context.tilex, context.tiley);
	}

	
	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, Card statsRef, int tilex, int tiley) {
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter,1,statsRef,Monster.class);		
		summonedMonster.setPositionByTile(gameState.getBoard().getTile(tilex,tiley));
		summonedMonster.setOwner(gameState.getTurnOwner());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		gameState.getBoard().getTile(tilex,tiley).addUnit(summonedMonster);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Drawing the monster on the board
		BasicCommands.drawUnit(out, summonedMonster, gameState.getBoard().getTile(tilex,tiley));
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Set monster statistics
		BasicCommands.setUnitHealth(out, summonedMonster, summonedMonster.getHP());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, summonedMonster, summonedMonster.getAttackValue());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// De-highlight tiles
		ArrayList <Tile> summonRange = gameState.getBoard().allSummonableTiles(gameState.getTurnOwner());
		for (Tile i : summonRange) {
			BasicCommands.drawTile(out, i, 0);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// >>> Mana costs --- leave out until mana cycle is implemented ingame
//		BasicCommands.addPlayer1Notification(out, "Player mana cost", 2);
//		gameState.getTurnOwner().loseMana(statsRef.getManacost());
//		
//		if(gameState.getTurnOwner() instanceof HumanPlayer) {
//			BasicCommands.setPlayer1Mana(out, gameState.getTurnOwner());
//		} else {
//			BasicCommands.setPlayer2Mana(out, gameState.getTurnOwner());
//		}
//		
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// >>> Delete card from Hand command --- later sprint
		}
}
