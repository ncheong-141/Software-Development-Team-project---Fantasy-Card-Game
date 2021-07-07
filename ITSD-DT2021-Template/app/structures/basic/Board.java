package structures.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;

//=============================Class description =============================//
// this class builds the board that the player will play on
// this class stores a 2D array of Tile objects that represent the board
// this class contains methods to access different tiles on the board
//==========================================================================//

public class Board {

	//class variables
	private Tile [][] gameBoard;
	//since the board for this version of the game is set to given constant values
	//the board lenght on the X and Y axis is represented by constant integer values
	private final int Y;
	private final int X;
	//storing references to the tile where the human and computer avatar will start for ease of access
	//note: still to be implemented, not a fundamental feature
	private Tile humanStart;
	private Tile computerStart;
	
	private int numUnitsOnBoard;
	private final int boardCapacity;

	private Monster unitSelected;

	private final int[] rangeH = {0,0,1,-1,1,-1,1,-1};
	private final int[] rangeW = {1,-1,0,0,-1,1,1,-1};

	public Board() {
		X = 9;
		Y = 5;
		this.boardCapacity = X*Y;
		this.numUnitsOnBoard = 0;
		gameBoard = new Tile[Y][X];
		for (int i = 0; i<Y; i++) {
			for (int k = 0; k<X; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(k, i);
				gameBoard [i][k].free = true; 	
				gameBoard[i][k].unitOnTile = null;
			}
		}
	}



	public Tile getHumanStart() {
		return humanStart;
	}

	public Tile getComputerStart() {
		return computerStart;
	}

	//getter method to access the 2D array of Tiles
	public Tile[][] getGameBoard() {
		return gameBoard;
	}

	public int getBoardCapacity() {
		return this.boardCapacity;
	}
	
	public void updateUnitCount(int delta) {
		this.numUnitsOnBoard += delta;
	}

	public ArrayList<Tile> getAllTilesList(){
		ArrayList<Tile> fullTileList = new ArrayList<Tile>();
		
		for (int i=0; i<gameBoard.length; i++) {
			for (int k = 0; k<gameBoard[0].length; k++) {
				fullTileList.add(gameBoard[i][k]);
			}
		}
		
		return fullTileList;
	}

	public void setGameBoard(Tile[][] gameBoard) {
		this.gameBoard = gameBoard;
	}

	public void setUnitSelected(Monster m){
		this.unitSelected = m;
	}

	public Monster getUnitSelected(){
		return this.unitSelected;
	}

	//Method to access a specific tile on the board given the X and Y coordinates
	public Tile getTile(int x, int y) {
		return gameBoard[y][x];
	}


	//=====================PLAYABLE TILES METHODS==================//

	//methods showing where player can summon monster/cast spell

	//1) This method returns a list of all tiles where a standard unit can be summoned 

