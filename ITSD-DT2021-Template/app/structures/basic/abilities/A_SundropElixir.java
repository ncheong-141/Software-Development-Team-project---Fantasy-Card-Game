package structures.basic.abilities;

import structures.GameState;
import structures.basic.Monster;

public class A_SundropElixir implements Ability {

	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType; 
	private int callID;
	
	// Constructor
	public A_SundropElixir(boolean enemyTarget, Class<? extends Monster> targetType) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		
		// Call ID of 3 signals this is called on Spell cast
		this.callID = 3; 
	}
	
	public A_SundropElixir() {
		this.enemyTarget = false;
		this.targetType = null; 
		
		// Call ID of 3 signals this is called on Spell cast
		this.callID = 3; 
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Add 5 health to a Unit. This Cannot take a unit over its starting health.
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		// Verbose variable/easy to change
		int healthIncreaseValue = 5;
		
		// Check if the +5 HP is greater than max hp, if so make monster HP = max
		if ( (targetMonster.getHP() + healthIncreaseValue) > targetMonster.getMaxHP()) {
			targetMonster.setHP(targetMonster.getMaxHP());
		}
		else {
			targetMonster.setHP(targetMonster.getHP() + healthIncreaseValue);
		}
		
		return true; 
	}
	// ================================================================================
	
	// Getters to communicate target information
	public boolean targetEnemy() {
		return enemyTarget; 
	}
	
	public Class<? extends Monster> getTargetType() {
		return targetType; 
	}
	
	public int getCallID() {
		return callID; 
	}
}
