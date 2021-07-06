package structures.basic.abilities;

import java.util.ArrayList;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Tile;

public class A_U_Provoke implements Ability{
	// Ability attributes 
	private boolean enemyTarget; 
	private Class<? extends Monster> targetType;
	private Call_IDs callID; 
	EffectAnimation eAnimation;
	
	
	// Constructor
	public A_U_Provoke(boolean enemyTarget, Class<? extends Monster> targetType, EffectAnimation eAnimation) {
		this.enemyTarget = enemyTarget;
		this.targetType = targetType; 
		this.eAnimation = eAnimation; 
		
		this.callID = Call_IDs.onUnitSelection;
	}
	
	
	/* Class methods */
	
	// ABILITY IMPLEMENTATION
	// ================================================================================
	// If an enemy unit can attack and is adjacent to any unit with provoke, 
	// then it can only choose to attack the provoke units. Enemy units cannot move when provoked.
	public boolean execute(Monster targetMonster, GameState gameState) {
		
		// Changes a temp storage of tiles to highlight (stored in gameState)
		// targetMonster inputed is the selected Monster, so need to check all around for Monsters with provoke
		
		int selectedUnitX = targetMonster.getPosition().getTilex(); 
		int selectedUnitY = targetMonster.getPosition().getTilex(); 		
		
		// Get all tiles adjacent to the selected unit with an enemy 
		ArrayList<Tile> enemyAdjTiles = gameState.getBoard().adjEnemyTiles(selectedUnitX, selectedUnitY, gameState.getTurnOwner());
		
		// Container for highlighting tiles (highlight all monsters with provoke, if more than one)
		ArrayList<Tile> enemyTileToHighlight = new ArrayList<Tile>(); 

		// Loop over enemies and check for an ability with provoke
		for (Tile enemyTile : enemyAdjTiles) {
			
			// Loop over all monster abilities on each tile
			for (Ability ability : enemyTile.getUnitOnTile().getMonsterAbility()) {
				
				// If monster has provoke
				if (ability instanceof A_U_Provoke) {
					
					// Set the tiles to only the enemy Monster position as it can only attack it 
					enemyTileToHighlight.add(enemyTile);					
				}
			}
		}

		// Check if there was any enemies with provoke and return a flag accordingly 
		if (enemyTileToHighlight.isEmpty()) {
			return false;
		}				
		else {

			// Set container in gameState to highlight tiles (done in UnitDisplayActionState, where this ability was called)
			gameState.setTileHighlightContainer(enemyTileToHighlight);
			
			return true; 
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
	
	public Call_IDs getCallID() {
		return callID; 
	}
	
	public EffectAnimation getEffectAnimation() {
		return eAnimation;
	}
}