	//this method can be called from elsewhere in the program
	//it returns a list of tiles where a given Player can summon a unit
	//Players clicks on a card >> this method is called >> all adjacent tiles to all player's units on the board are returned
	public ArrayList<Tile> allSummonableTiles(Player p){
		ArrayList <Tile> tileList = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if ((gameBoard[i][k].getUnitOnTile() != null)&& gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					tileList.addAll(this.calcRange(gameBoard[i][k]));
				}
			}
		}
		return tileList;
	}

	//helper method to allSummonableTiles
	//for any given tile it returns a list of tile in range
	//the range here is based on game specifications (any tile adjacent to a friendly unit)
	ArrayList<Tile> calcRange(Tile t){
		ArrayList<Tile> tileRange = this.adjTiles(t);
		tileRange.removeIf(tile -> !(tile.getFreeStatus()));
		return tileRange;
		
	}
	
	private ArrayList<Tile> adjTiles(Tile t){
		ArrayList<Tile> tileRange = new ArrayList<Tile>();
		int xPos = t.getTilex();
		int yPos = t.getTiley();

		System.out.println(xPos + " calcRange " + yPos);
		for (int i = 0; i<rangeH.length; i++) {
			if (xPos + rangeW[i] <0 || xPos + rangeW[i] > 8 || yPos + rangeH[i]<0 || yPos + rangeH[i] > 4) continue;
			else {
				
					Tile posTile = this.getTile(xPos+rangeW[i], yPos+rangeH[i]);
					//System.out.println(posTile.getTilex() + "  " + posTile.getTiley());
					tileRange.add(posTile);	
				
			}
		}
		return tileRange;
	}


	//2A)Method returns all tiles where a ENEMY unit is present (excl. avatar)
	public ArrayList<Tile> enemyTile(Player p){
		ArrayList<Tile> tileRange = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && (!(gameBoard[i][k].getUnitOnTile() instanceof Avatar)) && gameBoard[i][k].getUnitOnTile().getOwner()!=p) {
					tileRange.add(gameBoard[i][k]);
				}
			}	
		}
		return tileRange;
	}

	//2B)Method returns all tiles where a FRIENDLY unit is present (excl. avatar)
	public ArrayList<Tile> friendlyTile(Player p) {	
		ArrayList<Tile> tileRange = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null  && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					tileRange.add(gameBoard[i][k]);
				}
			}	
		}
		return tileRange;
	}


	//3)Method returns player's avatar tile position 
	public Tile ownAvatarTile (Player p) {
		Tile avatarTile = null;
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && (gameBoard[i][k].getUnitOnTile() instanceof Avatar) && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					avatarTile = gameBoard[i][k];
				}
			}	
		}
		
		return avatarTile;
	}

		

	//4) Method return enemy avatar's tile position 
	public Tile enemyAvatarTile (Player p, GameState g) { 
		if (p instanceof HumanPlayer) { 
			int x = g.getComputerAvatar().getPosition().getTilex(); int y =
			g.getComputerAvatar().getPosition().getTiley(); return this.getTile(x, y); }
	else { int x = g.getHumanAvatar().getPosition().getTilex(); int y =
			g.getHumanAvatar().getPosition().getTiley(); return this.getTile(x, y); } }

	//method returns all adijecent enemy tiles for a given position
	public ArrayList <Tile> adjEnemyTiles(int xPos, int yPos, Player p){
		ArrayList<Tile> tileRange = this.adjTiles(this.getTile(xPos, yPos));
		tileRange.removeIf(tile -> (tile.getFreeStatus()||tile.getUnitOnTile().getOwner()==p));
		return tileRange;
	}

	//================= UNIT MOVEMENTS METHODS ========================//
	
	/**
	 * alternative approach
	 * 
	 * trying to create method that does not allow for moving through enemies
	 * 	
	 */
	
	public ArrayList<Tile> moves(int xpos, int ypos, int moves){
		HashSet <Tile> tileList = new HashSet<Tile>();
		
		boolean[][][] visited = new boolean[this.Y][this.X][1];
		
		Tile startTile = this.getTile(xpos, ypos);
		
		State startState = new State(startTile, startTile.getUnitOnTile().getMovesLeft());
		
		Queue<State> queue = new LinkedList<State>();
		
		queue.add(startState);
		
		while(! queue.isEmpty()) {
			State current = queue.poll();
			if (current.moves == 0) {
				tileList.add(current.t);
				continue;
			}
			
			else {
				ArrayList<Tile> reachTiles = this.adjTiles(current.t);
				reachTiles.removeIf(tile ->!(tile.getFreeStatus()));
				for (Tile t : reachTiles) {
					if (visited[t.getTiley()][t.getTilex()][0] != true) {
						State nextState = new State(t, current.moves-1);
						queue.add(nextState);
						visited[t.getTiley()][t.getTilex()][0] = true;
					}	
				}
			}
		}
		
		ArrayList <Tile> list = new ArrayList<Tile>(tileList);
		return list;
	}
	
	//======INNNER CLASS=====//
	class State {
		int xpos, ypos, moves;
		Tile t;
		
		public State(Tile t, int moves) {
			this.moves = moves;
			this.t= t;
			this.xpos = t.getTilex();
			this.ypos = t.getTiley();
		}
	}

	//5) unitMovableTiles - this method returns a list of all tiles a selected unit can move to
	//within a given range based on the specified position
	public ArrayList<Tile> unitMovableTiles (int xpos, int ypos, int moveRange ){
		ArrayList <Tile> tileList = this.reachableTiles(xpos, ypos, moveRange);
		tileList.removeIf(t -> !(t.getFreeStatus()));

		return tileList;
	}

	//5A) this method returns all tiles a unit can reach based on position and movement range of unit
	//includes both occupied and unoccupied tiles
	private ArrayList<Tile> reachableTiles (int xpos, int ypos, int moveRange){
		ArrayList<Tile> reachTile = new ArrayList<Tile>();

		for (int i = xpos - moveRange; i <= (xpos + moveRange); i++) {

			for (int j = ypos - moveRange; j <= (ypos + moveRange); j++) {

				// Check if indices are within limits of the board
				if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) { 

					// System.out.println("i,j: " + i + "," + j);

					// Check each tile index combination is adds up to the range 
					// (abs(i -x) is the distance the current index is away from the monster position)
					if ( (Math.abs(i - xpos) + Math.abs(j - ypos)) <=moveRange) {
						reachTile.add(this.getTile(i, j));
						System.out.println(this.getTile(i,j));
					}
				}
			}  
		}
		return reachTile;
	}
	//====================ATTACK RANGE METHOD=====================//

	//6) unitAttackableTiles - returns a set of all tiles that a unit located at xpos and ypos can attack based on its attack and move range
	//the result is returned as a set to eliminate duplicate values within the set
	public ArrayList<Tile> unitAttackableTiles (int xpos, int ypos, int attackRange, int moveRange ){
		Player p = this.getTile(xpos, ypos).getUnitOnTile().getOwner();
		
		ArrayList<Tile> reachTiles;
		HashSet <Tile> tileList = new HashSet<Tile>();
		
		if (moveRange == 0) {
			ArrayList<Tile> list = new ArrayList<Tile>(this.calcAttackRange(xpos, ypos, attackRange, p));
			return list;
		}

		//get a list of all tiles that the unit can reach given their position and move range
		//this includes both free and occupied tiles
		reachTiles = this.reachableTiles(xpos, ypos, moveRange);

		

		//iterate over the list of tiles that can be reached 
		//if the tile has an enemy unit it is added to the set (no duplicate values)
		for (Tile t : reachTiles) {
			System.out.println(t + " x ");
			if (!(t.getFreeStatus()) && t.getUnitOnTile().getOwner()!=p) tileList.add(t);
		}

		//remove all occupied tiles (enemy or friendly) from reachable tiles list
		reachTiles.removeIf(t -> !(t.getFreeStatus()));		

		//the reachable tile list now only contains unoccupied tiles
		//for each of these unoccupied tiles (that the unit could move to)
		//the attack range (with that tile as origin) is calculated as a set 
		//the set is added to the set to returned (no duplicated values)
		for(Tile t : reachTiles) {
			System.out.println(t);
			HashSet <Tile> attRange = calcAttackRange(t.getTilex(), t.getTiley(), attackRange, p);
			tileList.addAll(attRange);

		}	
		ArrayList<Tile> list = new ArrayList<Tile>(tileList);
		return list;	
	}		  
	//6A) 
	public HashSet<Tile> calcAttackRange(int xpos, int ypos, int attackRange, Player p){
		HashSet<Tile> tileList = new HashSet<Tile>();

		System.out.println(xpos + " --- " + ypos);


		for (int i = xpos - attackRange; i <= (xpos + attackRange); i++) {

			for (int j = ypos - attackRange; j <= (ypos + attackRange); j++) {

				// Check if indices are within limits of the board
				if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) { 

					// System.out.println("i,j: " + i + "," + j);

					if ((i==xpos || j==ypos) && this.getTile(i, j).getUnitOnTile() != null && this.getTile(i, j).getUnitOnTile().getOwner() != p) {
						tileList.add(this.getTile(i, j));
						System.out.println(this.getTile(i,j)+ " o ");
					}

					else if ((Math.abs(i-xpos) == Math.abs(j - ypos))&&this.getTile(i, j).getUnitOnTile() != null && this.getTile(i, j).getUnitOnTile().getOwner() != p) {
						tileList.add(this.getTile(i, j));
						System.out.println(this.getTile(i,j)+ " p ");
					}
				}
			}
		}	
		return tileList;
	}
	//===============accessors methods==========================//

	//this method returns a list of all monsters (including avatars) on the board which have onCooldow == true
	//this variable signal that the monster cannot attack/move in the current turn (right after summoning)


	public ArrayList<Monster> coolDownCheck (Player p){
		ArrayList<Monster> monsterList = new ArrayList<Monster>();

		for (int i = 0; i <gameBoard.length; i++) {

			for (int k =0; k<gameBoard[0].length; k++) {
				
				if ((gameBoard[i][k].getUnitOnTile() != null)&& gameBoard[i][k].getUnitOnTile().getOnCooldown() && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					monsterList.add(this.gameBoard[i][k].getUnitOnTile()); 
				}
				 
			}
		}
		return monsterList;

	}
		
		public ArrayList<Monster> friendlyUnitList (Player p){
			ArrayList<Monster> monsterList = new ArrayList<Monster>();
			for (int i = 0; i <gameBoard.length; i++) {
				for (int k =0; k<gameBoard[0].length; k++) {
					if (gameBoard[i][k].getUnitOnTile() != null && gameBoard[i][k].getUnitOnTile().getOwner()==p) 							{
						monsterList.add(gameBoard[i][k].getUnitOnTile());
					}
				}	
			}
			return monsterList;
		}
	
	public ArrayList<Tile> allFreeTiles(){
		ArrayList<Tile> freeTilesList = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() == null) 							{
					freeTilesList.add(gameBoard[i][k]);
				}
			}	
		}
		return freeTilesList;
	}
	
	
}

	// To do:
	
	// allSummonableTiles() will probably need a change (once NV decide)
	
	// Think about stopping movement through enemy units
	
	