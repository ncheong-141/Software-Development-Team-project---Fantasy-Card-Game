package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.BigCard;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.HumanPlayer;
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
		
		
		
	/* Need to reorganise this chain of checks to optimise
	** Currently:	1) Check Tile occupancy
	**				2) Check if any card in hand is selected for play
	**				3) Check if playing card is Monster/Spell
	*/
	
		
		
	//	>>>>> Tile is occupied by Unit	//

		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
		
//			System.out.println("Unit present");
//			
//			// If there is a card selected in hand: 
//			if (gameState.getTurnOwner().getHand().getPlayingMode()) {
//				
//				// Retrieve selected card
//				Card selected = gameState.getTurnOwner().getHand().getSelectedCard();
//				
//				// If Card is a Monster
//				// Temp way of identifying, Card could contain a more useful Monster/Spell identifier
//				if(selected.getBigCard().getAttack() < 0) {			
//					System.out.println("Tile is already occupied.");	
//				} 
//				
//				// If Card is Spell
//				else {	
//					System.out.println("Selected card is a Spell card.");
//					
//					// Mana check: player mana vs mana cost
//					// Spell checks: can this spell be applied to this Unit
//					// Apply to target
//				}
//				
//			}
//			
//			// If there is no card selected in hand, run Monster logic; no difference in Av/Mon here
//			else {			
				System.out.println("Unit clicked");
//				Monster m = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
//				
//				// Moveable check here
//					
//				monsterLogic(m, gameState, out);	
//			}	
		}
		
	//	>>>>> Tile is unoccupied	//
		
		else {

//			// If there is a card selected in hand
//			if (gameState.getTurnOwner().getHand().getPlayingMode()) {
//
//				// 1) Retrieve selected card in hand
//				// 2) Identify card type for logic to be used
//				// 3) Convert cardName String to configFile name
//				// 4) Pass configname and Card to summonMonster
//				
//				// Retrieve selected card
//				Card selected = gameState.getTurnOwner().getHand().getSelectedCard();
//				
//				// If Card is a Monster
//				// Temp way of identifying, Card could contain a more useful Monster/Spell identifier
//				if(selected.getBigCard().getAttack() < 0) {
//					
//					// Mana vs mana cost check
//					if(gameState.getTurnOwner().getMana() >= selected.getManacost()) {
//						// Convert name to configFile name
//						String configName = selected.getCardname().replace(' ', '_');
//						configName = "u_" + configName;
//
//						System.out.println("Summoning monster...");
//						summonMonster(gameState, out , configName, selected, tilex, tiley);
//					} 
//					
//					else {
//						System.out.println("Not enough mana to summon Monster.");
//					}
//					
//				} 
//				
//				// If Card is Spell
//				else {
//					System.out.println("Can't activate a Spell on an empty tile.");
//				}
//				
//			}
//			
//			// No selected cards in Hand
//			else {
				System.out.println("That sure is an empty tile.");
//			}
		}
//	
	}



	
	
	/* Helper methods such as highlight unit, display unit stats etc */

	// MonsterLogic is for selecting + movement & attack
	static void monsterLogic(Monster m, GameState g, ActorRef o) {

		System.out.println("Monster name: " + m.getName());
		System.out.println("Monster HP: " + m.getHP());
		System.out.println("Monster attack: " + m.getAttackValue());
		System.out.println("Monster mana cost: " + m.getManaCost());
		
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
				
				// Movement + attack range tiles are displayed
				
			}
			
		} 
		
		else {
			System.out.println("You do not own this monster");
		}
		
	}
	
	
	// Summon monster method (for u_configFile, insert StaticConfFiles.u_.."
	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, Card statsRef, int tilex, int tiley) {
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(u_configFile,1,statsRef,Monster.class);		
		summonedMonster.setPositionByTile(gameState.getBoard().getTile(tilex,tiley));
		summonedMonster.setOwner(gameState.getTurnOwner());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		gameState.getBoard().getTile(tilex,tiley).addUnit(summonedMonster);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Drawing the monster on the board
		BasicCommands.drawUnit(out, summonedMonster, gameState.getBoard().getTile(tilex,tiley));
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Set monster statistics
		BasicCommands.setUnitHealth(out, summonedMonster, summonedMonster.getHP());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, summonedMonster, summonedMonster.getAttackValue());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Mana costs --- leave out until mana cycle is implemented ingame
//		BasicCommands.addPlayer1Notification(out, "Player mana cost", 2);
//		gameState.getTurnOwner().loseMana(statsRef.getManacost());
//		
//		if(gameState.getTurnOwner() instanceof HumanPlayer) {
//			BasicCommands.setPlayer1Mana(out, gameState.getTurnOwner());
//		} else {
//			BasicCommands.setPlayer2Mana(out, gameState.getTurnOwner());
//		}
//		
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Delete card from Hand command --- later sprint
		
	}
	

}