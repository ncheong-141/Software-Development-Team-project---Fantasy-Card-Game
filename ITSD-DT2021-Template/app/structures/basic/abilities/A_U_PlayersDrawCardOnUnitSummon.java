
package structures.basic.abilities;
import structures.GameState;
import structures.basic.Monster;

public class A_U_PlayersDrawCardOnUnitSummon implements Ability {
	
		// Ability attributes 
		private boolean enemyTarget; 
		private Class<? extends Monster> targetType; 
		private Call_IDs callID; 
		
		// Constructors
		public A_U_PlayersDrawCardOnUnitSummon(boolean enemyTarget, Class<? extends Monster> targetType) {
			this.enemyTarget = enemyTarget;
			this.targetType = targetType; 
		
			this.callID = Call_IDs.onSummon;
		}
		
		public A_U_PlayersDrawCardOnUnitSummon() {
			this.enemyTarget = false;
			this.targetType = null; 
			
			this.callID = Call_IDs.onSummon;
		}
		
		/* Class methods */
		
		// ABILITY IMPLEMENTATION
		// ================================================================================
		// Both players draw a card when this Unit is summoned. 
		// Ignore the Monster input since this ability doesnt affect Monsters
		public boolean execute(Monster targetMonster, GameState gameState) {
			return execute(gameState); 
		}
		
		// Can also just call this method, monsters shouldnt be inputed anyway
		public boolean execute(GameState gameState) {
		
			//gameState.getPlayerOne().getHand().drawCard(null, gameState.getPlayerOne().getDeck());
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

	

