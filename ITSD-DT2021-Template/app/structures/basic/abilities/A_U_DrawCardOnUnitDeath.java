
package structures.basic.abilities;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_U_DrawCardOnUnitDeath implements Ability {
	
		// Ability attributes 
		private boolean enemyTarget; 
		private Class<? extends Monster> targetType; 
		private Call_IDs callID; 
		EffectAnimation eAnimation; 
		
		// Constructors
		public A_U_DrawCardOnUnitDeath(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
			this.enemyTarget = enemyTarget;
			this.targetType = targetType; 
			this.eAnimation = eAnimation; 
			
			this.callID = Call_IDs.onDeath;
		}
		

		/* Class methods */
		
		// ABILITY IMPLEMENTATION
		// ================================================================================
		// Player draws card when this unit is defeated. 
		// Ignore the Monster input since this ability doesnt affect Monsters
		public boolean execute(Monster targetMonster, GameState gameState) {
			
			targetMonster.getOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());
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

	

