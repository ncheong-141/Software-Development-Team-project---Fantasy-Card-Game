package structures.basic;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import commands.BasicCommands;
import structures.basic.abilities.Ability;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Monster extends Unit{

	
	@JsonIgnore
	
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	public String name; 
	protected int HP;
	protected int maxHP;
	protected int attackValue; 
	
	protected int movesLeft;
	protected int attacksLeft;
	
	protected Player				owner;				// Player who owns the unit
	protected boolean 				selected;			// Tracks when the unit is selected on board by owner
	protected boolean				onCooldown;			// Tracks when the unit has actions left (move and/or attack)
	protected ArrayList <Ability>	monsterAbility;		// Any abilities the Monster has
	
	/* Constructor(s) */
	public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		super(id, animations, correction); // Specify id, UnitAnimationSet, ImageCorrection and/or Tile 
		
		// No attribute setting here as Monsters are initially created with a Unit reference, 
		// which could be required by the ObjectMapper which loads the JSon files 
		
	}
	
	// Default constructor for JSON
	public Monster(/*Card statsRef, Player o*/) {
		super(); 
		System.out.println("Im from the Monster default constructor!");

		this.movesLeft = 2;				// default move/attack values
		this.attacksLeft = 1;
		
		this.selected = false;
//		owner = o;
		//this.onCooldown = true;		// unit can be selected but not move/attack until toggled
		this.onCooldown = false;			// set up for testing
		
	}

	/* Class methods */ 
	
	// Move unit
	// Attack unit
	// Receive damage (HP reduction, counter attack if not from Spell) 
	// Use their ability
	
	// Move
	// Updates movesLeft and Position
	public boolean move(Tile t) {
		if(movesLeft > 0 && !(onCooldown)) {
			// Check change in position on Board dimension indexes to get to t
			int xchange = Math.abs(this.getPosition().getTilex() - t.getTilex());
			int ychange = Math.abs(this.getPosition().getTiley() - t.getTiley());
			// Move fails if index change exceeds ability to move
			if(xchange + ychange > movesLeft) {	return false;	}
			
			movesLeft -= (xchange+ychange);
			// Set position
			this.setPositionByTile(t);
		} else {	return false;	}
		
		if(this.movesLeft == 0 && this.attacksLeft == 0) {
			this.toggleCooldown();
		}
		return true;
	}
	
//	// Attack
//	// Returns the unit's attack and updates its status
//	public int attack() {
//		if(!(onCooldown)) {
//			this.attacksLeft -= 1;
//			return attackValue; 
//		}
//		if(this.attacksLeft == 0) {
//			this.toggleCooldown();
//		}
//		return 0;
//
//	}
	
	// Receive damage (attack, counter-attack or Spell dmg)
	public boolean defend(int d) {
		if(this.HP - d < 0) {
			this.HP = 0;
			System.out.println("Unit has died.");
			return false;
		} else {
			this.HP -= d;
			return true;
		}
	}
	
	
	
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
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setStatus(boolean i) {
		selected = i;
	}

	public void toggleSelect() {
		if(!onCooldown) {
			selected = !selected;
		}
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
	
	public void setMaxHP(int h) {
		this.maxHP = h;
	}
	
	public int getMovesLeft() {
		return movesLeft;
	}
	
	public void setMovesLeft(int m) {
		this.movesLeft = m;
	}
	
	public int getAttacksLeft() {
		return attacksLeft;
	}
	
	public void setAttacksLeft(int a) {
		this.attacksLeft = a;
	}
	
	// Indicates a Monster can no longer move & attack (if true)
	public boolean getOnCooldown() {
		return onCooldown;
	}
	
	// Switches cooldown status and dependent variables
	public void toggleCooldown() {
		this.onCooldown = !onCooldown;
		this.actionSet();
	}
	
	// Helper method for cooldown management
	private void actionSet() {
		if(onCooldown) {
			this.movesLeft = 0;
			this.attacksLeft = 0;
		} else {
			this.movesLeft = 2;
			this.attacksLeft = 1;
		}
	}
	
	// Getters/setters for Abilities to be put in
	
}
