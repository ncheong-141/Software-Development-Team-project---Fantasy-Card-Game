package structures.basic;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import events.gameplaystates.unitplaystates.*;


import structures.GameState;
public class ComputerPlayer extends Player {
	
//attack needed attributes
	private AIUnitStateController a;
	private HumanPlayer playerOne;
	private HumanPlayer playerTwo;
	private GameState gameState;
	private ArrayList<Tile> attackerTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;  //a list of attackable enemy 
	private ArrayList<Tile> attackRange;
	private Tile target; //attack target
	private Tile attacker; 
//attack needed attributes

	
	Board gameBoard;
	boolean playedAllPossibleCards;
	boolean madeAllPossibleMoves;
	Monster dummy;
	
	public ComputerPlayer(Deck d) {
		super();
		this.madeAllPossibleMoves = false;
		this.playedAllPossibleCards = false;
		this.dummy = new Monster();
		dummy.setOwner(this);
	}
	
	public ComputerPlayer(Deck d, Board b) {
		super();
		this.gameBoard = b;
		this.madeAllPossibleMoves = false;
		this.madeAllPossibleMoves = true;
		this.dummy = new Monster();
		dummy.setOwner(this);
	}
	
	public ComputerPlayer() {
		super(); 
		this.madeAllPossibleMoves = false;
		this.playedAllPossibleCards = false;
		
		this.deck = new Deck();
		this.deck.deckTwo();
		this.hand = new Hand();
		this.hand.initialHand(deck);
	}
	
	public void setGameBoard(Board b) {
		this.gameBoard = b;
	}
	
	
	//========================PLAYING CARDS METHODS ===================================//
	
	/**
	 * METHOD 1 
	 * 
	 */
	//method returns playable cards from the player's hand based on the mana cost
	private ArrayList <Card> playableCards(){
		ArrayList<Card> cardList = new ArrayList<Card>();
		for (Card c : this.hand.getHandList()) if (c.getManacost() <= this.getMana()) cardList.add(c);
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
		
		//!!!!!NOTE: order array - card will implement comparable on mana cost
		
		//instantiating a combo object (as an array list of card objects)
		CardCombo combo = new CardCombo();
		
		//iterating over the hand array
		for (int k = 0; k<playableCards.length; k++) {
			//adding the card at position kth in hand to a combo
			combo.add(playableCards[k]);
			
			//creating variable to track how much mana the player has left after (hypothetically) playing card k
			int manaLeft = this.getMana() - playableCards[k].getManacost();
			
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
						manaLeft = this.getMana() - playableCards[k].getManacost();
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
	//a combination is playable iff all the cards in the combination can be played on the abord (playable tiles are available)
	//method return true iff card combination is playable
	private boolean playableCombo(CardCombo combo) {
		//number of playable tiles available to computer player
		//number of tiles adj to a friendly unit
		int tilesAvailable = this.gameBoard.allSummonableTiles(this).size();
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
			
			int i =0;
			//while the diff remains negative AND there are still tiles to test in summonable tiles
			//loops places dummy monster on tile and recalculates delta
			while (delta < 0 && i<this.gameBoard.allSummonableTiles(this).size()) {
				this.gameBoard.allSummonableTiles(this).get(i).addUnit(this.dummy);
				delta = this.gameBoard.allSummonableTiles(this).size() - tilesNeeded;
				i++;
			}
			//remove dummy unit from board 
			if (this.dummy.getPosition() != null) {
				Tile t = this.gameBoard.getTile(this.dummy.getPosition().getTilex(), this.dummy.getPosition().getTiley());
				t.removeUnit();
			}
			//end of else condition, re-check delta to see if simulated placing of friendly units made combination payable
			if (delta >= 0) return true;
			else return false;
		}
	}	
	
		
	// after all actions, ComputerPlayer call computerEndTurn() to ends the turn
	// Removed @Override
	public void endTurn() {
		gameState.computerEnd();
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
					tiles= this.gameBoard.allSummonableTiles(this);
					t = tiles.get(i);
					compInstructions.add(new ComputerInstruction(c,t));
					
				}
			}
			
