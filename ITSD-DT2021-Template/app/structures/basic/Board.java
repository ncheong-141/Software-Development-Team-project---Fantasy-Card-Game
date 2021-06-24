package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
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

		private final int[] rangeH = {0,0,1,-1,1,-1,1,-1};
		private final int[] rangeW = {1,-1,0,0,-1,1,1,-1};
		
		public Board() {
			X = 9;
			Y = 5;
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

		public void setGameBoard(Tile[][] gameBoard) {
			this.gameBoard = gameBoard;
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
			private ArrayList<Tile> calcRange(Tile t){
				ArrayList<Tile> tileRange = new ArrayList<Tile>();
				int xPos = t.getTilex();
				int yPos = t.getTiley();
				
				System.out.println(xPos + " calcRange " + yPos);
				for (int i = 0; i<rangeH.length; i++) {
					if (xPos + rangeW[i] <0 || xPos + rangeW[i] > 8 || yPos + rangeH[i]<0 || yPos + rangeH[i] > 4) continue;
					else {
						if (this.getTile(xPos+rangeW[i], yPos+rangeH[i]).getFreeStatus()) {
							Tile posTile = this.getTile(xPos+rangeW[i], yPos+rangeH[i]);
							//System.out.println(posTile.getTilex() + "  " + posTile.getTiley());
							tileRange.add(posTile);	
						}
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
						if (gameBoard[i][k].getUnitOnTile() != null && (!(gameBoard[i][k].getUnitOnTile() instanceof Avatar)) && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
							tileRange.add(gameBoard[i][k]);
						}
					}	
				}
				return tileRange;
			}

			
			  //3)Method returns player's avatar tile position 
			public Tile ownAvatarTile (Player p) {
			  
			  int x = p.getAvatar().getPosition().getTilex(); int y =
			  p.getAvatar().getPosition().getTiley();
			  
			  return this.getTile(x, y); }
			  
			  //4) Method return enemy avatar's tile position 
			  public Tile enemyAvatarTile (Player p, GameState g) { if (p instanceof HumanPlayer) { int x =
			  g.getComputerAvatar().getPosition().getTilex(); int y =
			  g.getComputerAvatar().getPosition().getTiley(); return this.getTile(x, y); }
			  else { int x = g.getHumanAvatar().getPosition().getTilex(); int y =
			  g.getHumanAvatar().getPosition().getTiley(); return this.getTile(x, y); } }
			 
			  
			  //================= UNIT MOVEMENTS METHODS ========================//
			  
			  //5) standard movement range. This method returns an array list of
			  //all the tiles that a selected monster can move to
			  
			  public ArrayList<Tile> monsterMovableTiles(int xPos, int yPos, int range){
				  ArrayList <Tile> tileList = new ArrayList<Tile>();
				  Tile tile = this.getTile(xPos, yPos);			  
				  if (range == 0) return tileList;
				  else if (range == 1) {
					  tileList.addAll(this.calcRange(tile)); 
					  return tileList;
				  }
				  else if (range == 2) {
					  tileList.addAll(this.monsterMovableTiles(xPos, yPos));
				  }
				  
				  return tileList;
			  }
			  
			  private ArrayList<Tile> monsterMovableTiles(int xPos, int yPos){
				  ArrayList <Tile> tileList = new ArrayList<Tile>();
				  Tile monsterPos = this.getTile(xPos, yPos);
				  tileList.addAll(calcRange(monsterPos));
				  for (int i = -2; i<=2; i += 4) {
					  System.out.print("exec loop");
					  if (xPos+i >= 0 && xPos+i<9) {
						  if (this.getTile(xPos+i, yPos).getFreeStatus()) tileList.add(this.getTile(xPos+i,  yPos));
						  System.out.println(this.getTile(xPos+i, yPos));
					  }
					  if (yPos+i>=0 && yPos+i < 9) {
						  if (this.getTile(xPos, yPos+i).getFreeStatus()) tileList.add(this.getTile(xPos, yPos+i));
						  System.out.println(this.getTile(xPos, yPos+i));
					  }
				  }
				  
				  return tileList;
			  }
			
			  
			  //====================ATTACK RANGES METHODS=====================//
			  
			  //this method returns all "attackable" tiles. 
			  //attackable tiles are all adjacent tiles to specified tiles which contain an enemy unit
			  //the method returns an array list of tiles that meet this criteria
			  //the method takes the X position and Y position of the where the attacking monster is located
			  public ArrayList<Tile> attackableAdjTiles(int xPos, int yPos){
				  Tile tile = this.getTile(xPos, yPos);
				  ArrayList <Tile> tileList = new ArrayList<Tile>();
				  
				  tileList.addAll(this.calcRangeEnemy(tile));
				  
				  return tileList;
				  
			  }
			  
			  //helper method 
			  private ArrayList<Tile> calcRangeEnemy(Tile t){
					ArrayList<Tile> tileRange = new ArrayList<Tile>();
					int xPos = t.getTilex();
					int yPos = t.getTiley();
					
					Monster monster = t.getUnitOnTile();
					Player owner = monster.getOwner();
					
					System.out.println(xPos + " calcRange " + yPos);
					for (int i = 0; i<rangeH.length; i++) {
						if (xPos + rangeW[i] <0 || xPos + rangeW[i] > 8 || yPos + rangeH[i]<0 || yPos + rangeH[i] > 4) continue;
						else {
							if (!(this.getTile(xPos+rangeW[i], yPos+rangeH[i]).getFreeStatus()) && this.getTile(xPos+rangeW[i],  yPos+rangeH[i]).getUnitOnTile().getOwner()!=owner) {
								Tile posTile = this.getTile(xPos+rangeW[i], yPos+rangeH[i]);
								//System.out.println(posTile.getTilex() + "  " + posTile.getTiley());
								tileRange.add(posTile);	
							}
						}
					}
					return tileRange;
				}
			//===============accessors methods==========================//
			  
			  //this method returns a list of all monsters (including avatars) on the board which have onCooldow == true
			  //this variable signal that the monster cannot attack/move in the current turn (right after summoning)
		
			  public ArrayList<Monster> coolDownToggle (){
				  ArrayList<Monster> monsterList = new ArrayList<Monster>();
				  
				  for (int i = 0; i <gameBoard.length; i++) {
						for (int k =0; k<gameBoard[0].length; k++) {
							if ((gameBoard[i][k].getUnitOnTile() != null)&& gameBoard[i][k].getUnitOnTile().getOnCooldown()) {
								monsterList.add(this.gameBoard[i][k].getUnitOnTile());
							}
						}
					}
					return monsterList;
			  }
}

