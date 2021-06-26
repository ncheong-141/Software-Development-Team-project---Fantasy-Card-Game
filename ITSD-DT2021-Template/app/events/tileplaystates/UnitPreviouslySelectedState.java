package events.tileplaystates;

import structures.basic.*;

public class UnitPreviouslySelectedState implements GameplayStates {

	// State attributes
	GameplayStates subState; 

	// State constructor 
	public UnitPreviouslySelectedState() {	
		subState = null; 
	}
	
	public void execute(GameplayContext context) {
			
		System.out.println("In UnitPreviouslySelectedState.");
		context.debugPrint();
		
		// Load previous unit for use in next sub state (move or attack) 
		context.setLoadedUnit(context.getGameStateRef().getBoard().getUnitSelected());
		
		// Determine the substate (SummonMonster or Cast Spell)  (to lower case just so case isnt a problem ever) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("unit"): {
			// Attack
			// Check if enemy
			// Check if enemy if movement is required to attack the enemy
				// -> move and attack substate
				// if not not, attack substate 
			
			break;
		}
		
		case("empty"): {
			// Move
			subState = new UnitMoveActionSubState();
			break; 
		}
		
		}
		

		// Execute sub-state
		if (subState != null ) {
			subState.execute(context);
			
			// Deselect after action
			context.deselectAllAfterActionPerformed();
		}
		
	}
	
}