package structures.basic.abilities;
import structures.GameState;
import structures.basic.EffectAnimation;
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
	
	// Enum value used to control when an ability is called (e.g. on summon, death etc) 
	public Call_IDs getCallID(); 	
	
	// Effect animation for front end display
	public EffectAnimation getEffectAnimation();
}
