
package structures.basic.abilities;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_U_RangedAttacker implements Ability {
	
		// Ability attributes 
		private boolean enemyTarget; 
		private Class<? extends Monster> targetType; 
		private Call_IDs callID; 
		EffectAnimation eAnimation; 
		
		// Constructors
		public A_U_RangedAttacker(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
			this.enemyTarget = enemyTarget;
			this.targetType = targetType; 
			this.eAnimation = eAnimation; 
		
			this.callID = Call_IDs.construction;
		}
		

		/* Class methods */
		
		// ABILITY IMPLEMENTATION
		// ================================================================================
		// Monster can attack from anywhere on the Board
		// Ignore GameState input since Ability deals with Unit internal values
		public boolean execute(Monster targetMonster, GameState gameState) {
			

			// Set to board maximum dimensions
			int boardWidth = gameState.getBoard().getBoardWidth(); 
			int boardLength = gameState.getBoard().getBoardLength(); 
			
			// Set attack range to this (so can hit from corner to opposite corner)
			targetMonster.setAttackRange(boardWidth + boardLength);
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

	

