package events.gameplaystates.unitplaystates;

import events.gameplaystates.GameplayContext;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.abilities.*;
import akka.actor.ActorRef;


/* 
 * Purpose of this class is to encapuslate all control of UnitState classes into one object which makes it safe for an external class (such as the AI) to use. 
 */

/**
 * Method Signatures to use: 
 * 		- unitAttack		(Tile currentTile, Tile targetTile)
 * 		- unitMove			(Tile currentTile, Tile targetTile)
 * 		- unitMoveAndAttack	(Tile currentTile, Tile targetTile)
 * 		- summonMonster		(Card monsterToSummon, Tile targetTile)
 * 		- spellCast			(Card spellToCast, Tile targetTile)
 */
public class AIUnitStateController {

	
	// Attributes
	GameplayContext context; 

	// Constructor 
	public AIUnitStateController(ActorRef out, GameState gameState) {
		
		// GameplayContext for current state of the game depending on unit actions 
		context = new GameplayContext(gameState, out);
	}
	
	
	/** State controlling methods **/
	/** All unit states should handle all relevant checks and game logic **/ 
	public void unitAttack(Tile currentTile, Tile targetTile) {

		// Load relevant data into gameplay context for use in Unit state
		context.setLoadedUnit(currentTile.getUnitOnTile());
		
		IUnitPlayStates unitState = null;
		
		/*
		 * Unit checks to implement:
		 *		Enemy adjacent							->	Attack state
		 *		Enemy adjacent 		+ Ranged attacker	->	Attack state
		 *		Enemy in move range						->	Move, then attack
		 *		Enemy next to move range				->	Move, then attack
		 *		Enemy outside move range				->	Can't target
		 *		Enemy outside move range + Ranged attacker	->	Attack state
		 */
		
		// Check if target is adjacent (index change to targetTile on each board dimension does not exceed 1) OR
		// is ranged attacker, to call correct unit state (attack, or move+attack)
		if((Math.abs(currentTile.getTilex() - targetTile.getTilex()) <=1 && (Math.abs(currentTile.getTiley() - targetTile.getTiley()) <= 1))
				|| checkForRangedAttacker(currentTile.getUnitOnTile())) {
		
			// Enemy target is attackable right now
			unitState = new UnitAttackActionState(currentTile, targetTile);
		}
		else {
			
			// Current unit must move to enemy target first
			unitState = new UnitCombinedActionState(currentTile, targetTile);	
		}
		
		// Execute state
		if (unitState != null) {
			unitState.execute(context);
		}
	}
	
	// Unit Move
	public void unitMove(Tile currentTile, Tile targetTile) {
		
		// Load relevant data into gameplay context for use in Unit state
		context.setLoadedUnit(currentTile.getUnitOnTile());
		
		// Create unit state object for move
		IUnitPlayStates unitState = new UnitMoveActionState(currentTile, targetTile);
		
		// Execute state
		unitState.execute(context);
	}
	
	// Summon Monster
	public void summonMonster(Card monsterToSummon, Tile targetTile) {
		
		// Select card in Hand for update purposes later
		for(int i = 1; i <= context.getGameStateRef().getTurnOwner().getHand().getHandList().size(); i++) {
			if(context.getGameStateRef().getTurnOwner().getHand().getCardFromHand(i).getId() == monsterToSummon.getId()) {
				context.getGameStateRef().getTurnOwner().getHand().setSelectedCard(monsterToSummon);
				context.getGameStateRef().getTurnOwner().getHand().setSelCarPos(i);
			}
			if(i == 6 && context.getGameStateRef().getTurnOwner().getHand().getSelectedCard() == null) {	System.out.println("Selected card not set in AI Controller");	}
		}
		
		// Load relevant data into gameplay context for use in Unit state
		context.setLoadedCard(monsterToSummon);
		
		// Determine class type of the loaded card
		// Check if a Spell card 
		if (context.getLoadedCard().getBigCard().getAttack() < 0) {	
			context.setCardClasstype(Spell.class);
		}
		// else, Monster
		else {
			context.setCardClasstype(Monster.class);
		}
				
		// Create unit state object for move
		IUnitPlayStates unitState = new SummonMonsterState(targetTile);
		
		// Execute state
		unitState.execute(context);
	}
	
	
	// Cast Spell 
	public void spellCast(Card spellToCast, Tile targetTile) {
		
		// Select card in Hand for update purposes later
		for(int i = 1; i <= context.getGameStateRef().getTurnOwner().getHand().getHandList().size(); i++) {
			if(context.getGameStateRef().getTurnOwner().getHand().getCardFromHand(i).getId() == spellToCast.getId()) {
				context.getGameStateRef().getTurnOwner().getHand().setSelectedCard(spellToCast);
				context.getGameStateRef().getTurnOwner().getHand().setSelCarPos(i);
			}
			if(i == 6 && context.getGameStateRef().getTurnOwner().getHand().getSelectedCard() == null) {	System.out.println("Selected card not set in AI Controller");	}
		}
		
		// Load relevant data into gameplay context for use in Unit state
		context.setLoadedCard(spellToCast);
				
		// Create unit state object for move
		IUnitPlayStates unitState = new CastSpellState(targetTile);
		
		// Execute state
		unitState.execute(context);
	}
	
	// Helper to check whether input Unit is a Ranged attacker (i.e. does not need to move to
	// attack)
	private boolean checkForRangedAttacker(Monster m) {
		if(m.hasAbility()) {
			for(Ability a : m.getMonsterAbility()) {
				if(a instanceof A_U_RangedAttacker) {	return true;	}
			}
		}
		return false;
	}
	
}
