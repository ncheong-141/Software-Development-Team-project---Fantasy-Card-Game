package structures.basic.ComputerLogic;

import java.util.ArrayList;
import java.util.Collections;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Monster;
import structures.basic.Tile;

/**
 * 
 * @author Chiara Pascucci and Yufen Checn
 *	this class represents the options that a monster has for attacking
 *	it handles the logic to calculate all valid target for any given monster
 *	it also calculates the score of each specific target
 *	
 */
public class MonsterTargetOtpion implements Comparable <MonsterTargetOtpion> {
	//monster to perfom the attacl
	Monster m; 
	//list of possible targets (saved as tile objects)
	ArrayList<Tile> list;
	//score of the overall obj, set to equal the highest scoring tile in the list
	int score;
	//scoring criteria for targets, made static as they should be shared (the same) for all the instances of this class
	private static int killMod = 2;
	private static int isAvatar = 2;
	private static int hasSpecialAbility = 1;
	
	public MonsterTargetOtpion(Monster m, Board b){
		this.m = m;
		list = b.unitAttackableTiles(m.getPosition().getTilex(), m.getPosition().getTiley(), m.getAttackRange(), m.getMovesLeft());
		
		if (list == null || list.isEmpty() || list.size() == 0) {
			//System.out.println("no available attackable tiles for this monster " + m.getName());
			this.score = -1;
		}
		
		else {
			this.checkValidTargets();
			this.scoreTileList(b);
			if (list.size() == 0) {
				//System.out.println("no available attackable tiles for this monster " + m.getName());
				this.score = -1;
			}
			else{Collections.sort(list);
				this.score = list.get(0).getScore();
				//System.out.println("this monster top scoring tile is: " + list.get(0) + "with score: " + list.get(0).getScore() + " [in MTO constr line 29]");
			}
		}
	}
	
	private void checkValidTargets() {
		list.removeIf(tile -> (tile.getUnitOnTile().getAttackValue() >= m.getHP()));
	}
	public void scoreTileList(Board b) {
		//System.out.println("this monster can attack those tiles:");
		for (Tile t : list) {
			calcTileAttackScore(m, b, t);
			
			//System.out.println("tile: " + t + " with score: " + t.getScore());	
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
	
	private static void calcTileAttackScore(Monster m, Board b, Tile targetTile) {
	
		Monster enemy = targetTile.getUnitOnTile();
		
		int score = 0;
		
		if (enemy.getClass() == Avatar.class) score += isAvatar;
		if(enemy.getHP() <= m.getAttackValue()) score += killMod;
		if(enemy.hasAbility()) score += hasSpecialAbility;
		
		targetTile.setScore(score);
	}

	@Override
	public int compareTo(MonsterTargetOtpion o) {
		if (this.score > o.getScore()) return -1;
		else if (this.score < o.getScore()) return 1;
		else return 0;
	}

	
	public String toString() {
		return "MTO obj: monster: " + m.getName() + " score of obj: " + this.score;
	}

}
