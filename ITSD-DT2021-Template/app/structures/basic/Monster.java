package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Monster extends Unit{

	
	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	
	/* Sub class attributes */
	public String name; 
	private int HP; 
	private int attackValue; 
	private int manaCost; 
	//private Skills skills; 
	private int skillID; 
	
	boolean 	selected;
	
	/* Constructor(s) */
	public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		super(id, animations, correction); // Specify id, UnitAnimationSet, ImageCorrection and/or Tile 
		
		// No attribute setting here as Monsters are initially created with a Unit reference, which could be required by the ObjectMapper which loads the Json files 
	}
	
	// Empty constructor for testing 
	public Monster() {
		super(); 
	}


	/* Class methods */ 
	
	// Move unit
	// Attack unit
	// Defend from unit (counter attack and HP reduction if hit) 
	// Use their ability
	
	
	/* Getters and setters */ 
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHP() {
		return HP;
	}

	public void setHP(int hP) {
		HP = hP;
	}

	public int getAttackValue() {
		return attackValue;
	}

	public void setAttackValue(int attackValue) {
		this.attackValue = attackValue;
	}

	public int getManaCost() {
		return manaCost;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

//	public Skills getSkills() {
//		return skills;
//	}
//
//	public void setSkills(Skills skills) {
//		this.skills = skills;
//	}

	public int getSkillID() {
		return skillID;
	}

	public void setSkillID(int skillID) {
		this.skillID = skillID;
	}

	
}
