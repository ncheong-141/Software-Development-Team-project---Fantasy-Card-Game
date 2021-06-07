package structures.basic;

public class Avatar extends Unit {
	int attack, health;
	Position avatarPos;
	Player owner;
	int range;
	Board board;
	
	
	public Avatar() {}
	
	public Avatar(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Avatar(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Avatar(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	
	public void setOwner (Player p, Board b) {
		owner = p;
		board = b;
		this.setPositionByPlayer();
		this.health = p.getHealth();
		this.attack = 2;
	}
	
	private void setPositionByPlayer () {
		if (this.owner instanceof HumanPlayer) {
			this.setPositionByTile(board.getTile(2,3));
		}
		
		else if (this.owner instanceof ComputerPlayer) {
			this.setPositionByTile(board.getTile(8,3));
		}
	}
	
}
