package structures.basic.ComputerLogic;
import java.util.ArrayList;

import events.gameplaystates.*;
import events.gameplaystates.unitplaystates.*;
import structures.GameState;
import structures.basic.*;
public class ComputerAttackMonsterLogic {
	
	private Board gameBoard;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	private ArrayList<Tile> attackerTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;  //a list of attackable enemy for each attacker
	private Tile target; //attack target
	private Tile attacker; 
//	private UnitAttackActionState attackStart;

	public ComputerAttackMonsterLogic(ComputerPlayer p) {
		this.playerTwo = p;
		this.gameBoard = p.getGameBoard();
	}
	
	public void smartAttack(){
		    
		attackerTiles = findAttacker(); //return a list of monster that can perform attack in this turn
		
		    //go through each monster to perform attack
		for (Tile attacker:attackerTiles){    
			targetInRange = getTargetTiles(attacker);
			if (selectAttackTarget(attacker) == null){  //if no good target, skip attack, check next monster
				continue;
			}else{    //if there is good target, perform attack 
				target = selectAttackTarget(attacker);
				
				//actual attack action from here
				//need unit animation stuff
				//every attack is successful under this logic

				System.out.println("Attack successful.");
				
			}        
		}
	}
	//////////////////////////Yufen Attack Logic/////////////////////////
	/*

	loop through all attackable monsters can perform attack:

	for each attacker:
	get attackable tile range for this attacker 
	(will get new list of enemy every loop(attacker monster), without those were killed by previous attacker)
	call target selecting method,
			Target Selection Priority:
				1.try kill the one with highest attackValue, if none
				2.try kill the one with highest attackLeft, if none
				3.try kill the one with lowest HP, if can't kill any still
				4.return null

	 if no target return after selection, for this attacker. choose not to attack
	 go on to next actionable attacker
	*/


	//<helper> target selection process
	public Tile selectAttackTarget(Tile t){		    
		if( getHightAttackValue(t) != null){
			return target;
		}else if(getHightAttackLeft(t) != null){
			return target;   
		}else if ( getLowHP(t) != null){  
			return target;
		} else {
			return null;
		}
	}


	//<helper> select target in range with high damage, and can be killed
	private Tile getHightAttackValue(Tile t){
		    
		//from a list of t attackable tiles range, find the tile hold enemy with highest attack value, and check if it can be killed after attack.
		Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
		for (int i = 1; i < targetInRange.size(); i++) {  ////loop through targetInRange, find the enemy with highest attack times left
			if(targetInRange.get(i).getUnitOnTile().getAttackValue() > chosenE.getUnitOnTile().getAttackValue()) {  
				chosenE = targetInRange.get(i);  
			}	
		}
		if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  //// check if attacker can kill this one, avoid future threat and counter attack
			return chosenE;
		}else { 
			return null;
		} 
	}

	//<helper> select target in range with more times to attack, and can be killed
	private Tile getHightAttackLeft(Tile t){
		    
		 //from a list of t attackable tiles range, find the tile hold enemy with highest attack times left, and check if it can be killed after attack.
		Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
		for (int i = 1; i < targetInRange.size(); i++) {  //loop through targetInRange, find the enemy with highest attack times left
			if(targetInRange.get(i).getUnitOnTile().getAttacksLeft() > chosenE.getUnitOnTile().getAttacksLeft()) {  
				chosenE = targetInRange.get(i);  
			}	
		}
		if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  //// check if attacker can kill this one, avoid future threat and counter attack
			return chosenE;
		}else { 
			return null;
		} 
	}

	//<helper> select any target in range with lowest HP, can be killed
	private Tile getLowHP(Tile t){
		    
		//from a list of t attackable tiles range, find the tile hold enemy with lowest HP left, and check if it can be killed after attack.
		Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
		for (int i = 1; i < targetInRange.size(); i++) {  ////loop through targetInRange, find the enemy with highest attack times left
			if(targetInRange.get(i).getUnitOnTile().getHP() > chosenE.getUnitOnTile().getHP()) {  
				chosenE = targetInRange.get(i);  
			}	
		}
		if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  //// check if attacker can kill this one, avoid counter attack
			return chosenE;
		}else { 
			return null; 
		}
	}

//	//<helper> this increase more work, back up for counter attack
//		public Tile harmless(Tile t){
//		    //if couldn't find any enemy to kill, lower down the counter attack damage
//		    //from a list of t attackable tiles range, find the tile hold enemy with lowest attackValue(damage value), and make sure if won't be killed after counter attack
//		    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
//		
//		    //loop through targetInRange, find the enemy with least attackValue
//		    for (int i = 1; i < targetInRange.size(); i++) {  
//		        if(targetInRange.get(i).getUnitOnTile().getAttackValue() < chosenE.getUnitOnTile().getAttackValue()) {  
//		            chosenE = targetInRange.get(i);  
//		        }	
//		    }
//		    // check if attacker will be killed easily in the next turn after enemy counter attack 
//		    // having remain HP >=2,somewhat can avoid being first target for HumanPlayer in the next turn
//		    // if set HP bar > 0, any HumanPlayer can attack in the next turn
//		    if ((t.getUnitOnTile().getHP() - chosenE.getUnitOnTile().getAttackValue()) >= 2 ){  
//		        return chosenE;
//		    }else { 
//		        return null;
//		    } 
//		}


	//<helper>
		public ArrayList<Tile> getTargetTiles(Tile t){
		    targetInRange = gameBoard.unitAttackableTiles(t.getXpos(),t.getYpos(), t.getUnitOnTile().getAttackRange(), t.getUnitOnTile().getMovesLeft());
		    for (Tile rt : targetInRange) {
		        if (rt.getUnitOnTile().getOwner() != playerTwo) {  //tiles don't belong to computer player are enemy's
		            targetInRange.add(rt);
		        }
		    }
		    return targetInRange;
		}

	//<helper>
		public ArrayList<Tile> findAttacker(){  // find a list of tiles that hold monsters can perform attack
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
