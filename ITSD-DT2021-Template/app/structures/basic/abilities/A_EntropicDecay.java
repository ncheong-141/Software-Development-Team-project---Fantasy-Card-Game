package structures.basic.abilities;

import structures.basic.Avatar;
import structures.basic.Monster;

public class A_EntropicDecay implements Ability {
	
	
	// Ability attributes 
	boolean enemyTarget; 
	Class<? extends Monster> targetType; 
	
	// Constructor
	public A_EntropicDecay(boolean enemyTarget, Class<? extends Monster> targetType) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
	}
	
	public A_EntropicDecay() {
		this.enemyTarget = false;
		this.targetType = null; 
	}
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Reduce non-avatar entity HP to 0
	public boolean execute(Monster targetMonster) {
		
		if (!(targetMonster instanceof Avatar)) {
			targetMonster.setHP(0);
			return true; 
		}
		else {
			return false;
		}	
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
