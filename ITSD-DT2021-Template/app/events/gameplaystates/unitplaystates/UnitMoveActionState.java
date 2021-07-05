package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.UnitMoving;
import events.UnitStopped;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

public class UnitMoveActionState implements IUnitPlayStates {

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
		context.debugPrint();
		
		context.setLoadedUnit(currentTile.getUnitOnTile());
		if(context.getLoadedUnit() == null) {	System.out.println("Error, current tile has no unit.");	}
		
		// Perform unit move function
		unitMove(context); 
		
		// Loop until moveStopped trigger
		
		/***	Condition here for combined substate executing, which requires selection is maintained	***/
		if(!(context.getCombinedActive())) {
		
			/** Reset entity selection and board **/  
			context.deselectAllAfterActionPerformed();
			//  Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
		}
	}


	// Helper method
	
	private void unitMove(GameplayContext context) {
		
		// Get frequently used objects
		Monster mSelected = context.getGameStateRef().getBoard().getUnitSelected();
		ArrayList <Tile> mRange = context.getGameStateRef().getBoard().unitMovableTiles(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley(),mSelected.getMovesLeft());
		ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(mSelected.getPosition().getTilex(), mSelected.getPosition().getTiley(), mSelected.getAttackRange(), mSelected.getMovesLeft()));
		actRange.addAll(mRange);
		
		System.out.println("Movement target tile is x: " + targetTile.getTilex() + ", y: " + targetTile.getTiley());

		// If target tile is in movement range && monster can move, move there
		System.out.println("MovesLeft: " + mSelected.getMovesLeft());
		System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
		
		if(mRange.contains(targetTile) && mSelected.move(targetTile)) {
			
			System.out.println("MovesLeft: " + mSelected.getMovesLeft());
			System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
			
			// Deselect movement range --- implement specific visual path display later
			GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
			GeneralCommandSets.threadSleep();
			// Redraw selected tile visual
			BasicCommands.drawTile(context.out, currentTile, 0);
			GeneralCommandSets.threadSleep();GeneralCommandSets.threadSleep();

			// Update Tiles and Unit
			currentTile.removeUnit();
			targetTile.addUnit(mSelected);
			mSelected.setPositionByTile(targetTile);
			
			// Update front end, UnitAnimations could be moved to UnitMoving/Stopped
			// Move animation
			BasicCommands.playUnitAnimation(context.out, mSelected, UnitAnimationType.move);
			GeneralCommandSets.threadSleep();
			// Initiate move
			BasicCommands.moveUnitToTile(context.out, mSelected, targetTile);
			GeneralCommandSets.threadSleep();	
		}
		// Destination is not in movement range/unit cannot move
		else {	
			System.out.println("Can't complete move.");		
		}
	}
	
}
