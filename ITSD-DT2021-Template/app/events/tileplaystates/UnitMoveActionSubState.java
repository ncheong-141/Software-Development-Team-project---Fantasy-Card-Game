package events.tileplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.UnitMoving;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

public class UnitMoveActionSubState implements GameplayStates {

	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitMoveActionSubState.");
		context.debugPrint();
		
		// Perform unit move function
		unitMove(context); 
		
		// Call UnitMoving
		UnitMoving rules = new UnitMoving(); 
		
	}


	// Helper method
	
	private void unitMove(GameplayContext context) {
		
		// Temp setting of variables
		GameState gameState = context.getGameStateRef();
		ActorRef out = context.out; 
		int tilex = context.getTilex(); 
		int tiley = context.getTiley(); 

		// Get Monster and movement range
		Monster mSelected = gameState.getBoard().getUnitSelected();
		ArrayList <Tile> mRange = gameState.getBoard().unitMovableTiles(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley(),mSelected.getMovesLeft());
		
		Tile current = gameState.getBoard().getTile(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley());
		Tile target = gameState.getBoard().getTile(tilex, tiley);

		// If target tile is in movement range && monster can move, move there
		System.out.println("MovesLeft: " + mSelected.getMovesLeft());
		System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
		if(mRange.contains(target) && mSelected.move(target)) {
			System.out.println("MovesLeft: " + mSelected.getMovesLeft());
			System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
			
			// Deselect movement range --- implement specific visual path display later
			ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(current.getTilex(), current.getTiley(), mSelected.getAttackRange(), mSelected.getMovesLeft()));
			actRange.addAll(mRange);
			GeneralCommandSets.drawBoardTiles(out, actRange, 0);
			GeneralCommandSets.threadSleep();
			// Redraw selected tile visual
			BasicCommands.drawTile(out, current, 0);
			GeneralCommandSets.threadSleep();GeneralCommandSets.threadSleep();

			// Update Board and Tiles
			current.removeUnit();
			target.addUnit(mSelected);			
			BasicCommands.addPlayer1Notification(out, "Unit moving...", 4);
			GeneralCommandSets.threadSleep();
			
		// Update front end, UnitAnimations could be moved to UnitMoving/Stopped
			// Move animation
			BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.move);
			GeneralCommandSets.threadSleep();
			// Initiate move
			BasicCommands.moveUnitToTile(out, mSelected, gameState.getBoard().getTile(tilex, tiley));
			GeneralCommandSets.threadSleep();
			// Re-idle
			BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.idle);
			GeneralCommandSets.threadSleep();
			
			// Deselect after action finished
			mSelected.toggleSelect();
			context.deselectAllAfterActionPerformed();
		}
		
		// Destination is not in movement range/unit cannot move
		else {	
			System.out.println("Can't complete move.");		
		}
	}
}
