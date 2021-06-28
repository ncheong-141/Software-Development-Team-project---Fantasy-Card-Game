package events.tileplaystates;

import java.util.ArrayList;

import commands.BasicCommands;
import commands.GeneralCommandSets;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

public class UnitAttackActionSubState implements GameplayStates {

	// State attributes
	GameplayStates subState; 

	// State constructor 
	public UnitAttackActionSubState() {	
		subState = null; 
	}
	
	public void execute(GameplayContext context) {
		
		System.out.println("In UnitAttackActionSubState.");
		context.debugPrint();
		
		unitAttack(context);
		
	}
	
	private void unitAttack(GameplayContext context) {
		
		// Gather attacker and defender
		Monster attacker = (Monster) context.getLoadedUnit();
		Monster defender = context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley()).getUnitOnTile();
		
		// Retrieve frequently used Tile data
		Tile currentLocation = context.getGameStateRef().getBoard().getTile(context.getLoadedUnit().getPosition().getTilex(),context.getLoadedUnit().getPosition().getTiley());
		ArrayList <Tile> selectedAttackRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(currentLocation.getTilex(), currentLocation.getTiley(), currentLocation.getUnitOnTile().getAttackRange(),currentLocation.getUnitOnTile().getMovesLeft()));
		Tile enemyTarget = context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley());
		
		for(Tile t : selectedAttackRange) {
			System.out.println("Tile t x: " + t.getTilex() + ", y: " + t.getTiley());
		}
		
		// Check target is in attack range
		if(!(selectedAttackRange.contains(enemyTarget))) {	
			System.out.println("About to abort attack");
			return;
		}
		
		// Gather action range
		ArrayList <Tile> actRange = new ArrayList <Tile> (context.getGameStateRef().getBoard().unitAttackableTiles(attacker.getPosition().getTilex(), attacker.getPosition().getTiley(), attacker.getAttackRange(), attacker.getMovesLeft()));
		ArrayList <Tile> mRange = context.getGameStateRef().getBoard().unitMovableTiles(attacker.getPosition().getTilex(), attacker.getPosition().getTiley(), attacker.getMovesLeft());
		actRange.addAll(mRange);
		
		// If attacker can attack
		if(attacker.attack()) {
			System.out.println("Attack successful.");
			boolean outcome = defender.defend(attacker.getAttackValue());
			
			// Deselect tiles
			System.out.println("Started drawing Tiles");
			//GeneralCommandSets.drawBoardTiles(context.out, actRange, 0);
			int i = 0;
			for (Tile t : actRange) {
				// Leave defender tile highlighted
				if(t == context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley())) {
					continue;
				}
				BasicCommands.drawTile(context.out, t, 0);
				System.out.println("Drawn tile " + i);
				i++;
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			}
			System.out.println("Finished drawing Tiles method");
			GeneralCommandSets.threadSleepLong();
			
			// Play animations and set visuals
			BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.attack);
			BasicCommands.playUnitAnimation(context.out, defender, UnitAnimationType.hit);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.setUnitHealth(context.out, defender, defender.getHP());
			
			// Defender death check
			// Counter attack here
			
			// Re-idle (if condition for defender death later)
			BasicCommands.playUnitAnimation(context.out,attacker,UnitAnimationType.idle);
			BasicCommands.playUnitAnimation(context.out,defender,UnitAnimationType.idle);
			GeneralCommandSets.threadSleep();
			
			// Update both unit tile visuals
			BasicCommands.drawTile(context.out, context.getGameStateRef().getBoard().getTile(attacker.getPosition().getTilex(), attacker.getPosition().getTiley()), 0);			
			GeneralCommandSets.threadSleep();
			BasicCommands.drawTile(context.out, context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley()), 0);
			GeneralCommandSets.threadSleep();
			
			// De-select selected unit
			attacker.toggleSelect();
			context.deselectAllAfterActionPerformed();
			
		} 
		// Unit is unable to attack for some reason - internal attack values/summon cooldown/etc.
		else {
			System.out.println("Unit cannot attack.");
		}
		
	}
	
}
