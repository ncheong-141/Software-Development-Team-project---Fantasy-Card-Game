package events.tileplaystates;

import structures.GameState;
import structures.basic.Monster;
import structures.basic.Unit;
import akka.actor.ActorRef;
import commands.BasicCommands;




public class UnitDisplayActionsState implements GameplayStates{

	
	// State method
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitDisplayActionsState.");

		// Get the newly selected unit
		Unit newlySelectedUnit = context.getGameStateRef().getBoard().getTile(context.tilex, context.tiley).getUnitOnTile();
		
		// Display unit selected actions
		unitSelectedActions(newlySelectedUnit, context.getGameStateRef(), context.out, newlySelectedUnit.getClass());
		
		// Set unit as selected
		context.getGameStateRef().getBoard().setUnitSelected((Monster) newlySelectedUnit);
	}
	
	
	
	
		// unitSelectedActions is for selecting + movement & attack
		private void unitSelectedActions(Unit unit, GameState g, ActorRef o, Class<? extends Unit> classtype) {

			// Check class type entered 
			if (classtype == Monster.class) {
				
				// Cast unit to a Monster (note, only works if a monster is actually inputted) 
				Monster m = (Monster) unit; 
				
				// Current player owns clicked Monster
				if (m.getOwner() == g.getTurnOwner()) {
					System.out.println("You own this monster");
					
					// Deselect monster if already selected + apply visual
					if(m.isSelected()) {
						m.toggleSelect();
						BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 0);
						System.out.println("Deselected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
						System.out.println("Monster selected: " + m.isSelected());
						
						// Update movement + attack range tiles displayed
						
					}
					// Select monster + apply visual
					else {
						m.toggleSelect();
						BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 1);
						System.out.println("Selected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
						System.out.println("Monster selected: " + m.isSelected());
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

						// Movement + attack range tiles are displayed
					}
				} 
				
				else {
					System.out.println("You do not own this monster");
				}
				
			}
		}

}
