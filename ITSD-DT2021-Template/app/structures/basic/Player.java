package structures.basic;
import structures.*;
/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	int health;
	int mana;
	Avatar avatar;
	Deck deck;
	Hand hand;
	boolean dead;
	boolean turn;
	
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
		deck = new Deck();
		// not sure how to initialize the hand here
		this.dead = false;
		this.turn = false;		
		avatar = new Avatar();
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		//not sure how to use this
	}
	
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		deck = new Deck();
		// not sure how to initialize the hand here
		this.dead = false;
		this.turn = false;		
		avatar = new Avatar();
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		//not sure how to use this
	
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
	

	public void addMana(int addMana) {
		int newMana = mana + addMana;
		if(newMana > 9) {
			this.mana = 9;
		}
		
	}
	public void loseMana(int loseMana) {
		mana = mana - loseMana;
	//minimum mana will be checked when playing cards?
	}	
	
	
	
	public void addHealth(int addHealth) {
		int newHealth = health + addHealth;
		if (newHealth <= 20 && newHealth > 0 ) {
			this.health = newHealth;
		} 
		else {
			this.health = 20;
		}
	}	
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
	
	
}
