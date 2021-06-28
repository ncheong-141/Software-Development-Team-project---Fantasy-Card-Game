package events.tileplaystates;

import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.Unit;
import java.util.ArrayList;
import akka.actor.ActorRef;
import commands.*; 



public class UnitDisplayActionsState implements GameplayStates{

	
	// State method
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitDisplayActionsState.");

		// Get the newly selected unit
		Unit newlySelectedUnit = context.getGameStateRef().getBoard().getTile(context.tilex, context.tiley).getUnitOnTile();
		
		// Display unit selected actions
		boolean outcome = unitSelectedActions(newlySelectedUnit, context.getGameStateRef(),context.tilex, context.tiley, context.out, newlySelectedUnit.getClass());
		
		if(outcome) {
			context.getGameStateRef().getBoard().setUnitSelected((Monster) newlySelectedUnit);
			//System.out.println(context.getGameStateRef().getBoard().getUnitSelected().name);
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
				m.toggleSelect();
				g.getBoard().setUnitSelected(m);

				BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 1);
				System.out.println("Selected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
				System.out.println("Monster selected: " + m.isSelected());
				GeneralCommandSets.threadSleep();

				// Display movement + attack range tiles
				// Get ranges
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
				// Monster is not owned by Player
				if(m.getOwner() != g.getTurnOwner()) {
					System.out.println("You do not own this monster");
					return false;
				}
				// Monster doesn't have moves/attacks left
				else {
					System.out.println("Can't select this monster.");
					return false;
				}
			}
			
	}

}

