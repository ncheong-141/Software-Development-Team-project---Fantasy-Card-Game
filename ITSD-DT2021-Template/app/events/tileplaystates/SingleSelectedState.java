package events.tileplaystates;

public class SingleSelectedState implements GameplayStates{

	// State attributes
	GameplayStates subState; 

	// State constructor 
	public SingleSelectedState() {	
		subState = null; 
	}
	
	public void execute(GameplayContext context) {
			
		
		// Determine the substate (UnitDisplayActionsState or nothing for now) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("unit"): {
			subState = new UnitDisplayActionsState(); 
		}
		
		case("empty"): {
		}
		
		}
		
		
		// Execute sub-state
		subState.execute(context);
	}
	
}
