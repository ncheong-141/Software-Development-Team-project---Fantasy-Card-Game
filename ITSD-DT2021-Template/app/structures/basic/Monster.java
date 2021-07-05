package structures.basic;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import structures.basic.abilities.*;

public class Monster extends Unit{

	
	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	// Basic Monster attributes
	public String 					name; 
	protected int 					HP;				// Monster's current health value, between 0 - maxHP
	protected int 					maxHP;			// Maximum value a Monster's HP can be
	protected int 					attackValue; 	// How much damage Monster does in one attack action
	protected ArrayList <Ability>	abilities;		// Any abilities the Monster has
	
	// Action values
	protected int 			movesLeft;				// number of move actions Monster has left, tracks directly to range
	protected int 			attacksLeft;			// number of attack actions Monster has 'leftover', != range
	protected int			attacksMax;				// max number of attack actions a Monster can have, reset by Cooldown
	protected int 			attackRange;			// integer range of tiles (in all directions) for attacks
	
	// Gameplay info
	protected Player		owner;					// Player who owns the unit
	protected boolean		onCooldown;				// Tracks when the unit is capable of actions, where true = able
	
	
	/* Constructor(s) */
	
	// Default constructor for JSON
	public Monster() {
		super(); 
		
		// Some attributes are set by JSON Mapper (Unit class) or in the 
		// loadMonsterUnit ObjectBuilder method (using the Card object as reference):
			// name, HP, maxHP, attackValue, owner, abilities

		this.movesLeft = 0;				//
		this.attacksLeft = 0;			// Unit is summoned on cooldown 
		this.attacksMax = 1;			//
		this.attackRange = 1;			//
		
		this.onCooldown = true;			// Unit is summoned on cooldown
		
		this.abilities = null;			// Abilities set in ObjectBuilder for safe object construction
		
	}
	
	public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
		
		// Specify id, UnitAnimationSet, ImageCorrection and/or Tile
		super(id, animations, correction);  

	}
	


	/* Class methods */ 
	
	/* Move unit
	 * Attack unit
	 * Counter attack (specialised attack, works even onCooldown)
	 * Defend (HP reduction from any source) 
	 * Use ability (applicable to some)
	 */
	
	// Move
	// Returns the outcome of an attempt to move (successful or not) and updates move/location variables
	public boolean move(Tile t) {
		if(movesLeft > 0 && !(onCooldown)) {
			
			// Check change in Board dimension indices from current to t
			int xchange = Math.abs(this.getPosition().getTilex() - t.getTilex());
			int ychange = Math.abs(this.getPosition().getTiley() - t.getTiley());
			// Move fails if total change exceeds ability to move
			if(xchange + ychange > movesLeft) {	return false;	}
			
			movesLeft -= (xchange+ychange);
			this.setPositionByTile(t);
			
		} else {	return false;	}
		
		if(this.movesLeft == 0 && this.attacksLeft == 0) {	// this should check attack range == 0 really
			this.toggleCooldown();
		}
		return true;
	}
	
	// Attack
	// Returns the outcome of an attempt to attack (successful or not) and updates attack variables
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
	
	// Counter-attack
	// Returns the attackValue of a unit, intended to be called after surviving an attack.
	// Counter is not related to attack actions available.
	//Method here - no boolean, just return attackValue
	// counter();
	
	// Defend (receive damage)
	// Returns outcome of receiving damage (successful defence or death) and updates health
	public boolean defend(int d) {
		if(this.HP - d <= 0) {
			this.HP = 0;
			return false;
		} else {
			this.HP -= d;
			return true;
		}
	}
	
	// Heal (adjust health)
	// Updates HP from a heal action
	public void heal(int h) {
		if(this.HP + h > this.maxHP) {
			this.HP = this.maxHP;
		}
		else {
			this.HP += h;
		}
	}
	
	// Buff (adjust attack)
	// Adjusts attackValue statistic from a buff action
	public void buff(int b) {
		this.attackValue += b;
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
	
	public Player getOwner() {
		return owner;
	}
	
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
	
	public int getAttacksMax() {
		return attacksMax;
	}
	
	public void setAttacksMax(int mx) {
		this.attacksMax = mx;
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
		this.onCooldown = b;
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
			this.attacksLeft = this.attacksMax;
		}
	}
	
	public boolean hasAbility() {
		if(abilities != null) {
			return true;
		}
		return false;
	}
	
	public ArrayList <Ability> getMonsterAbility() {
		return abilities;
	}
	
	public void setAbility(ArrayList <Ability> abs) {
		abilities = abs;

	}
	
}
