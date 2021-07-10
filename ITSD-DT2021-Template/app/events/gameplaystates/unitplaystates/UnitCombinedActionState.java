package events.gameplaystates.unitplaystates;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.GameState;
import structures.basic.*;
import structures.basic.abilities.*;

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
		
		// Check for selected unit abilities that do not require adjacency for attack
		if(currentTile.getUnitOnTile().getMonsterAbility() != null) {
			for(Ability a : currentTile.getUnitOnTile().getMonsterAbility()) {
				if(a instanceof A_U_RangedAttacker) {
					
					// Attack from current tile
					destination = currentTile; 
					
					System.out.println("Selected unit is a ranged attacker, can attack without moving.");
					IUnitPlayStates UnitAttackState = new UnitAttackActionState(destination, enemyTarget);
					UnitAttackState.execute(context);
					break;
				}
			}
		}
			
		// CombinedAction state controls state flow
		context.setCombinedActive(true);
		
		// Build reference variables
		destination = null;
		if(destination == null && (enemyTarget != null)) {		
			System.out.println("Successful. enemyTarget is tile x: " + enemyTarget.getTilex() + ", y: " + enemyTarget.getTiley());	
		}
		
		// Find and set a tile destination for selected unit movement
		unitDestinationSet(context); 
		
	
		//  Executing unit states on a different thread as the time between them is reliant on Unit Stopped (which runs on the same thread as the back end) 
		Thread thread = new Thread(new ExecuteUnitStatesOnDifferentThread(context, currentTile, destination, enemyTarget));
		thread.start();
		Thread.yield();
	}


	public class ExecuteUnitStatesOnDifferentThread implements Runnable{
		
		// Class attributes
		private GameplayContext context; 
		private Tile currentTile;
		private Tile destination;
		private Tile enemyTarget;
		
		
		public ExecuteUnitStatesOnDifferentThread(GameplayContext context, Tile currentTile, Tile destination, Tile enemyTile) {
			this.context = context; 
			this.currentTile = currentTile;
			this.destination = destination; 
			this.enemyTarget = enemyTile;
		}
		
		public void run() {
			
			// Execute unit states 
			IUnitPlayStates unitMoveState = new UnitMoveActionState(currentTile, destination);	
			System.out.println("Calling MoveAction from CombinedAction...");
			unitMoveState.execute(context);
			
			// Wait for the Front end to give back a message (unit stopped)
			while (!context.getGameStateRef().canInteract) {
				GeneralCommandSets.threadSleep();		
			} 

			// Execute attack state
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
	}
	
	
	private boolean unitDestinationSet(GameplayContext context) {
				
		// Retrieve frequently used data
		Tile currentLocation = currentTile;
		// context.getGameStateRef().getBoard().getTile(context.getLoadedUnit().getPosition().getTilex(),context.getLoadedUnit().getPosition().getTiley());
		
		// Selected unit's ranges
		ArrayList <Tile> actRange; 
		ArrayList <Tile> moveRange = new ArrayList<Tile>();
		
		// Account for movement impairing debuffs
		if (context.getGameStateRef().useAdjustedMonsterActRange()) {
			
			actRange = context.getGameStateRef().getTileAdjustedRangeContainer();
			
			for (Tile t : context.getGameStateRef().getTileAdjustedRangeContainer()) {
				if (t.getUnitOnTile() == null) {
					moveRange.add(t);
				}
			}
		}
		else {
			moveRange = context.getGameStateRef().getBoard().unitMovableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getMovesLeft());
			actRange = context.getGameStateRef().getBoard().unitAttackableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getAttackRange(), currentLocation.getUnitOnTile().getMovesLeft());
			//actRange.addAll(moveRange);
		}

		// Check enemy is in attack range (enemies are retrieved only in attack range of total actRange)
		if(!(actRange.contains(enemyTarget))) {	
			System.out.println("Enemy is not in range.");
			return false;
		}
		
		/***	Find and set destination tile relative to enemy target	***/
		
		// Establish tiles adjacent to enemy that are within movement range
		ArrayList <Tile> options = context.getGameStateRef().getBoard().adjTiles(enemyTarget);
		
		// Select a destination tile from options - prefer cardinal (NESW) direction over diagonal
		// Check for cardinal and remove redundant tiles
		for(Tile t : options) {
			
			// Check if there is a unit on any of the tiles 
			if (t.getUnitOnTile() != null) {
				options.remove(t); 
			}
			else {
				// If total index difference of option and enemy is 1, tile is in cardinal direction relative to enemy
				if((Math.abs(enemyTarget.getTilex() - t.getTilex()) + (Math.abs(enemyTarget.getTiley() - t.getTiley()))) == 1) {
					destination = t;
					break;
				}
			}
		}
		
		
		// Otherwise, choose first option available and check if there is an option
		if(destination == null && options.size() > 0) {
			destination = options.get(0);
			return true;
		}
		else {
			System.out.println("No possible destination during unit combined state.");
			return false;
		}
	}
}




