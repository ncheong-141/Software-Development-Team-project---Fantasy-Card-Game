package events.gameplaystates.unitplaystates;

import structures.GameState;
import structures.basic.*;
import structures.basic.abilities.*;

import java.util.ArrayList;
import akka.actor.ActorRef;
import commands.*;
import events.gameplaystates.GameplayContext;

public class UnitDisplayActionsState implements IUnitPlayStates{

	/*
	 * 		State class for handling initial display of the potential-actions range
	 * 		of a unit when selected on the board. Manages front-end tile highlight display.
	 */
	

	/*** State attributes ***/
	
	private Tile currentTile; 
	

	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables received from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public UnitDisplayActionsState(Tile currentTile) {
		this.currentTile = currentTile;
	}
	
	
	
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitDisplayActionsState.");
		
		// Lock the user out of interfering interaction whilst state activity occurs
		/**===========================================**/
		context.getGameStateRef().userinteractionLock();
		/**===========================================**/

		// Get the newly selected unit
		Monster newlySelectedUnit = currentTile.getUnitOnTile();
		
		
		// Tracks outcome of either display method
		boolean unitPlayable = false; 
		
		// If selected unit has an ability affecting its action range, adjust display accordingly
		if (context.getGameStateRef().checkMonsterAbilityActivation(Call_IDs.onUnitSelection, newlySelectedUnit) 
				&& context.getGameStateRef().useAdjustedMonsterActRange()) {
			 
			System.out.println("Using Ability version of highlighting tiles.");
			unitPlayable = abilityAdjustedDisplay(context,newlySelectedUnit);
			
		}
		else {
			
			// Display selected unit actions as normal
			unitPlayable = unitSelectedActions(newlySelectedUnit, context.getGameStateRef(), currentTile.getTilex(), currentTile.getTiley(), context.out, newlySelectedUnit.getClass());
			
		}

		// If display happened successfully
		if(unitPlayable) {
			context.getGameStateRef().getBoard().setUnitSelected((Monster) newlySelectedUnit);
		} 
		
		
		/**===========================================**/
		context.getGameStateRef().userinteractionUnlock();
		/**===========================================**/
	}
	
	
	// Handles the display of movement and attack tiles around a unit and updates selected status
	private boolean unitSelectedActions(Monster unit, GameState gameState, int tilex, int tiley, ActorRef o, Class<? extends Unit> classtype) {
		
			// Cast unit to Monster (in the case of Avatars)
			Monster m = (Monster) unit; 
			
			// Monster is able to complete actions, per its own internal variables
			if (!(m.getOnCooldown())) {
				
				// Update GameState Board reference of selected unit
				gameState.getBoard().setUnitSelected(m);

				// Select the tile under Monster for user feedback regardless of range output
				System.out.println("Selected monster on Tile " + m.getPosition().getTile(gameState.getBoard()));
				BasicCommands.drawTile(o, gameState.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 1);
				GeneralCommandSets.threadSleep();

				// Get combined action range from various Board methods
				ArrayList <Tile> mRange = gameState.getBoard().unitMovableTiles(tilex,tiley,m.getMovesLeft());
				ArrayList <Tile> attRange = gameState.getBoard().unitAttackableTiles(tilex, tiley, m.getAttackRange(), m.getMovesLeft());
				ArrayList <Tile> actRange = mRange;			actRange.addAll(attRange);
				
				// Draw tile display
				for(Tile t : actRange) {
					// If attackRange contains t = draw as attack tile
					if(attRange.contains(t)) {
						BasicCommands.drawTile(o, t, 2);
						GeneralCommandSets.threadSleep();
					}
					// Else, draw as move range tile
					else {
						BasicCommands.drawTile(o, t, 1);
						GeneralCommandSets.threadSleep();
					}
				}
				return true;
					
			} 
			
			// Monster is unavailable for action
			else {
				System.out.println("Can't select this monster.");
				return false;
			}
			
	}

	// Handles the display of action tiles for a unit impaired by ability activations
	private boolean abilityAdjustedDisplay(GameplayContext context, Monster newlySelectedUnit) {
		// Draw out only playable tiles due to external factors such as abilities
		ArrayList<Tile> displayMoveableTiles = new ArrayList<Tile>(10); 
		ArrayList<Tile> displayAttackableTiles = new ArrayList<Tile>(10);
		
		for (Tile t : context.getGameStateRef().getTileAdjustedRangeContainer()) {
			if (t.getUnitOnTile() != null) {
				displayAttackableTiles.add(t);
			}
			else { 
				displayMoveableTiles.add(t);
			}
		}
		// Draw tiles per range type
		GeneralCommandSets.drawBoardTiles(context.out, displayMoveableTiles, 1);
		GeneralCommandSets.drawBoardTiles(context.out, displayAttackableTiles, 2);

		// Apply flags due to external factors
		if (newlySelectedUnit.hasAbility()) {
			for (Ability a : newlySelectedUnit.getMonsterAbility()) {
				
				// Switch monster to provoked (use tileAdjustedRangeContainer in move or attack state)
				if (a instanceof A_U_Provoke) {
					newlySelectedUnit.toggleProvoked();
				}
			}
		}
		
		return true; 
	}
	
}

