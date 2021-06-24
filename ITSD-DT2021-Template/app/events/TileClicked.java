package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.BigCard;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Card;
import structures.basic.HumanPlayer;
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
		
		
		
	/* Need to reorganise this chain of checks to optimise
	** Currently:	1) Check Tile occupancy
	**				2) Check if any card in hand is selected for play
	**				3) Check if playing card is Monster/Spell
	*/
	
		
		
	//	>>>>> Tile is occupied by Unit	//

		if (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null) {
			
			System.out.println("Unit present");
			System.out.println("Free status: " + gameState.getBoard().getTile(tilex, tiley).getFreeStatus());
			
			// Debugging section
			// ------------------------------------------------------------------------------------------
			// Create reference for monster on tile for debug
			Monster tileMonster = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();

			// Print monster stats to console for debugging
			System.out.println("Unit present");
			System.out.println("Monster clicked");
			System.out.println("Monster name: " + tileMonster.getName());
			System.out.println("Monster HP: " + tileMonster.getHP());
			System.out.println("Monster attack: " + tileMonster.getAttackValue());
			System.out.println("Monster mana cost: " + tileMonster.getManaCost());
			
			// TileClicked logic chain
			// ------------------------------------------------------------------------------------------
			// If there is a card selected in hand: 
			if (gameState.getTurnOwner().getHand().isPlayingMode()) {
				
				// Retrieve selected card
				Card selected = gameState.getTurnOwner().getHand().getSelectedCard();
				
				// If Card is a Monster
				// Temp way of identifying, Card could contain a more useful Monster/Spell identifier
				if(selected.getBigCard().getAttack() > 0) {			
					System.out.println("Tile is already occupied.");	
				} 
				
				// If Card is Spell
				else {	
					System.out.println("Selected card is a Spell card.");
					
					// Mana check: player mana vs mana cost
					// Spell checks: can this spell be applied to this Unit
					// Apply to target
					
//					// Hit the monster with a spell 
//					selectedAbility.execute(tileMonster);
//					System.out.println("Monster HP after ability hitting it: " + tileMonster.getHP());
		//
//					// Update display 
//					updateMonsterDisplay(out,tileMonster);
//					
//					// Need to try and get Spell effect animation, for Truestrike its immolation in the card file but how to link it to the static conf file?
//					EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
//					BasicCommands.playEffectAnimation(out, ef, gameState.getBoard().getTile(tilex , tiley));
					// ------------------------------------------------------------------------------------------
	
				}
				
			}
			
			// If there is no card selected in hand, run Monster logic; no difference in Av/Mon here
			else {			
				System.out.println("Unit clicked");
				Monster m = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				
				// Moveable check here
					
				monsterLogic(m, gameState, out);	
			}	
		}
		
	//	>>>>> Tile is unoccupied	//
		
		else {

			// If there is a card selected in hand
			if (gameState.getTurnOwner().getHand().isPlayingMode()) {

				// 1) Retrieve selected card in hand
				// 2) Identify card type for logic to be used
				// 3) Convert cardName String to configFile name
				// 4) Pass configname and Card to summonMonster
				
				// Retrieve selected card
				Card selected = gameState.getTurnOwner().getHand().getSelectedCard();
				
				// If Card is a Monster
				// Temp way of identifying, Card could contain a more useful Monster/Spell identifier
				if(selected.getBigCard().getAttack() > 0) {
					
					// Mana vs mana cost check --- input when mana cycle is implemented
					
						// Check selected Tile is in summonable range
						if((gameState.getBoard().allSummonableTiles(gameState.getTurnOwner())).contains(gameState.getBoard().getTile(tilex, tiley))) {
							
							String configName = selected.getCardname().replace(' ', '_').toLowerCase().trim();
							configName = "u_" + configName;
	
							System.out.println("Summoning monster...");
							summonMonster(gameState, out , configName, selected, tilex, tiley);
							
						} else {
							System.out.println("Can't summon monster on this tile.");
						}
						
				} 
				
				// If Card is Spell
				else {
					System.out.println("Can't activate a Spell on an empty tile.");
				}
				
			}
			
			// No selected cards in Hand
			else {
				System.out.println("That sure is an empty tile.");
			}
		}	
	}

	
	
	/* Helper methods such as highlight unit, display unit stats etc */


	// MonsterLogic is for selecting + movement & attack
	static void monsterLogic(Monster m, GameState g, ActorRef o) {

		
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
	
	
	// Summon monster method (for u_configFile, insert StaticConfFiles.u_.."

	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, Card statsRef, int tilex, int tiley) {
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter,1,statsRef,Monster.class);		
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
		
		// De-highlight tiles
		ArrayList <Tile> summonRange = gameState.getBoard().allSummonableTiles(gameState.getTurnOwner());
		for (Tile i : summonRange) {
			BasicCommands.drawTile(out, i, 0);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// >>> Mana costs --- leave out until mana cycle is implemented ingame
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
		
		// >>> Delete card from Hand command --- later sprint
		
	}
	
	
//	// Construct spell 
//	private void constructSpell(GameState gameState, ActorRef out, String c_configFile, int tilex, int tiley) {
//		Spell constructedSpell = (Spell) BasicObjectBuilders.loadCard(c_configFile,Spell.class);		
//
//		
//	}
	
	// Update display after effects have been applied
	private void updateMonsterDisplay(ActorRef out, Monster mUnit) {

		BasicCommands.setUnitAttack(out, mUnit, mUnit.getAttackValue());
		BasicCommands.setUnitHealth(out, mUnit, mUnit.getHP());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}	
	}


}