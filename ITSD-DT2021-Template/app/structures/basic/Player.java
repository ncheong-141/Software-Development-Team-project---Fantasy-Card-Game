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

	private int health;
	private int mana;
	protected Avatar avatar;
	protected Deck deck;
	protected Hand hand;
	protected ActorRef out;
//	private boolean dead;
//	private boolean turn;  move to EndTurn/GameState?
	private int position; // card position in hand
	private Card card;

	
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;		
//		this.dead = false; gameState?
//		this.turn = false; gameState?	
			
	
	}
	
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
//		this.dead = false; gameState?
//		this.turn = false; gameState?			

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
	
	//add Mana and check maximum
	public void addMana(int addMana) {
		int newMana = mana + addMana;
		if(newMana > 9) {
			this.mana = 9;
		}	
	}
	//delete Mana  (check minimum?)
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


	public Hand getHand() {
		return this.hand;
	}
	


	/*
	public void checkDead() {
		if (dead = true) {
			GameState.gameOver();
		}
	}	
	
	
	public void setTurn(){
		if (turn){
			turn = false;
		}else {
			turn = true;
		}
	}
	
	*/
	
	
	public Avatar getAvatar() {
		return avatar;
	}
	
	public void setAvatar(Avatar a) {
		this.avatar = a;
		
	}
	
	//create deck for player
	public void assignDeck() {
		this.deck.getDeck();
		
	}
	
	//create hand for player, along with first 3 cards in hand
	public void seeHand() {
		this.hand = new Hand(null);
		this.hand.initialHand(out, deck);
	}
	
	//draw card from deck when round ends, top card in deck is deleted
	public void drawFromDeck() {
			this.hand.drawCard(out, deck); 

		}

	
	
/*	(game logic inside Card class?)
	// take the monster card position in hand and play
	public void playCard(int position) {
	
		ArrayList<Card> currHand = this.hand.getHand();
		this.card= currHand.get(position);
		this.unit.setId(this.card.getId());
		
		
		if (this.unit instanceof Monster) {
			if(this.getMana() - this.card.getManacost() >= 0 ) {
				this.loseMana(this.card.getManacost()); //deduct mana
					
				// monster skills
					
				BasicCommands.deleteCard(out, position);  //discard the card after played
			}
		}
		
		
		else{  //spell played
			if(this.getMana() - this.card.getManacost() >= 0 ) {
				this.loseMana(this.card.getManacost()); //deduct mana
					
				// monster skills
					
				BasicCommands.deleteCard(out, position);  //discard the card after played
			}
		}
		
	*/

		
	}
		
		

}
