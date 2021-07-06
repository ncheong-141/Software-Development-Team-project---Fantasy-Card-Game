package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_U_BuffAttackHPIfEnemySpellCast  implements Ability {

	
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType; 
	private Call_IDs callID; 
	EffectAnimation eAnimation; 
	
	// Constructors
	public A_U_BuffAttackHPIfEnemySpellCast(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
	
		this.callID = Call_IDs.onEnemySpellCast;
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// If the enemy player casts a spell, this minion gains +1 attack and +1 health 
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		targetMonster.heal(1);
		targetMonster.buffAttack(1);
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