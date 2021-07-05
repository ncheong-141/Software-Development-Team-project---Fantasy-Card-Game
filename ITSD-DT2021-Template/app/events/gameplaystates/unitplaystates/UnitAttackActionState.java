package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import ch.qos.logback.core.Context;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.Avatar;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.*;
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
		
		/* Gather ranges
		/* Build attacker/defender attackRanges for checks --- formula should only reflect attackRange in this state, not
		/* cumulative attack + move range (since all movement takes place before this state).
		/* For attacker: attackRange should reflect unit's attacks range (omit move range)
		/* For defender: counterRange should reflect unit's attack range (omit move range)*/
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
		
		boolean survived;		// stores attack outcomes

		//	>>>>> Begin attack		//
		
		if(attacker.attack()) {
			System.out.println("Attack successful.");
			
			/***	Un-highlight range tiles (minus attacker/defender)	***/
			for (Tile t : actRange) {
				// Leave defender tile highlighted
				if(t == targetTile) {
					continue;
				}
				BasicCommands.drawTile(context.out, t, 0);
				GeneralCommandSets.threadSleep();
			}
			
			/***	Update defender		***/
			survived = defender.defend(attacker.getAttackValue());
			System.out.println("Defender has " + defender.getHP() + " HP");
			
			// If Avatar damaged ability check
			
			/***	Play animations and set visuals		***/
			// Check for attacker animations
			EffectAnimation arrows = checkRangedAttacker(attacker);
			
			// Non-ranged
			if(arrows == null) {	BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);		} 
			// Ranged
			else {
				BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);
				BasicCommands.playProjectileAnimation(context.out, arrows, 0, currentTile, targetTile);
			}
			BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.hit);
			BasicCommands.setUnitHealth(context.out, defender, defender.getHP());
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

			// Re-idle
			BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.idle);
			if(survived) {	BasicCommands.playUnitAnimation(context.out,defender,UnitAnimationType.idle); }
			GeneralCommandSets.threadSleep();		
			
			/***	Death and counter-attack check	***/
			if(!survived) {
				System.out.println("Defender is dead.");		
				
				// Check for attacker destination and reachable by defender (ranged/adjacent)
				
				// Counter attack
				
				// Play animation + sleep to let it happen
				BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.death);
				try {Thread.sleep(1300);} catch (InterruptedException e) {e.printStackTrace();}
				
				// Avatar death check
				if(defender instanceof Avatar) {
					
					// Player notification
					String winnerWinnerChickenDinner = "";
					if(context.getGameStateRef().getPlayerOne() == defender.getOwner()) {
						winnerWinnerChickenDinner = "You lose!";
					} else {
						winnerWinnerChickenDinner = "You win!";
					}
					BasicCommands.addPlayer1Notification(context.out,winnerWinnerChickenDinner, 0);
					
					// Call game end method
					context.getGameStateRef().gameOver();
					return;
				}
				
				// Delete method
				unitDeath(context, targetTile);
				
				// Remove from front-end
				BasicCommands.deleteUnit(context.out, defender);
				try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
				
				
			} else {
				System.out.println("Counter-attack incoming...");
				
				// Counter attack here - use counter Monster method
				
				// Death check		
				// Avatar death check
//				if(defender instanceof Avatar) {
//					
//					// Player notification
//					String winnerWinnerChickenDinner = "";
//					if(context.getGameStateRef().getPlayerOne() == defender.getOwner()) {
//						winnerWinnerChickenDinner = "You lose!";
//					} else {
//						winnerWinnerChickenDinner = "You win!";
//					}
//					BasicCommands.addPlayer1Notification(context.out,winnerWinnerChickenDinner, 0);
//					
//					// Game ends
//					context.getGameStateRef().gameOver();
//					return;
//				}
			
				// If Avatar damaged ability check
				
				// Re-idle -- need checks for both attack and counter target survival before re-idle
//				if(survived) {	BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.idle);	}
//				BasicCommands.playUnitAnimation(context.out,defender,UnitAnimationType.idle);
//				GeneralCommandSets.threadSleep();	
				
			}
			
		} 
		
		// Unit attack failed - internal attack values/summon cooldown/etc.
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
	
	// Unit death method to update location data and delete a Unit from Board
	private void unitDeath(GameplayContext context, Tile grave) {
		
		Monster deadUnit = grave.getUnitOnTile();
		
		// Check for onDeath ability
		if(deadUnit.hasAbility()) {
			for(Ability a : deadUnit.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.onDeath) {	a.execute(deadUnit,context.getGameStateRef()); 
				break;}
			}
		}
		
		// Update internal Tile values
		grave.removeUnit();
		deadUnit.setPosition(new Position(-1,-1,-1,-1));
		
	}
	
	// Check for Ranged Attacker ability and return EffectAnimation if true
	private EffectAnimation checkRangedAttacker(Monster attacker) {
		for(Ability a : attacker.getMonsterAbility()) {
			if(a instanceof A_U_RangedAttacker) {
				return a.getEffectAnimation();
			}
		}
		return null;
		
	}
	
}
