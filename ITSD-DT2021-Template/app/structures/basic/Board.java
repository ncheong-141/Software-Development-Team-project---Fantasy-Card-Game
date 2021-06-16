package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
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
	private final int[] rangeW = {1,-1,0,0,1,-1,1,-1};
	
	public Board() {
		X = 9;
		Y = 5;
		gameBoard = new Tile[Y][X];
		for (int i = 0; i<Y; i++) {
			for (int k = 0; k<X; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(k, i);
				gameBoard [i][k].free = true; 		
			}
		}
		
		//humanStart = gameBoard[3][2];
		//computerStart = gameBoard[3][8];
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
	
<<<<<<< HEAD
	//=====================PLAYABLE TILES METHODS==================//
	
		//methods showing where player can summon monster/cast spell
		
		//1) summoning of monster near friendly unit
		
		//this method can be called from elsewhere in the program
		//it returns a list of tiles where a given Player can summon a unit
		//Players clicks on a card >> this method is called
		public ArrayList<Tile> showSummonMonster(Player p){
			ArrayList <Tile> tilesToHighlight = new ArrayList<Tile>();
			for (int i = 0; i <gameBoard.length; i++) {
				for (int k =0; k<gameBoard[0].length; k++) {
					if (gameBoard[i][k].getUnitOnTile().getOwner()==p) {
						tilesToHighlight.addAll(this.calcRange(gameBoard[i][k]));
					}
				}
			}
			return tilesToHighlight;
		}
		
		//helper method to showSummonMonster
		//for any given tile it returns a list of tile in range
		//the range here is based on game specifications (any tile adjacent to a friendly unit)
		private ArrayList<Tile> calcRange(Tile t){
			ArrayList<Tile> tileRange = new ArrayList<Tile>();
			int xPos = t.getTilex();
			int yPos = t.getTiley();
			
			System.out.println(xPos + " " + yPos);
			for (int i = 0; i<rangeH.length; i++) {
				if (xPos + rangeW[i] <0 || xPos + rangeW[i] > 8 || yPos + rangeH[i]<0 || yPos + rangeH[i] > 4) continue;
				else {
					Tile posTile = this.getTile(xPos+rangeW[i], yPos+rangeH[i]);
					tileRange.add(posTile);
				}
			}
			return tileRange;
		}
		
		//2) summoning monster / casting spell anywhere on the board
		
		//returns all free tiles
		public ArrayList<Tile> summonAnywhere (){
			ArrayList<Tile> tileRange = new ArrayList<Tile>();
			for (int i = 0; i <gameBoard.length; i++) {
				for (int k =0; k<gameBoard[0].length; k++) {
					if (gameBoard[i][k].free) tileRange.add(gameBoard[i][k]);
				}
			}
			return tileRange;
		}
		
		//2A)Casting a spell on any of the enemy units (excl. avatar)
			//this same logic could be applied for unit that can attack anywhere on the board
		public ArrayList<Tile> enemyTile(Player p){
			ArrayList<Tile> tileRange = new ArrayList<Tile>();
			for (int i = 0; i <gameBoard.length; i++) {
				for (int k =0; k<gameBoard[0].length; k++) {
					if ((!(gameBoard[i][k].getUnitOnTile() instanceof Avatar)) && gameBoard[i][k].getUnitOnTile().getOwner()!=p) {
						tileRange.add(gameBoard[i][k]);
					}
				}	
			}
			return tileRange;
		}
		
		//2B)Casting a spell on any friendly unit (excl. avatar)
		public ArrayList<Tile> friendlyTile(Player p){	
			ArrayList<Tile> tileRange = new ArrayList<Tile>();
			for (int i = 0; i <gameBoard.length; i++) {
				for (int k =0; k<gameBoard[0].length; k++) {
					if ((!(gameBoard[i][k].getUnitOnTile() instanceof Avatar)) && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
						tileRange.add(gameBoard[i][k]);
					}
				}	
			}
			return tileRange;
		}
		
		//3) casting spell on own avatar(i.e. returning own avatar position)

		
		//4) casting spell on opponent avatar
	}


