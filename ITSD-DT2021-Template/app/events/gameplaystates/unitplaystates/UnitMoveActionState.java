package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import commands.*;
import structures.basic.*;
import events.gameplaystates.GameplayContext;


public class UnitMoveActionState implements IUnitPlayStates {

	/*
	 * 		State class for handling unit movement from one tile to the other.
	 * 		Manages checks for movement, ability activations, and updating internal
	 * 		game locational info.
	 */
	
	
	/*** State attributes ***/
	
	private Tile currentTile; 
	private Tile targetTile; 
	
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public UnitMoveActionState(Tile currentTile, Tile targetTile) {
		this.currentTile = currentTile;
		this.targetTile = targetTile; 
	}
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitMoveActionSubState.");
		
		
		// Lock the user out of interfering interaction whilst state activity occurs
		/**===========================================**/
		context.getGameStateRef().userinteractionLock();
		/**===========================================**/
		
		// Load unit in context
		context.setLoadedUnit(currentTile.getUnitOnTile());
		if(context.getLoadedUnit() == null) {	System.out.println("Error, current tile has no unit.");	}
		
		// Perform unit move method
		System.out.println("Target tile is:" + targetTile);
		unitMove(context); 
		
		/***	Condition here for combined substate executing, which requires selection is maintained	***/
		if(!(context.getCombinedActive())) {
		
			/** Reset entity selection and board **/  
			context.deselectAllAfterActionPerformed();
			//  Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			
			// Unlock after state activity complete
			/**===========================================**/
			context.getGameStateRef().userinteractionUnlock();
			/**===========================================**/
		}
	}


	
	// Handles the process flow of movement checks between target tiles 
	private void unitMove(GameplayContext context) {
		
		// Get frequently used objects
		Monster mSelected = (Monster) context.getLoadedUnit();
		ArrayList <Tile> actRange; 
		ArrayList <Tile> moveRange = new ArrayList<Tile>();
		
		/***		Check for impaired movement range due to abilities (i.e. Provoke)		***/
		if (context.getGameStateRef().useAdjustedMonsterActRange()) {
			
			// Act range calculate by abilities etc (external factors)
			actRange = context.getGameStateRef().getTileAdjustedRangeContainer();
			
			for (Tile t : context.getGameStateRef().getTileAdjustedRangeContainer()) {
				
				System.out.println(t.getTilex() + "," + t.getTiley());
				if (t.getUnitOnTile() == null) {
					if (t != currentTile) {
						moveRange.add(t);
					}
				}
			}
		}
		else {
			moveRange = context.getGameStateRef().getBoard().unitMovableTiles(currentTile.getTilex(), currentTile.getTiley(), currentTile.getUnitOnTile().getMovesLeft());
			actRange = context.getGameStateRef().getBoard().unitAttackableTiles(currentTile.getTilex(), currentTile.getTiley(), currentTile.getUnitOnTile().getAttackRange(), currentTile.getUnitOnTile().getMovesLeft());
			actRange.addAll(moveRange);
		}

		
		/***		Carry out movement and final checks		***/	
		// If target tile is in movement range
		if((!moveRange.isEmpty()) && moveRange.contains(targetTile)) {
			
			// Verbose output
			BasicCommands.addPlayer1Notification(context.out, "Monster moving!", 2);
			
			// If Monster move method is successful, per internal object checks
			if (mSelected.move(targetTile)) {
				System.out.println("MovesLeft: " + mSelected.getMovesLeft());
				System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
				
				// Deselect movement range
				GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
				GeneralCommandSets.threadSleep();
				// Redraw selected tile visual
				BasicCommands.drawTile(context.out, currentTile, 0);
				GeneralCommandSets.threadSleep();GeneralCommandSets.threadSleep();

				// Update Tiles and Unit
				currentTile.removeUnit();
				targetTile.addUnit(mSelected);
				mSelected.setPositionByTile(targetTile);
				
				// Set moving flag for tracking when movement is complete
				context.getGameStateRef().setUnitMovingFlag(true);
				
				// Update front end
				// Initiate move
				BasicCommands.moveUnitToTile(context.out, mSelected, targetTile);
				GeneralCommandSets.threadSleep();	
				// Move animation
				BasicCommands.playUnitAnimation(context.out, mSelected, UnitAnimationType.move);
				GeneralCommandSets.threadSleep();
			}
		}
		// Destination is not in movement range/unit cannot move
		else {	
			System.out.println("Can't complete move.");		
		}
	}
	
}



