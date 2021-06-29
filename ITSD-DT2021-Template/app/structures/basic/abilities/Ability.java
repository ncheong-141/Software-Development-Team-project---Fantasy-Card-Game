package structures.basic.abilities;
import structures.GameState;
import structures.basic.Monster;

public interface Ability {
	
	/*  
	 * Abilities must be able to execute, either passive (in constructor) or active (when called to execute)
	 * Using an interface too to allow a Monster to store ANY ability which lets the code be very flexible. 
	 * Abilities will implement Ability and be stored in Monster or Spell as a Ability reference. 
	 */

	// Notes on method execute(..): 
	// - Only executing on Monster entities as this includes all monster units and Avatar (since Avatar extends Monster), however,
	// - If an ability was not required to execute on a monster or Avatar, then make a new method execute() in that ability and implement there. 
	// - Return a boolean to flag if the ability was cast successfully. (if not, ability not used) 
	
	public boolean execute(Monster monsterEntity, GameState gameState); 
	

	// Enforce the getting of targetType and boolean for targeting enemy (if none applies, leave methods empty) such that attributes must be set
	// to define these game parameters 
	public Class<? extends Monster> getTargetType();
	public boolean targetEnemy(); 
	
	public Call_IDs getCallID(); 

	
	// Call ID
	/*
	 * 0 = No ability 
	 * 1 = Called on summon (just after construction) 
	 * 2 = Called on death 
	 * 3 = Called on enemy player casts spell 
	 * 4 = Provoke: called on unit selection (check if any enemy unit adjacent has provoke) 
	 * 5 = Called when friendly Avatar is dealt damage 
	 * 6 = Called when selecting a card (Play anywhere on the board ability) 
	 * ... 
	 */
	
}
