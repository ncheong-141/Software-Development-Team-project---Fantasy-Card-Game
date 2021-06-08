package structures.basic;

public class HumanPlayer extends Player {

	private String name;
	private Hand hand;
	private Avatar avatar;
	private Deck deck;
	
	public HumanPlayer(String name, Avatar avatar, Deck deck) {
		super();
		this.name = name;
		this.avatar = avatar;
		this.deck = deck;
		this.hand = initializeHand();
	}
	
	public String getName() {
		return name;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public Avatar getAvatar() {
		return avatar;
	}

	public Deck getDeck() {
		return deck;
	}
	
	
	public void discardCard(Card card) {
		//card disappears from hand once being played
	}
	
	
	public Hand initializeHand() {
		//assign 3 random cards from deck to hand
	}
	
	
	public void endTurn() {
		setMana(0);
		// assign one new card from top of the deck to hand
	}
	
	public void takeDamange(int damage) {
		setHealth(getHealth()-damage);
		//if new health is below 0 , die and call lose method
	}
	
	public void lose() {
		//destroy avatar on the board
	}
	
	
}



	
