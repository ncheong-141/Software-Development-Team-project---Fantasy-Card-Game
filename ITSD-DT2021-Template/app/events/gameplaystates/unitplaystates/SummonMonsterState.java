package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SummonMonsterState implements IUnitPlayStates {

	/*** State attributes ***/
	private Tile targetTile; 
	
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public SummonMonsterState(Tile targetTile) {
		this.targetTile = targetTile; 
	}
	
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In SummonMonsterSubState.");
		
		// If targetTile is in summon range check
		// Mana sufficient check
		
		summonMonster(context.getGameStateRef(), context.out, "dont have u_config file yet", context.getLoadedCard(), this.targetTile);
		
		/** Reset entity selection and board **/  
		// Deselect after action finished *if* not in the middle of move-attack action
		context.deselectAllAfterActionPerformed();
	
		//  Reset board visual (highlighted tiles)
		GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
	}

	
	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, Card statsRef, Tile summonTile) {
		
		// Mana cost
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
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter,1,statsRef,Monster.class);		
		summonedMonster.setPositionByTile(summonTile);
		summonedMonster.setOwner(gameState.getTurnOwner());
		GeneralCommandSets.threadSleep(); 
		
		// Summon animation
		EffectAnimation summonEf = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summonEf, summonTile);
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		summonTile.addUnit(summonedMonster);
		GeneralCommandSets.threadSleep();
		
		// Drawing summoned monster with stats on the board
		GeneralCommandSets.drawUnitWithStats(out, summonedMonster, summonTile);
		GeneralCommandSets.threadSleep();
		BasicCommands.playUnitAnimation(out, summonedMonster, UnitAnimationType.idle);
		GeneralCommandSets.threadSleep();
		
		// Set monster statistics
		BasicCommands.setUnitHealth(out, summonedMonster, summonedMonster.getHP());
		GeneralCommandSets.threadSleep();
		BasicCommands.setUnitAttack(out, summonedMonster, summonedMonster.getAttackValue());
		GeneralCommandSets.threadSleep();
		
		// Delete card from hand
//		Card selectedCard = context.getLoadedUnit();
		// Need position in hand from Noah to redraw Card with no highlight
//		BasicCommands.drawCard(out, selectedCard, intPosition, 0);
//		GeneralCommandSets.threadSleep();
//		BasicCommands.deleteCard(out, intPosition);
//		GeneralCommandSets.threadSleep();
		
		
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
