package structures.basic;
import structures.basic.abilities.Ability;

public class Spell extends Card {

	/* Sub class attributes */
	String spellName; 
	String abilityDescription; 
	
	Ability spellAbility; 
	
	/* Constructor */
	public Spell(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		
		// Super class constructor (Unit) 
		super(id, cardname, manacost, miniCard, bigCard); 
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
