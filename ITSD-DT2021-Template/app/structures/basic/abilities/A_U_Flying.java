package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class A_U_Flying implements Ability {

	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType; 
	private Call_IDs callID; 
	EffectAnimation eAnimation; 
	
	// Constructors
	public A_U_Flying(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
	
		this.callID = Call_IDs.construction;
	}
	
	// Flying units can move through enemy units
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// Monster can move anywhere on the Board
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		// Set to board maximum dimensions
		int boardWidth = gameState.getBoard().getBoardWidth(); 
		int boardLength = gameState.getBoard().getBoardLength(); 
		
		// Set max moves to board length and width (gameState.getBoard().get....)

		targetMonster.setMovesMax(boardWidth*boardLength);

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