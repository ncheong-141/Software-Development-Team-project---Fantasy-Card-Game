package structures.basic.abilities;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_S_Truestrike implements Ability {
	
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType;
	private Call_IDs callID; 
	EffectAnimation eAnimation;
	
	
	// Constructor
	public A_S_Truestrike(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
		
		this.callID = Call_IDs.noTimeConstraint;
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Deal 2 damage to  an enemy Unit
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		// Reduce Monster HP by 2
		targetMonster.defend(2);

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
