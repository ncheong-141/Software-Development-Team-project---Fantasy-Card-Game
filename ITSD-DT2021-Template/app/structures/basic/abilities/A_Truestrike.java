package structures.basic.abilities;
import structures.basic.Monster;

public class A_Truestrike implements Ability {
	
	// Ability attributes 
	boolean enemyTarget; 
	Class<? extends Monster> targetType; 
	
	// Constructor
	public A_Truestrike(boolean enemyTarget, Class<? extends Monster> targetType) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
	}
	
	public A_Truestrike() {
		this.enemyTarget = false;
		this.targetType = null; 
	}
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Deal 2 damage to  an enemy Unit
	public boolean execute(Monster targetMonster) {
		
		// Reduce Monster HP by 2
		targetMonster.setHP(targetMonster.getHP() - 2);
		
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
