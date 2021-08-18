package structures.basic.ComputerLogic;
import structures.basic.*;

/**
 * 
 * @author Chiara Pascucci and Yufen Chen
 * This class represent an instruction that the computer player can generate
 * each object contains data on the card to be played + tile where the card needs to be played exclusive-OR
 * data on the Monster that needs to move OR perform an attack and the target tile 
 */

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
		if (this.card == null) return "id:" + actor.getId() + "monster: " + actor.getName() + "at tile: (" + actor.getPosition().getTilex() + " - " + actor.getPosition().getTiley() + ") to tile " + targetTile;
		else return "play card " +  " id: " + this.card.getId() + " " + this.card.getCardname() + " on tile " + this.targetTile;
	}
}
