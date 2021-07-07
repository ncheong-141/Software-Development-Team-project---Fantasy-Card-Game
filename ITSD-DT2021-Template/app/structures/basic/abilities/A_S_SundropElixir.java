package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_S_SundropElixir implements Ability {

	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType; 
	private Call_IDs callID;
	EffectAnimation eAnimation; 
	
	// Constructor
	public A_S_SundropElixir(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
		
		this.callID = Call_IDs.noTimeConstraint;
	}

	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Add 5 health to a Unit. This Cannot take a unit over its starting health.
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		// Verbose variable/easy to change
		int healthIncreaseValue = 5;
		
		// Check if the +5 HP is greater than max hp, if so make monster HP = max (done in .heal() method)
		targetMonster.heal(healthIncreaseValue);

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
	
	public Call_IDs getCallID() {
		return callID; 
	}
	
	public EffectAnimation getEffectAnimation() {
		return eAnimation;
	}
}
