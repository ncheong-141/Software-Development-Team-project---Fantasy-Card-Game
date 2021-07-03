package events.gameplaystates.unitplaystates;

import events.gameplaystates.GameplayContext;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Spell;
import structures.basic.Tile;
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
		
		// Create unit state object for attack
		IUnitPlayStates unitState = new UnitAttackActionState(currentTile, targetTile);
		
		// Execute state
		unitState.execute(context);
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
	
	public void unitMoveAndAttack(Tile currentTile, Tile targetTile) {
		
		// Load relevant data into gameplay context for use in Unit state
		context.setLoadedUnit(currentTile.getUnitOnTile());
		
		// Create unit state object for move
		IUnitPlayStates unitState = new UnitCombinedActionState(currentTile, targetTile);
		
		// Execute state
		unitState.execute(context);
	}
	
	
	// Summon Monster
	public void summonMonster(Card monsterToSummon, Tile targetTile) {
		
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
		
		// Load relevant data into gameplay context for use in Unit state
		context.setLoadedCard(spellToCast);
				
		// Create unit state object for move
		IUnitPlayStates unitState = new CastSpellState(targetTile);
		
		// Execute state
		unitState.execute(context);
	}
}
