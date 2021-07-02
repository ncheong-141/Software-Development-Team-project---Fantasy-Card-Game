package structures.basic;
import commands.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.checkerframework.checker.units.qual.m;
import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import structures.GameState;
import events.*;
import structures.basic.*;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import events.tileplaystates.*;
public class ComputerPlayer extends Player {
	
	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	private Board gameBoard;
	private ArrayList<Tile> doAttackTile; // for back up methods
	private ArrayList<Tile> beAttackTile; // for back up methods
	private  ArrayList<Tile> attackRange;
	private ArrayList<Monster> doAttackMonster;
	private ArrayList<Monster> beAttackMonster;
	private Monster actorM;
	private Monster enemyM;
	
	public ComputerPlayer(Deck deck) {
		super(deck);
	}

	// after all actions, ComputerPlayer call computerEndTurn() to ends the turn
	@Override
	public void endTurn() {
		gameState.computerEnd();
	}


//	@Override  give 3 card in the first round from deckTwo	
//	public void firstThreeCards() {
//		// TODO Auto-generated method stub
//		super.firstThreeCards();
//	}

//	@Override assign deckOne for HumanPlayer
//	public void setDeck(ArrayList<Card> d) {
//		// TODO Auto-generated method stub
//		this.d = deck.deckTwo(); // need Deck to return deckTwo list
//	}
	
	//** get a list of tiles that hold AI monster  which can still perform attack
	
	//get ramdon AI monster to attack enemy randomly 

	public void randomAttack() {
		this.actorM = this.getRandomActorM();        //set chosen actorM
		this.enemyM = this.getRandomEnemyM(actorM);  // set chosen enemyM
		
		
		//=========attack action begin========
		
		//sort out attackLeft
		int attackLeft = actorM.getAttacksLeft()- 1; // set remain attack value	
		if(attackLeft == 0) { //if no attack left, cool monster
			actorM.toggleCooldown();
		}
		//need animation
		
		//sort out HP left
		int hpLeft = enemyM.getHP();
		//need animation?
		
	}
	
	
	
	//helper method
	private Monster getRandomActorM() {
		actorM = this.getRandomMonster(doAttackMonster); // get a random actoRM
		return actorM;
	}	
	//helper method	
	private Monster getRandomEnemyM(Monster m) { //given a actor monster, pick random target
		this.actorM = m;
		beAttackMonster = findEnemyForEachMonster(this.actorM); //get list of enemy for actorM
		enemyM = this.getRandomMonster(beAttackMonster); // get one in list randomly
		return enemyM;
	}
	
	
	public Monster getRandomMonster(ArrayList<Monster> toRandom) {
		int index = (int) Math.random() * ( toRandom.size() - 0 );
		return toRandom.get(index);
	}
	

	
	//======================= work with Monster ============
	//** get a list of AI monster that can still attack in its turn
	public ArrayList<Monster> doAttackMonster(){
		doAttackMonster = gameBoard.friendlyUnitList(playerOne);
		for (Monster fM : doAttackMonster) {
			if (fM.getAttacksLeft() == 0 ) {
				doAttackMonster.remove(fM);
			}			
		}
		return doAttackMonster;
	}	
	
	
	//for each doAttack monster, calcRange, list targetMonster
	public ArrayList<Monster> findEnemyForEachMonster(Monster m){
		
		ArrayList<Tile> rangeForEachMonster = gameBoard.calcRange(gameState.locateMonster(m));
		beAttackMonster = new ArrayList<Monster>();
		for (Tile rfT: rangeForEachMonster) {   		 // in the tilerange of each AI monster
			for (Tile eT: beAttackTile) {				 
				if(rangeForEachMonster.contains(eT)) {	 // check if enemy monster fall into the range
					Monster eM = eT.getUnitOnTile();	 
					beAttackMonster.add(eM);			 // collect attackable enemy monster for each AI monster
				}
			}
		}
		return beAttackMonster;
	}
	
	
	//get a list of attackable enemy monster on board
	/*
	 * public ArrayList<Monster> findAllEnemyToAttack() {
	 * 
	 * ArrayList<Monster> targetEnemy = new ArrayList<Monster>(); for (Tile fT :
	 * doAttackTile) { ArrayList<Tile> doAttackTile = gameBoard.calcRange(fT);
	 * //make board.calcRange(fT) for (Tile eT :beAttackTile) {
	 * if(doAttackTile.contains(eT) && (!targetEnemy.contains(eT))) {
	 * targetEnemy.add(eT.getUnitOnTile()); } } } return targetEnemy; }
	 */
	
	
	
	
	
	//=======================back up======================
	//** get a list of attackable range for "all" AI monsters, could be used to find out which enemy monster can be attacked
	public ArrayList<Tile> attackableRange(){
		this.attackRange = new ArrayList<Tile>();
	 	
	 	//below is to find out the attack range of each AI monster
	 	for (Tile calcT : doAttackTile) {	
	 	// ask this to be public in Board ->	private ArrayList<Tile> calcRange(Tile t)
	 	// board.calcRange()
	 		ArrayList<Tile> eachRange =  calcT.calcRange(gameBoard.getTile(calcT.unitOnTile.getPosition().getTilex(),calcT.unitOnTile.getPosition().getTilex()));
	 		//after getting attack range for each monster, add in board attackable tile range
	 		for (Tile addT : eachRange) {
	 			if(!attackRange.contains(addT)) {
	 				attackRange.add(addT);
	 			}
	 		}
	 	}
	 	return attackRange;
	}
	//========================back up=======================
	
	//==================== working with Tile ======back up========
	public ArrayList<Tile> doActtackTile(){

		this.doAttackTile = gameBoard.friendlyTile(playerTwo);
		
		//find monster still can attack
		for (Tile fT : doAttackTile) {
			if (fT.getUnitOnTile().attacksLeft == 0) {
				doAttackTile.remove(fT);
			}			
		}
		return doAttackTile;
	}
	
	//** get a list of tiles that hold AI monster  which can still perform attack
	public ArrayList<Tile> beActtackTile(){
		this.beAttackTile = gameBoard.friendlyTile(playerOne);
		//find tiles hold all enemy monster on gameboard
		//for possible further required game logic, do this as a method for now
		return beAttackTile;
	}
	//==================== working with Tile ======back up========
}
	