			return compInstructions;
		}
		
	//====================MOVING OF UNITS ON BOARD METHOD=======================//
	
		public ArrayList<ComputerInstruction> compPlayerMovesUnits(){
			MonsterTileOption [] listofMTO = this.getMonstersOptions(this.allMovableMonsters());
			return this.matchMonsterAndTile(listofMTO);
		}
		
		private ArrayList <Monster> allMovableMonsters(){
			ArrayList <Monster> myMonsters = this.gameBoard.friendlyUnitList(this);
			
			myMonsters.removeIf(m -> (m.getMovesLeft()<=0));
			
			return myMonsters;
		}
		
		private MonsterTileOption[] getMonstersOptions(ArrayList<Monster> list){
			
			//array list implementation
			//ArrayList<MonsterTileOption> monsterTiles = new ArrayList<MonsterTileOption>();
			//for (Monster m : list) {
				//monsterTiles.add(new MonsterTileOption(m, this.gameBoard));
			//}
			MonsterTileOption[] optionList = new MonsterTileOption[list.size()];
			
			for (int i = 0; i<optionList.length; i++) {
				optionList[i] = new MonsterTileOption (list.get(i), this.gameBoard);
			}
			
			return optionList;
		}
	
		private ArrayList<ComputerInstruction> matchMonsterAndTile (MonsterTileOption[] optionList){
			//sorting array based on value of top tile 
			Arrays.sort(optionList);
			
			//method returns an array list of computer instruction objs
			//each of those objs contains a monster to be moved and a target tiles
			ArrayList <ComputerInstruction> compMoves = new ArrayList<ComputerInstruction>();
			
			//this set keep track of tiles that have already been used as a target tile 
			//so no other monster should be added to it
			HashSet <Tile> tileUsed = new HashSet<Tile>();
			
			int k = 0;
			
			//for loop iterates over the list of (monster - list of tiles) objs (MLT) passed to the method
			for (int i = 0; i<optionList.length; i++) {
				//for each MLT the top tile is retrieved (k=0)
				//this is the tile with the highest score
				Tile t = optionList[i].getList().get(k);
			
				//boolean variable for testing purposes
				boolean tileFound = false;
				
				//creating a CI reference
				ComputerInstruction inst = null;
				
				//this checks if the top tile for the given monster (within the MLT obj) has already been used
				if (!(tileUsed.contains(t))){
					//if the best tile has not been used already, a new instruction is created passing it the monster within the MLT obj at optionList[i]
					//and the target tile t
					inst = new ComputerInstruction(optionList[i].getM(), t);
					//adding the new instruction object to the list to be returned 
					compMoves.add(inst);
					//adding the target tile to the tile used set
					tileUsed.add(inst.getTargetTile());
					tileFound = true;
					
					continue;
				}
				else {
					//this part of the code is executed if the tileUsed set already contains target tile t
					do {
						//incrementing k
						k++;
						//to retrieve the new best tile within the MLT obj
						t = optionList[i].getList().get(k);
						
					//checking if the new tile t is in the set already and if there are still tiles to be tested within the MLT obj	
					} while(tileUsed.contains(t)&& k<optionList[i].getList().size());
					
					//once the above loop terminates
					//this condition checks that the do-while loop terminated because a tile was found
					//if tile was found current target tile t is not in set
					if (!tileUsed.contains(t)) {
						tileFound = true;
						//creating a computer instruction with monster with MTL obj and current target tile
						inst = new ComputerInstruction(optionList[i].getM() , t);
						//adding tile to used tile set
						tileUsed.add(t);
						//adding new computer instruction to list to be returned
						compMoves.add(inst);
					}
				}
				//resetting value of k for next loop iteration
				k=0;
			}			
			return compMoves;	
		}
		//=========================inner class===============================//
		
		//this inner class represent a pairing of a monster belonging to comp player
		//and a list of tiles that the given monster can move to
		//each object has a score that is equal to the score of the first tile in the list
		//the list is ordered based on tile score (tile implements comparable)
		static class MonsterTileOption implements Comparable<MonsterTileOption> {
			Monster m; 
			ArrayList<Tile> list;
			double score;
			MonsterTileOption(Monster m, Board b){
				this.m = m;
				this.list = b.unitMovableTiles(m.getPosition().getTilex(), m.getPosition().getTiley(), m.getMovesLeft());
				if(list != null && !(list.isEmpty())) {
					for (Tile t : list) t.calcTileScore(m, b);
					Collections.sort(list);
					this.score = this.list.get(0).getScore();
				}
				else if (list.isEmpty()) this.score = -1.0;
				
			}
			
			public Monster getM() {
				return m;
			}
			
			public ArrayList<Tile> getList(){
				return this.list;
			}
			
			public double getScore() {
				return this.score;
			}
			

			@Override
			public int compareTo(MonsterTileOption o) {
			
				if (this.score > o.getScore()) return 1;
				else if (this.score < o.getScore()) return -1;
				else return 0;
			}
		}
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

//////////////Yufen Attack Method/////

