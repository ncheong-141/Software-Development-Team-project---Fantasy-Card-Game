package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_U_SummonAnywhere implements Ability {
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType;
	private Call_IDs callID; 
	EffectAnimation eAnimation;
	
	
	// Constructor
	public A_U_SummonAnywhere(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
		
		this.callID = Call_IDs.onCardClicked;
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// This Unit can be summoned anywhere on the board
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		gameState.getTileAdjustedRangeContainer().clear();
		
		// This ability is called when in the SummonMonsterState
		// Set the highlight tile container (used for ability specifics) in GameState to the tiles to be highlighted
		gameState.setTileAdjustedRangeContainer(gameState.getBoard().allFreeTiles());
		
		// Display in CardClicked as has connection to front end

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