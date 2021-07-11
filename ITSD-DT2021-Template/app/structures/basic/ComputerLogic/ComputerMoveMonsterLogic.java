package structures.basic.ComputerLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import structures.basic.Board;
import structures.basic.ComputerPlayer;
import structures.basic.Monster;
import structures.basic.Tile;

/**
 * 
 * @author Chiara Pascucci and Yufen Chen
 * This class handles the logic for choosing which units to move to which tiles
 * It gets instantiated in ComputerPlayer
 *
 */


public class ComputerMoveMonsterLogic {
	private ComputerPlayer player;
	
	public ComputerMoveMonsterLogic(ComputerPlayer p) {
		this.player = p;
	}
	
			//====================MOVING OF UNITS ON BOARD METHODS=======================//
	
			/**
			 * public method used in ComputerPlayer
			 * @return returns a list of ComputerInstruction object
			 * each object contains a monster (currently on the board) and a destination tile
			 * this method calls the private methods in this class in the order provided
			 */
			public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> movesUnits(Board gameBoard){
				ArrayList<Monster> movableMonsters = this.allMovableMonsters(gameBoard);
				if(movableMonsters.isEmpty()) return new ArrayList<structures.basic.ComputerLogic.ComputerInstruction>();
				
				
				ArrayList<MonsterTileOption> listofMTO = this.getMonstersOptions(movableMonsters, gameBoard);
				
				return this.matchMonsterAndTile(listofMTO);
			}
			
			/**
			 * 1.
			 * @return method returns a list of monster that the player can move in the current turn
			 * a monster can be moved iff it has moves left and if onCoolDown == false (monster has started on the board)
			 */
			private ArrayList <Monster> allMovableMonsters(Board gameBoard){
				ArrayList <Monster> myMonsters = gameBoard.friendlyUnitsWithAvatar(player);
				//System.out.println("num mosters I can move bf check: " + myMonsters.size());
				myMonsters.removeIf(m -> (m.getMovesLeft()<=0 || m.getOnCooldown()));
				//System.out.println("after check: " + myMonsters.size());
				
				return myMonsters;
			}
			
			/**
			 * 2.
			 * @param list of monster objects
			 * @return an array of MonsterTileOption objects
			 * each object in the array being return contains a monster and a list of tiles where that monster can move to
			 */
			private ArrayList<MonsterTileOption> getMonstersOptions(ArrayList<Monster> list, Board gameBoard){

				ArrayList<MonsterTileOption> optionList = new ArrayList<MonsterTileOption>();
				
				for (Monster m : list) {
					//System.out.println("calculating tile options for monster: " + m.getName());
					optionList.add(new MonsterTileOption (m, gameBoard));   
				}
				
				return optionList;
			}
		
			/**
			 * 3.
			 * @param optionList - list of MonsterTileOption objects
			 * @return a list of ComputerInstruction objects each containing a monster and a target tile (where the monster will move to)
			 */
			private ArrayList<ComputerInstruction> matchMonsterAndTile (ArrayList<MonsterTileOption> optionList){
				//sorting array based on value of top tile 
				Collections.sort(optionList);
				
				
				//method returns an array list of computer instruction objs
				//each of those objs contains a monster to be moved and a target tiles
				ArrayList <ComputerInstruction> compMoves = new ArrayList<ComputerInstruction>();
				
				//this set keep track of tiles that have already been used as a target tile 
				//so no other monster should be added to it
				HashSet <Tile> tileUsed = new HashSet<Tile>();
				
				int k = 0;
				
				//for loop iterates over the list of (monster - list of tiles) objs (MLT) passed to the method
				for (MonsterTileOption mto: optionList) {
					//for each MLT the top tile is retrieved (k=0)
					//this is the tile with the highest score
					if (mto.getList().isEmpty() || mto.getList() == null) continue;
					
					Tile t = mto.getList().get(k);
				
					//boolean variable for testing purposes
					boolean tileFound = false;
					
					//creating a CI reference
					ComputerInstruction inst = null;
					
					//this checks if the top tile for the given monster (within the MLT obj) has already been used
					if (!(tileUsed.contains(t))){
						//if the best tile has not been used already, a new instruction is created passing it the monster within the MLT obj at optionList[i]
						//and the target tile t
						inst = new ComputerInstruction(mto.getM(), t);
						//adding the new instruction object to the list to be returned 
						compMoves.add(inst);
						//adding the target tile to the tile used set
						tileUsed.add(inst.getTargetTile());
						tileFound = true;
						
						continue;
					}
					else {
						
						if (mto.getList().size() <= k) continue;
						//this part of the code is executed if the tileUsed set already contains target tile t
						do {
							//incrementing k
							k++;
							//to retrieve the new best tile within the MLT obj
							t = mto.getList().get(k);
							
						//checking if the new tile t is in the set already and if there are still tiles to be tested within the MLT obj	
						} while(tileUsed.contains(t)&& k<mto.getList().size()+1);
						
						//once the above loop terminates
						//this condition checks that the do-while loop terminated because a tile was found
						//if tile was found current target tile t is not in set
						if (!tileUsed.contains(t)) {
							tileFound = true;
							//creating a computer instruction with monster with MTL obj and current target tile
							inst = new ComputerInstruction(mto.getM() , t);
							//adding tile to used tile set
							tileUsed.add(t);
							//adding new computer instruction to list to be returned
							compMoves.add(inst);
						}
					}
					//resetting value of k for next loop iteration
					k=0;
				}	
				
				return compMoves;	
			}
			//=========================inner class===============================//
			
