package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.GameState;
import structures.basic.abilities.*;
import structures.basic.Card;
import structures.basic.EffectAnimation;
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
		
		// Build summonRange for checks
		summonRange = context.getGameStateRef().getBoard().allSummonableTiles(context.getGameStateRef().getTurnOwner());
		
		// Target tile within summon range && sufficient player mana check
		if(tileInSummonRange() && sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard())) {
			
			// Execute summon method
			summonMonster(context.getGameStateRef(), context.out, context.getLoadedCard().getConfigFile(), context.getLoadedCard(), this.targetTile);

			/** Delete card from Hand + update visual **/
			int cardIndexInHand = context.getGameStateRef().getTurnOwner().getHand().getSelCarPos(); 
			context.getGameStateRef().getTurnOwner().getHand().removeCard(cardIndexInHand);
			
			/** Reset entity selection and board **/  
			// Deselect after action finished
			context.deselectAllAfterActionPerformed();
			
			// Update UI 
			BasicCommands.deleteCard(context.out, cardIndexInHand);
		
			//  Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			
		} 
		
		else {
		// Verbose console messages for debugging, simplify for submission
			
			if(!tileInSummonRange()) {
				System.out.println("Tile is not in summon range.");
			} else if(!(sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard()))) {
				System.out.println("Insufficient mana to summon this monster.");
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
		BasicCommands.playUnitAnimation(out, summonedMonster, UnitAnimationType.idle);
		GeneralCommandSets.threadSleep();
		
		// Set monster statistics
		BasicCommands.setUnitHealth(out, summonedMonster, summonedMonster.getHP());
		GeneralCommandSets.threadSleep();
		BasicCommands.setUnitAttack(out, summonedMonster, summonedMonster.getAttackValue());
		GeneralCommandSets.threadSleep();
		
		// Check for on-summon triggers
			// Trigger abilities that permanently change the new object
			
		if(summonedMonster.getMonsterAbility() != null) {
			for(Ability a : summonedMonster.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.construction) {
					a.execute(summonedMonster, gameState);
				}
			}
	
			// Trigger abilities that happen at the game logic point of a new summon
			for(Ability a : summonedMonster.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.onSummon) {
					a.execute(summonedMonster, gameState);
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


