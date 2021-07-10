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
			
			// Execute summon method
			summonMonster(context.getGameStateRef(), context.out, context.getLoadedCard().getConfigFile(), context.getLoadedCard(), this.targetTile);
			
			// Update board counter for num Monsters
			context.getGameStateRef().getBoard().updateUnitCount(1);

			/** Delete card from Hand + update visual **/
			
			// Index variables
			int cardIndexInHand = context.getGameStateRef().getTurnOwner().getHand().getSelCarPos(); 
			int oldHandSize =  context.getGameStateRef().getTurnOwner().getHand().getHandList().size(); 	// How many UI cards to delete

			// Remove card
			context.getGameStateRef().getTurnOwner().getHand().removeCard(cardIndexInHand);
			
			/** Reset entity selection and board **/  
			// Deselect after action finished
			context.deselectAllAfterActionPerformed();
			
			// Only update Hand for Human player
			if (context.getGameStateRef().getTurnOwner() == context.getGameStateRef().getPlayerOne()) {
				GeneralCommandSets.drawCardsInHand(context.out, context.getGameStateRef(), oldHandSize, context.getGameStateRef().getTurnOwner().getHand().getHandList());
			}
		
			// Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			
		} 
		
		else {
		// Verbose console messages for debugging, simplify for submission
			
			if(!tileInSummonRange()) {
				System.out.println("Tile is not in summon range.");
			} else if(!(sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard()))) {
				System.out.println("Insufficient mana to summon this monster.");
				BasicCommands.addPlayer1Notification(context.out, "Get some mana!!", 1);
				GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			} else {
				System.out.println("Can't summon Monster, please try again.");
			}
			
		}

	}

	
	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, Card statsRef, Tile summonTile) {
		
		// Mana cost
		BasicCommands.addPlayer1Notification(out, "Player mana cost", 2);
		gameState.getTurnOwner().loseMana(statsRef.getManacost());
		
		if(gameState.getTurnOwner() == gameState.getPlayerOne()) {
			BasicCommands.setPlayer1Mana(out, gameState.getTurnOwner());
			GeneralCommandSets.threadSleep();
		} else {
			BasicCommands.setPlayer2Mana(out, gameState.getTurnOwner());
			GeneralCommandSets.threadSleep();
		}
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(u_configFile,statsRef,gameState.getTurnOwner(),Monster.class);		
		summonedMonster.setPositionByTile(gameState.getBoard().getTile(summonTile.getTilex(),summonTile.getTiley()));
		summonedMonster.setOwner(gameState.getTurnOwner());
		GeneralCommandSets.threadSleep(); 
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		summonTile.addUnit(summonedMonster);
		GeneralCommandSets.threadSleep();
		
		// Summon animation
		EffectAnimation summonEf = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summonEf, summonTile);
		
		// Drawing summoned monster with stats on the board
		GeneralCommandSets.drawUnitWithStats(out, summonedMonster, summonTile);
		GeneralCommandSets.threadSleep();
		
		// Check for on-summon triggers: permanent changes to Monster statistics, abilities activated by summoning
		checkForSummonTriggers(summonedMonster, gameState);
		
	}
	
	
	
	// Checking for triggered abilities: construction-related, summon-method related
	private void checkForSummonTriggers(Monster summonedMonster, GameState gameState) {
		
		if(summonedMonster.getMonsterAbility() != null) {	
			//System.out.println("Monster ability of monster: " + summonedMonster.getName() + ":  " + summonedMonster.getMonsterAbility());
			checkForConstruction(summonedMonster, gameState);
			checkForSummon(summonedMonster, gameState);	
		}	
		
	}
	
	// Abilities activated directly after object construction to permanently modify the Unit
	private void checkForConstruction(Monster summonedMonster, GameState gameState) {
		for(Ability a : summonedMonster.getMonsterAbility()) {
			if(a.getCallID() == Call_IDs.construction) {
				a.execute(summonedMonster, gameState);
			}
		}
	}
	
	// Abilities activated by the act of summoning in the game logic
	private void checkForSummon(Monster summonedMonster, GameState gameState) {
		for(Ability a : summonedMonster.getMonsterAbility()) {
			if(a.getCallID() == Call_IDs.onSummon) {
				System.out.println("Ability:" + a);
				
				// Target logic
				if (a.getTargetType() == Avatar.class) {
					
					if (a.targetEnemy() == false) {									
						a.execute(gameState.getHumanAvatar(), gameState);					
					}
					else {
						a.execute(gameState.getComputerAvatar(), gameState);
					}
				}
				else if (a.getTargetType() == Monster.class) {
						
					if (a.targetEnemy() == false) {
						// Assume can only target itself or avatars
						a.execute(summonedMonster, gameState);
					} else {
						// Not acting on enemy monsters atm? 
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


