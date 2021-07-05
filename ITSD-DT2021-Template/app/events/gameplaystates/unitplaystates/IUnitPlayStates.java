package events.gameplaystates.unitplaystates;

import events.gameplaystates.GameplayContext;


/*
 * Interface class for all types of unit gameplay states.
 * States should be independent from each other
 */
public interface IUnitPlayStates {

	// All State classes must implement an execute(..) method to play any gameplay logic related to that type of state. 
	public abstract void execute(GameplayContext context); 
}
