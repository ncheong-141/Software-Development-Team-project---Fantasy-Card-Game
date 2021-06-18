package structures.basic;
import structures.basic.abilities.Ability;

public class Spell extends Unit{

	/* Sub class attributes */
	String spellName; 
	String abilityDescription; 
	
	Ability spellAbility; 
	
	/* Constructor */
	public Spell(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		// Super class constructor (Unit) 
		super(id, animations, correction); 
	}

	/* Class methods */
	
	public Ability getAbility() {
		return spellAbility; 
	}
	
	public void setAbility(String name, Ability ability, String description) {
		this.spellName 			= name; 
		this.spellAbility 		= ability;
		this.abilityDescription = description;
	}
}
