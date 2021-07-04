package structures.basic;

import java.util.ArrayList;
import structures.GameState;
public class ComputerPlayer extends Player {
	
	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	private Board gameBoard;
	private ArrayList<Tile> attackTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;
	private ArrayList<Tile> attackRange;

//	private Tile actorT; //for random back up code
//	private Tile enemyT; //for random back up code
	
	public ComputerPlayer() {
		super();
	}


	
//////////////////////////////////////////////////////////**Ranking Methods**/////////////////////////////////////////////////////////	
	
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
		attackTiles = gameBoard.friendlyTile(playerTwo);
		
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
	@Override
	public void endTurn() {
		gameState.computerEnd();
	}

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

	
	



}
	
