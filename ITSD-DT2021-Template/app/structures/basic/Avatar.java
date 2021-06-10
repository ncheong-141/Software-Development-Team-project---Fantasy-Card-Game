package structures.basic;

import java.util.ArrayList;

public class Avatar extends Unit {
	Player owner;
	Board board;
	int health, attack;
	static final int[] moveH = {0,0,0,0,-2,-1,1,2,1,-1,-1,1};
	static final int[] moveW = {-2,-1,1,2,0,0,0,0,1,-1,1,-1};
	
	
	public Avatar() {}
	
	public Avatar(Player p, Board b) {
		super();
		this.owner = p;
		this.board = b;
	}

	
	public void setOwner (Player p, Board b) {
		owner = p;
		board = b;
		this.setPositionByPlayer();
		this.health = p.getHealth();
		
	}
	
	public void setHealth(int health) {
		this.health = health;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	private void setPositionByPlayer () {
		if (this.owner instanceof HumanPlayer) {
			this.setPositionByTile(board.getTile(2,1));
		}
		
		else if (this.owner instanceof ComputerPlayer) {
			this.setPositionByTile(board.getTile(2,7));
		}
	}
	
	public int getHealth() {
		return health;
	}

	public int getAttack() {
		return attack;
	}
	
	public Player getOwner() {
		return this.owner;
	}

	public ArrayList<Tile> possibleMoves() {
		ArrayList<Tile> moveList = new ArrayList<Tile>();
		int xPos = this.position.getTilex()-1;
		int yPos = this.position.getTiley()-1;
		for (int i = 0; i<moveH.length; i++) {
			if (xPos + moveW[i] <0 || xPos + moveW[i] > 8 || yPos + moveH[i]<0 || yPos + moveH[i] > 4) continue;
			
			else{
				Tile t = board.getTile(moveW[i]+xPos, moveH[i]+ yPos);
				moveList.add(t);
			}
			
		}
		
		return moveList;
	}
	
	public void move(Tile t) {
		this.setPositionByTile(t);	
	}
	
	
}
