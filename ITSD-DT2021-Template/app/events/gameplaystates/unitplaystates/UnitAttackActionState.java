package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import ch.qos.logback.core.Context;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

public class UnitAttackActionState implements IUnitPlayStates {

	/*** State attributes ***/
	private Tile 				currentTile; 
	private Tile 				targetTile; 
	
	private Monster				attacker;
	private Monster				defender;
	
	private ArrayList <Tile>	attackerAttackRange;
	private ArrayList <Tile>	defenderCounterRange;
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables received from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public UnitAttackActionState(Tile currentTile, Tile targetTile) {
		this.currentTile = currentTile;
		this.targetTile = targetTile; 
		this.attacker = null;
		this.defender = null;
		this.attackerAttackRange = null;
		this.defenderCounterRange = null;
	}
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitAttackActionSubState.");
		context.debugPrint();
		
		// Gather attacker and defender
		attacker = (Monster) context.getLoadedUnit();
		defender = targetTile.getUnitOnTile();
		
		// Gather ranges
		// Build attacker's attackRange for checks --- formula needs to be changed to just adjacent attack tiles, as
		// this state assumes units are next to each other before attack can occur
		// For attacker: attackRange should reflect the attacks it has left + range
		// For defender: attackRange should reflect an assumed ability to attack (range 1) since counter is not the same - generic adjacent range
		ArrayList <Tile> temp = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(currentTile.getTilex(), currentTile.getTiley(), attacker.getAttackRange(), attacker.getMovesLeft()));
		attackerAttackRange = temp;
		temp = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(targetTile.getTilex(), targetTile.getTiley(), defender.getAttackRange(), defender.getMovesLeft()));
		defenderCounterRange = temp;
		
		// Checks;
		// Tile not in attack range
		// Adjacent enemies with Provoke
		if(tileInAttackRange()) {
			unitAttack(context);
			
			/***	Condition here for combined substate executing, which requires selection is maintained	***/
			if(!(context.getCombinedActive())) {
				
				/** Reset entity selection and board **/  
				// Deselect after action finished *if* not in the middle of move-attack action
				context.deselectAllAfterActionPerformed();
			
				//  Reset board visual (highlighted tiles)
				GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			}	
			
		} 
		
		else {
			// Other verbose check messages to be added
			
			System.out.println("Enemy is not in attack range.");
		}
		


	}
	
	private void unitAttack(GameplayContext context) {
				
		// Gather action range
		ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(attacker.getPosition().getTilex(), attacker.getPosition().getTiley(), attacker.getAttackRange(), attacker.getMovesLeft()));
		ArrayList <Tile> mRange = context.getGameStateRef().getBoard().unitMovableTiles(attacker.getPosition().getTilex(), attacker.getPosition().getTiley(), attacker.getMovesLeft());
		actRange.addAll(mRange);
		
		// If attacker can attack (true returned by Monster method)
		if(attacker.attack()) {
			System.out.println("Attack successful.");
			boolean survived = defender.defend(attacker.getAttackValue());
			
			// Deselect tiles
			System.out.println("Started drawing Tiles");
			//GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
			for (Tile t : actRange) {
				// Leave defender tile highlighted
				if(t == targetTile) {
					continue;
				}
				BasicCommands.drawTile(context.out, t, 0);
				GeneralCommandSets.threadSleep();
			}
			System.out.println("Finished drawing Tiles method");
			GeneralCommandSets.threadSleepLong();
			
			// Play animations and set visuals
			BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);
			BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.hit);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.setUnitHealth(context.out, defender, defender.getHP());
			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
			
			// Defender death + counter attack check
			if(!survived) {
				System.out.println("Defender is dead.");
				
				// Play animation + sleep to let it happen
				BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.death);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				
				// Remove unit from front-end
				BasicCommands.deleteUnit(context.out, defender);
				try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
				
				// Update internal Tile values
				
				// Destroy object?
				
			} else {
				System.out.println("Counter-attack incoming...");
				
				// Counter attack here
				
			}
			
			
		} 
		// Unit is unable to attack for some reason - internal attack values/summon cooldown/etc.
		else {
			System.out.println("Unit cannot attack.");
		}
		
	}
	
	/*	Helper methods	*/
	
	private boolean tileInAttackRange() {
		if(attackerAttackRange.contains(targetTile)) return true;
		return false;
	}
	
}
