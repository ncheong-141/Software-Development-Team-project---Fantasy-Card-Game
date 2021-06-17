package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Monster;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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
			
			System.out.println("Unit present");
		
			// Check if avatar  (i dont think we need to distinguish between avatar or monster here but for the sake of the print) 
			if (gameState.getBoard().getTile(tilex, tiley).getUnitOnTile() instanceof Avatar) {
				
				System.out.println("Avatar clicked");
				Avatar a = (Avatar) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				//avatarLogic(a, gameState, out);
			}
			else {
				Monster m = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				
				// Do something like highlight unit etc. 
				System.out.println("Monster clicked");
				monsterLogic(m, gameState, out);
			}
		}
		// IF NO UNIT IS PRESENT JUST SUMMON THIS UNIT
		else {
			summonMonster(gameState, out, StaticConfFiles.u_fire_spitter,tilex,tiley);
		}
	
	}



	
	
	/* Helper methods such as highlight unit, display unit stats etc */

	static void monsterLogic(Monster m, GameState g, ActorRef o) {

		System.out.println("Monster name: " + m.getName());
		System.out.println("Monster HP: " + m.getHP());
		System.out.println("Monster attack: " + m.getAttackValue());
		System.out.println("Monster mana cost: " + m.getManaCost());
		
		// Note that currently Monster's do not auto-set their owners,
		// Do this manually with .setOwner(Player p) before trying to select
		if (m.getOwner() == g.getTurnOwner()) {
			System.out.println("You own this monster");
			
			// Deselect monster if already selected + apply visual
			if(m.isSelected()) {
				m.toggleSelect();
				BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTiley(), (m.getPosition()).getTilex()), 0);
				System.out.println("Have deselected monster on Tile " + m.getPosition().getTiley() + "," + m.getPosition().getTilex());
				System.out.println("Monster selected: " + m.isSelected());
				
				// Update movement + attack range tiles
				
			}
			// Select monster + apply visual
			else {
				
				m.toggleSelect();
				BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTiley(), (m.getPosition()).getTilex()), 1);
				System.out.println("Have selected monster on Tile " + m.getPosition().getTiley() + "," + m.getPosition().getTilex());
				System.out.println("Monster selected: " + m.isSelected());
				
				// Movement + attack range tiles are displayed
				
			}
			
		} else {
			System.out.println("You do not own this monster");
		}	
	}
	
	
	// Summon monster method (for u_configFile, insert StaticConfFiles.u_.."
	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, int tilex, int tiley) {
		

		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadUnit(u_configFile, 1, Monster.class);		
		summonedMonster.setPositionByTile(gameState.getBoard().getTile(tilex,tiley));
		summonedMonster.setOwner(gameState.getTurnOwner());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Drawing the monster on the board
		BasicCommands.drawUnit(out, summonedMonster, gameState.getBoard().getTile(tilex,tiley));
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		gameState.getBoard().getTile(tilex,tiley).addUnit(summonedMonster);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	
	
//	static void avatarLogic (Avatar a, GameState g, ActorRef o) {
//		if (a.getOwner() == g.getTurnOwner()) {
//			ArrayList<Tile> moveList = a.possibleMoves();
//			for (Tile t : moveList) BasicCommands.drawTile(o, t, 2);
//		}
//	}

}