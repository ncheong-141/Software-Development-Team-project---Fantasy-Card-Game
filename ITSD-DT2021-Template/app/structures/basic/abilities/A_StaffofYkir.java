package structures.basic.abilities;

import structures.basic.Avatar;
import structures.basic.Monster;

public class A_StaffofYkir implements Ability {

	// Ability attributes 
	boolean enemyTarget; 
	Class<? extends Monster> targetType; 
	
	// Constructor
	public A_StaffofYkir(boolean enemyTarget, Class<? extends Monster> targetType) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
	}
	
	public A_StaffofYkir() {
		this.enemyTarget = false;
		this.targetType = null; 
	}
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Add + 2 attack to avatar
	public boolean execute(Monster targetMonster) {
		
		int additionalAttackValue = 2; 
		
		// Add two attack to avatar
		if (targetMonster instanceof Avatar) {
			System.out.println("Avatar attack: " + targetMonster.getAttackValue());
			targetMonster.setAttackValue(targetMonster.getAttackValue() + additionalAttackValue);
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
}
