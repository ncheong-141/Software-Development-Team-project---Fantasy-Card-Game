package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.*;

public class UnitCombinedActionState implements IUnitPlayStates {
	
	
	/*** State attributes ***/
	private Tile currentTile; 
	private Tile destination;
	private Tile enemyTarget;
	
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public UnitCombinedActionState(Tile currentTile, Tile targetTile) {
		this.currentTile = currentTile; 
		this.destination = null;
		this.enemyTarget = targetTile; 
	}
	
	
	public void execute(GameplayContext context) {
			
			System.out.println("In UnitCombinedActionSubState.");
			context.debugPrint();
			
			context.setCombinedActive(true);
			
			// Build reference variables
			destination = null;
			if(destination == null && (enemyTarget != null)) {		
				System.out.println("Successful. enemyTarget is tile x: " + enemyTarget.getTilex() + ", y: " + enemyTarget.getTiley());	
			}
			
			// Find and set a tile destination for selected unit movement
			unitDestinationSet(context); 

			// Execute selected unit movement
			IUnitPlayStates unitMoveState = new UnitMoveActionState(currentTile, destination);	
			System.out.println("Calling MoveAction from CombinedAction...");
			unitMoveState.execute(context);
			
			// Update clicked tile context references (moved here for use of attack, not required anymore for move since inputted tile) 
			// Update clicked Tile context references for attack state
			context.setClickedTile(destination);
			
			System.out.println("Context clicked values are x: " + context.getClickedTile().getTilex() + " and y: " + context.getClickedTile().getTiley());
			System.out.println("Calling AttackAction from CombinedAction...");
			
			// Execute attack between units
			IUnitPlayStates UnitAttackState = new UnitAttackActionState(destination, enemyTarget);
			UnitAttackState.execute(context);
			
			// Finish combined State execution
			context.setCombinedActive(false);
			
			
			/** Reset entity selection and board **/  
			// Deselect after combined action
			context.deselectAllAfterActionPerformed();
			
			// Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());

	}

	private void unitDestinationSet(GameplayContext context) {
				
		// Retrieve frequently used data
		Tile currentLocation = context.getGameStateRef().getBoard().getTile(context.getLoadedUnit().getPosition().getTilex(),context.getLoadedUnit().getPosition().getTiley());
		// Selected unit's ranges
		ArrayList <Tile> moveRange = context.getGameStateRef().getBoard().unitMovableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getMovesLeft());
		ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getAttackRange(), currentLocation.getUnitOnTile().getMovesLeft()));
		actRange.addAll(moveRange);
		
		// Check enemy is in attack range (enemies are retrieved only in attack range of total actRange)
		if(!(actRange.contains(enemyTarget))) {	
			System.out.println("Enemy is not in range.");
			// deselect here?
			return;
		}
		
		/***	Find and set destination tile relative to enemy target	***/
		// Establish tiles adjacent to enemy that are within movement range
		
		// Two tiles are adjacent when: tile1x - tile2x <=1 && tile1y - tile2y <= 1
		// Get a movement range from enemy's position (encompasses attack range)
		ArrayList <Tile> temp = context.getGameStateRef().getBoard().unitMovableTiles(context.getClickedTile().getTilex(), context.getClickedTile().getTiley(), 2);
		ArrayList <Tile> options = new ArrayList <Tile> ();
		for(Tile t : temp) {
			// If tile is adjacent to enemy
			if((Math.abs(enemyTarget.getTilex() - t.getTilex()) <= 1) && (Math.abs(enemyTarget.getTiley() - t.getTiley()) <= 1)) {
				// && If adjacent tile is in selected unit's movement range
				if(moveRange.contains(t)) {
					options.add(t);
					System.out.println("Option added: tile " + t.getTilex() + "," + t.getTiley());
				}
			}
		}
		
		// Select a destination tile from options - prefer cardinal (NESW) direction over diagonal
		// Check for cardinal
		for(Tile t : options) {
			// If total index difference of option and enemy is 1, tile is in cardinal direction relative to enemy
			if((Math.abs(enemyTarget.getTilex() - t.getTilex()) + (Math.abs(enemyTarget.getTiley() - t.getTiley()))) == 1) {
				destination = t;
				break;
			}
		}
		// Otherwise, choose first option available
		if(destination == null) {
			destination = options.get(0);
		}
		
	}
	
}