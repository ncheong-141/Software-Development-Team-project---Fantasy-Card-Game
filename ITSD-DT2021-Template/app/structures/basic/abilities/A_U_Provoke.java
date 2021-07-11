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
		/**
		 * This ability is checked for relative to the selected unit and is called to check when a new Unit is selected.
		 * In the ability implementation, it checks for enemies in action range tiles, if they have provoke, it sets the Tile container for highlighted Tiles 
		 * in gameState which is then displayed to the Front end through the UnitDisplayActionState (control flow branch).
		 */

		
		/***
		 * 
		 * 1.) Get the full movement and attack tiles (attack tiles by action tiles - movement tiles)
		 * 2.) Loop over action range
		 * 		- Get the full threat range of the provoke monster(s) if present
		 * 		- Return 
		 * 
		 * 3.) Make a new arraylist container for each Tile type (M,T,A,TA,TM) 
		 * 		- Loop over threat range and check if each tile and classify them accordingly (add to array container) 
		 * 		- M tiles = Possible movement tiles
		 * 		- T tiles = Threatened tiles (by provoked unit)
		 * 		- A tiles = Possible attack tiles (if enemy monster on)
		 * 		- P tiles = tiles with provoking unit
		 * 		- TA tiles = Possible attack tiles which is also threatened by enemy
		 * 		- TM tiles = Possible movement tiles which are threatened by enemy
		 * 		- TAP tiles = Tiles with provoking monster able to attack 
		 * 
		 * 4.) Enforce conditions on each tile type lists (to formulate list of possible tiles)
		 * 		- Add all movement tiles 
		 * 		- Can only add a TA and A tiles if adjacent to an M tile.
		 * 		- Can only add TM tiles if they are cardinally adjacent to a movement tile  
		 * 	- If youre located in a TM tile you have to attack (cant exit out loop early incase theres more than one) 
		 *  - Tile that selected unit is on is an M tile for condition checking
		 */
		
		// Clear container before calculating new display tiles
		gameState.getTileAdjustedRangeContainer().clear();
		
		
		
		/** =======================================================================
		 * Set up: Get the full movement, attack and actionable tiles for reference
		 *  =======================================================================
		 **/
		int selectedUnitX = targetMonster.getPosition().getTilex(); 
		int selectedUnitY = targetMonster.getPosition().getTiley(); 		
		
		System.out.println("Selected monster: " + selectedUnitX + "," + selectedUnitY);
 
		// All actionable tiles
		ArrayList<Tile> actionableTiles = gameState.getBoard().unitAllActionableTiles(selectedUnitX, selectedUnitY, targetMonster.getMovesLeft(), targetMonster.getAttackRange());
				
		// All moveable tiles for reference (reachAble tiles returns all tiles with no tiles removed due to Units) 
		ArrayList<Tile> moveableTiles 	= gameState.getBoard().unitMovableTiles(selectedUnitX, selectedUnitY, targetMonster.getMovesLeft());
		moveableTiles.add(targetMonster.getPosition().getTile(gameState.getBoard()));
		
		printTiles("Moveablee tiles", moveableTiles);
		
		// All attackable tiles for reference 
		ArrayList<Tile> attackableTiles = new ArrayList<Tile>(10); 
		for (int i = 0; i < actionableTiles.size(); i++) {
			
			if (!moveableTiles.contains(actionableTiles.get(i))) {
				attackableTiles.add(actionableTiles.get(i)); 
			}
		}
		
		printTiles("AttackableTiles", attackableTiles);
		
		// Provoke monster threat range 
		ArrayList<Tile> threatenedTiles = new ArrayList<Tile>(10); 
		ArrayList<Tile> provokingMonsterTiles = new ArrayList<Tile>(2);
		
		// Check if there is a provoking monster in action range
		for (Tile t : actionableTiles) {
			
			// If there is a Unit and is enemy
			if (t.getUnitOnTile() != null) {	
				
				// If it has an ability
				if (t.getUnitOnTile().getMonsterAbility() != null && (t.getUnitOnTile().getOwner() == gameState.getEnemyPlayer())) { 
					for (Ability ability : t.getUnitOnTile().getMonsterAbility() ) {
						
						if (ability instanceof A_U_Provoke) {
							
							// Generate threatened tiles (all adjacent tiles) (needs to be like this incase more than 1 provoke monster)
							ArrayList<Tile> temp = gameState.getBoard().adjTiles(t); 
							temp.add(t); 
							
							for (Tile tempT : temp) {
								threatenedTiles.add(tempT);
							}
							
							// Keep track of the provoking monster for reference
							provokingMonsterTiles.add(t);
						}
					}
				}
			}
		}
		
		printTiles("Threatened tiles", threatenedTiles);
		printTiles("Provoke Monster tiles", provokingMonsterTiles);
		
		
		// Terminate ability of no condition to apply
		if (threatenedTiles.isEmpty()) {
			return false;
		}
		

		
		/** ==============================================================================================
		 * Classify the tiles into types depending on the relation to the select unit and provoke monster
		 * ===============================================================================================
		 **/
		// Classified tile list (tiles with arbitrary type attached)
		ArrayList<ClassifiedTile> classifiedActionableTiles = new ArrayList<ClassifiedTile>(actionableTiles.size()); 
		
		// Iterate over actionablity tiles and generate a classified actionable tiles lit 
		for (Tile t : actionableTiles) {
			
			// Create tile with type and set the type using class inner method 
			ClassifiedTile tileWithType = new ClassifiedTile(t); 
			
			// Sets ClassifiedTile with type M,A,T,P (these are concatenated to form other characteristic tiles such as TM for threatened and in movement range)
			tileWithType.setType(gameState, threatenedTiles, moveableTiles, attackableTiles, provokingMonsterTiles);
			
			// Add the tile to the array
			classifiedActionableTiles.add(tileWithType);
		}
		
		// debug
		for (ClassifiedTile ct : classifiedActionableTiles) {
			System.out.println("Tile: " + ct.getTile().getTilex() + "," + ct.getTile().getTiley() + "Type: " + ct.getType());
		}
		
		
		/** =============================================================================================================== 
		 * Iterate over classified tiles and apply conditions. Add all tiles to a return array which satisfy conditions 
		 *  =============================================================================================================== 
		 * **/
		
		// Return array
		ArrayList<Tile> displayTiles = new ArrayList<Tile>(10);

		// Apply conditions to each characteristic tile type 
		for (ClassifiedTile ct : classifiedActionableTiles) {
			
			/**
			 * 		If youre located in a TM tile you have to attack (cant exit out loop early incase theres more than one) 
			 * 		- Add all movement tiles. Tile that selected unit is on is an M tile
			 * 		- Can only add a TA and A tiles if adjacent to an M tile. Also only add these tiles if they have an enemy unit.
			 * 		- Can only add TM tiles if they are cardinally adjacent to a movement tile 
			 * 		- Add TAP tiles
			 * 		
			 */
			
			// Add all movement tiles. Tile that selected unit is on is an M tile
			if (ct.getType().equals("M")) {
				displayTiles.add(ct.getTile());
			}
			
			// Can only add a TA and A tiles if adjacent to an M tile
			if (ct.getType().equals("A") || ct.getType().equals("TA")) {
								
				// First check if there is a Enemy Unit on the tile (no point displaying if not) 
				if (ct.getTile().getUnitOnTile() != null) {
					if (ct.getTile().getUnitOnTile().getOwner() == gameState.getEnemyPlayer()) {
					
						// Generate adjacent tiles to overlay
						ArrayList<Tile> adjacentTiles = gameState.getBoard().adjTiles(ct.getTile()); 

						// Relate all adjacent tiles with the classified tiles (find overlap)
						for (ClassifiedTile ct2 : classifiedActionableTiles) {
							if (adjacentTiles.contains(ct2.getTile())) {

								// If any of the adjacent tiles are an M tile
								if (ct2.getType().equals("M")) {
									displayTiles.add(ct.getTile());
								}
							}
						}
					}
				}
			}
			
			//  Can only add TM tiles if they are CARDINALLY adjacent to a movement tile
			if (ct.getType().equals("TM")) {
				
				// If selected unit is on a threatened tile (add threatening monster tile and return)
				if (ct.getTile().getUnitOnTile() == targetMonster) {
					
					// Force clear all added tiles (better to do this check at the start but inconvenient with how its set up) 
					displayTiles.clear();
					
					// Check closest provoking monster
					displayTiles.add(provokingMonsterTiles.get(0));			// Hard coding first one in cause lazy
					gameState.setTileAdjustedRangeContainer(displayTiles);
					return true;
				}
				
				// Calculate all cardinallyAdjacentTiles
				ArrayList<Tile> cardinallyAdjacentTiles = gameState.getBoard().cardinallyAdjTiles(ct.getTile()); 		
				
				// Relate all adjacent tiles with the classified tiles (find overlap)
				for (ClassifiedTile ct2 : classifiedActionableTiles) {
					if (cardinallyAdjacentTiles.contains(ct2.getTile())) {

						if (ct2.getType().equals("M")) {
							displayTiles.add(ct.getTile());
						}
					}
				}
			}
			
			// If tile is the provoking monster which is in attack range
			if (ct.getType().equals("TAP")) {
				displayTiles.add(ct.getTile());
			}
		}
		
		
		
		/** Set gameState temp container to use for adjusting the attack/move range **/
	
		// Remove tile selected monster is on (only used for tile condition considerations)
