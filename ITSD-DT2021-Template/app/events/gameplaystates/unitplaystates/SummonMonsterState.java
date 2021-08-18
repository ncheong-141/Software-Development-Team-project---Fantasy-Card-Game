package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.GameState;
import structures.basic.abilities.*;
import structures.basic.Avatar;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SummonMonsterState implements IUnitPlayStates {

	/*** State attributes ***/
	private Tile 				targetTile;
	private ArrayList <Tile> 	summonRange;
	
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public SummonMonsterState(Tile targetTile) {
		this.targetTile = targetTile;
		this.summonRange = null;
	}
	
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In SummonMonsterSubState.");
		

		/**===========================================**/
		context.getGameStateRef().userinteractionLock();
		/**===========================================**/

		
		
		/** Build summon range **/
		// Use adjusted range is monster has special skill on summoe
		if (context.getGameStateRef().useAdjustedMonsterActRange()) {
			
			// Abilities with adjusted act range populate this container with adjusted range
			summonRange = context.getGameStateRef().getTileAdjustedRangeContainer();
		}
		else {
			summonRange = context.getGameStateRef().getBoard().allSummonableTiles(context.getGameStateRef().getTurnOwner());
		}
		
		
		// Target tile within summon range && sufficient player mana check
		if(tileInSummonRange() && sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard())) {
			
			// Verbose output
			BasicCommands.addPlayer1Notification(context.out, "Monster summoned!", 2);
			
			// Execute summon method
			summonMonster(context, context.out, context.getLoadedCard().getConfigFile(), context.getLoadedCard(), this.targetTile);
			

			// Update board counter for num Monsters
			context.getGameStateRef().getBoard().updateUnitCount(1);

			
			/** Delete card from Hand + update visual **/
			
			// Index variables
			int cardIndexInHand = context.getGameStateRef().getTurnOwner().getHand().getSelCarPos(); 
			int oldHandSize =  context.getGameStateRef().getTurnOwner().getHand().getHandList().size(); 	// How many UI cards to delete

			// Remove card
			System.out.println("Removing card: " + context.getGameStateRef().getTurnOwner().getHand().getCardFromHand(cardIndexInHand).getCardname());
			context.getGameStateRef().getTurnOwner().getHand().removeCard(cardIndexInHand);
			for(Card c : context.getGameStateRef().getTurnOwner().getHand().getHandList()) {
				System.out.println("id: " + c.getId() + " name " + c.getCardname());
			}
			
			// Only update Hand for Human player
			if (context.getGameStateRef().getTurnOwner() instanceof HumanPlayer) {
				GeneralCommandSets.drawCardsInHand(context.out, context.getGameStateRef(), oldHandSize, context.getGameStateRef().getTurnOwner().getHand().getHandList());
			}
		
			// Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
		
			
			/** Reset entity selection and board **/  
			// Deselect after action finished
			context.deselectAllAfterActionPerformed();
		}
		
		else {
		// Verbose console messages for debugging, simplify for submission
			
			if(!tileInSummonRange()) {
				System.out.println("Tile is not in summon range.");
			} else if(!(sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard()))) {
				System.out.println("Insufficient mana to summon this monster.");
				BasicCommands.addPlayer1Notification(context.out, "Need more mana.", 1);
				GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			} else {
				System.out.println("Can't summon Monster, please try again.");
			}
			
		}
		
		
		/**===========================================**/
		context.getGameStateRef().userinteractionUnlock();
		/**===========================================**/
	}

	
	public void summonMonster(GameplayContext context, ActorRef out, String u_configFile, Card statsRef, Tile summonTile) {
		
		// Mana cost application due to summon monster
		context.getGameStateRef().getTurnOwner().loseMana(statsRef.getManacost());
		GeneralCommandSets.updatePlayerStats(out, context.getGameStateRef());
		
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(u_configFile,statsRef,context.getGameStateRef().getTurnOwner(),Monster.class);		
		summonedMonster.setPositionByTile(context.getGameStateRef().getBoard().getTile(summonTile.getTilex(),summonTile.getTiley()));
		summonedMonster.setOwner(context.getGameStateRef().getTurnOwner());
		GeneralCommandSets.threadSleep(); 
		
		// Add unit to tile on board
		summonTile.addUnit(summonedMonster);
		GeneralCommandSets.threadSleep();
		
		// Summon animation
		EffectAnimation summonEf = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summonEf, summonTile);
		
		// Drawing summoned monster with stats on the board
		GeneralCommandSets.drawUnitWithStats(out, summonedMonster, summonTile);
		GeneralCommandSets.threadSleep();
		
		// Check for on-summon triggers: permanent changes to Monster statistics, abilities activated by summoning
		checkForSummonTriggers(summonedMonster, context);
		
	}
	
	
	
	// Checking for triggered abilities: construction-related, summon-method related
	private void checkForSummonTriggers(Monster summonedMonster, GameplayContext context) {
		
		if(summonedMonster.hasAbility()) {	
			checkForConstructionAbilityActivation(summonedMonster, context);
			checkForSummonAbilityActivation(summonedMonster, context);	
		}	
		
	}
	
	// Abilities activated directly after object construction to permanently modify the Unit
	private void checkForConstructionAbilityActivation(Monster summonedMonster, GameplayContext context) {
		
		if (summonedMonster.getMonsterAbility() != null ) {
			for(Ability a : summonedMonster.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.construction) {
					a.execute(summonedMonster, context.getGameStateRef());
				}
			}
		}
	}
	
	// Abilities activated by the act of summoning in the game logic
	private void checkForSummonAbilityActivation(Monster summonedMonster, GameplayContext context) {

		if (summonedMonster.getMonsterAbility() != null ) {

			for(Ability a : summonedMonster.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.onSummon) {
					System.out.println("Ability:" + a);

					// Target logic
					if (a.getTargetType() == Avatar.class) {

						// Avatar reference
						Avatar targetAvatar; 

						// Set avatar target based on ability taget
						if (a.targetEnemy() == false) {						
							targetAvatar = context.getGameStateRef().getHumanAvatar(); 
						}
						else {
							targetAvatar = context.getGameStateRef().getComputerAvatar();
						}

						// Execute and play animations
						BasicCommands.playUnitAnimation(context.out, summonedMonster, UnitAnimationType.channel);
						GeneralCommandSets.threadSleepOverride(100);
						if (a.getEffectAnimation() != null) {
							BasicCommands.playEffectAnimation(context.out, a.getEffectAnimation(), context.getGameStateRef().getHumanAvatar().getPosition().getTile(context.getGameStateRef().getBoard()));
							GeneralCommandSets.threadSleep();
						}
						BasicCommands.playUnitAnimation(context.out, summonedMonster, UnitAnimationType.idle);

						// Execute ability
						a.execute(targetAvatar, context.getGameStateRef());
					}
					
					else if (a.getTargetType() == Monster.class) {

						// Monster reference
						Monster targetMonster; 

						if (a.targetEnemy() == false) {
							// Assume can only target itself atm but allies can be implemented when abilities of that kind are added
							targetMonster = summonedMonster;
						} else {
							// Not acting on enemy monsters atm, would need to obtain ref from board
							targetMonster = summonedMonster; // Placeholder
						}		
						a.execute(targetMonster, context.getGameStateRef());

					} else {

						//Other abilities: Draw cards on summon currently present in the game
						a.execute(null, context.getGameStateRef());

						// Draw card reprint happens soon after 
					}
				}
			}
		}	
	}


	
	/*	Helper methods	*/
	// Returns true if the clicked targetTile is within a Player's summon range
	private boolean tileInSummonRange() {
		if(summonRange.contains(targetTile)) {	return true;	}
		return false;
	}
	
	// Returns true if Player has sufficient mana to cover the card's playing cost
	private boolean sufficientMana(Player p, Card mon) {
		System.out.println("Turn owner has: " + p.getMana() + " mana");
		if(p.getMana() - mon.getManacost() >= 0) {	return true;	}
		return false;
	}
	
}


