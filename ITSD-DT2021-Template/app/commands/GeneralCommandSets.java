package commands;

import java.util.ArrayList;

import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
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
	private static final int threadSleepTimeLong = 400; 
	private static final int bufferSize = 16; 
	
	// Draw tiles to the board
	public static void drawBoardTiles(ActorRef out, ArrayList<Tile> tilesToDraw, int tileColour) {
	
		// Iterate over tiles
		// Need to consider the maximum buffer size and do this in batches if over 16 tiles to highlight (buffer size = 16)
		if (tilesToDraw.size() < bufferSize) {
			for (int i = 0; i < tilesToDraw.size(); i++) {
				BasicCommands.drawTile(out, tilesToDraw.get(i), tileColour);
				try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}	
			}
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
						try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
				// If bNum is the final batch, only iterate up to the ArrayList length 
				else {
					for (int i = bNum*bufferSize; i < tilesToDraw.size(); i++) {
						BasicCommands.drawTile(out, tilesToDraw.get(i), tileColour);
						try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
				
				// Long sleep to allow for buffer to empty properly...
				threadSleepOverride(1000); 
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
		try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
		try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
		
		if (unit.getClass() == Monster.class || unit.getClass() == Avatar.class) {
			
			// Cast reference to Monster (Avatar extends Monster so works for this class too)
			Monster mUnit = (Monster) unit; 
			
			// Set Unit statistics
			BasicCommands.setUnitHealth(out, mUnit, mUnit.getHP());
			try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.setUnitAttack(out, mUnit, mUnit.getAttackValue());
			try {Thread.sleep(threadSleepTime);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	
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