//		displayTiles.removeIf(tile -> (tile == targetMonster.getPosition().getTile(gameState.getBoard())));
		displayTiles.remove(targetMonster.getPosition().getTile(gameState.getBoard()));
		
		System.out.println("MovesLeft provke: " + targetMonster.getMovesLeft());
		
		// Set output
		gameState.setTileAdjustedRangeContainer(displayTiles);
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
	
	
	/** Inner class for classifying tiles **/ 
	private class ClassifiedTile {
		
		// Attributes
		private String 	type; 
		private Tile 	tile; 
		
		// Constructor
		private ClassifiedTile(Tile t) {
			this.tile = t; 
			type = ""; 
		}
		
		public Tile getTile() {
			return tile; 
		}
		
		public String getType() {
			return type;
		}
		
		// Set type of tile (M (movement) ,A (attackable) ,T (threatened by provoking monster) ,P (provoking monster) )
		public void setType(GameState gameState, ArrayList<Tile> threatenedTiles, ArrayList<Tile> moveableTiles, ArrayList<Tile> attackableTiles, ArrayList<Tile> provokingMonsterTiles) {
			
			// Check each list and append characteristic character 
		
			if (threatenedTiles.contains(tile)) {
				type += "T";
			}
			
			if (moveableTiles.contains(tile)) {
				
				// Check if a unit is on the tile, if so, if its an enemy convert to attack tile
				if (tile.getUnitOnTile() != null ) {
					if (tile.getUnitOnTile().getOwner() == gameState.getEnemyPlayer()) {
						type += "A";
					}
					else { 
						type += "M"; 
					}
				}
				else { 
					type += "M"; 
				}
			}
			
			if (attackableTiles.contains(tile)) {
				type += "A";
			}
			
			if (provokingMonsterTiles.contains(tile)) {
				type += "P";
			}
		}
	}
	
	
	/** Helper methods **/
	
	public void printTiles(String desc, ArrayList<Tile> tiles) {
		
		System.out.println(desc + "\n");
		
		for (Tile t : tiles) {
			System.out.println(t.getTilex() + "," + t.getTiley());
		}
	}
}