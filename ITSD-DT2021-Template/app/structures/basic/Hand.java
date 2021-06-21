package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;

public class Hand {
	private int curr;//keeps track of no of cards in hand
	private ArrayList<Card> hand;// array of card objects comprising the hand
	private boolean playingMode;
	private Card selectedCard;
	
	public Hand(ArrayList<Card> hand) {//constructor for hand 
		super();
		this.curr = 0;
		this.hand = hand;
		this.playingMode=false;
		this.selectedCard=null;
	}
	
	public void initialHand(ActorRef out, Deck deck) { //allows player to receive initial hand
		ArrayList<Card> drawDeck= deck.getDeck();//create temporary instance of player deck
		
		//finds top three cards from deck
		Card cardOne= drawDeck.get(0);
		Card cardTwo= drawDeck.get(1);
		Card cardThree= drawDeck.get(2);
		
		//draws top three deck cards into hand
		BasicCommands.drawCard(out, cardOne, 1, 0);
		BasicCommands.drawCard(out, cardTwo, 2, 0);
		BasicCommands.drawCard(out, cardThree, 3, 0);
		
		//adds the cards to the Hand class's array of Cards to keep track 
		//of them for later playing and deleting of cards
		hand.add(cardOne);
		hand.add(cardTwo);
		hand.add(cardThree);
		 
		//removes top three cards from deck
		deck.delCard(0);
		deck.delCard(1);
		deck.delCard(2);
		
		setCurr(3);// sets current hand size
	}
	//allows players to draw card on round end
	public void drawCard(ActorRef out, Deck deck) {
		curr=getCurr();
		if (curr<6) {//checks that hand is not full
		
		//creates temporary deck and finds top card	
		ArrayList<Card> drawDeck= deck.getDeck();
		Card drawn= drawDeck.get(0);
		
		//draws top card from deck and increments current card count
		BasicCommands.drawCard(out, drawn, curr, 0);
		curr++;
		
		deck.delCard(0);//removes card from deck
		setCurr(curr);//sets new no of cards in hand
		}
		else {//warns player if hand is full
			BasicCommands.addPlayer1Notification(out, "Hand Full", 2);
		}
	}
	
	public Card getCardFromHand(int i) {
		return.getHand().get(i);
	}
	
	
	//getters and setters
	public int getCurr() {
		return curr;
	}
	public void setCurr(int curr) {
		this.curr = curr;
	}
	public ArrayList<Card> getHand() {
		return hand;
	}
	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}
	public boolean isPlayingMode() {
		return playingMode;
	}
	public void setPlayingMode(boolean playingMode) {
		this.playingMode = playingMode;
	}
	public Card getSelectedCard() {
		return selectedCard;
	}
	public void setSelectedCard(Card selectedCard) {
		this.selectedCard = selectedCard;
	}
}
		
		
		