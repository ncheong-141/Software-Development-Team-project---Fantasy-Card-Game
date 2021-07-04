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
	
	protected int movesLeft;							// move actions left, tracks directly to range
	protected int attacksLeft;							// attack actions left, != range
	protected int attackRange;							// tile range for attacks
	
	protected Player				owner;				// Player who owns the unit
	protected boolean 				selected;			// Tracks when the unit is selected on board by owner
	protected boolean				onCooldown;			// Tracks when the unit has actions left (move and/or attack)
	protected ArrayList <Ability>	monsterAbility;		// Any abilities the Monster has
	
	/* Constructor(s) */
	public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		super(id, animations, correction); // Specify id, UnitAnimationSet, ImageCorrection and/or Tile 
		
	}
	
	// Default constructor for JSON
	public Monster(/*Card statsRef, Player o*/) {
		super(); 

		this.movesLeft = 0;				//
		this.attacksLeft = 0;			// Unit is summoned on cooldown 
		this.attackRange = 1;			//
		
		this.selected = false;
//		owner = o;
		this.onCooldown = true;			// Unit is summoned on cooldown
		System.out.println("As a Monster I am: " + this.getOnCooldown());
		
	}

	/* Class methods */ 
	
	// Move unit
	// Attack unit
	// Receive damage (HP reduction, counter attack if not from Spell) 
	// Use their ability
	
	// Move
	// Returns outcome of an attempt to move (successful or not) and updates move variables
	public boolean move(Tile t) {
		if(movesLeft > 0 && !(onCooldown)) {
			// Check change in Board dimension indices from current to t
			int xchange = Math.abs(this.getPosition().getTilex() - t.getTilex());
			int ychange = Math.abs(this.getPosition().getTiley() - t.getTiley());
			// Move fails if index change exceeds ability to move
			if(xchange + ychange > movesLeft) {	return false;	}
			
			movesLeft -= (xchange+ychange);
			// Set position
			this.setPositionByTile(t);
		} else {	return false;	}
		
		if(this.movesLeft == 0 && this.attacksLeft == 0) {	// this should check attack range == 0 really
			this.toggleCooldown();
		}
		return true;
	}
	
	// Attack
	// Returns the outcome of an attack (successful or not) and updates attack variables
	public boolean attack() {
		// Check if Monster is able to attack
		if(this.onCooldown) {
			return false; 
		}
		this.attacksLeft -= 1;
		if(this.attacksLeft == 0) {
			this.toggleCooldown();
		}
		return true;
	}
	
	// Counter
	
	// Returns outcome of receiving damaged (attack/counter-attack/Spell dmg) and updates health
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
	
	public int getAttackRange() {
		return attackRange;
	}
	
	public void setAttackRange(int a) {
		this.attackRange = a;
	}
	
	// Indicates a Monster can no longer move & attack (if true)
	public boolean getOnCooldown() {
		return onCooldown;
	}

	// temporary for testing
	public void setCooldown(boolean b) {
		onCooldown = b;
		this.actionSet();
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
	
	public boolean getMonsterAbility() {
		if(abilities != null) {
			return true;
		}
		return false;
	}
	
}
