package structures.basic;

import java.util.ArrayList;

public class Avatar extends Unit {
	Player owner;
	Board board;
	int health, attack;
	static final int[] moveH = {0,0,0,0,1,1,1,2,-1,-1,-1,-2};
	static final int[] moveW = {2,1,-1,-2,-1,0,1,0,-1,0,1,0};
	
	
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

	private ArrayList<Tile> possibleMoves() {
		ArrayList<Tile> moveList = new ArrayList<Tile>();
		
		for (int i = 0; i<moveH.length; i++) {
			Tile t = board.getTile(moveH[i], moveW[i]);
			if (t.free == true) moveList.add(t);
		}
		
		return moveList;
	}
	
	public void move(Tile t) {
		this.setPositionByTile(t);	
	}
	
	
}
