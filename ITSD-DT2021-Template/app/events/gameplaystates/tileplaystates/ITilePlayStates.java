package events.gameplaystates.tileplaystates;
import events.gameplaystates.GameplayContext;

/**
 * Interface class for Tile states. 
 * Ensures tile states have an execute method and can be polymorphically references where required.
 */
public interface ITilePlayStates {

	// All State classes must implement an execute(..) method to play any gameplay logic related to that type of state. 
	public void execute(GameplayContext context); 
}
