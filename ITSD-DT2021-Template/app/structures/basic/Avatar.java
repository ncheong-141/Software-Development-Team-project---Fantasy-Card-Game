package structures.basic;

public class Avatar extends Unit {
	int attack, health;
	Position avatarPos;
	Player owner;
	int range;
	
	public Avatar (int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
	}
	
	public Avatar (int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		
	}
	
	public Avatar(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
	}
}
