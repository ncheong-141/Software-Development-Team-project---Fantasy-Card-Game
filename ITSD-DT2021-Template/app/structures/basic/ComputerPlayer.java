package structures.basic;
import structures.basic.ComputerLogic.*;
import java.util.ArrayList;
import java.util.HashSet;

import structures.GameState;
public class ComputerPlayer extends Player {
	
//attack needed attributes
//	private AIUnitStateController a;
	private HumanPlayer playerOne;
	private HumanPlayer playerTwo;
	private GameState gameState;
	private ArrayList<Tile> attackerTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;  //a list of attackable enemy 
	private ArrayList<Tile> attackRange;

	private Tile target; //attack target
	private Tile attacker; 
//attack needed attributes

	

	private int hPBenchMark;

	Board gameBoard;
	boolean playedAllPossibleCards;
	boolean madeAllPossibleMoves;	

	
	public ComputerPlayer() {
		super(); 
		this.madeAllPossibleMoves = false;
		this.playedAllPossibleCards = false;
		
		this.deck = new Deck();
		this.deck.deckTwo();
		this.hand = new Hand();
		this.hand.initialHand(deck);
	}
	
	
	public void setGameBoard(Board b) {
		this.gameBoard = b;
	}
	
	public Board getGameBoard() {
		return this.gameBoard;
	}
	
	public int getHPBenchMark() {
		
		return this.hPBenchMark;
	}
	
	public void setHPBenchMark(int hp) {
		this.hPBenchMark = hp;
	}
	
	public ArrayList<ComputerInstruction> playCards(){
		ComputerPlayCardsLogic play = new ComputerPlayCardsLogic(this);
		
		return play.playCards();
	}
	
	public ArrayList<ComputerInstruction> moveMonsters(){
		ComputerMoveMonsterLogic move = new ComputerMoveMonsterLogic(this);
		return move.movesUnits();
	}
	
	//leaving this method here to be used by both play cards and move logic
	public static void calcTileScore(Monster m, Board b, Tile targetTile) {
		//tile where monster is currently located
		Tile currTile = b.getTile(m.getPosition().getTilex(), m.getPosition().getTiley());

		//calculate which enemy tiles are in range from the would be (WB) tile
		HashSet <Tile> wBAttackable = b.calcAttackRange(targetTile.getTilex(), targetTile.getTiley(), m.getAttackRange(), m.getOwner());
		
		//get all tiles that this monster could attack from its current tile (with enemies on them)
		HashSet<Tile> currAttackable = b.calcAttackRange(currTile.getTilex(), currTile.getTiley(), m.getAttackRange(), m.getOwner());
		
		
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
		gameState.computerEnd();
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

//////////////Yufen Attack Method/////

//if wrap all selecting target methods in another class, this is in ComputerPlayer or AI execution class?
	public void smartAttack(){
	    
	    attackerTiles = findAttacker(); //return a list of monster that can perform attack in this turn
	
	    //go through each monster to perform attack
	    for (Tile acttacker:attackerTiles){    
	        targetInRange = getTargetTiles(attacker);
	        if (selectAttackTarget(attacker) == null){  //if no good target, skip attack, check next monster
	            continue;
	        }else{    //if there is good target, perform attack 
	            target = selectAttackTarget(attacker);
	            a.unitAttack(attacker, target);
	            //events/gameplaystates/unitplaystates/AIUnitStateController
	        }
	    }
	}
//////////////////////////Yufen Attack Logic/////////////////////////
/*
new class(findingBestTarget?) function

loop through all attackable monsters can perform attack:

for each attacker:
get attackable tile range for this attacker 
(will get new list of enemy every loop, without those were killed by previous attacker), would this solve the concern that you mentioned?
call target selecting method, if nothing return, for this attacker. choose not to attack

Target Priority:
1.try kill the one with highest attackValue, if none
2.try kill the one with highest attackLeft, if none
3.try kill the one with lowest HP, if can't keill any
4.check if attacker will be killed easily in the next turn after enemy counter attack 
5.otherwise, it's not worth attack

*/






//<helper>
	private Tile selectAttackTarget(Tile t){
	    if( getHightAttackValue(t) == null){
	        getHightAttackLeft(t);
	    }else if(getHightAttackLeft(t) == null){
	        getLowHP(t);   
	    } else if ( getLowHP(t) == null){  
	        harmless(t);
	    } else if (harmless(t) == null){
	    } else {
	        return null;  
	    }
	}


//<helper>
	private Tile getHightAttackValue(Tile t){
	    
	    //from a list of t attackable tiles range, find the tile hold enemy with highest attack value, and check if it can be killed after attack.
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with highest attack times left
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getAttackValue() > chosenE.getUnitOnTile().getAttackValue()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker can kill this one, avoid future threat and counter attack
	    if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}

//<helper>
	private Tile getHightAttackLeft(Tile t){
	    
	    //from a list of t attackable tiles range, find the tile hold enemy with highest attack times left, and check if it can be killed after attack.
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with highest attack times left
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getAttacksLeft() > chosenE.getUnitOnTile().getAttacksLeft()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker can kill this one, avoid future threat and counter attack
	    if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}

//<helper>
	private Tile getLowHP(Tile t){
	    
	    //from a list of t attackable tiles range, find the tile hold enemy with lowest HP left, and check if it can be killed after attack.
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with highest attack times left
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getHP() > chosenE.getUnitOnTile().getHP()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker can kill this one, avoid counter attack
	    if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}

//<helper>
	private Tile harmless(Tile t){
	    //if couldn't find any enemy to kill, lower down the counter attack damage
	    //from a list of t attackable tiles range, find the tile hold enemy with lowest attackValue(damage value), and make sure if won't be killed after counter attack
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with least attackValue
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getAttackValue() < chosenE.getUnitOnTile().getAttackValue()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker will be killed easily in the next turn after enemy counter attack 
	    // having remain HP >=2,somewhat can avoid being first target for HumanPlayer in the next turn
	    // if set HP bar > 0, any HumanPlayer can attack in the next turn
	    if ((t.getUnitOnTile().getHP() - chosenE.getUnitOnTile().getAttackValue()) >= 2 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}


//<helper>
	private ArrayList<Tile> getTargetTiles(Tile t){
	    targetInRange = gameBoard.calcRange(t);
	    for (Tile rt : targetInRange) {
	        if (rt.getUnitOnTile().getOwner() != playerTwo) {  //tiles don't belong to computer player are enemy's
	            targetInRange.add(rt);
	        }
	    }
	    return targetInRange;
	}

//<helper>
	private ArrayList<Tile> findAttacker(){  // find a list of tiles that hold monsters can perform attack
	    attackerTiles = gameBoard.friendlyTile(playerTwo);
	    
	    for (Tile t : attackerTiles) {
	        if (t.getUnitOnTile().getAttacksLeft() == 0 ) {  // if there is no attack time left, skip this monster in attack action
	            attackerTiles.remove(t);
	        }
	        if ( getTargetTiles(t).size() == 0) {  // if there is no potential target in attackable range, skip this monster in attack action
	            attackerTiles.remove(t);
	        }
	    }
	    return attackerTiles;
	}	
}

