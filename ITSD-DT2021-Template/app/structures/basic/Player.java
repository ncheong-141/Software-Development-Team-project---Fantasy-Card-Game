package structures.basic;

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
	
	
	private int test =1;

	public Player() {
		this.health = 20;
		this.mana = 0;
	}

	//add Mana and check maximum	
	public void addMana(int addMana) {
		int newMana = mana + addMana;
		if(newMana > 9) {
			this.mana = 9;
		}	else {
			this.mana = newMana;
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
	}

	public Hand getHand() {
		return hand;
	}
	
	public void setHand(Hand h) {
		this.hand = h;
	}


}


