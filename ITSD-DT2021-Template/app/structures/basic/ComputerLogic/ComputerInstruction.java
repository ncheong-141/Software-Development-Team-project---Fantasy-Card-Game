package structures.basic.ComputerLogic;
import structures.basic.*;

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
	
	
	public String toString() {
		if (this.card == null) return "monster: " + actor.getName() + "at tile: (" + actor.getPosition().getTilex() + " - " + actor.getPosition().getTiley() + ") to tile " + targetTile;
		else return "play card " + this.card.getCardname() + " on tile " + this.targetTile;
	}
}
