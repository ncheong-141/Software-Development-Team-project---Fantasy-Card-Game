package structures.basic.ComputerLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.ComputerPlayer;
import structures.basic.Hand;
import structures.basic.Monster;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.abilities.Ability;

/**
 * 
 * @author Chiara Pascucci and Yufen Chen
 * this class contains and handles the logic used by the computer player to decide what cards to play
 * this class is instantiated within computer player
 * the public method of this class is called by computer player is called to get a list of computer instructions
 * the list is made up ComputerInstruction objects that hold a reference to the card to be played and the tile to play it on
 *
 */

public class ComputerPlayCardsLogic {
	private Hand hand;
	private ComputerPlayer player;
	
	//Constructor
	public ComputerPlayCardsLogic(ComputerPlayer p) {
		this.player = p;
		this.hand = p.getHand();
		System.out.println("===========card logic method============== \nCards in comp player's hand \n");
		for (Card c : this.hand.getHandList()) System.out.println(c.getCardname() + " manacost " + c.getManacost());
	}
	
	/**
	 * 
	 * @param gameBoard
	 * @return list of computer instruction objects
	 * this method uses the private methods in this class to calculate all available valid card combinations and select the best one
	 */
	
	public ArrayList<structures.basic.ComputerLogic.ComputerInstruction> playCards(Board gameBoard){
		
		ArrayList<Card> cardList = this.playableCards();
		//System.out.println("in comp player card logic. playable cards list size: " + cardList.size());
		//getting the list of possible card combinations
		ArrayList <CardCombo> possCombos = this.cardCombos(cardList, gameBoard);
		
		CardCombo combinationToBePlayed = this.chooseCombo(possCombos);
		
		HashMap<Tile, Card> cardAndTargetCombo = this.matchCardToTile(combinationToBePlayed, gameBoard);
		
		//extracting the best card combination based on logic in chooseCombo method
		ArrayList<ComputerInstruction> cardsToBePlayed = this.computeMoves(cardAndTargetCombo); 
		
		//returning the choosen combination of cards 
		return cardsToBePlayed;
		
	}
	
	
	/**
	 * 1.
	 * playableCards
	 * @return ArrayList of Card objects
	 * List contains all cards that the player could play. 
	 * The individual mana cost of every single card in the last is less than or equal to mana currently available to player
	 */
		//method returns playable cards from the player's hand based on the mana cost
		private ArrayList <Card> playableCards(){
			ArrayList<Card> cardList = new ArrayList<Card>();
			
			//System.out.println("player tot mana: " + player.getMana());
			for (Card c : this.hand.getHandList()) {
				//System.out.print(c.getManacost());
				if (c.getManacost() <= player.getMana()) { 
					//System.out.println("adding card line 60");
					cardList.add(c);
				
				}
			
			}
			return cardList;
		}
		
		/**
		 * 2.
		 * @param list of card objects
		 * @return list of all possible playable combinations of cards from the given list
		 * card permutation algorithm provided at the bottom of this class
		 */
		
		private ArrayList<CardCombo> cardCombos(ArrayList <Card> list, Board gameBoard){
			//method will return a list of combinations of cards
			//a card combination (combo) is represented as a set
			ArrayList<CardCombo> comboList = new ArrayList<CardCombo>();
			if (list.size() == 0) {
				//System.out.println("no playable cards at this time");
				return comboList;
			}
			//converting playablecards list into an array to an array for ease of indexing
			Card [] playableCards = new Card [list.size()];
			for (int i = 0; i<playableCards.length; i++) {
				playableCards[i] = list.get(i);
			}
			Arrays.sort(playableCards);
			
			//System.out.println("=== here is the sorted array====");
			
			//for (int i = 0; i<playableCards.length; i++) System.out.println(playableCards[i].getCardname());
			//instantiating a combo object (as an array list of card objects)
			CardCombo combo = new CardCombo();
			
			int manaLeft = player.getMana(); //System.out.println( "mana left at the start : " + manaLeft);
			
			//iterating over the hand array
			for (int k = 0; k<playableCards.length; k++) {
				//System.out.println("==========Considering card " +k +"th  "+ playableCards[k].getCardname() + " =========="
					//+ "\n mana left: " + manaLeft);
				
				if (playableCards[k].getManacost() <= manaLeft) {
					//System.out.println("adding card "+k+"th to current combo");
					//adding the card at position kth in list to a combo
					combo.add(playableCards[k]);
					//creating variable to track how much mana the player has left after (hypothetically) playing card k
					manaLeft -= playableCards[k].getManacost(); //System.out.println("mana left after adding card: " + manaLeft);
				}
				
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
					manaLeft = player.getMana();
					//move on to k+1 th card in hand
					//System.out.println("finishing the combo, mana left reset :" + manaLeft);
					continue;
				}
				
				//if the leftover mana is bigger than the least expensive card in the array
				//iterate over the array, starting from k+1th to check possible combos
				
					for (int i = k+1; i<playableCards.length; i++) {
						//System.out.println("printing i: " +i + "printin mana left: " + manaLeft);
						//if the next card's cost clears leftover mana
						//add card to current combo, add combo to combo list
						//reset combo and reset mana to player's mana - cost of card k
						if (playableCards[i].getManacost() == manaLeft) {
							combo.add(playableCards[i]);
							comboList.add(combo);
							combo = new CardCombo();
							manaLeft -= playableCards[i].getManacost();
						}
						//if leftover mana is more than enough to add the next card to the current combo
						//add card to combo, update leftover mana
						else if (playableCards[i].getManacost()<manaLeft) {
							combo.add(playableCards[i]);
							
							manaLeft -= playableCards[i].getManacost();
							//System.out.println("adding card " +i+"th at line 147. ManaLeft : " +manaLeft);
							//if leftover mana is less than cheapest card no need to check rest of the array
							if (manaLeft < playableCards[playableCards.length-1].getManacost()) {
								//System.out.println("breaking");
								
								break;
							}
						}
						//if leftover mana is not enough for next card, move on to next card
						
						continue;
					}
				
			}
			
