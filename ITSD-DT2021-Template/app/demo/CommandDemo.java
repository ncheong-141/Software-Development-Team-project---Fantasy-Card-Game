package demo;

import java.util.ArrayList;
import java.util.HashSet;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Deck;
import structures.basic.EffectAnimation;
import structures.basic.Hand;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.A_U_DoubleAttacker;
import structures.basic.abilities.Ability;
import structures.basic.abilities.AbilityToUnitLinkage;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class contains an illustration of calling all of the commands provided
 * in the ITSD Card Game Template
 * @author Dr. Richard McCreadie
 *
 */
public class CommandDemo {

	/**
	 * This is a demo of the various commands that can be executed
	 * 
	 * WARNING: This is a very long-running method, as it has a lot
	 * thread sleeps in it. The back-end will not respond to commands
	 * while this method is processing, e.g. if you refresh the game
	 * page or try to kill the server you will get a long delay before
	 * anything happens.
	 */

	public static void executeDemoTester(ActorRef out, GameState gameState) {
		
		// Instantites players, deck, hand and board done in gameSate
		
		
		// Set player HP, Mana
		gameState.getPlayerOne().setHealth(8);
		gameState.getPlayerOne().setMana(10);
		
		gameState.getPlayerTwo().setHealth(8);
		gameState.getPlayerTwo().setMana(10);

		
		// Create Card objects to use
		Card cBlazeHound = BasicObjectBuilders.loadCard(StaticConfFiles.c_blaze_hound, 2, Card.class);
		Card cFireSpitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 3, Card.class);

		// Add them to the hand
		
