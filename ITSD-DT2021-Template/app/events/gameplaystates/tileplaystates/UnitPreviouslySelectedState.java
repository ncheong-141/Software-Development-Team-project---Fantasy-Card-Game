package events.gameplaystates.tileplaystates;

import java.util.ArrayList;

import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.unitplaystates.IUnitPlayStates;
import events.gameplaystates.unitplaystates.UnitAttackActionState;
import events.gameplaystates.unitplaystates.UnitCombinedActionState;
import events.gameplaystates.unitplaystates.UnitDisplayActionsState;
import events.gameplaystates.unitplaystates.UnitMoveActionState;
import structures.basic.*;

public class UnitPreviouslySelectedState implements ITilePlayStates {

	// State attributes
	IUnitPlayStates unitState;		// Unit Play state to be executed
	Tile currentTile; 				// Current tile (Unit position)
	Tile targetTile; 				// Target tile (Unit target) 

	// State constructor 
	public UnitPreviouslySelectedState() {	
		unitState = null;
		currentTile = null; 
		targetTile = null; 
	}
	
	public void execute(GameplayContext context) {
			
		/* Selected units can:
		 * - move
		 * - attack
		 * - move & attack
		 * - be deselected
		 * - switch selection to another friendly unit
		 */
		
		System.out.println("In UnitPreviouslySelectedState.");
		context.debugPrint();
		
		// Load previously selected unit for use in next sub state (move or attack) 
		context.setLoadedUnit(context.getGameStateRef().getBoard().getUnitSelected());
		
		// Set tiles unit will interact with
		int currentX = context.getLoadedUnit().getPosition().getTilex(); 
		int currentY = context.getLoadedUnit().getPosition().getTiley(); 
		
		currentTile = context.getGameStateRef().getBoard().getTile(currentX, currentY);
		targetTile = context.getClickedTile();
		
		
		// Determine the unitState (attack , reselect friendly or move) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("enemy unit"): {		
			/*** Check distance from UnitSelected ***/
			
			// Near --- enemy unit tile has total tile index difference of <=1 on either Board dimension, which does not require the unit to move before attack
			// Change to Board adjacentEnemies.contains(targetTile) method
			if(Math.abs(currentTile.getTilex() - targetTile.getTilex()) <=1 && (Math.abs(currentTile.getTiley() - targetTile.getTiley()) <= 1)) {
				
				// Attack
				System.out.println("Creating AttackAction substate...");
				unitState = new UnitAttackActionState(currentTile, targetTile);
				break;
			} 
			
			// Far --- enemy unit is outside a non-movement range
			else {
				
				// Move & Attack
				System.out.println("Creating CombinedAction substate...");
				unitState = new UnitCombinedActionState(currentTile, targetTile);
				break;
			}
			
		}
		
		case("friendly unit"): {
			
			// Selected unit has been clicked, de-select it
			if (targetTile == currentTile) {
				
				Monster selectedUnit = (Monster) context.getLoadedUnit();
				
				// Update unit's occupied tile visual
				BasicCommands.drawTile(context.out, currentTile, 0);
				GeneralCommandSets.threadSleep();
				
				// Update unit's action range tile visual
				ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(currentTile.getTilex(), currentTile.getTiley(), selectedUnit.getAttackRange(), selectedUnit.getMovesLeft()));
				ArrayList <Tile> mRange = context.getGameStateRef().getBoard().unitMovableTiles(currentTile.getTilex(),currentTile.getTiley(),selectedUnit.getMovesLeft());
				actRange.addAll(mRange);
				GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
				GeneralCommandSets.threadSleepLong();
				System.out.println("Finished de-highlighting tiles.");
				
				// Update selected reference (last)
				// selectedUnit.toggleSelect();
				System.out.println("Monster selected: " + context.getGameStateRef().getBoard().getUnitSelected().isSelected());
				System.out.println("Deselected monster on Tile " + selectedUnit.getPosition().getTilex() + "," + selectedUnit.getPosition().getTiley());
				context.deselectAllAfterActionPerformed();
				break;
				
			} 
			
			// Clicked other friendly unit, switch selection
			else {
				
				// Variable change for old unit
				Monster old = (Monster) context.getLoadedUnit();
				context.deselectAllAfterActionPerformed();
				
				// Visual change for old unit
				GeneralCommandSets.drawUnitDeselect(context.out, context.getGameStateRef(), old);
				
				// Variable + visual change for new unit
				// Pass target tile (occupied by new unit) to DisplayActions state to complete
				unitState = new UnitDisplayActionsState(targetTile);	
				break;
				
			}
			
		}
		
		case("empty"): {
			
			// Move
			unitState = new UnitMoveActionState(currentTile, targetTile);
			break;

		}
		
		}
		
		/*** Execute Unit state***/
		if (unitState != null ) {
			unitState.execute(context);
		}
		
	}
	
}