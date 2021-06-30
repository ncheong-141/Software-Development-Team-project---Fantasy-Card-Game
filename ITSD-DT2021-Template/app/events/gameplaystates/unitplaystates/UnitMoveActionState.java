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

public class UnitMoveActionState implements ITilePlayStates {

	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitMoveActionSubState.");
		context.debugPrint();
		
		// Perform unit move function
		unitMove(context); 
		
		// Call UnitMoving
//		UnitMoving rules = new UnitMoving(); 
		
		// Call UnitStopped
//		UnitStopped continued = new UnitStopped();
		
		
		/***	Condition here for combined substate executing, which requires selection is maintained	***/
		if(!(context.getCombinedActive())) {
			
		
			/** Reset entity selection and board **/  
			// Deselect after action finished *if* not in the middle of move-attack action
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
		Tile current = context.getGameStateRef().getBoard().getTile(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley());
		Tile target = context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley());
		System.out.println("Movement target tile is x: " + target.getTilex() + ", y: " + target.getTiley());

		// If target tile is in movement range && monster can move, move there
		System.out.println("MovesLeft: " + mSelected.getMovesLeft());
		System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
		
		if(mRange.contains(target) && mSelected.move(target)) {
			
			System.out.println("MovesLeft: " + mSelected.getMovesLeft());
			System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
			
			// Deselect movement range --- implement specific visual path display later
			GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
			GeneralCommandSets.threadSleep();
			// Redraw selected tile visual
			BasicCommands.drawTile(context.out, current, 0);
			GeneralCommandSets.threadSleep();GeneralCommandSets.threadSleep();

			// Update Tiles and Unit
			current.removeUnit();
			target.addUnit(mSelected);
			mSelected.setPositionByTile(target);
			BasicCommands.addPlayer1Notification(context.out, "Unit moving...", 4);
			GeneralCommandSets.threadSleep();
			
			// Update front end, UnitAnimations could be moved to UnitMoving/Stopped
			// Move animation
			BasicCommands.playUnitAnimation(context.out, mSelected, UnitAnimationType.move);
			GeneralCommandSets.threadSleep();
			// Initiate move
			BasicCommands.moveUnitToTile(context.out, mSelected, context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley()));
			GeneralCommandSets.threadSleep();	
		}
		// Destination is not in movement range/unit cannot move
		else {	
			System.out.println("Can't complete move.");		
		}
	}
}