			comboList.removeIf(c -> !(this.playableCombo(c, gameBoard)));
			
			return comboList;
		}
		
		/**
		 * 2a (helper method to method 
		 * @param combo = CardCombo object. This object represent a possible card combination 
		 * it is represented as a set of cards
		 * @return method returns true if and only if every card in the combination could be summoned on the current board
		 */
	
		private boolean playableCombo(CardCombo combo, Board gameBoard) {
			//number of playable tiles available to computer player
			//number of tiles adj to a friendly unit
			int tilesAvailable = gameBoard.allSummonableTiles(player).size();
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
				while (delta < 0 && i< gameBoard.allSummonableTiles(player).size()) {
					gameBoard.allSummonableTiles(player).get(i).addUnit(dummy);
					delta = gameBoard.allSummonableTiles(player).size() - tilesNeeded;
					i++;
				}
				//remove dummy unit from board 
				if (dummy.getPosition() != null) {
					Tile t = gameBoard.getTile(dummy.getPosition().getTilex(), dummy.getPosition().getTiley());
					t.removeUnit();
				}
				//end of else condition, re-check delta to see if simulated placing of friendly units made combination payable
				if (delta >= 0) return true;
				else return false;
			}
		}	
		
		/**
		 * 3.
		 * @param combo (CardCombo object)
		 * @param gameBoard
		 * @return Map of tile-card combination, representing a card to be played and the target tile
		 * A HashMap is used with the tile as key to ensure no two cards are played on the same tile
		 */
		
		private HashMap <Tile, Card> matchCardToTile(CardCombo combo, Board gameBoard){
			HashMap <Tile, Card> map = new HashMap<Tile, Card>();
			
			ArrayList<Tile> possTileList = gameBoard.allSummonableTiles(player);
			
			ArrayList<Card> cardList = new ArrayList<Card>(combo.getCardCombo());
			ArrayList<Spell> spellList = new ArrayList<Spell>();
			ArrayList<Card> monsterList = new ArrayList<Card>();
		
			
			//handling monster card tile allocations
			for (Card c : cardList) {
				if (c.getClass() == Spell.class) spellList.add((Spell)c);
				else monsterList.add(c);
			}
			
			if (monsterList.size() <= possTileList.size()) {
			
				Iterator <Card> itCard = monsterList.iterator();
				Iterator <Tile> itTile = possTileList.iterator();
				
				while (itCard.hasNext() && itTile.hasNext()) {
					map.put(itTile.next(), itCard.next());
				}
				
			}
			
			else {
				Monster dummy = new Monster();
				dummy.setOwner(player);
				int i =0;
				
				do {
					possTileList.get(i).addUnit(dummy);
					possTileList = gameBoard.allSummonableTiles(player);
					i++;
				}while (monsterList.size() > possTileList.size() );	
				
			}
			
			//handling spell cards tile allocation
		
			for (Spell spell : spellList) {
				Tile tilez = null;
				ArrayList<Ability> abilityList = spell.getAbilityList();
				Ability a = abilityList.get(0);
				if (a.targetEnemy() && a.getTargetType() == Avatar.class ) {
					tilez = gameBoard.enemyAvatarTile(player);
					map.put(tilez,spell);
				}
				
				else if (!a.targetEnemy() && a.getTargetType() == Avatar.class) {
					tilez = gameBoard.ownAvatarTile(player);
					map.put(tilez, spell);
				}
				
				else if (a.getTargetType() != Avatar.class) {
					if (a.targetEnemy()) {
						tilez = gameBoard.enemyTile(player).get(0);
						if (tilez != null) map.put(tilez, spell);
					}
					else {
						tilez = gameBoard.friendlyTile(player).get(0);
						if (tilez != null) map.put(tilez, spell);
					}
				}

			}
			
			return map;
		}
		
		/**
		 * 4.
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
		 * 5.
		 * @param combo = CardCombo object
		 * @return a list of ComputerInstruction objects
		 * each computer instruction object contains a card from the given card combination and the target tile where to play it
		 */
		//methods returns list of cards that computer player wants to play
		//as a list of ComputerMoves objs (Card + target tile)
			private ArrayList<ComputerInstruction> computeMoves(HashMap<Tile, Card> combo){
				
				ArrayList<ComputerInstruction> compInstructions = new ArrayList<ComputerInstruction>();
				if (combo.isEmpty()) return compInstructions;
		
				Set<Tile> keySet = combo.keySet();
				
				for (Tile t : keySet) {
					compInstructions.add(new ComputerInstruction (combo.get(t), t));
				}
				
				return compInstructions;
			}
			
			//!!NOTE!!!//need to update this if we are to keep it!
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
