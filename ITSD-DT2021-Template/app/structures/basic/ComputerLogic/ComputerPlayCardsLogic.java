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
	}
	
	//========================PLAYING CARDS METHODS ===================================//
	
		/**
		 * METHOD 1 
		 * 
		 */
		//method returns playable cards from the player's hand based on the mana cost
		private ArrayList <Card> playableCards(){
			ArrayList<Card> cardList = new ArrayList<Card>();
			for (Card c : this.hand.getHandList()) if (c.getManacost() <= player.getMana()) cardList.add(c);
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
					8.1.3) reset mana left to total mana - k mana cost
					8.1.4) reset combo (new empty combo) and continue
				8.2) else if mana cost of i is < mana left
					8.2.1) add i to current combo
					8.2.2) update mana left and continue
					8.2.3) if new left mana value is less than last card in hand break
				8.3) else continue 
		9) terminate yielding list of combos

		
		****/
		
		/**
		 * METHOD 2
		 */
		private ArrayList<CardCombo> cardCombos(ArrayList <Card> list){
			
			//method will return a list of combinations of cards
			//a card combination (combo) is represented as an ArrayList
			ArrayList<CardCombo> comboList = new ArrayList<CardCombo>();
			
			//converting hand to an array for ease of indexing

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
		 * METHOD 3
		 */
		//this method checks if a given card combination is playable
		//a combination is playable iff all the cards in the combination can be played on the board (playable tiles are available)
		//method return true iff card combination is playable
		private boolean playableCombo(CardCombo combo) {
			//number of playable tiles available to computer player
			//number of tiles adj to a friendly unit
			int tilesAvailable = this.gameBoard.allSummonableTiles(player).size();
			int tilesNeeded = 0;
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
		 * METHOD 4
		 * 
		 */
		//return card(s) to be played by comp player
			public ArrayList <ComputerInstruction> playComputerCards(){
				
				//getting the list of possible card combinations
				ArrayList <CardCombo> possCombos = this.cardCombos(this.playableCards());
				
				//extracting the best card combination based on logic in chooseCombo method
				
				ArrayList<ComputerInstruction> cardsToBePlayed = this.computeMoves(this.chooseCombo(possCombos)); 
				
				//returning the choosen combination of cards 
				return cardsToBePlayed;
			}
		
		/**
		 * METHOD 5
		 */
			private CardCombo chooseCombo(ArrayList<CardCombo> possCombos) {
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
		 * METHOD 6
		 */
		//methods returns list of cards that computer player wants to play
		//as a list of ComputerMoves objs (Card + target tile)
			private ArrayList<ComputerInstruction> computeMoves(CardCombo combo){
				ArrayList<ComputerInstruction> compInstructions = new ArrayList<ComputerInstruction>();
				
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
}
