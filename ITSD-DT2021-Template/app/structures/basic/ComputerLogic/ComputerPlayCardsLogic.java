package structures.basic.ComputerLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import structures.basic.Board;
import structures.basic.Card;
import structures.basic.ComputerPlayer;
import structures.basic.Hand;
import structures.basic.Monster;
import structures.basic.Tile;

public class ComputerPlayCardsLogic {
	private Hand hand;
	private Board gameBoard;
	private ComputerPlayer player;
	
	public ComputerPlayCardsLogic(ComputerPlayer p) {
		this.player = p;
		this.hand = p.getHand();
		this.gameBoard = p.getGameBoard();
		System.out.println("Cards in comp player's hand \n");
		for (Card c : this.hand.getHandList()) System.out.println(c.getCardname() + " manacost " + c.getManacost());
	}
	
	
	public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> playCards(){
		
		
		ArrayList<Card> cardList = this.playableCards();
		System.out.println("in comp player card logic. playable cards list size: " + cardList.size());
		//getting the list of possible card combinations
		ArrayList <CardCombo> possCombos = this.cardCombos(cardList);
		
		CardCombo combinationToBePlayed = this.chooseCombo(possCombos);
		
		//extracting the best card combination based on logic in chooseCombo method
		ArrayList<ComputerInstruction> cardsToBePlayed = this.computeMoves(combinationToBePlayed); 
		
		//returning the choosen combination of cards 
		return cardsToBePlayed;
		
	}
	//========================PLAYING CARDS METHODS ===================================//
	
	/**
	 * playableCards
	 * @return ArrayList of Card objects
	 * List contains all cards that the player could play. 
	 * The individual mana cost of every single card in the last is less than or equal to mana currently available to player
	 */
		//method returns playable cards from the player's hand based on the mana cost
		private ArrayList <Card> playableCards(){
			ArrayList<Card> cardList = new ArrayList<Card>();
			
			System.out.println("player tot mana: " + player.getMana());
			for (Card c : this.hand.getHandList()) {
				System.out.print(c.getManacost());
				if (c.getManacost() <= player.getMana()) { 
					System.out.println("adding card");
					cardList.add(c);
				
				}
			
			}
			return cardList;
		}
		

		
		/**
		 * @param list of card objects
		 * @return list of all possible playable combinations of cards from the given list
		 * card permutation algorithm provided at the bottom of this class
		 */
		
		private ArrayList<CardCombo> cardCombos(ArrayList <Card> list){
			
			//method will return a list of combinations of cards
			//a card combination (combo) is represented as a set
			ArrayList<CardCombo> comboList = new ArrayList<CardCombo>();
			
			if (list.size() == 0) {
				System.out.println("no playable cards at this time");
				return comboList;
			}
			
			//converting playablecards list into an array to an array for ease of indexing
			Card [] playableCards = new Card [this.playableCards().size()-1];
			for (int i = 0; i<playableCards.length; i++) {
				playableCards[i] = list.get(i);

			}
			
			Arrays.sort(playableCards);

			//instantiating a combo object (as an array list of card objects)
			CardCombo combo = new CardCombo();
			
			//iterating over the hand array
			for (int k = 0; k<playableCards.length; k++) {
				//adding the card at position kth in hand to a combo
				combo.add(playableCards[k]);
				
				//creating variable to track how much mana the player has left after (hypothetically) playing card k
				int manaLeft = player.getMana() - playableCards[k].getManacost();
				
				//if playing card k clears the player's mana
				//or if the leftover mana is less than the mana cost of the least "expensive" card in the ordered array
				//no need to try and combine this card with other in the player's hand
				//list containing card k added to overall comboList
				//also check if k is the last card in hand
				//if so this will be a combo on its own and for loop terminates
				if (manaLeft == 0 || manaLeft < playableCards[playableCards.length-1].getManacost() || k == playableCards.length-1) {
					comboList.add(combo);
					//reference combo is re-assigned a new ArrayList obj (empty)
					combo = new CardCombo();
					//move on to k+1 th card in hand
					continue;
				}
				
				//if the leftover mana is bigger than the least expensive card in the array
				//iterate over the array, starting from k+1th to check possible combos
				else {
					for (int i = k+1; i<playableCards.length; i++) {
						//if the next card's cost clears leftover mana
						//add card to current combo, add combo to combo list
						//reset combo and reset mana to player's mana - cost of card k
						if (playableCards[i].getManacost() == manaLeft) {
							combo.add(playableCards[i]);
							comboList.add(combo);
							combo = new CardCombo();
							manaLeft = player.getMana() - playableCards[k].getManacost();
						}
						//if leftover mana is more than enough to add the next card to the current combo
						//add card to combo, update leftover mana
						else if (playableCards[i].getManacost()<manaLeft) {
							combo.add(playableCards[i]);
							manaLeft -= playableCards[i].getManacost();
							//if leftover mana is less than cheapest card no need to check rest of the array
							if (manaLeft < playableCards[playableCards.length-1].getManacost())break;
						}
						//if leftover mana is not enough for next card, move on to next card
						else {
							continue;
						}
					}
				}
			}
			
			comboList.removeIf(c -> !(this.playableCombo(c)));
			
			return comboList;
		}
		