		// Create Friendly Unit objects to use (sets HP, name, ability, onwer already) 
		Monster[] fmArray = new Monster[5]; 
		fmArray[0] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cFireSpitter, gameState.getPlayerOne(), Monster.class);
		fmArray[1] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cFireSpitter, gameState.getPlayerOne(), Monster.class);
		fmArray[2] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_blaze_hound, cBlazeHound, gameState.getPlayerOne(), Monster.class);
		fmArray[3] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_blaze_hound, cBlazeHound, gameState.getPlayerOne(), Monster.class);
		fmArray[4] = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_blaze_hound, cBlazeHound, gameState.getPlayerOne(), Monster.class);
		
		for (int i = 0; i < fmArray.length; i++) {
			fmArray[i].setPositionByTile(gameState.getBoard().getTile(0, i));
			gameState.getBoard().getTile(0, i).addUnit(fmArray[i]);
			fmArray[i].setMovesLeft(2);
			fmArray[i].setAttacksLeft(1);
			fmArray[i].setCooldown(false);
		}
		 
		
		/** UI **/
		
		// Display states
		GeneralCommandSets.updatePlayerStats(out, gameState);
		
		// Draw board
		GeneralCommandSets.boardVisualReset(out, gameState);
		
		// Draw avatars
		GeneralCommandSets.drawUnitWithStats(out, gameState.getHumanAvatar(), gameState.getBoard().getTile(1, 2));
		GeneralCommandSets.drawUnitWithStats(out, gameState.getComputerAvatar(), gameState.getBoard().getTile(7, 2));
		
		// Draw units 
		for (int i = 0; i < fmArray.length; i++) {
			GeneralCommandSets.drawUnitWithStats(out,fmArray[i], fmArray[i].getPosition().getTile(gameState.getBoard()));
		}
		
		// Draw cards
		GeneralCommandSets.drawCardsInHand(out, gameState, gameState.getPlayerOne().getHand().getHand());
		
		
	}

	public static void executeDemoUnits(ActorRef out, GameState gameState) {

		// drawTile
		Board gameBoard = new Board();

		for (int i = 0; i<gameBoard.getGameBoard().length; i++) {
			for (int k = 0; k<gameBoard.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, gameBoard.getGameBoard()[i][k], 0);
			}
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// loadCard
		Card cfire_spitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 1, Card.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// drawUnit
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		Monster fire_spitter = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cfire_spitter, gameState.getTurnOwner(), Monster.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		fire_spitter.setPositionByTile(gameBoard.getTile(3,2));
		fire_spitter.setOwner(gameState.getTurnOwner());
		BasicCommands.drawUnit(out, fire_spitter, gameBoard.getTile(3,2));
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		gameState.getBoard().getTile(3, 2).addUnit(fire_spitter);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		// Display unit stats
		BasicCommands.addPlayer1Notification(out, "Displaying stats of monster", 2);
		BasicCommands.setUnitAttack(out, fire_spitter, fire_spitter.getAttackValue());
		BasicCommands.setUnitHealth(out, fire_spitter, fire_spitter.getHP());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}	

	}


	public static void executeDemoUnitsNicholas(ActorRef out, GameState g) {


		// Draw board
		Board gameBoard = new Board();

		for (int i = 0; i<gameBoard.getGameBoard().length; i++) {
			for (int k = 0; k<gameBoard.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, gameBoard.getGameBoard()[i][k], 0);
			}
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		g.setTurnOwner(g.getPlayerOne());
		g.getPlayerOne().setMana(8);

		// Set up friendly Unit to summon next to
		Avatar humanAvatar = g.getHumanAvatar();
		BasicCommands.drawUnit(out, humanAvatar, g.getBoard().getTile(1, 2));
		GeneralCommandSets.threadSleep();
		
		// loadCard
		Card enemyfire_spitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, StaticConfFiles.u_fire_spitter, 102, Card.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		Card friendfire_spitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, StaticConfFiles.u_fire_spitter, 103, Card.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		Card twofriendfire_spitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, StaticConfFiles.u_fire_spitter, 104, Card.class);

		
		// Enemy unit
		// drawUnit
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		Monster ENEMY_fire_spitter = BasicObjectBuilders.loadMonsterUnit(enemyfire_spitter.getConfigFile(), enemyfire_spitter, (Player) g.getPlayerTwo(), Monster.class);
		GeneralCommandSets.threadSleep();
		
		ENEMY_fire_spitter.setPositionByTile(gameBoard.getTile(3,4));
		ENEMY_fire_spitter.setOwner(g.getPlayerTwo());
		ENEMY_fire_spitter.setHP(1);
		g.getBoard().getTile(3,4).addUnit(ENEMY_fire_spitter);
		BasicCommands.drawUnit(out, ENEMY_fire_spitter, gameBoard.getTile(3,4));
		GeneralCommandSets.threadSleep();

		
		// drawUnit
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		Monster ffire_spitter = (Monster) BasicObjectBuilders.loadMonsterUnit(friendfire_spitter.getConfigFile(), friendfire_spitter, (Player) g.getPlayerOne(), Monster.class);
		GeneralCommandSets.threadSleep();


		ffire_spitter.setPositionByTile(gameBoard.getTile(3,2));
		ffire_spitter.setOwner(g.getTurnOwner());
		ffire_spitter.setAttacksLeft(10);
		ffire_spitter.setMovesLeft(3);
		ffire_spitter.setCooldown(false);
		g.getBoard().getTile(3,2).addUnit(ffire_spitter);
		BasicCommands.drawUnit(out, ffire_spitter, gameBoard.getTile(3,2));
		GeneralCommandSets.threadSleep();

		
		// loadCard
		Card ctrustrike = BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 1, Card.class);
		ctrustrike.setConfigFile(StaticConfFiles.c_truestrike);
		Card cstaffofykir = BasicObjectBuilders.loadCard(StaticConfFiles.c_staff_of_ykir, 2, Card.class);
		cstaffofykir.setConfigFile(StaticConfFiles.c_staff_of_ykir);
		Card centropybrokenskill = BasicObjectBuilders.loadCard(StaticConfFiles.c_entropic_decay, 3, Card.class);
		centropybrokenskill.setConfigFile(StaticConfFiles.c_entropic_decay);
		Card csundropelixir = BasicObjectBuilders.loadCard(StaticConfFiles.c_sundrop_elixir, 4, Card.class);
		csundropelixir.setConfigFile(StaticConfFiles.c_sundrop_elixir);

		
		// Create a tempHand for testing
		ArrayList <Card> cards = new ArrayList <Card> ();
		cards.add(twofriendfire_spitter);
		cards.add(ctrustrike);
		cards.add(csundropelixir);
		cards.add(cstaffofykir);
		cards.add(centropybrokenskill);


		// Use temporary setHand to give to HumanPlayer for testing - proper Hand creation needs to be setup
		Hand hand = new Hand(); 
		hand.setHand(cards);
		g.getTurnOwner().setHand(hand);

		// draw cards in hand
		int i = 0;	// position in hand where card is drawn, assumes Hand is not currently holding illegal number (>6)
		for(Card c : g.getTurnOwner().getHand().getHand()) { // get list of cards from Hand from Player
			BasicCommands.drawCard(out, c, i, 0);
			i++;
		}

	}


	public static void executeDemoBoard(ActorRef out, GameState g) {
		

	}


	public static void executeDemoPlayer(ActorRef out) {
	}
	public static void executeDemoDeckHand(ActorRef out, GameState g) {
		
		// draw the Board
		Board gameBoard = new Board();

		for (int i = 0; i<gameBoard.getGameBoard().length; i++) {
			for (int k = 0; k<gameBoard.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, gameBoard.getGameBoard()[i][k], 0);
			}
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		Deck playOneDeck = g.getPlayerOne().getDeck();
		Card testCard = playOneDeck.getDeck().get(12);
		ArrayList <Card> cards = new ArrayList <Card> ();
		cards.add(testCard);

		System.out.println("config file in demo is: " + testCard.getConfigFile());
		Monster mcf = BasicObjectBuilders.loadMonsterUnit(testCard.getConfigFile(), testCard, g.getPlayerOne(), Monster.class);
		
		// Try to draw
		Tile one = g.getBoard().getTile(3, 4);
		one.addUnit(mcf);
		mcf.setPositionByTile(one);
		BasicCommands.drawUnit(out, mcf, one);
		
		// Use temporary setHand to give to HumanPlayer for testing - proper Hand creation needs to be setup
		Hand hand = new Hand(); 
		hand.setHand(cards);
		g.getTurnOwner().setHand(hand);

		// draw cards in hand
		int i = 0;	// position in hand where card is drawn, assumes Hand is not currently holding illegal number (>6)
		for(Card c : g.getTurnOwner().getHand().getHand()) { // get list of cards from Hand from Player
			BasicCommands.drawCard(out, c, i, 0);
			i++;
		}
		
		// Give player mana
		g.getTurnOwner().setMana(8);
		
	}

	public static void executeDemoSummon(ActorRef out, GameState g) {

		
	}

	public static void executeTileHighlightDemo (ActorRef out, GameState g) {

		Board board = g.getGameBoard();

		Tile tone = board.getTile(3,1);
		Tile tTwo = board.getTile(4, 3);
		Tile tThree = board.getTile(2, 4);
		Tile tFour = board.getTile(4, 1);

		// loadCard
		Card cfire_spitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 1, Card.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// drawUnit

		Monster fire_spitter = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cfire_spitter, (Player) g.getPlayerTwo(), Monster.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		fire_spitter.setPositionByTile(tone);
		tone.addUnit(fire_spitter);
		BasicCommands.drawUnit(out, fire_spitter, tone);

		// drawUnit

		Monster fire_spitterTwo = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cfire_spitter, (Player) g.getPlayerTwo(), Monster.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		fire_spitterTwo.setPositionByTile(tTwo);
		tTwo.addUnit(fire_spitterTwo);
		BasicCommands.drawUnit(out, fire_spitterTwo, tTwo);


		// drawUnit

		Monster fire_spitterThree = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cfire_spitter, (Player) g.getPlayerTwo(), Monster.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		fire_spitterThree.setPositionByTile(tThree);
		tThree.addUnit(fire_spitterThree);
		BasicCommands.drawUnit(out, fire_spitterThree, tThree);

		Monster fire_spitterFour = (Monster) BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cfire_spitter, (Player) g.getPlayerTwo(), Monster.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		fire_spitterFour.setPositionByTile(tFour);
		tFour.addUnit(fire_spitterFour);
		BasicCommands.drawUnit(out, fire_spitterFour, tFour);

		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		//ArrayList <Tile> listMove = g.getGameBoard().unitMovableTiles(1, 2, 2);

		/*
		 * for (Tile t : listMove) { BasicCommands.drawTile(out, t, 1); } try
		 * {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		 */
		HashSet<Tile> list = g.getGameBoard().unitAttackableTiles(1, 2, 2,3);

		for (Tile t : list) {
			BasicCommands.drawTile(out, t, 2);
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
	}

	public static void executeAbilityDemo (ActorRef out, GameState g) {
		

		// Draw the Board
		Board gameBoard = new Board();

		for (int i = 0; i<gameBoard.getGameBoard().length; i++) {
			for (int k = 0; k<gameBoard.getGameBoard()[0].length; k++) {
				BasicCommands.drawTile(out, gameBoard.getGameBoard()[i][k], 0);
			}
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		g.setTurnOwner(g.getPlayerOne());
		g.getPlayerOne().setMana(8);
		BasicCommands.setPlayer1Mana(out, g.getPlayerOne());
		System.out.println("From demo, Player one has: " + g.getPlayerOne().getMana());
		
		// loadCard
		Card cfire_spitter = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 1, Card.class);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// Set up friendly double attacker unit
		Monster mfire_spitter = BasicObjectBuilders.loadMonsterUnit(StaticConfFiles.u_fire_spitter, cfire_spitter, g.getTurnOwner(), Monster.class);
		ArrayList <Ability> temp = new ArrayList <Ability> ();
//		A_U_DoubleAttacker ab1 = new A_U_DoubleAttacker();
//		A_U_RangedAttacker ab2 = new A_U_RangedAttacker();
//		temp.add(ab1);		/*temp.add(ab2);*/
		mfire_spitter.setAbility(temp);
		for(Ability a : mfire_spitter.getMonsterAbility()) {	a.execute(mfire_spitter, g);	}
		mfire_spitter.toggleCooldown();
		mfire_spitter.setAttackValue(2);
		mfire_spitter.setHP(4);
		
		// Set up an enemy
		Monster aiAv = g.getComputerAvatar();
		aiAv.setOwner(g.getPlayerTwo());
		aiAv.setHP(3);
		aiAv.setAttackValue(2);
		
		// Get Tiles and set Units
		Tile tileFriendStart = g.getBoard().getTile(5, 1);
		Tile tileEnemyStart = g.getBoard().getTile(7, 2);
		
		aiAv.setPositionByTile(tileEnemyStart);
		tileEnemyStart.addUnit(aiAv);
		mfire_spitter.setPositionByTile(tileFriendStart);
		tileFriendStart.addUnit(mfire_spitter);
		
		// Draw stuff
		BasicCommands.drawUnit(out, mfire_spitter, tileFriendStart);
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, mfire_spitter, mfire_spitter.getAttackValue());
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, mfire_spitter, mfire_spitter.getHP());
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.drawUnit(out, aiAv, tileEnemyStart);
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, aiAv, aiAv.getAttackValue());
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, aiAv, aiAv.getHP());
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}

		
	}
}




