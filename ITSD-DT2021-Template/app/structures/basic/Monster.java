package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Monster extends Unit{

	
	@JsonIgnore
	
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	public String name; 
	protected int HP;
	protected int attackValue; 
	protected int manaCost; 
	
	protected boolean 	selected;
	protected Player	owner;
	protected int 		maxHP;
//	private Ability		monsterAbility;
	
	/* Constructor(s) */
	public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		super(id, animations, correction); // Specify id, UnitAnimationSet, ImageCorrection and/or Tile 
		
		// No attribute setting here as Monsters are initially created with a Unit reference, 
		// which could be required by the ObjectMapper which loads the JSon files 
		
	}
	
	// Empty constructor for testing 
	public Monster() {
		super(); 
	}

	public void basicSetup() {
		
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
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setStatus(boolean i) {
		selected = i;
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
	
	public int getMaxHP() {
		return maxHP;
	}
	
	public void setMaxHP(int x) {
		this.maxHP = x;
	}
	
//	public Ability getAbility() {
//		return monsterAbility;
//	}
//	
//	public void setAbility(Ability ab) {
//		this.monsterAbility = ab;
//	}
	
}
