package events.gameplaystates.tileplaystates;

import events.gameplaystates.GameplayContext;
import events.gameplaystates.unitplaystates.IUnitPlayStates;
import events.gameplaystates.unitplaystates.UnitDisplayActionsState;
import structures.basic.Tile;

public class SingleSelectedState implements ITilePlayStates{

	// State attributes
	IUnitPlayStates unitState; 
	Tile currentTile; 

	// State constructor 
	public SingleSelectedState() {	
		unitState = null; 
		currentTile = null; 
	}
	
	
	/*** State method ***/

	public void execute(GameplayContext context) {
			
		System.out.println("In SingleSelectedState");
		context.debugPrint();

		// Set currentTile 
		currentTile = context.getClickedTile(); 

		// Determine the substate (UnitDisplayActionsState or nothing for now) 
		switch (context.getTileFlag().toLowerCase()) {

		case("friendly unit"): {
			System.out.println("Hit friendly unit case in SingleSelected State");
			unitState = new UnitDisplayActionsState(currentTile); 
			break;
		}
		
		case("enemy unit"): {
			System.out.println("You don't own this Unit");
			break;
		}
		
		case("empty"): {
			System.out.println("Nice empty tile click buddy.");
			return; 
		}
		case("default"):{
			System.out.println("Hit default case in SingleSelectedState.");
			return;
		}
		}
		
		
		// Execute sub-state
		if (unitState != null) {
			unitState.execute(context);
		}
	}
	
}
