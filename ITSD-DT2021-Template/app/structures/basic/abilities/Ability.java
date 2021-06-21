package structures.basic.abilities;
import structures.basic.Monster;

public interface Ability {
	
	/*  
	 * Abilities must be able to execute, either passive (in constructor) or active (when called to execute)
	 * Using an interface too to allow a Monster to store ANY ability which lets the code be very flexible. 
	 * Abilities will implement Ability and be stored in Monster or Spell as a Ability reference. 
	 */

	// Only executing on Monster entities as this includes all monster units and Avatar (since Avatar extends Monster).
	// Return a boolean to flag if the ability was cast successfully. (if not, ability not used) 
	
	public boolean execute(Monster monsterEntity); 
	

}
