
package structures.basic.abilities;
import structures.GameState;
import structures.basic.Monster;

public class A_U_DoubleAttacker implements Ability {
	
		// Ability attributes 
		private boolean enemyTarget; 
		private Class<? extends Monster> targetType; 
		private Call_IDs callID; 
		
		// Constructors
		public A_U_DoubleAttacker(boolean enemyTarget, Class<? extends Monster> targetType) {
			this.enemyTarget = enemyTarget;
			this.targetType = targetType; 
		
			this.callID = Call_IDs.construction;
		}
		
		public A_U_DoubleAttacker() {
			this.enemyTarget = false;
			this.targetType = null; 
			
			this.callID = Call_IDs.construction;
		}
		
		/* Class methods */
		
		// ABILITY IMPLEMENTATION
		// ================================================================================
		// Monster has the ability to attack twice
		// Ignore GameState input since Ability deals with Unit internal values
		public boolean execute(Monster targetMonster, GameState gameState) {
			return execute(targetMonster); 
		}
		
		// Alternative method signature that reflects actual behaviour
		public boolean execute(Monster targetMonster) {

			targetMonster.setAttacksMax(2);
			
			// For unusual cases where a Monster is summoned off cooldown (has attacksLeft after summoning):
			if(targetMonster.getAttacksLeft() > 0) {	targetMonster.setAttacksLeft(targetMonster.getAttacksMax());	}
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

	

