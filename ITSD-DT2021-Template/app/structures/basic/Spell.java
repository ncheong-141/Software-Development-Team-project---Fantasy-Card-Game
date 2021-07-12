package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import structures.basic.abilities.*;



public class Spell extends Card {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	
	/** Sub class attributes **/
	private String spellName; 							// Name of spell
	private Ability spellAbility; 						// Spell uses an ability, likewise to Monster, to enact its effects.
	private Class<? extends Monster> targetMonster; 	// Specified target type in .class form
	
	
	/** Constructor **/
	public Spell(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		
		// Super class constructor (Unit) 
		super(id, cardname, manacost, miniCard, bigCard); 
	}
	
	// Required default constructor for jackson object instantiation
	public Spell() {
		super(); 
	}

	
	/** Class methods **/
	
	public Ability getAbility() {
		return spellAbility; 
	}
	
	public void setAbility(String name, Ability ability) {
		this.spellName 			= name; 
		this.spellAbility 		= ability;
	}
}
