package structures.basic;
import structures.basic.ComputerLogic.*;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * 
 * @author Chiara Pascucci and Yufen Chen
 * This class represents the computer player
 * It instantiates the different computer logic objects:
 * 	- ComputerPlayCardsLogic
 * 	- ComputerAttackMonsterLogic
 * 	- ComputerMoveMonsterLogic
 * 
 * it calls methods through those objects to compute its moves
 *
 */


public class ComputerPlayer extends Player {

	//this variable represents the health level that the computer player will check against
	//to decide whether to favour aggressive move or defensive moves
	private int hPBenchMark;
	
	//constructor
	public ComputerPlayer() {
		super(); 
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
	
	//this method returns the full list of cards + target tile for summoning a card on a tile
	public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> playCards(Board gameBoard){
		ComputerPlayCardsLogic play = new ComputerPlayCardsLogic(this);
		
		return play.playCards(gameBoard);
	}
	
	//this method returns the full list of monster + target tile for moving the moster
	public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> moveMonsters(Board gameBoard){
		ComputerMoveMonsterLogic move = new ComputerMoveMonsterLogic(this);
		return move.movesUnits(gameBoard);
	}
	
	//this method returns the full list of monster + target (enemy) tile
	public ArrayList <structures.basic.ComputerLogic.ComputerInstruction> performAttacks(Board gameBoard){
		ComputerAttackMonsterLogic attack = new ComputerAttackMonsterLogic (this);
		return attack.computerAttacks(gameBoard);
	}
		
	
}


