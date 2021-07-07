package structures.basic.ComputerLogic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import structures.GameState;
import structures.basic.*;
import structures.basic.ComputerLogic.ComputerMoveMonsterLogic.MonsterTileOption;
public class ComputerAttackMonsterLogic {
	private Board gameBoard;
	private static int killMod = 2;
	private static int isAvatar = 2;
	private static int hasSpecialAbility = 1;
	private ComputerPlayer player;
	private ArrayList<Tile> attackerTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;  //a list of attackable enemy for each attacker
	private Tile target; //attack target

	public ComputerAttackMonsterLogic(ComputerPlayer p) {
		this.player = p;
		this.gameBoard = p.getGameBoard();
	}
	
	public ArrayList <ComputerInstruction> computerAttacks(){
		ArrayList <ComputerInstruction> list = new ArrayList<ComputerInstruction>();
		
		ArrayList <Monster> monstersThatCanAttack = this.monstersThatCanAttack();
		
		if (monstersThatCanAttack.isEmpty()) return list;
		
		MonsterTargetOption [] monstersAndTheirListOfTargets = this.getMonstersPossTargets(monstersThatCanAttack, gameBoard);
		
		list = this.matchMonsterAndTarget(monstersAndTheirListOfTargets);
		
		return list;
		
	}
	
	private ArrayList<Monster> monstersThatCanAttack(){
		ArrayList <Monster> list = gameBoard.friendlyUnitList(player);
		list.removeIf(m->(m.getAttacksLeft() <=0 || m.getOnCooldown()));
		return list;		
	}
	
	private MonsterTargetOption [] getMonstersPossTargets(ArrayList<Monster> monstersThatCanAttackList, Board b){
		
		
		if(monstersThatCanAttackList.isEmpty()) return null;
		
		MonsterTargetOption [] list = new MonsterTargetOption[monstersThatCanAttackList.size()-1];
		
		int i = 0;
		for (Monster m : monstersThatCanAttackList) {
			
			MonsterTargetOption attackTargets = new MonsterTargetOption(m, this.gameBoard);
			
			if (attackTargets.getList().isEmpty()) continue;
			
			else {
				list[i] = attackTargets;
				i++;
			}
			
		}
		
		return list;
	}
	
	private ArrayList<ComputerInstruction> matchMonsterAndTarget(MonsterTargetOption[] targOptsList){
		ArrayList<ComputerInstruction> list = new ArrayList<ComputerInstruction>();
		
		if (targOptsList == null) return list;
		
		Arrays.sort(targOptsList);
		
		HashSet <Tile> targets = new HashSet<Tile>();
		
		int k = 0;
		for (int i = 0; i<targOptsList.length; i++) {
			
			Tile targTile = targOptsList[i].getList().get(k);
			
			if (!targets.contains(targTile)) {
				ComputerInstruction inst = new ComputerInstruction (targOptsList[i].getM(), targTile);
				list.add(inst);
				targets.add(targTile);
				continue;
			}
			
			else {
				do {
					
					k++;
					targTile = targOptsList[i].getList().get(k);
					
				}while(targets.contains(targTile) && k < targOptsList[i].getList().size());
				
				if (!targets.contains(targTile)) {
					ComputerInstruction inst = new ComputerInstruction (targOptsList[i].getM(), targTile);
					list.add(inst);
					targets.add(targTile);
				}
				
				k=0;
			}
			
		}
		
		return list;
	}
	
	public static void calcTileAttackScore(Monster m, Board b, Tile targetTile) {
		//tile where monster is currently located
		Tile currTile = b.getTile(m.getPosition().getTilex(), m.getPosition().getTiley());
		Monster enemy = targetTile.getUnitOnTile();
		
		int score = 0;
		
		if (enemy instanceof Avatar) score += isAvatar;
		if(enemy.getHP() <= m.getAttackValue()) score += killMod;
		if(enemy.hasAbility()) score += hasSpecialAbility;
		
		targetTile.setScore(score);
	}
	
	
	static class MonsterTargetOption implements Comparable<MonsterTileOption> {
		Monster m; 
		Board b;
		ArrayList<Tile> list;
		int score;
		MonsterTargetOption(Monster m, Board b){
			this.m = m;
			this.b =b;
			list = b.unitAttackableTiles(m.getPosition().getTilex(), m.getPosition().getTiley(), m.getAttackRange(), m.getMovesLeft());
			if (!list.isEmpty()) {
				this.checkValidTargets();
				this.scoreTileList();
				Collections.sort(list);
				this.score = list.get(0).getScore();
			}
			
		}
		
		private void checkValidTargets() {
			list.removeIf(tile -> (tile.getUnitOnTile().getAttackValue() >= m.getHP()));
		}
		public void scoreTileList() {
			for (Tile t : list) {
				calcTileAttackScore(m, b, t);
			}
		}
		
		public Monster getM() {
			return m;
		}
		
		public ArrayList<Tile> getList(){
			return this.list;
		}
		
		public int getScore() {
			return this.score;
		}
		

		@Override
		public int compareTo(MonsterTileOption o) {
		//same approach as in move logic, trying to order from highest to lowest score
			if (this.score > o.getScore()) return -1;
			else if (this.score < o.getScore()) return 1;
			else return 0;
		}
	}
	//////////////Yufen Attack Method/////

	//if wrap all selecting target methods in another class, this is in ComputerPlayer or AI execution class?
	public void smartAttack(){
		    
		attackerTiles = findAttacker(); //return a list of monster that can perform attack in this turn
		
		    //go through each monster to perform attack
		for (Tile attacker:attackerTiles){    
			targetInRange = getTargetTiles(attacker);
			if (selectAttackTarget(attacker) == null){  //if no good target, skip attack, check next monster
				continue;
			}else{    //if there is good target, perform attack 
				target = selectAttackTarget(attacker);
			//a.unitAttack(attacker, target);
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
	public Tile selectAttackTarget(Tile t){
		    
			
		if( getHightAttackValue(t) != null){
			return target;
		}else if(getHightAttackLeft(t) != null){
			return target;   
		}else if ( getLowHP(t) != null){  
			return target;
		}
		else if (harmless(t) != null){
			return target;
		} else {
			return null;
		}
	}


	//<helper>
	public Tile getHightAttackValue(Tile t){
		    
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
	public Tile getHightAttackLeft(Tile t){
		    
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
		public Tile getLowHP(Tile t){
		    
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
		public Tile harmless(Tile t){
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
		public ArrayList<Tile> getTargetTiles(Tile t){
		    targetInRange = gameBoard.unitAttackableTiles(t.getXpos(),t.getYpos(), t.getUnitOnTile().getAttackRange(), t.getUnitOnTile().getMovesLeft());
		    for (Tile rt : targetInRange) {
		        if (rt.getUnitOnTile().getOwner() != player) {  //tiles don't belong to computer player are enemy's
		            targetInRange.add(rt);
		        }
		    }
		    return targetInRange;
		}

	//<helper>
		public ArrayList<Tile> findAttacker(){  // find a list of tiles that hold monsters can perform attack
		    attackerTiles = gameBoard.friendlyTile(player);
		    
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
