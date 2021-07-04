package structures.basic;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


import structures.GameState;
public class ComputerPlayer extends Player {
	
	private HumanPlayer playerOne;
	private GameState gameState;
	private ArrayList<Tile> attackTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;
	private ArrayList<Tile> attackRange;


//	private Tile actorT; //for random back up code
//	private Tile enemyT; //for random back up code

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
		this.dummy = new Monster();
		dummy.setOwner(this);
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
		Card [] hand = new Card [this.hand.getHand().size()-1];
		for (int i = 0; i<hand.length; i++) {
			hand[i] = list.get(i);
		}
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

//===========choose by Highest currentHP =============
	public Tile selectAttackerByHP(){
		Tile chosenA = attackTiles.get(0);    //set compared chosenA as the first tile in attackTiles
		for (int i = 1; i < attackTiles.size(); i++) {  //loop through attackTiles
			if(attackTiles.get(i).getUnitOnTile().getHP() > chosenA.getUnitOnTile().getHP()) {  //get loaded monster, if the monster has higher current HP, 
				chosenA = attackTiles.get(i);  //make it  as chosenA to a tile hold attacker
			}	
		}
		return chosenA;
	}

//===========choose by Highest attackValue(HP damage) ============
	public Tile selectAttackerByDamage(){
		Tile chosenA = attackTiles.get(0);    //set compared chosenA as the first tile in attackTiles
		for (int i = 1; i < attackTiles.size(); i++) {  //loop through attackTiles
			if(attackTiles.get(i).getUnitOnTile().getAttackValue() > chosenA.getUnitOnTile().getAttackValue()){ //get loaded monster, if the monster has higher damageHP value, 
				chosenA = attackTiles.get(i); //make it  as chosenA to a tile hold attacker
			}	
		}
		return chosenA;
	}	
//===========choose by Highest attackTimes =============
	public Tile selectAttackerByTimes(){
		Tile chosenA = attackTiles.get(0);    //set compared chosenA as the first tile in attackTiles
		for (int i = 1; i < attackTiles.size(); i++) {  //loop through attackTiles
			if(attackTiles.get(i).getUnitOnTile().getAttacksLeft() > chosenA.getUnitOnTile().getAttacksLeft()){ //get loaded monster, if the monster has higher attack times currently, 
				chosenA = attackTiles.get(i); //make it  as chosenA to a tile hold attacker
			}	
		}
		return chosenA;
	}	
	
//===============choose by Lowest HP========================	
	public Tile selectEnemyByHP(){
		Tile chosenE = targetInRange.get(0); //set compared chosenE as the first Tile in targetInRange
		for (int i = 1; i < targetInRange.size(); i++) {  //loop through targetInRange
			if(targetInRange.get(i).getUnitOnTile().getHP() < chosenE.getUnitOnTile().getHP()) {   //get loaded monster, if the enemy has lower current HP, 
				chosenE = targetInRange.get(i);  //make it  as chosenE to a tile hold enemy target
			}	
		}
		return chosenE;
	}	
	
//////////////////////////////////////////////////////////**ArrayList Generator Method**/////////////////////////////////////////////////////////		
	 
//=============find attack target in range for current gameOwner's=======
//helper method for intellectual ComputerPlayer behaviour, possibly reused to predict HumanPlayer action
	public ArrayList<Tile> getTargetTiles(Tile t){
		ArrayList<Tile> targetInRange = gameBoard.calcRange(t);
		for (Tile rt : targetInRange) {
			if (rt.getUnitOnTile().owner != gameState.getTurnOwner()) {
				targetInRange.add(rt);
			}
		}
		return targetInRange;
	}


//======get a list of Computer monster that can perform attack this turn============
//get rid of those have no attack count left
//get rid of those have nothing to attack
	public ArrayList<Tile> findAttacker(){
		attackTiles = gameBoard.friendlyTile(this);
		
		for (Tile fT : attackTiles) {
			attackRange = gameBoard.calcRange(fT);
			if (fT.getUnitOnTile().getAttacksLeft() == 0 ) {
				attackRange.remove(fT);
			}
			if ( getTargetTiles(fT).size() == 0) {
				attackRange.remove(fT);
			}
		}
		return attackRange;
	}	
		
	// after all actions, ComputerPlayer call computerEndTurn() to ends the turn
	@Override
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

////	==================randomAttack=====back up=====================
//
//	public void randomAttack() {
//		this.actorT = this.getRandomActorT();        //set chosen actorM
//		this.enemyT = this.getRandomEnemyT(actorT);  // set chosen enemyM
//		
//		
//		//=========attack action begin========
//		
//		//sort out attackLeft
//		int attackLeft = actorT.getUnitOnTile().getAttacksLeft()- 1; // set remain attack value	
//		if(attackLeft == 0) { //if no attack left, cool monster
//			actorT.getUnitOnTile().toggleCooldown();
//		}
//		//need animation
//		
//		//sort out HP left
//		int hpLeft = enemyT.getUnitOnTile().getHP();
//		//need animation?
//		
//	}
//		
//	//helper method
//	private Tile getRandomActorT() {
//		actorT = this.getRandomTile(attackTiles); // get a random actoRM
//		return actorT;
//	}	
//	//helper method	
//	private Tile getRandomEnemyT(Tile t) { //given a actor monster, pick random target
//		targetInRange = getTargetTiles(t); //get list of enemy for actorM
//		enemyT = this.getRandomTile(targetInRange); // get one in list randomly
//		return enemyT;
//	}
//	
//	public Tile getRandomTile(ArrayList<Tile> toRandom) {
//		int index = (int) Math.random() * ( toRandom.size() - 0 );
//		return toRandom.get(index);
//	}
////	================randomAttack=====back up=================	
	
