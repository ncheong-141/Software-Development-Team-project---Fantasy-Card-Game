package structures.basic;

import java.util.ArrayList;


import commands.BasicCommands;
import structures.basic.Card;

public class Hand {
	private int curr;//keeps track of no of cards in hand
	private ArrayList<Card> handList;// array of card objects comprising the hand
	private Card selectedCard;//card selected for play
	private int selCarPos;
	
	public Hand() {//constructor for hand 
		super();
		this.curr = 1;
		this.handList = new ArrayList<Card>() ;
		this.selectedCard=null;
		this.selCarPos=-1;
	}
	
	public void initialHand(Deck deck) { //allows player to receive initial hand
		ArrayList<Card> drawDeck= deck.getCardList();//create temporary instance of player deck
			
		//finds top three cards from deck
		Card cardOne= drawDeck.get(0);
		Card cardTwo= drawDeck.get(1);
		Card cardThree= drawDeck.get(2);

		
		//adds the cards to the Hand class's array of Cards to keep track 
		//of them for later playing and deleting of cards
		handList.add(cardOne);
		handList.add(cardTwo);
		handList.add(cardThree);
		 
		//removes top three cards from deck
		deck.delCard(0);
		deck.delCard(1);
		deck.delCard(2);
		
		setCurr(3);// sets current hand size
	}
	//allows players to draw card on round end
	public void drawCard( Deck deck) {
		curr=getCurr();
		//creates temporary deck and finds top card	
		ArrayList<Card> drawDeck= deck.getCardList();
		Card drawn= drawDeck.get(0);
		if (curr<6) {//checks that hand is not full
			handList.add(drawn);
		//increments current card count
			deck.delCard(0);//removes card from deck
			curr++;
			setCurr(curr);//sets new no of cards in hand
		}
		else {//if hand is full discards drawn card
			deck.delCard(0);//removes card from deck
		}
		
	}
	
	public Card getCardFromHand(int i) {
		return getHandList().get(i);
	}
	
	public void removeCard(int i) {
		handList.remove(i);
		setCurr(getCurr()-1);

	}
	
	//getters and setters
	public int getCurr() {
		return curr;
	}
	public void setCurr(int curr) {
		this.curr = curr;
	}
	public ArrayList<Card> getHandList() {
		return handList;
	}
	public void setHandList(ArrayList<Card> hand) {
		this.handList = hand;
	}
	public Card getSelectedCard() {
		return selectedCard;
	}
	public void setSelectedCard(Card selectedCard) {
		this.selectedCard = selectedCard;
	}
	public int getSelCarPos() {
		return selCarPos;
	}
	public void setSelCarPos(int selCarPos) {
		this.selCarPos = selCarPos;
	}
	
}