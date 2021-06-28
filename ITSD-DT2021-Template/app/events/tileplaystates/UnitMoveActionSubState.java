package events.tileplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.UnitMoving;
import events.UnitStopped;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

public class UnitMoveActionSubState implements GameplayStates {

	public void execute(GameplayContext context) {
		
		System.out.println("In UnitMoveActionSubState.");
		context.debugPrint();
		
		// Perform unit move function
		unitMove(context); 
		
		// Call UnitMoving
//		UnitMoving rules = new UnitMoving(); 
		
		// Call UnitStopped
//		UnitStopped continued = new UnitStopped();
		
	}


	// Helper method
	
	private void unitMove(GameplayContext context) {
		
		// Temp setting of variables
		GameState gameState = context.getGameStateRef();
		ActorRef out = context.out; 
		int tilex = context.getTilex(); 
		int tiley = context.getTiley(); 

		// Get frequently used objects
		Monster mSelected = gameState.getBoard().getUnitSelected();
		ArrayList <Tile> mRange = gameState.getBoard().unitMovableTiles(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley(),mSelected.getMovesLeft());
		ArrayList <Tile> actRange = new ArrayList <Tile> (gameState.getBoard().unitAttackableTiles(mSelected.getPosition().getTilex(), mSelected.getPosition().getTiley(), mSelected.getAttackRange(), mSelected.getMovesLeft()));
		actRange.addAll(mRange);
		Tile current = gameState.getBoard().getTile(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley());
		Tile target = gameState.getBoard().getTile(tilex, tiley);
		System.out.println("Movement target tile is x: " + target.getTilex() + ", y: " + target.getTiley());

		// If target tile is in movement range && monster can move, move there
		System.out.println("MovesLeft: " + mSelected.getMovesLeft());
		System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
		if(mRange.contains(target) && mSelected.move(target)) {
			System.out.println("MovesLeft: " + mSelected.getMovesLeft());
			System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
			
			// Deselect movement range --- implement specific visual path display later
			GeneralCommandSets.drawBoardTiles(out, actRange, 0);
			GeneralCommandSets.threadSleep();
			// Redraw selected tile visual
			BasicCommands.drawTile(out, current, 0);
			GeneralCommandSets.threadSleep();GeneralCommandSets.threadSleep();

			// Update Tiles and Unit
			current.removeUnit();
			target.addUnit(mSelected);
			mSelected.setPositionByTile(target);
			BasicCommands.addPlayer1Notification(out, "Unit moving...", 4);
			GeneralCommandSets.threadSleep();
			
		// Update front end, UnitAnimations could be moved to UnitMoving/Stopped
			// Move animation
			BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.move);
			GeneralCommandSets.threadSleep();
			// Initiate move
			BasicCommands.moveUnitToTile(out, mSelected, gameState.getBoard().getTile(tilex, tiley));
			GeneralCommandSets.threadSleep();		GeneralCommandSets.threadSleep();
			
			/***	Condition here for combined substate executing, which requires selection is maintained	***/
			if(!(context.getCombinedActive())) {
				// Deselect after action finished *if* not in the middle of move-attack action
				mSelected.toggleSelect();
				context.deselectAllAfterActionPerformed();
			}

		}
		
		// Destination is not in movement range/unit cannot move
		else {	
			System.out.println("Can't complete move.");		
		}
	}
}