//if wrap all selecting target methods in another class, this is in ComputerPlayer or AI execution class?
	public void smartAttack(){
	    
	    attackerTiles = findAttacker(); //return a list of monster that can perform attack in this turn
	
	    //go through each monster to perform attack
	    for (Tile acttacker:attackerTiles){    
	        targetInRange = getTargetTiles(attacker);
	        if (selectAttackTarget(attacker) == null){  //if no good target, skip attack, check next monster
	            continue;
	        }else{    //if there is good target, perform attack 
	            target = selectAttackTarget(attacker);
	            a.unitAttack(attacker, target);
	            //events/gameplaystates/unitplaystates/AIUnitStateController
	        }
	    }
	}
//////////////////////////Yufen Attack Logic/////////////////////////
/*
new class(findingBestTarget?) function

loop through all attackable monsters can perform attack:

for each attacker:
get attackable tile range for this attacker 
(will get new list of enemy every loop, without those were killed by previous attacker), would this solve the concern that you mentioned?
call target selecting method, if nothing return, for this attacker. choose not to attack

Target Priority:
1.try kill the one with highest attackValue, if none
2.try kill the one with highest attackLeft, if none
3.try kill the one with lowest HP, if can't keill any
4.check if attacker will be killed easily in the next turn after enemy counter attack 
5.otherwise, it's not worth attack

*/






//<helper>
	private Tile selectAttackTarget(Tile t){
	    if( getHightAttackValue(t) == null){
	        getHightAttackLeft(t);
	    }else if(getHightAttackLeft(t) == null){
	        getLowHP(t);   
	    } else if ( getLowHP(t) == null){  
	        harmless(t);
	    } else if (harmless(t) == null){
	    } else {
	        return null;  
	    }
	}


//<helper>
	private Tile getHightAttackValue(Tile t){
	    
	    //from a list of t attackable tiles range, find the tile hold enemy with highest attack value, and check if it can be killed after attack.
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with highest attack times left
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getAttackValue() > chosenE.getUnitOnTile().getAttackValue()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker can kill this one, avoid future threat and counter attack
	    if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}

//<helper>
	private Tile getHightAttackLeft(Tile t){
	    
	    //from a list of t attackable tiles range, find the tile hold enemy with highest attack times left, and check if it can be killed after attack.
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with highest attack times left
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getAttacksLeft() > chosenE.getUnitOnTile().getAttacksLeft()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker can kill this one, avoid future threat and counter attack
	    if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}

//<helper>
	private Tile getLowHP(Tile t){
	    
	    //from a list of t attackable tiles range, find the tile hold enemy with lowest HP left, and check if it can be killed after attack.
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with highest attack times left
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getHP() > chosenE.getUnitOnTile().getHP()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker can kill this one, avoid counter attack
	    if ((chosenE.getUnitOnTile().getHP() - t.getUnitOnTile().getHP())<=0 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}

//<helper>
	private Tile harmless(Tile t){
	    //if couldn't find any enemy to kill, lower down the counter attack damage
	    //from a list of t attackable tiles range, find the tile hold enemy with lowest attackValue(damage value), and make sure if won't be killed after counter attack
	    Tile chosenE = targetInRange.get(0);    //set compared chosenE as the first tile in targetInRange
	
	    //loop through targetInRange, find the enemy with least attackValue
	    for (int i = 1; i < targetInRange.size(); i++) {  
	        if(targetInRange.get(i).getUnitOnTile().getAttackValue() < chosenE.getUnitOnTile().getAttackValue()) {  
	            chosenE = targetInRange.get(i);  
	        }	
	    }
	    // check if attacker will be killed easily in the next turn after enemy counter attack 
	    // having remain HP >=2,somewhat can avoid being first target for HumanPlayer in the next turn
	    // if set HP bar > 0, any HumanPlayer can attack in the next turn
	    if ((t.getUnitOnTile().getHP() - chosenE.getUnitOnTile().getAttackValue()) >= 2 ){  
	        return chosenE;
	    }else { 
	        return null;
	    } 
	}


//<helper>
	private ArrayList<Tile> getTargetTiles(Tile t){
	    targetInRange = gameBoard.calcRange(t);
	    for (Tile rt : targetInRange) {
	        if (rt.getUnitOnTile().getOwner() != playerTwo) {  //tiles don't belong to computer player are enemy's
	            targetInRange.add(rt);
	        }
	    }
	    return targetInRange;
	}

//<helper>
	private ArrayList<Tile> findAttacker(){  // find a list of tiles that hold monsters can perform attack
	    attackerTiles = gameBoard.friendlyTile(playerTwo);
	    
	    for (Tile t : attackerTiles) {
	        if (t.getUnitOnTile().getAttacksLeft() == 0 ) {  // if there is no attack time left, skip this monster in attack action
	            attackerTiles.remove(t);
	        }
	        if ( getTargetTiles(t).size() == 0) {  // if there is no potential target in attackable range, skip this monster in attack action
	            attackerTiles.remove(t);
	        }
	    }
	    return attackerTiles;
	}	
}