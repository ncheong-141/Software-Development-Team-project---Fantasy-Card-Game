package events.tileplaystates;

public class SingleSelectedState implements GameplayStates{

	// State attributes
	GameplayStates subState; 

	// State constructor 
	public SingleSelectedState() {	
		subState = null; 
	}
	
	public void execute(GameplayContext context) {
			
		System.out.println("In SingleSelectedState");
		context.debugPrint();


		// Determine the substate (UnitDisplayActionsState or nothing for now) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("unit"): {
			subState = new UnitDisplayActionsState(); 
			break;
		}
		
		case("empty"): {
			return; 
		}
		case("default"):{
			return;
		}
		}
		
		
		// Execute sub-state
		if (subState != null) {
			subState.execute(context);
			
			// Deselect after action
			//context.deselectAllAfterActionPerformed();
		}
	}
	
}
