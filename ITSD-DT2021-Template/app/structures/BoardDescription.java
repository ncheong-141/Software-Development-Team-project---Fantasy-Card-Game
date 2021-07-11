package structures;

public class BoardDescription {
	
						//X-AXIS

			//[0,0][1,0][2,0][3,0][4,0][5,0][6,0][7,0][8,0]
			//[0,1][1,1][2,1][3,1][4,1][5,1][6,1][7,1][8,1]
//Y-AXIS	//[0,2][1,2][2,2][3,2][4,2][5,2][6,2][7,2][8,2]
			//[0,3][1,3][2,3][3,3][4,3][5,3][6,3][7,3][8,3]
			//[0,4][1,4][2,4][3,4][4,4][5,4][6,4][7,4][8,4]


//PUBLIC METHODS

//getTile(int X, int Y) --> returns Tile obj at X and Y coordinates

//getGameBoard() --> return a 2-D tile array used to represent the board

//allSummonableTiles(Player p) --> returns list (arrayList) of tiles objs where player P can summon a monster

//enemyTile(Player p) --> returns list of all tiles on the board which contain a enemy monster, excl enemy's avatar

//friendlyTile(Player p) --> return list of all tiles on the board which contain a friendly monster, excl p's own avatar

//ownAvatarTile (Player p) --> returns tile where p's avatar is located

//enemyAvatarTile (Player p, GameState g) --> returns tile where enemy avatar is located

//monsterMovableTiles(int xPos, int yPos, int range) --> returns list of all FREE tiles within range of the specific position
							

//attackableAdjTiles(int xPos, int yPos) --> return list of all adjacent tiles to specified position that contain an enemy unit

//coolDownToggle () --> return a list of all monster objects on the board that have variable onCooldown==true
}

