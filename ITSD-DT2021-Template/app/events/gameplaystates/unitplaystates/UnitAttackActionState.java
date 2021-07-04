package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import ch.qos.logback.core.Context;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.Ability;
import structures.basic.abilities.Call_IDs;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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
		// Build attacker/defender attackRanges for checks --- formula should only ever reflect attackRange in this state, not
		// cumulative attack + move range (since all movement takes place before this state).
		// For attacker: attackRange should reflect unit's attacks range (omit move range)
		// For defender: counterRange should reflect unit's attack range (omit move range)
		ArrayList <Tile> temp = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(currentTile.getTilex(), currentTile.getTiley(), attacker.getAttackRange(), attacker.getMovesLeft()));
		attackerAttackRange = temp;
		temp = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(targetTile.getTilex(), targetTile.getTiley(), defender.getAttackRange(), defender.getMovesLeft()));
		defenderCounterRange = temp;
		
		// Checks;
		// Tile not in attack range
		// Adjacent enemies with Provoke
		if(tileInAttackRange()) {
			unitAttack(context);
			
			// This might always happen regardless of Combined(move always before attack?)
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
		
		// If attacker's attack is successful
		if(attacker.attack()) {
			System.out.println("Attack successful.");
			
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
			
			boolean survived = defender.defend(attacker.getAttackValue());
			System.out.println("Defender has " + defender.getHP() + " HP");
			
			// If Avatar damaged ability check
			
			// Play animations and set visuals
			// Non-ranged attacker
			if(attacker.getAttackRange() == 1) {
				BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);
			} 
			// Ranged attacker, different animation
			else {
				EffectAnimation arrows = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
				BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);
				BasicCommands.playProjectileAnimation(context.out, arrows, 0, currentTile, targetTile);
			}
			BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.hit);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.setUnitHealth(context.out, defender, defender.getHP());
			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}

			// Defender death + counter attack check
			if(!survived) {
				System.out.println("Defender is dead.");		
				
				// Play animation + sleep to let it happen
				BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.death);
				try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}
				
				// Remove from front-end
				BasicCommands.deleteUnit(context.out, defender);
				try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
				
				// Delete method
				unitDeath(context, targetTile);
				
			} else {
				System.out.println("Counter-attack incoming...");
				
				// Counter attack here - use counter Monster method
				
				// Death check
			
				// If Avatar damaged ability check
				
			}
			
			// Re-idle -- need checks for both attack and counter target survival before re-idle
			BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.idle);
			if(survived) {	BasicCommands.playUnitAnimation(context.out,defender,UnitAnimationType.idle); }
			GeneralCommandSets.threadSleep();		
			
		} 
		// Unit is unable to attack for some reason - internal attack values/summon cooldown/etc.
		else {
			System.out.println("Unit cannot attack.");
		}
		
	}
	
	/*	Helper methods	*/
	
	// Checks the user's selected Tile is within the attack range of the selected unit
	private boolean tileInAttackRange() {
		if(attackerAttackRange.contains(targetTile)) return true;
		return false;
	}
	
	// Helper method to update internal game data and delete a Unit onDeath
	private void unitDeath(GameplayContext context, Tile grave) {
		
		Monster deadUnit = grave.getUnitOnTile();
		
		// Check for onDeath ability
		if(deadUnit.hasAbility()) {
			for(Ability a : deadUnit.getAbility()) {
				if(a.getCallID() == Call_IDs.onDeath) {	a.execute(attacker,context.getGameStateRef()); }
			}
		}
		
		// Update internal Tile values
		grave.removeUnit();
		deadUnit.setPosition(new Position(-1,-1,-1,-1));
		
		System.out.println("grave occupied: " + grave.getUnitOnTile());
		// Dereference object
	}
	
}
