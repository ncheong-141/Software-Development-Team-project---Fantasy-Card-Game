package structures.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A basic representation of a tile on the game board. Tiles have both a pixel position
 * and a grid position. Tiles also have a width and height in pixels and a series of urls
 * that point to the different renderable textures that a tile might have.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Tile implements Comparable<Tile>{

	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	List<String> tileTextures;
	int xpos;
	int ypos;
	int width;
	int height;
	int tilex;
	int tiley;
	
	private static  double inRangeScore = - 0.5;
	
	private static  double bringsEnemyInRange = 0.2; 
	// Added attribute
	boolean free;
	Monster unitOnTile; 	// Storing a unit in the tile to reference when a tile is clicked
	double score;

	public Tile() {}
	
	public Tile(String tileTexture, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		tileTextures = new ArrayList<String>(1);
		tileTextures.add(tileTexture);
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
		
		this.free = true;
		this.unitOnTile = null;
		this.score = 0;
	}
	
	public Tile(List<String> tileTextures, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		this.tileTextures = tileTextures;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
		
		this.free = true;
		this.unitOnTile = null;
		this.score = 0;
	}
	public List<String> getTileTextures() {
		return tileTextures;
	}
	public void setTileTextures(List<String> tileTextures) {
		this.tileTextures = tileTextures;
	}
	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getTilex() {
		return tilex;
	}
	public void setTilex(int tilex) {
		this.tilex = tilex;
	}
	public int getTiley() {
		return tiley;
	}
	public void setTiley(int tiley) {
		this.tiley = tiley;
	}
	public boolean getFreeStatus() {
		return free; 
	}
	public Monster getUnitOnTile() {
		return unitOnTile; 
	}
	
	//added method to be able to add and remove any unit to/ from a tile
	//the method takes in a Monster type object (which could be a Monster or Avatar)
	//both methods check that the tile is actually free when adding a moster
	//and there there is a monster to be removed when trying to remove a monster
	public boolean addUnit (Monster m) {
		if (!(this.free) || !(this.unitOnTile==null)) {
			return false;
		}
		else {
			this.unitOnTile = m;
			this.free = false;
			m.setPositionByTile(this);
			
			return true;			
		}
	}

	public boolean removeUnit () {
		if (this.free || this.unitOnTile==null) return false;
		else {
			this.free = true;
			this.unitOnTile = null;
			return true;
		}
	}
	
	public double calcTileScore(Monster m, Board b) {
		//tile where monster is currently located
		Tile currTile = b.getTile(m.getPosition().getTilex(), m.getPosition().getTiley());

		//calculate which enemy tiles are in range from the would be (WB) tile
		HashSet <Tile> wBAttackable = b.calcAttackRange(xpos, ypos, m.getAttackRange(), m.getOwner());
		
		//get all tiles that this monster could attack from its current tile (with enemies on them)
		HashSet<Tile> currAttackable = b.calcAttackRange(currTile.getTilex(), currTile.getTiley(), m.getAttackRange(), m.getOwner());
		
		
		if (wBAttackable.size() > currAttackable.size()) this.score += this.bringsEnemyInRange;
		
		//all tiles on the board with an enemy unit on it
		ArrayList <Tile> enemyTilesOnBoard = b.enemyTile(m.getOwner());
		
		int currAttackableByEnemy = 0;
		int wBAttackableByEnemy = 0;
		
		for (Tile t : enemyTilesOnBoard) {
			Monster mnstr = t.getUnitOnTile();
			int x = t.getTilex();
			int y = t.getTiley();
			
			//NOTE need to check when moves left gets reset
			HashSet<Tile> tilesEnemyCanAttack = b.unitAttackableTiles(x, y, mnstr.getAttackRange(), mnstr.getMovesLeft());
			
			if (tilesEnemyCanAttack.contains(this)) wBAttackableByEnemy++;
			if (tilesEnemyCanAttack.contains(currTile)) currAttackableByEnemy ++;
		}
		
		
		if (wBAttackableByEnemy > currAttackableByEnemy) this.score += inRangeScore;
		
		return score;
	}

	/**
	 * Loads a tile from a configuration file
	 * parameters.
	 * @param configFile
	 * @return
	 */
	public static Tile constructTile(String configFile) {
		
		try {
			Tile tile = mapper.readValue(new File(configFile), Tile.class);
			return tile;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
		
	}

	@Override
	public int compareTo(Tile o) {
		if (this.getScore() > o.getScore()) return 1;
		else if (this.getScore() < o.getScore()) return -1;
		return 0;
	}

	public double getScore() {
		return score;
	}
	
	
	
}
