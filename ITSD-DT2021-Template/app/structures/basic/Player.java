package structures.basic;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import java.util.ArrayList;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import events.CardClicked;
import events.EndTurnClicked;
import structures.GameState;
/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	protected int health;
	protected int mana;
	protected Hand hand;
	protected Deck deck;
	protected Avatar avatar;
	protected ArrayList<Card> h;
	protected ArrayList<Card> d;
	protected ActorRef out;
	protected EndTurnClicked e;
	
	public Player(Deck deck) {
		this.health = 20;
		this.mana = 0;
		this.deck = new Deck();
		this.hand = new Hand(h);
		System.out.println("In Player constructor");

//		for (Card d : this.deck.getDeck()) {
//			System.out.println(d.getCardname());
//		}
	}
	
	public Player(int health, int mana) {
		this.health = health;
		this.mana = mana;
		this.deck = new Deck();
		this.hand = new Hand(h);
	
	}
	public Player(int health, int mana, Deck deck) {
		super();
		this.health = health;
		this.mana = mana;
		this.deck = new Deck();
		this.hand = new Hand(h);
	}
		
	//add Mana and check maximum	
	public void addMana(int addMana) {
		int newMana = mana + addMana;
		if(newMana > 9) {
			this.mana = 9;
		}	
	}
	
	//delete Mana , when use cards
	
	public void loseMana(int loseMana) {
		mana = mana - loseMana;
	}	
	

	//add Health and check maximum
	public void addHealth(int addHealth) {
		int newHealth = health + addHealth;
		if (newHealth <= 20 && newHealth > 0 ) {
			this.health = newHealth;
		} 
		else {
			this.health = 20;
		}
	}	
	
	//lose health and check if it's dead, health =0
	public void loseHealth(int loseHealth) {
		int newHealth = health - loseHealth;
		if(newHealth <= 0) {
			this.health = 0;
			GameState.gameOver(); 
		}
	}

	//create first 3 cards in hand
	public void firstThreeCards() {
//		this.hand = new Hand(this.deck.getDeck());		
		hand.initialHand(out, deck);
		System.out.println("In setPlayerHand()");
		for (Card d : this.deck.getDeck()) {
			System.out.println(d.getCardname());
		}
	}
	
	//draw card from deck when round ends, top card in deck is deleted
	public void drawFromDeck() {
			this.hand.drawCard(out, deck); 
		}

	
	//playing cards,take the position of card in hand
	public void playCard(int p) {
		this.hand.getCardFromHand(p);
	}
	
	public void endTurn() {
		e.processEvent(out, null, null);
	}
	
	
// temp method to aid demo
//	public void setHand(ArrayList <Card> h) {
//		this.hand = new Hand(h);
//	}
	
// getters & setters	
	public Avatar getAvatar() {
		return avatar;
	}
	
	public void setAvatar(Avatar a) {
		this.avatar = a;	
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getMana() {
		return mana;
	}
	
	public void setMana(int mana) {
		this.mana = mana;
	}
	
	public Deck getDeck() {
		return deck;
	}

	public void setDeck (ArrayList<Card> d) {
		this.d = d;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(ArrayList<Card> h) {
		this.h = h;
	}	

	
}
		
		

