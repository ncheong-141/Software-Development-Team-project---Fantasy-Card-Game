package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_U_BuffAttackIfAvatarTakesDamage  implements Ability {

	
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType; 
	private Call_IDs callID; 
	EffectAnimation eAnimation; 
	
	// Constructors
	public A_U_BuffAttackIfAvatarTakesDamage(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
	
		this.callID = Call_IDs.onFriendlyAvatarDamageTaken;
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// If your avatar is dealt damage, this unit gains +2 attack 
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		// Heal the Avatar 3 HP
		targetMonster.buffAttack(2);
		return true; 
	}

	// ================================================================================
	
	
	// Getters to communicate target and call chronology information
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