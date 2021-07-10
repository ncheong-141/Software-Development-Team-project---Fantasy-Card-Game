package events.gameplaystates.unitplaystates;

import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.abilities.A_U_Provoke;
import structures.basic.abilities.Ability;
import structures.basic.abilities.Call_IDs;

import java.util.ArrayList;
import akka.actor.ActorRef;
import commands.*;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates; 



public class UnitDisplayActionsState implements IUnitPlayStates{


	/*** State attributes ***/
	
	private Tile currentTile; 
	

	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public UnitDisplayActionsState(Tile currentTile) {
		this.currentTile = currentTile;
	}
	
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitDisplayActionsState.");

		// Get the newly selected unit
		Monster newlySelectedUnit = currentTile.getUnitOnTile();
		
		boolean unitPlayable = false; 
		
		// Check for skills which can affect where unit can move and if it was successful (i.e. tile container not empty)
		if (context.getGameStateRef().checkMonsterAbilityActivation(Call_IDs.onUnitSelection, newlySelectedUnit) 
				&& context.getGameStateRef().useAdjustedMonsterActRange()) {
			 
			System.out.println("Using Ability version of highlighting tiles.");

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
			GeneralCommandSets.drawBoardTiles(context.out, displayMoveableTiles, 1);
			GeneralCommandSets.drawBoardTiles(context.out, displayAttackableTiles, 2);

			// Set boolean to control unit selected
			unitPlayable = true;

			// Apply flags due to external factors
			for (Ability a : newlySelectedUnit.getMonsterAbility()) {
		
				// Switch monster to provoked (use tileAdjustedRangeContainer in move or attack state)
				if (a instanceof A_U_Provoke) {
					newlySelectedUnit.toggleProvoked();
				}
			}
		}
		else {
			// Display unit selected actions
			unitPlayable = unitSelectedActions(newlySelectedUnit, context.getGameStateRef(), currentTile.getTilex(), currentTile.getTiley(), context.out, newlySelectedUnit.getClass());
		}

		
		if(unitPlayable) {
			context.deselectAllAfterActionPerformed();
			context.getGameStateRef().getBoard().setUnitSelected((Monster) newlySelectedUnit);
		} else {
			return;
		}

	}
	
	
	// unitSelectedActions is for selecting + movement & attack
	private boolean unitSelectedActions(Unit unit, GameState g, int tilex, int tiley, ActorRef o, Class<? extends Unit> classtype) {
		
			// Cast unit to a Monster (note, only works if a monster is actually inputted) 
			Monster m = (Monster) unit; 
			
			// Monster is actionable
			if (!(m.getOnCooldown())) {
				System.out.println("You own this monster");
				
				// Select monster + apply visual
				//m.toggleSelect();
				g.getBoard().setUnitSelected(m);

				BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 1);
				System.out.println("Selected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
				//System.out.println("Monster selected: " + m.isSelected());
				GeneralCommandSets.threadSleep();

				// Display movement + attack range tiles
				// Get ranges
				System.out.println("Moves left for windshrike dude: " +  m.getMovesLeft());
				ArrayList <Tile> mRange = g.getBoard().unitMovableTiles(tilex,tiley,m.getMovesLeft());
				ArrayList <Tile> aRange = new ArrayList <Tile> (g.getBoard().unitAttackableTiles(tilex, tiley, m.getAttackRange(), m.getMovesLeft()));
				ArrayList <Tile> actRange = mRange;		actRange.addAll(aRange);
				
				// Change GeneralCommandSets thing to account for multiple ranges later
				for(Tile t : actRange) {
					// If aRange contains t = draw as attack tile
					if(aRange.contains(t)) {
						BasicCommands.drawTile(o, t, 2);
						GeneralCommandSets.threadSleep();
					}
					// Else, draw as move range tile
					else {
						BasicCommands.drawTile(o, t, 1);
						GeneralCommandSets.threadSleep();
					}
				}
				System.out.println("Finished highlighting tiles.");
				return true;
					
			} 
			
			// Monster not actionable
			else {
				// Monster is not owned by Player --- Don't think this is needed anymore
//				if(m.getOwner() != g.getTurnOwner()) {
//					System.out.println("You do not own this monster");
//					return false;
//				}
				// Monster doesn't have moves/attacks left
//				else {
					System.out.println("Can't select this monster.");
					return false;
//				}
			}
			
	}

}

