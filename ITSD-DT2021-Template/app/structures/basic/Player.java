package structures.basic;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import java.util.ArrayList;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;

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

	public Player() {
		this.health = 20;
		this.mana = 0;
		this.deck = new Deck();
		this.hand = new Hand();
		this.hand.initialHand(deck);
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
	
// add to assist Avatar class
	public void changeHealth(int delta) {
		this.health += delta;
		if (this.health > 20) this.health = 20;
		if (this.health<=0 ) {
			this.health=0;
			GameState.gameOver();
		}
	}
		
	
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
		if (this.health <= 0) GameState.gameOver();  
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
	
	public void setDeck(Deck d){
		this.deck = d;
		this.deck.deckOne();
	}

	public Hand getHand() {
		return hand;
	}
	
	public void setHand(Hand h) {
		this.hand = h;
	}


}
		
		// To do:
		// Move setting of deck and instantiation of it from the gameState into the Player constructor
		// Move hand instantiation/set up from gamestate into Player constructor

