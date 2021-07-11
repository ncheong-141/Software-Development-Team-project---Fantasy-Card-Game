package commands;

import java.util.ArrayList;

import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Hand;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

/*
 * This class contains a list of methods which can be used to automate/streamline sets of UI commands 
 * It also keeps it standard across the entire application
 */

import akka.actor.ActorRef;


public class GeneralCommandSets {
	
	private static final int threadSleepTime = 30; 
	private static final int threadSleepTimeLong = 100; 
	private static final int bufferSize = 16; 
	
	// Draw tiles to the board
	public static void drawBoardTiles(ActorRef out, ArrayList<Tile> tilesToDraw, int tileColour) {

		// Iterate over tiles
		// Need to consider the maximum buffer size and do this in batches if over 16 tiles to highlight (buffer size = 16)
		if (tilesToDraw.size() < bufferSize) {
			for (int i = 0; i < tilesToDraw.size(); i++) {
				BasicCommands.drawTile(out, tilesToDraw.get(i), tileColour);
			}
			try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}	
		}
		else { 
			
			// Calculate how many batches you need approximately
			int batchSize = (tilesToDraw.size()/bufferSize) + 1; 	// Integer division floors (does not include remainder) so assume + 1 always
			
			// Iterate over batches (bNum starts at 1 as multiplying 
			for (int bNum = 0; bNum < batchSize; bNum++) {
			
				// If batch number is not the final batch
				if (bNum < batchSize -1 ) {
					for (int i = bNum*bufferSize; i < (bNum+1)*bufferSize; i++) {
						BasicCommands.drawTile(out, tilesToDraw.get(i), tileColour);
					}
					try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}

				}
				// If bNum is the final batch, only iterate up to the ArrayList length 
				else {
					for (int i = bNum*bufferSize; i < tilesToDraw.size(); i++) {
						BasicCommands.drawTile(out, tilesToDraw.get(i), tileColour);
					}
					try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
				}
				
				// Long sleep to allow for buffer to empty properly...
				//threadSleepOverride(1000); 
			}
		}
	}

	// Verbose board reset method for code clarity
	public static void boardVisualReset(ActorRef out, GameState gameState) {
		
		drawBoardTiles(out, gameState.getBoard().getAllTilesList(), 0);
	}

	
	// Draw a unit with stats if a Monster
	public static void drawUnitWithStats(ActorRef out, Unit unit, Tile onTile) {
		
	
		BasicCommands.drawUnit(out, unit, onTile);
		GeneralCommandSets.threadSleep(); 
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
		GeneralCommandSets.threadSleep(); 
		
		if (unit.getClass() == Monster.class || unit.getClass() == Avatar.class) {
			
			// Cast reference to Monster (Avatar extends Monster so works for this class too)
			Monster mUnit = (Monster) unit; 
			
			// Set Unit statistics
			BasicCommands.setUnitHealth(out, mUnit, mUnit.getHP());
			GeneralCommandSets.threadSleep(); 
			BasicCommands.setUnitAttack(out, mUnit, mUnit.getAttackValue());
			GeneralCommandSets.threadSleep(); 
		}
	}
	
	
	/// Redraw all Unit stats general command
	public static void redrawAllUnitStats(ActorRef out, GameState gameState) {
		
		System.out.println("In redrawAllUnitStats"); 
		
		// Loop over all friendly and enemy tiles and update
		for (Tile t : gameState.getBoard().friendlyTile(gameState.getPlayerOne())) {
			
			// Redraw stats
			BasicCommands.setUnitAttack(out, t.getUnitOnTile(), t.getUnitOnTile().getAttackValue());
			threadSleep();
			BasicCommands.setUnitHealth(out, t.getUnitOnTile(), t.getUnitOnTile().getHP());
			threadSleep();
		}
		
		for (Tile t : gameState.getBoard().enemyTile(gameState.getPlayerOne())) {
			
			// Redraw stats
			BasicCommands.setUnitAttack(out, t.getUnitOnTile(), t.getUnitOnTile().getAttackValue());
			threadSleep();
			BasicCommands.setUnitHealth(out, t.getUnitOnTile(), t.getUnitOnTile().getHP());
			threadSleep();
		}
		
		// Avatars
		BasicCommands.setUnitAttack(out, gameState.getHumanAvatar(), gameState.getHumanAvatar().getAttackValue());
		threadSleep();
		BasicCommands.setUnitHealth(out, gameState.getHumanAvatar(), gameState.getHumanAvatar().getHP());
		threadSleep();

		BasicCommands.setUnitAttack(out, gameState.getComputerAvatar(), gameState.getComputerAvatar().getAttackValue());
		threadSleep();
		BasicCommands.setUnitHealth(out, gameState.getComputerAvatar(), gameState.getComputerAvatar().getHP());
		threadSleep();
		
	}
	
	// Reset tiles covering a given unit's range
	public static void drawUnitDeselect(ActorRef out, GameState gameState, Unit unit) {
		if(unit.getClass() == Monster.class || unit.getClass() == Avatar.class) {
			
			// Cast for Monster methods/values
			Monster mUnit = (Monster) unit;
			
			// Update Monster's occupied tile
			Tile location = gameState.getBoard().getTile(mUnit.getPosition().getTilex(), mUnit.getPosition().getTiley());
			BasicCommands.drawTile(out, location, 0);
			
			// Get Monster range
			ArrayList <Tile> actRange = new ArrayList <Tile> (gameState.getBoard().unitAttackableTiles(mUnit.getPosition().getTilex(), mUnit.getPosition().getTiley(), mUnit.getAttackRange(), mUnit.getMovesLeft()));
			actRange.addAll(gameState.getBoard().unitMovableTiles(mUnit.getPosition().getTilex(), mUnit.getPosition().getTiley(), mUnit.getMovesLeft()));
			
			drawBoardTiles(out, actRange, 0);
			
		}
		
	}
	
	
	// Update player stats
	public static void updatePlayerStats(ActorRef out, GameState gameState) {
		
		// Set player 1 stats in UI
		BasicCommands.setPlayer1Health(out, gameState.getPlayerOne());
		GeneralCommandSets.threadSleep(); 
		BasicCommands.setPlayer1Mana(out, gameState.getPlayerOne());
		GeneralCommandSets.threadSleep(); 

		
		// Set player 2 stats in UI
		BasicCommands.setPlayer2Health(out, gameState.getPlayerTwo());
		GeneralCommandSets.threadSleep(); 
		BasicCommands.setPlayer2Mana(out, gameState.getPlayerTwo());
		GeneralCommandSets.threadSleep(); 

	}
	
	// Show entire Hand 
	public static void drawCardsInHand(ActorRef out, GameState gameState, int oldHandSize, ArrayList<Card> cardsInHand) {

		if(gameState.isTwoPlayerMode() ) {// Delete/hide all cards in the UI
			for (int i = 0; i < oldHandSize; i++) {
				BasicCommands.deleteCard(out, i);
			}
			GeneralCommandSets.threadSleep(); 
		}
		 
		// Show all the cards in the UI in new positions 
		int i = 0;	
		for(Card c : cardsInHand) { // get list of cards from Hand from Player
			BasicCommands.drawCard(out, c, i, 0);
			i++;
			GeneralCommandSets.threadSleep(); 
		}
	}
	
	
	
	// General thread commands
	public static void threadSleep() {
		try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public static void threadSleepLong() {
		try {Thread.sleep(threadSleepTimeLong);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public static void threadSleepOverride(int ms) {
		try {Thread.sleep(ms);} catch (InterruptedException e) {e.printStackTrace();}

	}
	
	
}
