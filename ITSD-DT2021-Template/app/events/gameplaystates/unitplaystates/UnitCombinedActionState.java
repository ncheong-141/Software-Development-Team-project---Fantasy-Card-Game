package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import structures.basic.*;
import structures.basic.abilities.*;

public class UnitCombinedActionState implements IUnitPlayStates {
	
	/*
	 * 		State class for handling combined move and attack actions. Manages the flow of
	 * 		accessing states sequentially, changes in tile destination, and required unit and action checks.
	 */
	
	
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
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
	
		System.out.println("In UnitCombinedActionSubState.");
				
		// Lock the user out of interfering interaction whilst state activity occurs
		/**===========================================**/
		context.getGameStateRef().userinteractionLock();
		/**===========================================**/
		
		// Check for unit abilities that do not require adjacency to attack
		if(currentTile.getUnitOnTile().getMonsterAbility() != null) {
			for(Ability a : currentTile.getUnitOnTile().getMonsterAbility()) {
				
				// If attacking monster is a ranged attacker
				if(a instanceof A_U_RangedAttacker) {
					// Attack from current tile
					destination = currentTile; 
					
					// Execute Attack state
					IUnitPlayStates UnitAttackState = new UnitAttackActionState(destination, enemyTarget);
					System.out.println("Ranged Attacker, calling AttackAction from header of CombinedAction...");
					System.out.println("Destination " + destination);
					UnitAttackState.execute(context);
					break;
				}
			}
		}
			
		// CombinedAction state controls state flow when active
		context.setCombinedActive(true);
		
		// Build state reference variables
		if(destination == null && (enemyTarget != null)) {		
			System.out.println("enemyTarget is tile x: " + enemyTarget.getTilex() + ", y: " + enemyTarget.getTiley());	
		}
		
		// Select a destination for moving consistently with method
		unitDestinationSet(context); 
	
		// Executing unit states on a different thread as the time between them is reliant on UnitStopped (which runs on the same thread as the back end) 
		// Can't ask main thread to wait since it will block front end signals and need UnitStopped message
		Thread thread = new Thread(new ExecuteUnitStatesOnDifferentThread(context));
		thread.start();
		
	}


	public class ExecuteUnitStatesOnDifferentThread implements Runnable{
		
		// Class attributes
		GameplayContext context;

		public ExecuteUnitStatesOnDifferentThread(GameplayContext context) {
			this.context = context; 
		}
		
		public void run() {
			
			// Execute move state
			IUnitPlayStates unitMoveState = new UnitMoveActionState(currentTile, destination);	
			System.out.println("Calling MoveAction from CombinedAction...");
			System.out.println("Destination " + destination);
			unitMoveState.execute(context);
			
			// Wait for the Front end to finish (UnitStopped) before continuing
			while (context.getGameStateRef().getUnitMovingFlag()) {
				GeneralCommandSets.threadSleep();		
			} 

			// Execute attack state
			System.out.println("Calling AttackAction from CombinedAction...");
			System.out.println("Destination " + destination);
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
			
			// Unlock after state activity complete
			/**===========================================**/
			context.getGameStateRef().userinteractionUnlock();
			/**===========================================**/
		}
	}
	
	/* Method for selecting a destination tile for attacking unit to move to.
	 * If no destination found, return false (boolean return for developer clarity).	*/
	private boolean unitDestinationSet(GameplayContext context) {
		
		/***	Establish unit's available actions		***/
		
		// Retrieve frequently used data
		Tile currentLocation = currentTile;
		
		// Selected unit's movement and collective action ranges
		ArrayList <Tile> actRange; 
		ArrayList <Tile> moveRange = new ArrayList<Tile>();
		
		// Account for movement impairing debuffs (i.e. Provoke)
		if (context.getGameStateRef().useAdjustedMonsterActRange()) {
			
			actRange = context.getGameStateRef().getTileAdjustedRangeContainer();
			
			for (Tile t : context.getGameStateRef().getTileAdjustedRangeContainer()) {
				if (t.getUnitOnTile() == null) {	moveRange.add(t);	}
			}
		}
		else {
			moveRange = context.getGameStateRef().getBoard().unitMovableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getMovesLeft());
			actRange = context.getGameStateRef().getBoard().unitAttackableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getAttackRange(), currentLocation.getUnitOnTile().getMovesLeft());
			actRange.addAll(moveRange);
		}

		// Check enemy is in attack range (all action tiles are attackable)
		if(!(actRange.contains(enemyTarget))) {	
			System.out.println("Enemy is not in range.");
			return false;
		}
		
		/***	Find and set destination tile relative to enemy target	***/
		
		// Get potential destination tiles adjacent to enemy && within movement range
		ArrayList <Tile> temp = context.getGameStateRef().getBoard().adjTiles(enemyTarget);
		ArrayList <Tile> options = new ArrayList<Tile>(10); 
		for(Tile t : temp) {
			if(moveRange.contains(t)) {
				options.add(t);
			}
		}
		
		// Select destination from options | first preference = cardinal direction from enemy (NESW)
		// Check for cardinal and remove redundant tiles
		for(Tile t : options) {
			
			// Remove if tile not available 
			if (t.getUnitOnTile() != null) {
				options.remove(t); 
			}
			else {
				// If available tile is cardinal, designate it
				if((Math.abs(enemyTarget.getTilex() - t.getTilex()) + (Math.abs(enemyTarget.getTiley() - t.getTiley()))) == 1) {
					destination = t;
					break;
				}
			}
		}
		
		// If no cardinals available, choose any first option available
		if(options.size() > 0) {
			destination = options.get(0);
			return true;
		}
		else {
			System.out.println("No possible destination during unit combined state.");
			return false;
		}
	}
}




