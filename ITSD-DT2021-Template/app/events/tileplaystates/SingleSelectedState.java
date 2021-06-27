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

		case("friendly unit"): {
			System.out.println("Hit friendly unit case in SingleSelected State");
			subState = new UnitDisplayActionsState(); 
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
		if (subState != null) {
			subState.execute(context);
			
			// Deselect after action
			//context.deselectAllAfterActionPerformed();
		}
	}
	
}