		/**
		 * 
		 * @param combo = CardCombo object. This object represent a possible card combination 
		 * it is represented as a set of cards
		 * @return method returns true if and only if every card in the combination could be summoned on the current board
		 */
	
		private boolean playableCombo(CardCombo combo) {
			//number of playable tiles available to computer player
			//number of tiles adj to a friendly unit
			int tilesAvailable = this.gameBoard.allSummonableTiles(player).size();
			int tilesNeeded = 0;
			
			if (tilesAvailable <=0 || combo.getCardCombo().isEmpty()) return false;
			//count how many cards in the card combo need to be played in a tile adj to a friendly unit
			//NOTE: need to implement check to see if board still has capacity (max number of units X*Y)
			for (Card c : combo.getCardCombo()) {
				if (!(c.playableAnywhere())) tilesNeeded++;
			}
			
			//compare the diff between adj friendly tiles available and needed
			int delta = tilesAvailable - tilesNeeded;
			
			//if diff = 0 or positive then terminate yielding true
			if (delta >= 0) return true;
			//else, place simulate placing dummy unit to tiles in all summonable tiles list in order
			//if addition of a dummy results in pos delta terminate yielding true
			else {
				Monster dummy = new Monster(); 
				dummy.setOwner(player);
				int i =0;
				//while the diff remains negative AND there are still tiles to test in summonable tiles
				//loops places dummy monster on tile and recalculates delta
				while (delta < 0 && i<this.gameBoard.allSummonableTiles(player).size()) {
					this.gameBoard.allSummonableTiles(player).get(i).addUnit(dummy);
					delta = this.gameBoard.allSummonableTiles(player).size() - tilesNeeded;
					i++;
				}
				//remove dummy unit from board 
				if (dummy.getPosition() != null) {
					Tile t = this.gameBoard.getTile(dummy.getPosition().getTilex(), dummy.getPosition().getTiley());
					t.removeUnit();
				}
				//end of else condition, re-check delta to see if simulated placing of friendly units made combination payable
				if (delta >= 0) return true;
				else return false;
			}
		}	
		
		/**
		 * 
		 * @param possCombos = list of playable card combinations
		 * @return one CardCombo obj, which is the best card combination out of the given list
		 */
			private CardCombo chooseCombo(ArrayList<CardCombo> possCombos) {
				if (possCombos.isEmpty()) return new CardCombo();
				if (player.getHealth() <= player.getHPBenchMark()) {
					for (CardCombo cb : possCombos) {
						cb.calcDefenseScore();
					}
				}
				
				else {
					for (CardCombo cb : possCombos) cb.calcAttackScore();
				}
				
				Collections.sort(possCombos);
				return possCombos.get(possCombos.size()-1);			
			}
		
		/**
		 * 
		 * @param combo = CardCombo object
		 * @return a list of ComputerInstruction objects
		 * each computer instruction object contains a card from the given card combination and the target tile where to play it
		 */
		//methods returns list of cards that computer player wants to play
		//as a list of ComputerMoves objs (Card + target tile)
			private ArrayList<ComputerInstruction> computeMoves(CardCombo combo){
				
				ArrayList<ComputerInstruction> compInstructions = new ArrayList<ComputerInstruction>();
				if (combo.isEmpty()) return compInstructions;
				ArrayList <Tile> tiles = null;
				Tile t = null;
				int i = 0;
				for (Card c : combo.getCardCombo()) {
					if (c.playableAnywhere()) {
						t = this.gameBoard.allFreeTiles().get(0); // will need to fine tune logic, for now this is hard-coded to pick first tile
						compInstructions.add(new ComputerInstruction(c,t));
					}
					else {
						tiles= this.gameBoard.allSummonableTiles(player);
						t = tiles.get(i);
						compInstructions.add(new ComputerInstruction(c,t));
						
					}
				}
				
				return compInstructions;
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
						8.1.3) reset mana left to total mana - k mana cost
						8.1.4) reset combo (new empty combo) and continue
					8.2) else if mana cost of i is < mana left
						8.2.1) add i to current combo
						8.2.2) update mana left and continue
						8.2.3) if new left mana value is less than last card in hand break
					8.3) else continue 
			9) terminate yielding list of combos

			
			****/
}
