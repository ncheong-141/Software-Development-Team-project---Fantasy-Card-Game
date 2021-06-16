package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;

public class Hand {
	private int curr;//keeps track of no of cards in hand
	private ArrayList<Card> hand;// array of card objects comprising the hand
	
	public Hand( ArrayList<Card> hand) {//constructor for hand 
		super();
		this.curr = 0;
		this.hand = hand;
	}
	
	public void initialHand(ActorRef out, Deck deck) { //allows player to receive initial hand
		ArrayList<Card> drawDeck= deck.getDeck();//create temporary instance of palyer deck
		
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
		curr=getCurr();//gets current hand size
		if (curr<6) {//checks that hand is not full
		ArrayList<Card> drawDeck= deck.getDeck();//creates temporary copy of deck
		Card drawn= drawDeck.get(0);// finds top card of deck
		