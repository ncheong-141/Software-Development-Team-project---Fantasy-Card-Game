package structures.basic.abilities;

import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Monster;

public class A_EntropicDecay implements Ability {
	
	
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType;
	private int callID;
	
	// Constructor
	public A_EntropicDecay(boolean enemyTarget, Class<? extends Monster> targetType) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		
		// Call ID of 3 signals this is called on Spell cast
		this.callID = 3; 
	}
	
	public A_EntropicDecay() {
		this.enemyTarget = false;
		this.targetType = null; 
		
		// Call ID of 3 signals this is called on Spell cast
		this.callID = 3; 
	}
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Reduce non-avatar entity HP to 0
	public boolean execute(Monster targetMonster, GameState gameState) {
		
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
	
	public int getCallID() {
		return callID; 
	}
}
