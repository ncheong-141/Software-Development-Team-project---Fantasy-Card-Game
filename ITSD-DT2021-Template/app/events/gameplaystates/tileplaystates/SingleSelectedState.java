package events.gameplaystates.tileplaystates;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.unitplaystates.IUnitPlayStates;
import events.gameplaystates.unitplaystates.UnitDisplayActionsState;
import structures.basic.Tile;

/**
 * This state is for when a unit has been selected without any valid previous input such as 
 * another previously selected unit OR card. It is used for displaying Unit actions primarily.
**/
public class SingleSelectedState implements ITilePlayStates{

	/** State attributes **/
	IUnitPlayStates unitState; 	// Unit state class reference
	Tile currentTile; 			// Current tile the unit selected is on

	/** State constructor **/ 
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
				unitState = new UnitDisplayActionsState(currentTile); 
				break;
			}
			
			case("enemy unit"): {
				System.out.println("You don't own this Unit");
				break;
			}
			
			case("empty"): {
				System.out.println("Nice empty tile click buddy.");
				break; 
			}
			
			case("default"):{
				System.out.println("Hit default case in SingleSelectedState.");
				break;
			}
			
		}
			
		/** Execute sub-state **/
		
		if (unitState != null) {
			unitState.execute(context);
		}
	}
	
}
