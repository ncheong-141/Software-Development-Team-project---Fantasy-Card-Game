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
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
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
		
		
				
	
		
		
		// TileClicked logic chain
		// ------------------------------------------------------------------------------------------
		
		/*
		 * Structure of TileClicked logic
		 *  0. Check if its the players turn (return from method if not)
		    1. Check if Unit present
			1T 
				2. Check if Card is selected in hand
				2T	3. Check card type
					3M - Do nothing
					3S - Cast spell on monster if applicable 
				2F	Select monster, open the movement options 
			1F
				2. Check if Card is selected in hand
				2T 	3. Check card type
					3M - Summon monster
					3P - Do nothing
				2F Do nothing since no card or unit
		*/
		
		
		// Check if its the players turn. Do not want the player to be able to alter the game while the computer is playing
//		if (!checkIfPlayerTurn(gameState, out)) {
//			return; 
//		}
		
		// Check if a Unit is present on the tile clicked. 
		if (checkUnitPresent(gameState,tilex,tiley) == true) {
			

			// Debugging section
			// ------------------------------------------------------------------------------------------
			// Create reference for monster on tile for debug
			Monster tileMonster = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
			
			System.out.println("Unit present");
			System.out.println("Free status: " + gameState.getBoard().getTile(tilex, tiley).getFreeStatus());
			
			// Print monster stats to console for debugging
			System.out.println("Monster name: " + tileMonster.getName());
			System.out.println("Monster HP: " + tileMonster.getHP());
			System.out.println("Monster attack: " + tileMonster.getAttackValue());
			// -------------------------------------------------------------------------------------------
			
			

			// Check if a card is currently active in the hand (been clicked) 
			if (checkCardClicked(gameState) == true) {
				
				// Retrieve selected card
				Card selected = gameState.getTurnOwner().getHand().getSelectedCard();
				
				// Check if card is a Monster or Spell
				if(checkIfMonsterCard(selected) == true) {		
					System.out.println("Selected card is a Monster card.");	
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
			
			// If there is no card selected in hand, unit is selected. 
			else if (checkCardClicked(gameState) == false) {		
				
				System.out.println("Unit selected");
				Monster selectedMonster = (Monster) gameState.getBoard().getTile(tilex, tiley).getUnitOnTile();
				
				// Moveable check here
				if (checkIfUnitMovable(selectedMonster)) {
					unitSelectedActions(selectedMonster, gameState, tilex, tiley, out, Monster.class);	
				}
			}	
		}
	
		// Tile is unnoccupied 	
		else if (checkUnitPresent(gameState,tilex,tiley) == false){

			// If there is a card selected in hand
			if (checkCardClicked(gameState) == true) {

				// 1) Retrieve selected card in hand
				// 2) Identify card type for logic to be used
				// 3) Convert cardName String to configFile name
				// 4) Pass configname and Card to summonMonster
				
				// Retrieve selected card
				Card selected = gameState.getTurnOwner().getHand().getSelectedCard();
				
				// Check if card is a Monster or Spell
				if(checkIfMonsterCard(selected) == true) {		
					
					// Mana vs mana cost check --- input when mana cycle is implemented
					
					// Check selected Tile is in summonable range
					// What a condition!!
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
			
			// If there is no card selected in hand or any unit present
			else if (checkCardClicked(gameState) == false) {
				
				// Only movement can occur (no Unit on destination tile)
				// Board method to retrieve selected Monster
				if(gameState.getBoard().getUnitSelected() == null) {
					System.out.println("No selected unit.");
					return;
				}
				Monster mSelected = gameState.getBoard().getUnitSelected();
				// Get movement range
				ArrayList <Tile> mRange = gameState.getBoard().unitMovableTiles(tilex,tiley,mSelected.getMovesLeft());

				// If destination tile is in movement range, move there
				if(mRange.contains(gameState.getBoard().getTile(tilex,tiley))) {
					
					// Visual feedback to player of the path to be taken --- currently just deselects all
					mSelected.toggleSelect();
					int i = 0;
					for(Tile t : mRange) {
						BasicCommands.drawTile(out, t, 0);
						try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
						System.out.println("Tile print " + i);
						i++;
					}
					BasicCommands.drawTile(out, gameState.getBoard().getTile((mSelected.getPosition()).getTilex(), (mSelected.getPosition()).getTiley()), 0);
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

					Tile current = gameState.getBoard().getTile(mSelected.getPosition().getTilex(),mSelected.getPosition().getTiley());
					Tile target = gameState.getBoard().getTile(tilex, tiley);
					
					// Check Monster can move to the target
					System.out.println("MovesLeft: " + mSelected.getMovesLeft());
					System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
					if(mSelected.move(target)) {
						System.out.println("MovesLeft: " + mSelected.getMovesLeft());
						System.out.println("Monster on cooldown: " + mSelected.getOnCooldown());
						// Update Board and Tiles
						current.removeUnit();
						target.addUnit(mSelected);
						gameState.getBoard().setUnitSelected(null);
						
						BasicCommands.addPlayer1Notification(out, "Unit moving...", 4);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
						
					// Update front end, UnitAnimations could be moved to UnitMoving/Stopped
						// Move animation
						BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.move);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
						// Initiate move
						BasicCommands.moveUnitToTile(out, mSelected, gameState.getBoard().getTile(tilex, tiley));
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
						System.out.println("Movement is complete, I'm back in TileClicked rn.");
						// Re-idle
						BasicCommands.playUnitAnimation(out, mSelected, UnitAnimationType.idle);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
					}
					
				}
				// Destination is not in movement range (deselect?)
				else {	
					System.out.println("Target out of range.");		
				}
			}
			else { /**/ }
	}			
}

	
	
	// Remove later
	/* Condition check helper methods to keep code clear */
	private boolean checkIfPlayerTurn(GameState gameState, ActorRef out) {
		if ((gameState.getTurnOwner() instanceof HumanPlayer)) {
			BasicCommands.addPlayer1Notification(out, "Not your turn!", 2);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}	
			return true;
		}
		else {
			return false; 
		}
	}
	
	private boolean checkUnitPresent(GameState gameState, int tilex, int tiley) {	
		return (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null);
	}
	
	private boolean checkCardClicked(GameState gameState) {
		return (gameState.getTurnOwner().getHand().isPlayingMode());
	}
	
	private boolean checkIfMonsterCard(Card selected) {
		return (selected.getBigCard().getAttack() > 0);
	}
	
	
	private boolean checkIfUnitMovable(Unit unit) {
		return true; 
	}
	
	
	/* Helper methods such as highlight unit, display unit stats etc */


	// unitSelectedActions is for when a unit is selected + displaying its movement & attack movement
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
	
	
	// Summon monster method (for u_configFile, insert StaticConfFiles.u_.."

	public void summonMonster(GameState gameState, ActorRef out, String u_configFile, Card statsRef, int tilex, int tiley) {
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter,1,statsRef,Monster.class);		
		summonedMonster.setPositionByTile(gameState.getBoard().getTile(tilex,tiley));
		summonedMonster.setOwner(gameState.getTurnOwner());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Keep before add Unit because drawTile doesn't work on tile underneath new monster for some reason
		// De-highlight tiles
		ArrayList <Tile> summonRange = gameState.getBoard().allSummonableTiles(gameState.getTurnOwner());
		for (Tile i : summonRange) {
			BasicCommands.drawTile(out, i, 0);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		gameState.getBoard().getTile(tilex,tiley).addUnit(summonedMonster);
		System.out.println("This is immediately after summon.");
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Drawing the monster on the board
		BasicCommands.drawUnit(out, summonedMonster, gameState.getBoard().getTile(tilex,tiley));
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, summonedMonster, UnitAnimationType.idle);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Set monster statistics
		BasicCommands.setUnitHealth(out, summonedMonster, summonedMonster.getHP());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, summonedMonster, summonedMonster.getAttackValue());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		
		// De-select card
		Card selectedCard = gameState.getTurnOwner().getHand().getSelectedCard();
		// Need position in hand from Noah to redraw Card with no highlight
//		BasicCommands.drawCard(out, selectedCard, position, 0);
		selectedCard.setClicked(false);
		gameState.getTurnOwner().getHand().setPlayingMode(false);
		gameState.getTurnOwner().getHand().setSelectedCard(null);
		
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