package structures.basic;
import structures.basic.ComputerLogic.*;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


import structures.GameState;
public class ComputerPlayer extends Player {
	
	private HumanPlayer playerOne;
	private GameState gameState;
	private ArrayList<Tile> attackTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;
	private ArrayList<Tile> attackRange;
	private int hPBenchMark;


//	private Tile actorT; //for random back up code
//	private Tile enemyT; //for random back up code

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

//===========choose by Highest currentHP =============
	public Tile selectAttackerByHP(){
		Tile chosenA = attackTiles.get(0);    //set compared chosenA as the first tile in attackTiles
		for (int i = 1; i < attackTiles.size(); i++) {  //loop through attackTiles
			if(attackTiles.get(i).getUnitOnTile().getHP() > chosenA.getUnitOnTile().getHP()) {  //get loaded monster, if the monster has higher current HP, 
				chosenA = attackTiles.get(i);  //make it  as chosenA to a tile hold attacker
			}	
		}
		return chosenA;
	}

//===========choose by Highest attackValue(HP damage) ============
	public Tile selectAttackerByDamage(){
		Tile chosenA = attackTiles.get(0);    //set compared chosenA as the first tile in attackTiles
		for (int i = 1; i < attackTiles.size(); i++) {  //loop through attackTiles
			if(attackTiles.get(i).getUnitOnTile().getAttackValue() > chosenA.getUnitOnTile().getAttackValue()){ //get loaded monster, if the monster has higher damageHP value, 
				chosenA = attackTiles.get(i); //make it  as chosenA to a tile hold attacker
			}	
		}
		return chosenA;
	}	
//===========choose by Highest attackTimes =============
	public Tile selectAttackerByTimes(){
		Tile chosenA = attackTiles.get(0);    //set compared chosenA as the first tile in attackTiles
		for (int i = 1; i < attackTiles.size(); i++) {  //loop through attackTiles
			if(attackTiles.get(i).getUnitOnTile().getAttacksLeft() > chosenA.getUnitOnTile().getAttacksLeft()){ //get loaded monster, if the monster has higher attack times currently, 
				chosenA = attackTiles.get(i); //make it  as chosenA to a tile hold attacker
			}	
		}
		return chosenA;
	}	
	
//===============choose by Lowest HP========================	
	public Tile selectEnemyByHP(){
		Tile chosenE = targetInRange.get(0); //set compared chosenE as the first Tile in targetInRange
		for (int i = 1; i < targetInRange.size(); i++) {  //loop through targetInRange
			if(targetInRange.get(i).getUnitOnTile().getHP() < chosenE.getUnitOnTile().getHP()) {   //get loaded monster, if the enemy has lower current HP, 
				chosenE = targetInRange.get(i);  //make it  as chosenE to a tile hold enemy target
			}	
		}
		return chosenE;
	}	
	
//////////////////////////////////////////////////////////**ArrayList Generator Method**/////////////////////////////////////////////////////////		
	 
//=============find attack target in range for current gameOwner's=======
//helper method for intellectual ComputerPlayer behaviour, possibly reused to predict HumanPlayer action
	public ArrayList<Tile> getTargetTiles(Tile t){
		ArrayList<Tile> targetInRange = gameBoard.calcRange(t);
		for (Tile rt : targetInRange) {
			if (rt.getUnitOnTile().owner != gameState.getTurnOwner()) {
				targetInRange.add(rt);
			}
		}
		return targetInRange;
	}


//======get a list of Computer monster that can perform attack this turn============
//get rid of those have no attack count left
//get rid of those have nothing to attack
	public ArrayList<Tile> findAttacker(){
		attackTiles = gameBoard.friendlyTile(this);
		
		for (Tile fT : attackTiles) {
			attackRange = gameBoard.calcRange(fT);
			if (fT.getUnitOnTile().getAttacksLeft() == 0 ) {
				attackRange.remove(fT);
			}
			if ( getTargetTiles(fT).size() == 0) {
				attackRange.remove(fT);
			}
		}
		return attackRange;
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

////	==================randomAttack=====back up=====================
//
//	public void randomAttack() {
//		this.actorT = this.getRandomActorT();        //set chosen actorM
//		this.enemyT = this.getRandomEnemyT(actorT);  // set chosen enemyM
//		
//		
//		//=========attack action begin========
//		
//		//sort out attackLeft
//		int attackLeft = actorT.getUnitOnTile().getAttacksLeft()- 1; // set remain attack value	
//		if(attackLeft == 0) { //if no attack left, cool monster
//			actorT.getUnitOnTile().toggleCooldown();
//		}
//		//need animation
//		
//		//sort out HP left
//		int hpLeft = enemyT.getUnitOnTile().getHP();
//		//need animation?
//		
//	}
//		
//	//helper method
//	private Tile getRandomActorT() {
//		actorT = this.getRandomTile(attackTiles); // get a random actoRM
//		return actorT;
//	}	
//	//helper method	
//	private Tile getRandomEnemyT(Tile t) { //given a actor monster, pick random target
//		targetInRange = getTargetTiles(t); //get list of enemy for actorM
//		enemyT = this.getRandomTile(targetInRange); // get one in list randomly
//		return enemyT;
//	}
//	
//	public Tile getRandomTile(ArrayList<Tile> toRandom) {
//		int index = (int) Math.random() * ( toRandom.size() - 0 );
//		return toRandom.get(index);
//	}
////	================randomAttack=====back up=================	
// To do:

