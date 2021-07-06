package structures.basic.abilities;

import structures.GameState;
import structures.basic.Avatar;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_S_StaffofYkir implements Ability {

	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType; 
	private Call_IDs callID;
	EffectAnimation eAnimation; 
	
	// Constructor
	public A_S_StaffofYkir(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation;
		
		this.callID = Call_IDs.noTimeConstraint;
	}

	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Add + 2 attack to avatar
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		int additionalAttackValue = 2; 
		
		// Add two attack to avatar (done in buff())
		if (targetMonster instanceof Avatar) {
			System.out.println("Avatar attack: " + targetMonster.getAttackValue());
			targetMonster.buffAttack(additionalAttackValue);
			System.out.println("Avatar attack after: " + targetMonster.getAttackValue());

			return true; 
		}
		else {
			System.out.println("Not instance of avatar");
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
