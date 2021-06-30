package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.*;

public class UnitCombinedActionSubState implements ITilePlayStates {
	
	Tile destination;
	Tile enemyTarget;
	
	public void execute(GameplayContext context) {
			
			System.out.println("In UnitCombinedActionSubState.");
			context.debugPrint();
			
			context.setCombinedActive(true);
			
			// Build reference variables
			destination = null;
			enemyTarget = context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley());
			if(destination == null && (enemyTarget != null)) {		System.out.println("Successful. enemyTarget is tile x: " + enemyTarget.getTilex() + ", y: " + enemyTarget.getTiley());	}
			
			// Find and set a tile destination for selected unit movement
			unitDestinationSet(context); 
	
			// Update clicked tile context references for use by move substate
			context.setTilex(destination.getTilex());
			context.setTiley(destination.getTiley());
			
			// Execute selected unit movement
			ITilePlayStates UnitMoveActionSubState = new UnitMoveActionSubState();
			System.out.println("Calling MoveAction from CombinedAction...");
			UnitMoveActionSubState.execute(context);
			
			// Update clicked Tile context references for attack state
			context.setTilex(enemyTarget.getTilex());
			context.setTiley(enemyTarget.getTiley());
			System.out.println("Context clicked values are x: " + context.getTilex() + " and y: " + context.getTiley());
			System.out.println("Calling AttackAction from CombinedAction...");
			
			// Execute attack between units
			ITilePlayStates UnitAttackActionSubState = new UnitAttackActionSubState();
			UnitAttackActionSubState.execute(context);
			
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
		ArrayList <Tile> temp = context.getGameStateRef().getBoard().unitMovableTiles(context.getTilex(), context.getTiley(), 2);
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