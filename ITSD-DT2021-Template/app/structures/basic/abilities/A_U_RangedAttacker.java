
package structures.basic.abilities;
import structures.GameState;
import structures.basic.Monster;

public class A_U_RangedAttacker implements Ability {
	
		// Ability attributes 
		private boolean enemyTarget; 
		private Class<? extends Monster> targetType; 
		private Call_IDs callID; 
		
		// Constructors
		public A_U_RangedAttacker(boolean enemyTarget, Class<? extends Monster> targetType) {
			this.enemyTarget = enemyTarget;
			this.targetType = targetType; 
		
			this.callID = Call_IDs.construction;
		}
		

		/* Class methods */
		
		// ABILITY IMPLEMENTATION
		// ================================================================================
		// Monster can attack from anywhere on the Board
		// Ignore GameState input since Ability deals with Unit internal values
		public boolean execute(Monster targetMonster, GameState gameState) {
			return execute(targetMonster); 
		}
		
		// Alternative method signature that reflects actual behaviour
		public boolean execute(Monster targetMonster) {

			// Range value set to arbitrary high number for now
			targetMonster.setAttackRange(60);
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

}

	

