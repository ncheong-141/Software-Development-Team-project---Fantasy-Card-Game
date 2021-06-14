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
	
	private boolean 	selected;
	private Player		owner;
	
	/* Constructor(s) */
	public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		super(id, animations, correction); // Specify id, UnitAnimationSet, ImageCorrection and/or Tile 
		this.setStatus();
		
		// No attribute setting here as Monsters are initially created with a Unit reference, 
		// which could be required by the ObjectMapper which loads the Json files 
	}
	
	// Empty constructor for testing 
	public Monster() {
		super(); 
	}


	/* Class methods */ 
	
	// Move unit
	// Attack unit
	// Receive damage (HP reduction, counter attack if not from Spell) 
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
	
	private void setStatus() {
		selected = false;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void toggleSelect() {
		selected = !selected;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	// Will be removing this when Vic can get owner to be set at object instantiation
	public void setOwner(Player p) {
		owner = p;
	}
	
}
