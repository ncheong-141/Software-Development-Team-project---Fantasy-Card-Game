package events.tileplaystates;

/*
 * Interface class for all types of gameplay states.
 * States should be independent from each other
 */
public interface GameplayStates {

	// All State classes must implement an execute(..) method to play any gameplay logic related to that type of state. 
	public void execute(GameplayContext context); 
}
