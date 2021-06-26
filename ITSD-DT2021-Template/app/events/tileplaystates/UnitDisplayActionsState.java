package events.tileplaystates;

import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;




public class UnitDisplayActionsState implements GameplayStates{

	
	// State method
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitDisplayActionsState.");

		// Get the newly selected unit
		Unit newlySelectedUnit = context.getGameStateRef().getBoard().getTile(context.tilex, context.tiley).getUnitOnTile();
		
		// Display unit selected actions
		unitSelectedActions(newlySelectedUnit, context.getGameStateRef(),context.tilex, context.tiley, context.out, newlySelectedUnit.getClass());
		
		// Set unit as selected
		context.getGameStateRef().getBoard().setUnitSelected((Monster) newlySelectedUnit);
		
		//System.out.println(context.getGameStateRef().getBoard().getUnitSelected().name);
	}
	
	
	
	
	// unitSelectedActions is for selecting + movement & attack
	private void unitSelectedActions(Unit unit, GameState g, int tilex, int tiley, ActorRef o, Class<? extends Unit> classtype) {


		// Check class type entered 
		if (classtype == Monster.class) {
			
			// Cast unit to a Monster (note, only works if a monster is actually inputted) 
			Monster m = (Monster) unit; 
			
			// Current player owns clicked Monster
			if (m.getOwner() == g.getTurnOwner()) {

				// + getSelectedUnit check

				System.out.println("You own this monster");
				
				// Deselect monster if already selected + apply visual
				if(m.isSelected()) {
					m.toggleSelect();

					g.getBoard().setUnitSelected(null);
					BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 0);
					System.out.println("Deselected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
					System.out.println("Monster selected: " + m.isSelected());
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
					
					// Update movement range tiles displayed
					ArrayList <Tile> mRange = g.getBoard().unitMovableTiles(tilex,tiley,m.getMovesLeft());
					for(Tile t : mRange) {
						BasicCommands.drawTile(o, t, 0);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
					}
					System.out.println("Finished un-highlighting tiles.");
					
					// If selectedUnit != Unit on clicked Tile, switch to new Unit
					
				}
				// Select monster + apply visual
				else if(!(m.isSelected())) {
					m.toggleSelect();
					g.getBoard().setUnitSelected(m);

					BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 1);
					System.out.println("Selected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
					System.out.println("Monster selected: " + m.isSelected());
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}


					// Display movement range tiles
					ArrayList <Tile> mRange = g.getBoard().unitMovableTiles(tilex,tiley,m.getMovesLeft());
					for(Tile t : mRange) {
						BasicCommands.drawTile(o, t, 1);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
					}
					System.out.println("Finished highlighting tiles.");


				}
			} 
			
			else {
				System.out.println("You do not own this monster");
			}
			
		}

	}

}
