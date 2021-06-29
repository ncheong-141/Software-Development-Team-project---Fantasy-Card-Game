
package structures.basic.abilities;
import structures.GameState;
import structures.basic.Monster;

public class A_U_PlayersDrawCardOnUnitSummon implements Ability {
	
		// Ability attributes 
		private boolean enemyTarget; 
		private Class<? extends Monster> targetType; 
		private int callID; 
		
		// Constructors
		public A_U_PlayersDrawCardOnUnitSummon(boolean enemyTarget, Class<? extends Monster> targetType) {
			this.enemyTarget = enemyTarget;
			this.targetType = targetType; 
			
			// Call ID of 1 signals use ability on summon
			this.callID = 1;
		}
		
		public A_U_PlayersDrawCardOnUnitSummon() {
			this.enemyTarget = false;
			this.targetType = null; 
			
			// Call ID of 1 signals use ability on summon
			this.callID = 1;
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
		
		public int getCallID() { 
			return callID;
		}

}

	

