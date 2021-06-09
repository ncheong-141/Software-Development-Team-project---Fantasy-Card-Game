package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		
		// Check if Unit present
		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
			
			// Do something like highlight unit etc. 
			System.out.println("Monster clicked");
			System.out.println("Monster name: " + gameState.getBoard().getTile(tilex, tiley).getMonsterReference().getName());
			System.out.println("Monster HP: " + gameState.getBoard().getTile(tilex, tiley).getMonsterReference().getHP());
			System.out.println("Monster attack: " + gameState.getBoard().getTile(tilex, tiley).getMonsterReference().getAttackValue());
			System.out.println("Monster mana cost: " + gameState.getBoard().getTile(tilex, tiley).getMonsterReference().getManaCost());
			
			// Change/access monster here
			Tile tile = BasicObjectBuilders.loadTile(3, 2);
			BasicCommands.drawTile(out, tile, 1);
		}
		else {
			System.out.println("No monster present... except there should be but this code seems to think 'addMonster' is not adding a monster");
		}
	}
	
	/* Helper methods such as highlight unit, display unit stats etc */

}
