package events.tileplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
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
		GeneralCommandSets.threadSleep(); 
		
		// Keep before add Unit because drawTile doesn't work on tile underneath new monster for some reason
		// De-highlight tiles
		ArrayList <Tile> summonRange = gameState.getBoard().allSummonableTiles(gameState.getTurnOwner());
		GeneralCommandSets.drawBoardTiles(out, summonRange, 0);
		GeneralCommandSets.threadSleepLong();
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		gameState.getBoard().getTile(tilex,tiley).addUnit(summonedMonster);
		GeneralCommandSets.threadSleep();
		
		// Drawing the monster on the board
		BasicCommands.drawUnit(out, summonedMonster, gameState.getBoard().getTile(tilex,tiley));
		GeneralCommandSets.threadSleep();
		BasicCommands.playUnitAnimation(out, summonedMonster, UnitAnimationType.idle);
		GeneralCommandSets.threadSleep();
		
		// Set monster statistics
		BasicCommands.setUnitHealth(out, summonedMonster, summonedMonster.getHP());
		GeneralCommandSets.threadSleep();
		BasicCommands.setUnitAttack(out, summonedMonster, summonedMonster.getAttackValue());
		GeneralCommandSets.threadSleep();
		
		// De-select card (visual only)
		Card selectedCard = gameState.getTurnOwner().getHand().getSelectedCard();
		// Need position in hand from Noah to redraw Card with no highlight
//		BasicCommands.drawCard(out, selectedCard, position, 0);
		
		// Keep this as the flag for how deselect method works
		gameState.getTurnOwner().getHand().setPlayingMode(false);

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
	
	
// >>>>>>>>>>	Leave in for now, may be useable	
	
//	if((gameState.getBoard().allSummonableTiles(gameState.getTurnOwner())).contains(gameState.getBoard().getTile(tilex, tiley))) {
//
//		String configName = selected.getCardname().replace(' ', '_').toLowerCase().trim();
//		configName = "u_" + configName;
//
//		System.out.println("Summoning monster...");
//		summonMonster(gameState, out , configName, selected, tilex, tiley);
//
//	} else {
//		System.out.println("Can't summon monster on this tile.");
//	}
}
