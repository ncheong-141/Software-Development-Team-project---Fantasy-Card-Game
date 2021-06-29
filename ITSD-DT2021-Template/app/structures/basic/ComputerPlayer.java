package structures.basic;


import java.util.ArrayList;
import java.util.Collections;




public class ComputerPlayer extends Player {
	
	public ComputerPlayer(Deck d) {
		super(d);
	}
	
	
	
	
	//method returns playable cards from the player's hand based on the mana cost
	private ArrayList <Card> playableCards(){
		ArrayList<Card> cardList = new ArrayList<Card>();
		for (Card c : this.hand.getHand()) if (c.getManacost() <= this.getMana()) cardList.add(c);
		return cardList;
	}
	
	/*****
	//card(s) permutations algorithm
	
	Let comboList be a list of card combinations
	Let combo be a list of cards 
	Let hand be an array of cards currently in player's hand
	1)set k = 0
	2)while k < hand length repeat:
		3)set card = hand[k]
		4)add card to current combo list
		5)update mana left to pay for card[k]
		6)if mana left = 0 OR mana left < mana cost of cheapest card OR if card is last card in hand
			6.1)add this combo to list of combos
		7)else set i = k+1
		8)while i < length of hand repeat:
			8.1) if mana cost of i = mana left
				8.1.1) add i to current combo
				8.1.2) add current combo to combo list
				8.1.3) rest mana left to total mana - k mana cost
				8.1.4) reset combo (new empty combo) and continue
			8.2) else if mana cost of i is < mana left
				8.2.1) add i to current combo
				8.2.2) update mana left and continue
				8.2.3) if new left mana value is less than last card in hand break
			8.3) else continue 
	9) terminate yielding list of combos

	
	****/
	private ArrayList<CardCombo> cardCombos(ArrayList <Card> list){
		
		//method will return a list of combinations of cards
		//a card combination (combo) is represented as an ArrayList
		ArrayList<CardCombo> comboList = new ArrayList<CardCombo>();
		
		//converting hand to an array for ease of indexing
		Card [] hand = (Card[]) list.toArray();
		//!!!!!NOTE: order array - card will implement comparable on mana cost
		
		//instantiating a combo object (as an array list of card objects)
		CardCombo combo = new CardCombo();
		
		//iterating over the hand array
		for (int k = 0; k<hand.length; k++) {
			//adding the card at position kth in hand to a combo
			combo.add(hand[k]);
			
			//creating variable to track how much mana the player has left after (hypothetically) playing card k
			int manaLeft = this.getMana() - hand[k].getManacost();
			
			//if playing card k clears the player's mana
			//or if the leftover mana is less than the mana cost of the least "expensive" card in the ordered array
			//no need to try and combine this card with other in the player's hand
			//list containing card k added to overall comboList
			//also check if k is the last card in hand
			//if so this will be a combo on its own and for loop terminates
			if (manaLeft == 0 || manaLeft < hand[hand.length-1].getManacost() || k == hand.length-1) {
				comboList.add(combo);
				//reference combo is re-assigned a new ArrayList obj (empty)
				combo = new CardCombo();
				//move on to k+1 th card in hand
				continue;
			}
			
			//if the leftover mana is bigger than the least expensive card in the array
			//iterate over the array, starting from k+1th to check possible combos
			else {
				for (int i = k+1; i<hand.length; i++) {
					//if the next card's cost clears leftover mana
					//add card to current combo, add combo to combo list
					//reset combo and reset mana to player's mana - cost of card k
					if (hand[i].getManacost() == manaLeft) {
						combo.add(hand[i]);
						comboList.add(combo);
						combo = new CardCombo();
						manaLeft = this.getMana() - hand[k].getManacost();
					}
					//if leftover mana is more than enough to add the next card to the current combo
					//add card to combo, update leftover mana
					else if (hand[i].getManacost()<manaLeft) {
						combo.add(hand[i]);
						manaLeft -= hand[i].getManacost();
						//if leftover mana is less than cheapest card no need to check rest of the array
						if (manaLeft < hand[hand.length-1].getManacost())break;
					}
					//if leftover mana is not enough for next card, move on to next card
					else {
						continue;
					}
				}
			}
		}		
		return comboList;
	}

	//return card(s) to be played by comp player
		public ArrayList <Card> playComputerCards(){
			
			//getting the list of possible card combinations
			ArrayList <CardCombo> possCombos = this.cardCombos(this.playableCards());
			
			//extracting the best card combination based on logic in chooseCombo method
			CardCombo choosenCombo = this.chooseCombo(possCombos); 
			
			//returning the choosen combination of cards 
			return choosenCombo.getCardCombo();
		}
		
		private CardCombo chooseCombo(ArrayList<CardCombo> possCombos) {
			Collections.sort(possCombos);
			return possCombos.get(possCombos.size()-1);			
		}
		
		
		
	
	//==================OVERALL NOTES ON COMP PLAYER CLASS========================//
	
	/***
	1)calculate all possible card combination that can be played given mana available
		for each combination calculate utility (-/+)
		if card has special ability (+/- X)
		weight each combination based on a,b,c par (health, card no.)
		select best combination (highest score)
	
	2)card(s) to be played selection returned
	
	3)for each card in selection scan board to find playable tiles
		for each card order tiles in list based on a,b,c par 
	
	4)compare playable tiles for cards to be played to ensure no overlap
		if overlap, select card with highest U
	
	5)set played card to true
	
	6)Scan board for all friendly units
	
	7)For each friendly unit check if:
		has enemy in range
		is in enemy range
		survive counter
		obtain kill
		no. attack left
		no. moves left
		has relevant special ability
		target (health + attack)
	
	8) calculate best move for each unit on the board based on ^:
		player's health
		opponent's health
		no. of cards
	
	9) display move and attack action(s)
		for each move update game
	
	10) check that played cards and move units are true (no more moves left to make)
	
	11) end turn	
	
		
	 ***/


}



	
