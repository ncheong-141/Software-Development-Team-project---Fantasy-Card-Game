package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Spell;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import structures.basic.abilities.*;

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

		
		/*
		 *  Testing set up area 
		 */ 
		// ---------------------------------------------------------------
		ArrayList<Ability> abilitySelection = new ArrayList<Ability>(10); 
		abilitySelection.add(new A_Truestrike()); 
		abilitySelection.add(new A_SundropElixir()); 
		abilitySelection.add(new A_StaffofYkir());
		abilitySelection.add(new A_EntropicDecay());
		
		Ability selectedAbility = abilitySelection.get(3);
		
		//Spell Truestrike = (Spell) BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike,0, Spell.class);
		//Truestrike.setAbility("Truestrike", selectedAbility, "Does 2 damage");
		// ---------------------------------------------------------------
		
		
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		
		// Check if Unit present
		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
			
			System.out.println("Unit present");
			System.out.println("Free status: " + gameState.getBoard().getTile(tilex, tiley).getFreeStatus());
			
			// Create reference for monster on tile 
			Monster tileMonster = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();

			// Do something like highlight unit etc. 
			System.out.println("Monster clicked");
			System.out.println("Monster name: " + tileMonster.getName());
			System.out.println("Monster HP: " + tileMonster.getHP());
			System.out.println("Monster attack: " + tileMonster.getAttackValue());
			System.out.println("Monster mana cost: " + tileMonster.getManaCost());

			// Atm this is only highlight tiles and de-highlight 
			monsterClickedProcedure(tileMonster, gameState, out);

			// Hit the monster with a spell 
			selectedAbility.execute(tileMonster);
			System.out.println("Monster HP after ability hitting it: " + tileMonster.getHP());

			// Update display 
			updateMonsterDisplay(out,tileMonster);
			
			// Need to try and get Spell effect animation, for Truestrike its immolation in the card file but how to link it to the static conf file?
			EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
			BasicCommands.playEffectAnimation(out, ef, gameState.getBoard().getTile(tilex , tiley));

		}
		// IF NO UNIT IS PRESENT JUST SUMMON THIS UNIT
		else {
			summonMonster(gameState, out, StaticConfFiles.u_fire_spitter,tilex,tiley);
		}
	
	}

	
	
	/* Helper methods such as highlight unit, display unit stats etc */

	private void monsterClickedProcedure(Monster m, GameState g, ActorRef o) {

		
		// Note that currently Monster's do not auto-set their owners,
		// Do this manually with .setOwner(Player p) before trying to select
		if (m.getOwner() == g.getTurnOwner()) {
			System.out.println("You own this monster");
			
			// Deselect monster if already selected + apply visual
			if(m.isSelected()) {
				m.toggleSelect();
				BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 0);
				System.out.println("Have deselected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
				System.out.println("Monster selected: " + m.isSelected());
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

				// Update movement + attack range tiles
				
			}
			// Select monster + apply visual
			else {
				
				m.toggleSelect();
				BasicCommands.drawTile(o, g.getBoard().getTile((m.getPosition()).getTilex(), (m.getPosition()).getTiley()), 1);
				System.out.println("Have selected monster on Tile " + m.getPosition().getTilex() + "," + m.getPosition().getTiley());
				System.out.println("Monster selected: " + m.isSelected());
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

				// Movement + attack range tiles are displayed
				
			}
			
		} else {
			System.out.println("You do not own this monster");
		}	
	}
	
	
	// Summon monster method (for u_configFile, insert StaticConfFiles.u_.."
	private void summonMonster(GameState gameState, ActorRef out, String u_configFile, int tilex, int tiley) {
		

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
		
		// Update the display 
		updateMonsterDisplay(out, summonedMonster);
	}
	
	
	// Update display after effects have been applied
	private void updateMonsterDisplay(ActorRef out, Monster mUnit) {

		BasicCommands.setUnitAttack(out, mUnit, mUnit.getAttackValue());
		BasicCommands.setUnitHealth(out, mUnit, mUnit.getHP());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}	
	}
	
//	static void avatarLogic (Avatar a, GameState g, ActorRef o) {
//		if (a.getOwner() == g.getTurnOwner()) {
//			ArrayList<Tile> moveList = a.possibleMoves();
//			for (Tile t : moveList) BasicCommands.drawTile(o, t, 2);
//		}
//	}

}