//public static void executeDemo(ActorRef out) {
//
//	BasicCommands.addPlayer1Notification(out, "Command Demo", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} // these cause processing to wait for a number of milliseconds.
//
//	// addPlayer1Notification
//	BasicCommands.addPlayer1Notification(out, "addPlayer1Notification", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[3,2]", 2);
//	Tile tile = BasicObjectBuilders.loadTile(3, 2);
//	BasicCommands.drawTile(out, tile, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawUnit
//	BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
//	Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
//	unit.setPositionByTile(tile); 
//	BasicCommands.drawUnit(out, unit, tile);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// setUnitAttack
//	BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
//	BasicCommands.setUnitAttack(out, unit, 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// setUnitHealth
//	BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
//	BasicCommands.setUnitHealth(out, unit, 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// playUnitAnimation [Move]
//	BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Move]", 2);
//	BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// playUnitAnimation [Attack]
//	BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
//	BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// playUnitAnimation [Death]
//	BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Death]", 3);
//	BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
//	try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// deleteUnit
//	BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
//	BasicCommands.deleteUnit(out, unit);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[8,4]", 2);
//	Tile tile2 = BasicObjectBuilders.loadTile(8, 4);
//	BasicCommands.drawTile(out, tile2, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawUnit
//	BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
//	Unit fire_spitter = BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter, 1, Unit.class);
//	fire_spitter.setPositionByTile(tile); 
//	BasicCommands.drawUnit(out, fire_spitter, tile);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawUnit
//	BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
//	Unit planar_scout = BasicObjectBuilders.loadUnit(StaticConfFiles.u_planar_scout, 2, Unit.class);
//	planar_scout.setPositionByTile(tile2); 
//	BasicCommands.drawUnit(out, planar_scout, tile2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// playProjectileAnimation
//	BasicCommands.addPlayer1Notification(out, "playProjectileAnimation", 2);
//	EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
//	BasicCommands.playUnitAnimation(out, fire_spitter, UnitAnimationType.attack);
//	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//	BasicCommands.playProjectileAnimation(out, projectile, 0, tile, tile2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//	BasicCommands.playUnitAnimation(out, planar_scout, UnitAnimationType.death);
//
//	// deleteUnit
//	BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
//	BasicCommands.deleteUnit(out, planar_scout);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// deleteUnit
//	BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
//	BasicCommands.deleteUnit(out, fire_spitter);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Highlight", 2);
//	BasicCommands.drawTile(out, tile, 1);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
//	BasicCommands.drawTile(out, tile, 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[3,2]", 2);
//	BasicCommands.drawTile(out, tile, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[4,2]", 2);
//	Tile tile3 = BasicObjectBuilders.loadTile(4, 2);
//	BasicCommands.drawTile(out, tile3, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[5,2]", 2);
//	Tile tile4 = BasicObjectBuilders.loadTile(5, 2);
//	BasicCommands.drawTile(out, tile4, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawTile
//	BasicCommands.addPlayer1Notification(out, "drawTile[5,3]", 2);
//	Tile tile5 = BasicObjectBuilders.loadTile(5, 3);
//	BasicCommands.drawTile(out, tile5, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawUnit
//	BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
//	unit.setPositionByTile(tile); 
//	BasicCommands.drawUnit(out, fire_spitter, tile);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// moveUnitToTile
//	BasicCommands.addPlayer1Notification(out, "moveUnitToTile", 2);
//	BasicCommands.moveUnitToTile(out, fire_spitter, tile5);
//	unit.setPositionByTile(tile5); 
//	try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// moveUnitToTile
//	BasicCommands.addPlayer1Notification(out, "moveUnitToTile (back)", 2);
//	BasicCommands.moveUnitToTile(out, fire_spitter, tile);
//	unit.setPositionByTile(tile); 
//	try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// deleteUnit
//	BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
//	BasicCommands.deleteUnit(out, fire_spitter);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// Effects
//	BasicCommands.addPlayer1Notification(out, "playEffectAnimation", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	String[] effects = {
//			StaticConfFiles.f1_buff,
//			StaticConfFiles.f1_inmolation,
//			StaticConfFiles.f1_martyrdom,
//			StaticConfFiles.f1_summon
//	};
//
//	for (String effectFile : effects) {
//		BasicCommands.addPlayer1Notification(out, effectFile, 2);
//		EffectAnimation ef = BasicObjectBuilders.loadEffect(effectFile);
//		BasicCommands.playEffectAnimation(out, ef, tile);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	}
//
//	// Player Cards
//	BasicCommands.addPlayer1Notification(out, "Player Test", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// setPlayer1Health
//	BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
//	Player humanPlayer = new Player(20, 0);
//	BasicCommands.setPlayer1Health(out, humanPlayer);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// setPlayer1Health
//	BasicCommands.addPlayer1Notification(out, "setPlayer2Health", 2);
//	Player aiPlayer = new Player(20, 0);
//	BasicCommands.setPlayer2Health(out, aiPlayer);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// Mana
//	for (int m = 0; m<10; m++) {
//		BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
//		humanPlayer.setMana(m);
//		BasicCommands.setPlayer1Mana(out, humanPlayer);
//		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//	}
//
//	// Mana
//	for (int m = 0; m<10; m++) {
//		BasicCommands.addPlayer1Notification(out, "setPlayer2Mana ("+m+")", 1);
//		aiPlayer.setMana(m);
//		BasicCommands.setPlayer2Mana(out, aiPlayer);
//		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//	}
//
//	// Player Hand Test
//	BasicCommands.addPlayer1Notification(out, "Player Hand Test", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//
//	// drawCard [1]
//	BasicCommands.addPlayer1Notification(out, "drawCard [1u]", 2);
//	Card hailstone_golem = BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, 0, Card.class);
//	BasicCommands.drawCard(out, hailstone_golem, 1, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawCard [1] Highlight
//	BasicCommands.addPlayer1Notification(out, "drawCard [1u] Highlight", 2);
//	BasicCommands.drawCard(out, hailstone_golem, 1, 1);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// deleteCard [1]
//	BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
//	BasicCommands.deleteCard(out, 1);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawCard [1]
//	BasicCommands.addPlayer1Notification(out, "drawCard [1s]", 2);
//	Card entropic_decay = BasicObjectBuilders.loadCard(StaticConfFiles.c_entropic_decay, 0, Card.class);
//	BasicCommands.drawCard(out, entropic_decay, 1, 0);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// drawCard [1] Highlight
//	BasicCommands.addPlayer1Notification(out, "drawCard [1s] Highlight", 2);
//	BasicCommands.drawCard(out, entropic_decay, 1, 1);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	// deleteCard [1]
//	BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
//	BasicCommands.deleteCard(out, 1);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	BasicCommands.addPlayer1Notification(out, "Player 1 Cards", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	String[] deck1Cards = {
//			StaticConfFiles.c_azure_herald,
//			StaticConfFiles.c_azurite_lion,
//			StaticConfFiles.c_comodo_charger,
//			StaticConfFiles.c_fire_spitter,
//			StaticConfFiles.c_hailstone_golem,
//			StaticConfFiles.c_ironcliff_guardian,
//			StaticConfFiles.c_pureblade_enforcer,
//			StaticConfFiles.c_silverguard_knight,
//			StaticConfFiles.c_sundrop_elixir,
//			StaticConfFiles.c_truestrike
//	};
//
//	for (String deck1CardFile : deck1Cards) {
//		// drawCard [1]
//		BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
//		Card card = BasicObjectBuilders.loadCard(deck1CardFile, 0, Card.class);
//		BasicCommands.drawCard(out, card, 1, 0);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// drawCard [1] Highlight
//		BasicCommands.addPlayer1Notification(out, deck1CardFile+" Highlight", 2);
//		BasicCommands.drawCard(out, card, 1, 1);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// deleteCard [1]
//		BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
//		BasicCommands.deleteCard(out, 1);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//	}
//
//	String[] deck2Cards = {
//			StaticConfFiles.c_blaze_hound,
//			StaticConfFiles.c_bloodshard_golem,
//			StaticConfFiles.c_entropic_decay,
//			StaticConfFiles.c_hailstone_golem,
//			StaticConfFiles.c_planar_scout,
//			StaticConfFiles.c_pyromancer,
//			StaticConfFiles.c_serpenti,
//			StaticConfFiles.c_rock_pulveriser,
//			StaticConfFiles.c_staff_of_ykir,
//			StaticConfFiles.c_windshrike,
//	};
//
//	for (String deck2CardFile : deck2Cards) {
//		// drawCard [1]
//		BasicCommands.addPlayer1Notification(out, deck2CardFile, 2);
//		Card card = BasicObjectBuilders.loadCard(deck2CardFile, 0, Card.class);
//		BasicCommands.drawCard(out, card, 1, 0);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// drawCard [1] Highlight
//		BasicCommands.addPlayer1Notification(out, deck2CardFile+" Highlight", 2);
//		BasicCommands.drawCard(out, card, 1, 1);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// deleteCard [1]
//		BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
//		BasicCommands.deleteCard(out, 1);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//	}
//
//
//	// moveUnitToTile
//	BasicCommands.addPlayer1Notification(out, "Deck 1 Units Test", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	String[] deck1Units = {
//			StaticConfFiles.u_comodo_charger,
//			StaticConfFiles.u_hailstone_golem,
//			StaticConfFiles.u_azure_herald,
//			StaticConfFiles.u_azurite_lion,
//			StaticConfFiles.u_pureblade_enforcer,
//			StaticConfFiles.u_ironcliff_guardian,
//			StaticConfFiles.u_silverguard_knight,
//			StaticConfFiles.u_fire_spitter
//	};
//
//	int unitID = 3;
//	for (String deck1CardFile : deck1Units) {
//		BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
//		unit = BasicObjectBuilders.loadUnit(deck1CardFile, unitID, Unit.class);
//		unit.setPositionByTile(tile); 
//		BasicCommands.drawUnit(out, unit, tile);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// playUnitAnimation [Move]
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Move]", 2);
//		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// playUnitAnimation [Attack]
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
//		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// playUnitAnimation [Death]
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Death]", 3);
//		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
//		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// deleteUnit
//		BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
//		BasicCommands.deleteUnit(out, unit);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		unitID++;
//	}
//
//	// moveUnitToTile
//	BasicCommands.addPlayer1Notification(out, "Deck 2 Units Test", 2);
//	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//	String[] deck2Units = {
//			StaticConfFiles.u_blaze_hound,
//			StaticConfFiles.u_bloodshard_golem,
//			StaticConfFiles.u_hailstone_golemR,
//			StaticConfFiles.u_planar_scout,
//			StaticConfFiles.u_pyromancer,
//			StaticConfFiles.u_rock_pulveriser,
//			StaticConfFiles.u_serpenti,
//			StaticConfFiles.u_windshrike
//	};
//
//	for (String deck2CardFile : deck2Units) {
//		BasicCommands.addPlayer1Notification(out, deck2CardFile, 2);
//		unit = BasicObjectBuilders.loadUnit(deck2CardFile, unitID, Unit.class);
//		unit.setPositionByTile(tile); 
//		BasicCommands.drawUnit(out, unit, tile);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// playUnitAnimation [Move]
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Move]", 2);
//		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// playUnitAnimation [Attack]
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
//		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// playUnitAnimation [Death]
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Death]", 3);
//		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
//		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		// deleteUnit
//		BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
//		BasicCommands.deleteUnit(out, unit);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//		unitID++;
//	}
//
//
//}
