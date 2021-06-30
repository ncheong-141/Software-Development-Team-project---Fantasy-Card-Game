package events.gameplaystates.tileplaystates;

import java.util.ArrayList;

import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.unitplaystates.UnitAttackActionState;
import events.gameplaystates.unitplaystates.UnitCombinedActionState;
import events.gameplaystates.unitplaystates.UnitDisplayActionsState;
import events.gameplaystates.unitplaystates.UnitMoveActionState;
import structures.basic.*;

public class UnitPreviouslySelectedState implements ITilePlayStates {

	// State attributes
	ITilePlayStates subState; 

	// State constructor 
	public UnitPreviouslySelectedState() {	
		subState = null; 
	}
	
	public void execute(GameplayContext context) {
			
		/*
		 * Selected units can:
		 * - move
		 * - attack
		 * - move & attack
		 * - be deselected
		 * - have selection switched to different unit
		 */
		
		System.out.println("In UnitPreviouslySelectedState.");
		context.debugPrint();
		
		// 
		// Load previously selected unit for use in next sub state (move or attack) 
		context.setLoadedUnit(context.getGameStateRef().getBoard().getUnitSelected());
		// Retrieve clicked tile for reference in condition checks
		Tile clickedTile = context.getGameStateRef().getBoard().getTile(context.getTilex(),context.getTiley());
		
		// Determine the substate (attack , reselect friendly or move) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("enemy unit"): {
			
			/*** Check distance from UnitSelected ***/
			System.out.println("Before near check");
			// Near --- enemy unit tile has index difference with current tile of <=1 on board dimensions, which does not require the selected unit to move before attack
			if(Math.abs(context.getLoadedUnit().getPosition().getTilex() - clickedTile.getTilex()) <=1 && (Math.abs(context.getLoadedUnit().getPosition().getTiley() - clickedTile.getTiley()) <= 1)) {
				// Attack
				System.out.println("Creating AttackAction substate...");
				subState = new UnitAttackActionState();
				break;
			} 
			
			// Far (wherever you are) --- enemy unit is outside a non-movement range
			else {
				// Move & Attack
				System.out.println("Creating CombinedAction substate...");
				subState = new UnitCombinedActionState();
				break;
			}
			
		}
		
		case("friendly unit"): {
			
			// Player re-clicks the selected unit, de-select it
			if (clickedTile.getUnitOnTile() == context.getLoadedUnit()) {
				
				Monster selectedUnit = (Monster) context.getLoadedUnit();
				
				BasicCommands.drawTile(context.out, context.getGameStateRef().getBoard().getTile((selectedUnit.getPosition()).getTilex(), (selectedUnit.getPosition()).getTiley()), 0);
				GeneralCommandSets.threadSleep();
				
				// Update action range tiles displayed
				ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(selectedUnit.getPosition().getTilex(), selectedUnit.getPosition().getTiley(), selectedUnit.getAttackRange(), selectedUnit.getMovesLeft()));
				ArrayList <Tile> mRange = context.getGameStateRef().getBoard().unitMovableTiles(context.getLoadedUnit().getPosition().getTilex(),context.getLoadedUnit().getPosition().getTiley(),context.getGameStateRef().getBoard().getUnitSelected().getMovesLeft());
				actRange.addAll(mRange);
				GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
				GeneralCommandSets.threadSleepLong();
				System.out.println("Finished un-highlighting tiles.");
				
				// Update selected reference last for now
				context.getGameStateRef().getBoard().getUnitSelected().toggleSelect();
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
				// Pass to DisplayActions state to complete
				ITilePlayStates UnitDisplayActionsState = new UnitDisplayActionsState();
				UnitDisplayActionsState.execute(context);
				break;
				
			}
			
		}
		
		case("empty"): {
			
			// Move
			subState = new UnitMoveActionState();
			break;

		}
		
		}
		
		// Execute sub-state
		if (subState != null ) {
			subState.execute(context);
		}
		
	}
	
}