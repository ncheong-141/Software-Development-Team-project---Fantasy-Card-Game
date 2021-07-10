package structures.basic;
import structures.basic.ComputerLogic.*;
import java.util.ArrayList;
import java.util.HashSet;

import structures.GameState;
public class ComputerPlayer extends Player {

	private int hPBenchMark;

	boolean playedAllPossibleCards;
	boolean madeAllPossibleMoves;	
	
	public ComputerPlayer() {
		super(); 
		/*
		 * this.madeAllPossibleMoves = false; this.playedAllPossibleCards = false;
		 * 
		 * this.deck = new Deck(); this.deck.deckTwo(); this.hand = new Hand();
		 * this.hand.initialHand(deck);
		 */
	}
	
	public String toString() {
		return "Player Two";
	}

	
	public int getHPBenchMark() {
		
		return this.hPBenchMark;
	}
	
	public void setHPBenchMark(int hp) {
		this.hPBenchMark = hp;
	}
	
	public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> playCards(Board gameBoard){
		ComputerPlayCardsLogic play = new ComputerPlayCardsLogic(this);
		
		return play.playCards(gameBoard);
	}
	
	public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> moveMonsters(Board gameBoard){
		ComputerMoveMonsterLogic move = new ComputerMoveMonsterLogic(this);
		return move.movesUnits(gameBoard);
	}
	
	public ArrayList <structures.basic.ComputerLogic.ComputerInstruction> performAttacks(Board gameBoard){
		ComputerAttackMonsterLogic attack = new ComputerAttackMonsterLogic (this);
		return attack.computerAttacks(gameBoard);
	}
	
	//leaving this method here to be used by both play cards and move logic
	public static void calcTileMoveScore(Monster m, Board b, Tile targetTile) {
		//tile where monster is currently located
		Tile currTile = b.getTile(m.getPosition().getTilex(), m.getPosition().getTiley());

		//calculate which enemy tiles are in range from the would be (WB) tile
		HashSet <Tile> wBAttackable = b.calcAttackRange(targetTile.getTilex(), targetTile.getTiley(), m.getAttackRange(), m.getOwner());
		
		//get all tiles that this monster could attack from its current tile (with enemies on them)
		HashSet<Tile> currAttackable = b.calcAttackRange(currTile.getTilex(), currTile.getTiley(), m.getAttackRange(), m.getOwner());
		
		System.out.println(wBAttackable.size() + "  " + currAttackable.size());
		
		
		if (wBAttackable.size() > currAttackable.size()) targetTile.setScore(Tile.getBringsEnemyInRange());
		
		//all tiles on the board with an enemy unit on it
		ArrayList <Tile> enemyTilesOnBoard = b.enemyTile(m.getOwner());
		
		int currAttackableByEnemy = 0;
		int wBAttackableByEnemy = 0;
		
		for (Tile t : enemyTilesOnBoard) {
			Monster mnstr = t.getUnitOnTile();
			int x = t.getTilex();
			int y = t.getTiley();
			
			//NOTE need to check when moves left gets reset
			ArrayList<Tile> tilesEnemyCanAttack = b.unitAttackableTiles(x, y, mnstr.getAttackRange(), mnstr.getMovesLeft());
			
			if (tilesEnemyCanAttack.contains(targetTile)) wBAttackableByEnemy++;
			if (tilesEnemyCanAttack.contains(currTile)) currAttackableByEnemy ++;
		}
		
		
		if (wBAttackableByEnemy > currAttackableByEnemy) targetTile.setScore(Tile.getInRangeScore());
		

		//return targetTile.getScore();
	}
	
	

	// after all actions, ComputerPlayer call computerEndTurn() to ends the turn
	// Removed @Override
	public void endTurn() {
//		GameState.computerEnd();
	}	
	
}

	//==================OVERALL NOTES ON COMP PLAYER CLASS========================//
	
	/***
	1)calculate all possible card combination that can be played given mana available
		for each combination calculate utility (-/+)
		if card has special ability (+/- X)
		weight each combination based on a,b,c par (health, card no.)
		select best combination (highest score)
	
	2)card(s) to be played selection returned
	
	3)for each card in selection scan board to find playable tiles
		for each card order tiles in list based on a,b,c par 
	
	4)compare playable tiles for cards to be played to ensure no overlap
		if overlap, select card with highest U
	
	5)set played card to true
	
	6)Scan board for all friendly units
	
	7)For each friendly unit check if:
		has enemy in range
		is in enemy range
		survive counter
		obtain kill
		no. attack left
		no. moves left
		has relevant special ability
		target (health + attack)
	
	8) calculate best move for each unit on the board based on ^:
		player's health
		opponent's health
		no. of cards
	
	9) display move and attack action(s)
		for each move update game
	
	10) check that played cards and move units are true (no more moves left to make)
	
	11) end turn	
	
		
	 ***/

