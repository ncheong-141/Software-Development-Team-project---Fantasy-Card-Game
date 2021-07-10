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
		// This needs changing to an attack range method that does not include moveable tiles
		attackerAttackRange  = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(currentTile.getTilex(), currentTile.getTiley(), attacker.getAttackRange(), 0));
		defenderCounterRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(targetTile.getTilex(), targetTile.getTiley(), defender.getAttackRange(), 0));
		
		
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
				
		// Gather action range for board visual
		ArrayList <Tile> actRange;
		
		// Use adjust action range based on movement impaired effects
		if (attacker.hasActionRangeImpaired()) {
			actRange = context.getGameStateRef().getTileAdjustedRangeContainer();
		}		
		else {
			actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(attacker.getPosition().getTilex(), attacker.getPosition().getTiley(), attacker.getAttackRange(), attacker.getMovesLeft()));
		}

		
		// Stores interaction outcomes
		boolean survived;		

		/***		>>>>> Attack			***/
		
		System.out.println(attacker.getName() + " has " + attacker.getAttacksLeft() + " attacks left");
		if(attacker.attack()) {
				System.out.println("Attack successful. " + attacker.getName() + " has " + attacker.getAttacksLeft() + " attacks left");
			
			// Update defender
			survived = defender.defend(attacker.getAttackValue());
			System.out.println("Defender has " + defender.getHP() + " HP");
			
			/***	Re-draw range tiles (leave attacker/defender highlighted for user)	***/
			for (Tile t : actRange) {
				// Defender
				if(t == targetTile) {
					continue;
				}
				BasicCommands.drawTile(context.out, t, 0);
				GeneralCommandSets.threadSleep();
			}
			
			/***	Play animations and set visuals		***/
			playAttackAnimations(attacker, defender, context);
			
			// If friendly Avatar damaged ability check
			checkAvatarDamaged(defender, context);

			// Re-idle alive units
			BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.idle);
			if(survived) {	BasicCommands.playUnitAnimation(context.out,defender,UnitAnimationType.idle);	}
			GeneralCommandSets.threadSleep();	
			
				/***	Death check	***/
				if(!survived) {
					// De-highlight attacker for game action clarity to user
					BasicCommands.drawTile(context.out, currentTile, 0);
					
					// Play animation + sleep to let it happen
					BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.death);
					try {Thread.sleep(1300);} catch (InterruptedException e) {e.printStackTrace();}		
					
					// Check for Avatar death/game end
					if(checkForAvatarDeath(defender, context)) {
						return;
					}
					// Unit dies
					else {
						unitDeath(context, targetTile);
					}	

			}
			
			/***	>>>>> Counter-attack			***/
				
			else {
				System.out.println("Counter-attack incoming...");
				
				// Check for attacker destination and reachable by defender (ranged/adjacent)
					// Ranged defender check
				
				// Counter attack
				survived = attacker.defend(defender.counter());
				
				/***	Switch attacker/defender highlights for user clarity	***/
				// Initial attacker
				BasicCommands.drawTile(context.out, currentTile, 2);
				GeneralCommandSets.threadSleep();
				// Initial defender
				BasicCommands.drawTile(context.out, targetTile, 1);
				GeneralCommandSets.threadSleep();
				
				/***	Play animations and set visuals		***/
				playAttackAnimations(defender, attacker, context);
				
				// If friendly Avatar damaged ability check
				checkAvatarDamaged(attacker, context);
				
				/***	Death check	***/
				if(!survived) {
					// De-highlight attacker tile for game action clarity to user
					BasicCommands.drawTile(context.out, targetTile, 0);
					
					// Play animation + sleep to let it happen
					BasicCommands.playUnitAnimation(context.out, attacker, UnitAnimationType.death);
					GeneralCommandSets.threadSleep();
					
					// Check for Avatar death/game end
					if(checkForAvatarDeath(attacker, context)) {
						return;
					}
					// Unit dies
					else {
						unitDeath(context, currentTile);
					}	
				}
				
				// Re-idle alive units
				if(survived) {	BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.idle);	}
				BasicCommands.playUnitAnimation(context.out,defender,UnitAnimationType.idle);
				GeneralCommandSets.threadSleep();	
				
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
		if(attackerAttackRange.contains(targetTile)) 
			return true;
		return false;
	}
	
	/***			Methods used by attack stages			***/
	
	
	// Playing attack animations/effects for attacker and receiver
	// --- note order of input arguments is key to output
	private void playAttackAnimations(Monster attacker, Monster receiver, GameplayContext context) {
			
		// Ranged
		if(attacker.getAbAnimation() != null /*Could need an && for near attacks exempt*/) {
			BasicCommands.playProjectileAnimation(context.out, attacker.getAbAnimation(), 0, attacker.getPosition().getTile(context.getGameStateRef().getBoard()), receiver.getPosition().getTile(context.getGameStateRef().getBoard()));
		}
		// Executes for both ranged and non-ranged attacks
		BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);
		
		BasicCommands.playUnitAnimation(context.out, receiver, UnitAnimationType.hit);
		BasicCommands.setUnitHealth(context.out, receiver, receiver.getHP());
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
	}
	
	// Unit death --- method to update location data and delete a Unit from Board
	private void unitDeath(GameplayContext context, Tile grave) {
		
		Monster deadUnit = grave.getUnitOnTile();
		
		// Check for onDeath ability
		if(deadUnit.hasAbility()) {
			for(Ability a : deadUnit.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.onDeath) {	
					a.execute(deadUnit,context.getGameStateRef()); 
					break;
				}
			}
		}
		
		// Update internal location values
		grave.removeUnit();
		deadUnit.setPosition(new Position(-1,-1,-1,-1));	// might not need
		
		// Remove from front-end
		BasicCommands.deleteUnit(context.out, deadUnit);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		
	}
	
	// Avatar death check --- method checks that the death of a unit is not an Avatar, calls gameOver if so
	private boolean checkForAvatarDeath(Monster deadUnit, GameplayContext context) {
		
		if(isAvatar(deadUnit)) {
			
			// Player notification
			String endgameMessage = "";
			if(context.getGameStateRef().getPlayerOne() == deadUnit.getOwner()) {
				endgameMessage += "You lose!";
			} else {
				endgameMessage += "You win!";
			}
			BasicCommands.addPlayer1Notification(context.out,endgameMessage, 2);
			
			// Game ends
			context.getGameStateRef().gameOver();
			return true;
			
		}
		return false;
	}
	
	/***			Small checks			***/
	
	// Simple helper to check if a Monster is an Avatar
	private boolean isAvatar(Monster m) {
		if(m.getClass() == Avatar.class) {	return true;	}
		return false;
	}
	
	// Return EffectAnimation if true
	private EffectAnimation checkRangedAttacker(Monster attacker) {
		if(attacker.hasAbility()) {
			for (Ability a : attacker.getMonsterAbility()) {
				if(a instanceof A_U_RangedAttacker) {
					return a.getEffectAnimation();			// will need to change this when effectanimation stored in monster?
				}
			}
		}
		return null;	
	}
	
	// Check if a Monster that has received damage:
	// 1) is a friendly Avatar
	// 2) will trigger any present friendly Unit with a related ability
	private boolean checkAvatarDamaged(Monster a, GameplayContext context) {
		
		// If friendly Avatar condition is not satisfied
		if(!(isAvatar(a)) || !(a.getOwner() == context.getGameStateRef().getTurnOwner()) ) {	return false;	}
		
		// Check for friendly units with ability
		else {
			
			ArrayList <Monster> friendlies = context.getGameStateRef().getBoard().friendlyUnitList(a.getOwner());
			// For each ally of Avatar a
			for(Monster m : friendlies) {
				if(m.hasAbility()) {
					// For each ability
					for(Ability abi : m.getMonsterAbility()) {
						
						// If ability is triggered by friendly Avatar damage
						if(abi.getCallID() == Call_IDs.onFriendlyAvatarDamageTaken) {
							abi.execute(m, context.getGameStateRef());
							return true;
						}
						
					}
				}
			}
			
		}
		
		return false;
		
	}
	
}
