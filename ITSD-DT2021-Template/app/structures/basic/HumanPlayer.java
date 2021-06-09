package structures.basic;

import commands.BasicCommands;
import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import events.CardClicked;
import events.EndTurnClicked;
import structures.GameState;
import com.fasterxml.jackson.databind.JsonNode;


public class HumanPlayer extends Player {

//	private String name;
//	private Hand hand;
//	private Deck deck;
//	//private ActorRef out;
//	private Unit unit;  //Unitcard Spellcard
//	private int position; //card in hand position
//	private Tile tile;
//	private Avatar avatar;
//	
//	
//	
//	public HumanPlayer(String name, Avatar avatar, Deck deck) {
//		super();
//		this.name = name;
//		this.avatar = avatar;
//		this.deck = deck;
//		this.hand = initializeHand(); //not sure about this, need to check Hand.java
//	}
//	
//	public String getName() {
//		return name;
//	}
//	
//	/*
//	 * take position to select card from hand highlight card play the card if it
//	 * is monster card(UnitCard, take a tile position and display on board then card
//	 * is discarded from hand afterwards
//	 */	
//	
//	public void playCard() {
//		// not completed yet
//		
//		CardClicked.processEvent(ActorRef out, GameState gameState, JsonNode message);
//		BasicCommands.drawCard(out, unit, position, mode);;
//		if (this.unit instanceof UnitCard) {
//			BasicCommands.drawUnit(out, avatar, tile);
//			BasicCommands.drawTile(out, tile, position);
//		}
//		
//		else(this.unit instanceof SpellCard){
//			//perform what the spell card suppose to do
//		}
//		
//		BasicCommands.deleteCard(out, position);
//		
//	}
		
	
	/*
	 * when unit health =0 take the unit and delete from board
	 */
//	
//	public void disCardUnit() {
//		//destroy avatar on the board
//		//not completed yet
//		BasicCommands.deleteUnit(out, unit);
//	}
//	
//	
	
	/*
	 * public void endTurn() { EndTurnClicked end = new EndTurnClicked(); }
	 * integrated EndTurnClicked.java & GameState
	 */
	
	
	
	/*not sure about this, need to check Hand.java
	 * 
	 * public Hand initializeHand() { 
	 * 
	 * assign 3 random cards from deck to hand 
	 * }
	 */
	
	
	
	/*
	 * public void takeDamange(int damage) { setHealth(getHealth()-damage); //if new
	 * health is below 0 , die and call lose method }
	 */
	

}



	
