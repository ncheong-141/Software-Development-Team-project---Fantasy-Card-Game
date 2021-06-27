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

	public void execute(GameplayContext context) {
		
		// Perform unit move function
		unitMove(context); 
		
		// Call UnitMoving
		UnitMoving rules = new UnitMoving(); 
		
	}


	// Helper method
	
	public void unitMove(GameplayContext context) {
		
		// Temp setting of variables
		GameState gameState = context.getGameStateRef();
		ActorRef out = context.out; 
		int tilex = context.tilex; 
		int tiley = context.tiley; 
		
		
		Monster mSelected = gameState.getBoard().getUnitSelected();

		// Get movement range
		ArrayList <Tile> mRange = gameState.getBoard().unitMovableTiles(tilex,tiley,mSelected.getMovesLeft());

		// If destination tile is in movement range, move there
		if(mRange.contains(gameState.getBoard().getTile(tilex,tiley))) {
			
			// Visual feedback to player of the path to be taken --- currently just deselects all
			mSelected.toggleSelect();
			int i = 0;
			for(Tile t : mRange) {
				BasicCommands.drawTile(out, t, 0);
				GeneralCommandSets.threadSleep();
				System.out.println("Tile print " + i);
				i++;
			}
			BasicCommands.drawTile(out, gameState.getBoard().getTile((mSelected.getPosition()).getTilex(), (mSelected.getPosition()).getTiley()), 0);
			GeneralCommandSets.threadSleep();

			Tile current = gameState.getBoard().getTile(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley());
			Tile target = gameState.getBoard().getTile(tilex, tiley);
			
			// Check Monster can move to the target
			System.out.println("MovesLeft: " + mSelected.getMovesLeft());
			System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
			if(mSelected.move(target)) {
				System.out.println("MovesLeft: " + mSelected.getMovesLeft());
				System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
				// Update Board and Tiles
				current.removeUnit();
				target.addUnit(mSelected);
				gameState.getBoard().setUnitSelected(null);
				
				BasicCommands.addPlayer1Notification(out, "Unit moving...", 4);
				GeneralCommandSets.threadSleep();
				
			// Update front end, UnitAnimations could be moved to UnitMoving/Stopped
				// Move animation
				BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.move);
				GeneralCommandSets.threadSleep();
				// Initiate move
				BasicCommands.moveUnitToTile(out, mSelected, gameState.getBoard().getTile(tilex, tiley));
				GeneralCommandSets.threadSleep();
				System.out.println("Movement is complete, I'm back in TileClicked rn.");
				// Re-idle
				BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.idle);
				GeneralCommandSets.threadSleep();
			}
			
		}
		// Destination is not in movement range (deselect?)
		else {	
			System.out.println("Target out of range.");		
		}
	}
}
