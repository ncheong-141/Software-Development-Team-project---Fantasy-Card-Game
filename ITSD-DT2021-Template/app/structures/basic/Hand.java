package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;

public class Hand {
	private int curr;
	private ArrayList<Card> hand;
	
	public Hand( ArrayList<Card> hand) {
		super();
		this.curr = 0;
		this.hand = hand;
	}
	
	public void initialHand(ActorRef out, Deck deck) {
		ArrayList<Card> drawDeck= deck.getDeck();
		Card cardOne= drawDeck.get(0);
		Card cardTwo= drawDeck.get(1);
		Card cardThree= drawDeck.get(2);
		
		BasicCommands.drawCard(out, cardOne, 1, 0);
		BasicCommands.drawCard(out, cardTwo, 2, 0);
		BasicCommands.drawCard(out, cardThree, 3, 0);
		
		hand.add(cardOne);
		hand.add(cardTwo);
		hand.add(cardThree);
		 
		deck.delCard(0);
		deck.delCard(1);
		deck.delCard(2);
		
		setCurr(3);
	}
	
	public void drawCard(ActorRef out, Deck deck) {
		curr=getCurr();
		if (curr<6) {
		ArrayList<Card> drawDeck= deck.getDeck();
		Card drawn= drawDeck.get(0);
		
		curr++;
		BasicCommands.drawCard(out, drawn, curr, 0);
		
		deck.delCard(0);
		setCurr(curr);
		}
		else {
			BasicCommands.addPlayer1Notification(out, "Hand Full", 2);
		}
	}
	
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
}