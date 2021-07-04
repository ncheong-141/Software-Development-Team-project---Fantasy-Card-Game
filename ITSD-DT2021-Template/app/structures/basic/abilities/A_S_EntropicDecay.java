package structures.basic.abilities;

import structures.GameState;
import structures.basic.Avatar;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_S_EntropicDecay implements Ability {
	
	
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType;
	private Call_IDs callID;
	EffectAnimation eAnimation;
	
	// Constructor
	public A_S_EntropicDecay(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation;
		
		this.callID = Call_IDs.noTimeConstraint;
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
	
	public Call_IDs getCallID() {
		return callID; 
	}
	
	public EffectAnimation getEffectAnimation() {
		return eAnimation;
	}
}
