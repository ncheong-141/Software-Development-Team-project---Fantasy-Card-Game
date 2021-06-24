package structures.basic.abilities;

import structures.basic.Monster;

public class A_SundropElixir implements Ability {

	// Ability attributes 
	boolean enemyTarget; 
	Class<? extends Monster> targetType; 
	
	// Constructor
	public A_SundropElixir(boolean enemyTarget, Class<? extends Monster> targetType) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
	}
	
	public A_SundropElixir() {
		this.enemyTarget = false;
		this.targetType = null; 
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Add 5 health to a Unit. This Cannot take a unit over its starting health.
	public boolean execute(Monster targetMonster) {
		
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
}