			/**
			 * 	this inner class represent a pairing of a monster belonging to comp player
				and a list of tiles that the given monster can move to
				each object has a score that is equal to the score of the first tile in the list
				the list is ordered based on tile score (tile implements comparable)
			 * 
			 */
			
			
			
			static class MonsterTileOption implements Comparable<MonsterTileOption> {
				Monster m; 
				ArrayList<Tile> list;
				double score;
				private static  int inRangeScore = - 1;
				private static  int bringsEnemyInRange = 2; 
				MonsterTileOption(Monster m, Board b){
					this.m = m;
					this.list = b.unitMovableTiles(m.getPosition().getTilex(), m.getPosition().getTiley(), m.getMovesLeft());
					//System.out.println("number of movabale tiles (line 161) : " + list.size());
					if(list != null && !(list.isEmpty())) {
						for (Tile t : list) {
							this.calcTileMoveScore(m,b,t);
							//System.out.println(" tile ( "+t.getTilex() + " - " + t.getTiley() + " ) score: " + t.getScore());
						}
						Collections.sort(list);
						this.score = this.list.get(0).getScore();
					}
					else if (list.isEmpty()) this.score = -1.0;
					
				}
				
				public Monster getM() {
					return m;
				}
				
				public ArrayList<Tile> getList(){
					return this.list;
				}
				
				public double getScore() {
					return this.score;
				}
				

				@Override
				public int compareTo(MonsterTileOption o) {
				
					if (this.score > o.getScore()) return -1;
					else if (this.score < o.getScore()) return 1;
					else return 0;
				}
				
				
				//logic for scoring tiles from movement perspective 
				private void calcTileMoveScore(Monster m, Board b, Tile targetTile) {
					//tile where monster is currently located
					Tile currTile = b.getTile(m.getPosition().getTilex(), m.getPosition().getTiley());

					//calculate which enemy tiles are in range from the would be (WB) tile
					HashSet <Tile> wBAttackable = b.calcAttackRange(targetTile.getTilex(), targetTile.getTiley(), m.getAttackRange(), m.getOwner());
				
					//get all tiles that this monster could attack from its current tile (with enemies on them)
					HashSet<Tile> currAttackable = b.calcAttackRange(currTile.getTilex(), currTile.getTiley(), m.getAttackRange(), m.getOwner());
				
					//System.out.println(wBAttackable.size() + "  " + currAttackable.size());

					if (wBAttackable.size() > currAttackable.size()) targetTile.setScore(bringsEnemyInRange);
				
					//all tiles on the board with an enemy unit on it
					ArrayList <Tile> enemyTilesOnBoard = b.enemyTile(m.getOwner());
				
					int currAttackableByEnemy = 0;
					int wBAttackableByEnemy = 0;
				
					for (Tile t : enemyTilesOnBoard) {
						Monster mnstr = t.getUnitOnTile();
						int x = t.getTilex();
						int y = t.getTiley();

						ArrayList<Tile> tilesEnemyCanAttack = b.unitAttackableTiles(x, y, mnstr.getAttackRange(), mnstr.getMovesLeft());
					
						if (tilesEnemyCanAttack.contains(targetTile)) wBAttackableByEnemy++;
						if (tilesEnemyCanAttack.contains(currTile)) currAttackableByEnemy ++;
					}

					if (wBAttackableByEnemy > currAttackableByEnemy) targetTile.setScore(inRangeScore);
			
				}
				
				
				
			}
			
			
			
}
