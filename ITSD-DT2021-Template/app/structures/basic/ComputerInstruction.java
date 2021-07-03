package structures.basic;

public class ComputerInstruction {
	protected Card card;
	protected Tile targetTile;
	protected Monster actor;
	
	public ComputerInstruction(Card c, Tile t) {
		this.card = c;
		this.targetTile = t;
		this.actor = null;
	}
	
	public ComputerInstruction(Monster m, Tile t) {
		this.actor = m;
		this.targetTile = t;
	}

	public Card getCard() {
		return card;
	}

	public Tile getTargetTile() {
		return targetTile;
	}

	public Monster getActor() {
		return actor;
	}
	
}
