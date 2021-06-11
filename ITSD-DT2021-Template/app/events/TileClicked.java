package events;


import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Monster;
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
		if (gameState.getBoard().getTile(tiley , tilex).getUnitOnTile() != null) {
			
			System.out.println("Unit present");
		
			// Check if avatar  (i dont think we need to distinguish between avatar or monster here but for the sake of the print) 
			if (gameState.getBoard().getTile(tiley, tilex).getUnitOnTile() instanceof Avatar) {
				
				System.out.println("Avatar clicked");
				Avatar a = (Avatar) gameState.getBoard().getTile(tiley, tilex).getUnitOnTile();
				avatarLogic(a, gameState, out);
			}
			else {
				System.out.println("Monster clicked");
				Monster m = (Monster) gameState.getBoard().getTile(tiley, tilex).getUnitOnTile();
				monsterLogic(m, gameState, out);
			}
		}
	
	}

	/* Helper methods such as highlight unit, display unit stats etc */

	static void monsterLogic(Monster m, GameState g, ActorRef o) {
		// Do something like highlight unit etc. 
		System.out.println("Monster clicked");
		System.out.println("Monster name: " + m.getName());
		System.out.println("Monster HP: " + m.getHP());
		System.out.println("Monster attack: " + m.getAttackValue());
		System.out.println("Monster mana cost: " + m.getManaCost());

		// Change/access monster here
//		Tile tile = BasicObjectBuilders.loadTile(3, 2);
//		BasicCommands.drawTile(o, tile, 1);
	}
	
	static void avatarLogic (Avatar a, GameState g, ActorRef o) {
		if (a.getOwner() == g.getTurnOwner()) {
			ArrayList<Tile> moveList = a.possibleMoves();
			for (Tile t : moveList) BasicCommands.drawTile(o, t, 2);
		}
	}

}
