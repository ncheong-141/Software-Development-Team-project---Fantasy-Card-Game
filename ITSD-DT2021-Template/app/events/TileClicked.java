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

		// Code user name
		/*
		 * 0 - Nicholas
		 * 1 - Yufen
		 * 2 - Victoria 
		 * 3 - Chiara
		 * 4 - Noah
		 */
		int user = 0;
		
		// Switch statement to change code user implementation
		switch (user) {
		
		case (0):{
			System.out.println("TileClicked user: Nicholas"); 
			nicholasImpl(out, gameState, message);
			break;
		}
		case (1):{
			System.out.println("TileClicked user: Yufen");
			yufenImpl(out, gameState, message);
			break;
		}
		case (2):{
			System.out.println("TileClicked user: Victoria");
			victoriaImpl(out, gameState, message);
			break;
		}
		case (3):{
			System.out.println("TileClicked user: Chiara");
			chiaraImpl(out, gameState, message);
			break;
		}
		case (4):{
			System.out.println("TileClicked user: Noah"); 
			noahImpl(out, gameState, message);
			break;
		}
		default: {
			System.out.println("Please select user for TileClicked"); 
		}
		}

	}


	/* ======================================================================
	 * User methods 
	 * 
	 * Call the method with your name and implement any code here (copy any other persons code when needed)
	 * e.g. Copy code in chiaraImpl to click avatar or for monster you can use victorias or mine etc. 
	 * This is just to avoid any merge conflicts. 
	 * 
	 * ====================================================================== */
	
	private void nicholasImpl(ActorRef out, GameState gameState, JsonNode message) {
		
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		
		// Check if Unit present
		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
			
			System.out.println("Unit present");
		
			// Check if avatar  (i dont think we need to distinguish between avatar or monster here but for the sake of the print) 
			if (gameState.getBoard().getTile(tilex, tiley).getUnitOnTile() instanceof Avatar) {
				
				System.out.println("Avatar clicked");
				Avatar a = (Avatar) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				avatarLogic(a, gameState, out);
			}
			else {
				System.out.println("Monster clicked");
				Monster m = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				
				// Do something like highlight unit etc. 
				System.out.println("Monster clicked");
				monsterLogic(m, gameState, out);

			}
		}
	}
	
	private void yufenImpl(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
	}
	
	private void victoriaImpl(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		// Check if Unit present
		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
			
			System.out.println("Unit present");
		
			// Check if avatar  (i dont think we need to distinguish between avatar or monster here but for the sake of the print) 
			if (gameState.getBoard().getTile(tilex, tiley).getUnitOnTile() instanceof Avatar) {
				
				System.out.println("Avatar clicked");
				Avatar a = (Avatar) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				avatarLogic(a, gameState, out);
			}
			else {
				System.out.println("Monster clicked");
				Monster m = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				
				// Do something like highlight unit etc. 
				System.out.println("Monster clicked");
				monsterLogic(m, gameState, out);
			}
		} else {
			System.out.println("Empty tile clicked");
		}
	}
	
	private void chiaraImpl(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		// Check if Unit present
		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
			
			System.out.println("Unit present");
		
			// Check if avatar  (i dont think we need to distinguish between avatar or monster here but for the sake of the print) 
			if (gameState.getBoard().getTile(tilex, tiley).getUnitOnTile() instanceof Avatar) {
				
				System.out.println("Avatar clicked");
				Avatar a = (Avatar) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				avatarLogic(a, gameState, out);
			}
			else {
				System.out.println("Monster clicked");
				Monster m = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				
				// Do something like highlight unit etc. 
				System.out.println("Monster clicked");
				monsterLogic(m, gameState, out);
			}
		}
	}
	
	private void noahImpl(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
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
	
	static void avatarLogic (Avatar a, GameState g, ActorRef o) {
	
	}

	
	
}
