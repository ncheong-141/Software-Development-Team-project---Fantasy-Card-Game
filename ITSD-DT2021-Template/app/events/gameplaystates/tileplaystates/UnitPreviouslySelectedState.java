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

/**
 * UnitPreviouslySelectedState:
 * This state is for when a unit is previously selected and the user
 * requires this info be used when doing their enxt action such as click to
 * attack another unit
 */
public class UnitPreviouslySelectedState implements ITilePlayStates {

	/** State attributes**/
	IUnitPlayStates unitState;		// Unit Play state to be executed
	Tile currentTile; 				// Current tile (Unit position)
	Tile targetTile; 				// Target tile (Unit target) 

	/** State constructor **/ 
	public UnitPreviouslySelectedState() {	
		unitState = null;
		currentTile = null; 
		targetTile = null; 
	}
	
	
	/** State method **/
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
		
		// Set current and target tiles for refence
		currentTile = context.getGameStateRef().getBoard().getTile(currentX, currentY);
		targetTile = context.getClickedTile();
		
		
		// Determine the unitState (attack , reselect friendly or move) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("enemy unit"): {		
			
			/*** Check distance from UnitSelected ***/
			if(Math.abs(currentTile.getTilex() - targetTile.getTilex()) <= currentTile.getUnitOnTile().getAttackRange() && (Math.abs(currentTile.getTiley() - targetTile.getTiley()) <= currentTile.getUnitOnTile().getAttackRange())) {
				
				// Attack
				System.out.println("Creating AttackAction substate...");
				unitState = new UnitAttackActionState(currentTile, targetTile);
				break;
			} 
			else {
				
				// Move & Attack in a combined state as unit is out of range
				System.out.println("Creating CombinedAction substate...");
				unitState = new UnitCombinedActionState(currentTile, targetTile, context);
				break;
			}
			
		}	
		case("friendly unit"): {
			
			// Selected unit has been clicked, de-select it
			if (targetTile == currentTile) {
				
				// Unit cast as monster for reference
				Monster selectedUnit = (Monster) context.getLoadedUnit();
				
				// Update unit's occupied tile visual
				BasicCommands.drawTile(context.out, currentTile, 0);
				GeneralCommandSets.threadSleep();
				
				// Update unit's action range tile visual
				GeneralCommandSets.drawUnitDeselect(context.out, context.getGameStateRef(), selectedUnit);

				// Update selected reference (last)
				System.out.println("Deselected monster on Tile " + selectedUnit.getPosition().getTilex() + "," + selectedUnit.getPosition().getTiley());
				
				// Deselct all after an action performed (hard deselectes Units and Cards
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
			
			// Move action to the empty tile
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