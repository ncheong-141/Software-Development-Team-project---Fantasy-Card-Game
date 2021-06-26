package events.tileplaystates;

import java.util.ArrayList;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;


// TEMP
import akka.actor.ActorRef;


/*
 * GameplayContext holds the current state of the game and the behaviour of which varies depending on the current state
 * " The Context class doesn't implement state-specific behavior directly. Instead, Context refers to the State interface 
 *   for performing state-specific behavior (state.operation()), which makes Context independent of how state-specific behavior is implemented"
 */
public class GameplayContext {

	// Attributes
	private GameplayStates				currentStates;

	private Card 						loadedCard; 
	private Class<?> 					cardClasstype; 

	private Unit						loadedUnit; 
	private GameState					gameStateRef; 
	private String						tileFlag; 
	int 								tilex;
	int 								tiley; 
	
	// Temp variables just to make code work

	ActorRef out; 
	
	// Constructor
	public GameplayContext(GameState gameState, ActorRef out, int tilex, int tiley) {
		this.currentStates = null; 
		this.gameStateRef = gameState;
		this.tilex = tilex;
		this.tiley = tiley; 

		// Temp variable setting 
		this.out = out; 
	}
	
	// Class methods
	
	// State method (called from within Context) 
	public void executeAndCreateSubStates() {
		
		
		/* --------------------------------------------------------------------------------
		 * Check if a Unit has been clicked/is on the tile clicked (or if its empty)
		 * -------------------------------------------------------------------------------- */
		
		// Flag needed to determine what what substate is required (e.g. SummonMonster or CastSpell)
		if (checkUnitPresentOnTile(gameStateRef, tilex, tiley)) {
			this.setTileFlag("unit");
		}
		else {
			this.setTileFlag("empty"); 
		}
		
		
		
		/*
		 * Combination of different user inputs to substates 
		 * 
		 *   Unit + card selected 	-> Cast spell (if spell card)
		 *   Unit only				-> Display unit actions 
		 *   Card + Empty 			-> SummonMonster (if Monster card) 
		 *   Unit + Empty 			-> Move unit
		 *   Unit + Unit			-> Attack Unit (if enemy) 
		 */
		System.out.println("In GameplayContext.");

		// Execute state created from previous user input (specified in TileClicked)
		currentStates.execute(this);
	}
	
	
	
	
	// Getters and setters
	public GameplayStates getCurrentStates() {
		return currentStates;
	}

	public void addCurrentState(GameplayStates state) {
		this.currentStates = state;
	}
	
	public Card getLoadedCard() {
		return loadedCard;
	}

	public void setLoadedCard(Card loadedCard) {
		this.loadedCard = loadedCard;
	}

	public GameState getGameStateRef() {
		return gameStateRef;
	}

	public void setGameStateRef(GameState gameStateRef) {
		this.gameStateRef = gameStateRef;
	}

	public Class<?> getCardClasstype() {
		return cardClasstype;
	}

	public void setCardClasstype(Class<?> classtype) {
		this.cardClasstype = classtype;
	}

	public Unit getLoadedUnit() {
		return loadedUnit;
	}

	public void setLoadedUnit(Unit loadedUnit) {
		this.loadedUnit = loadedUnit;
	}

	public String getTileFlag() {
		return tileFlag;
	}

	public void setTileFlag(String tileFlag) {
		this.tileFlag = tileFlag;
	}

	/* Helper methods */
	private boolean checkUnitPresentOnTile(GameState gameState, int tilex, int tiley) {	
		return (gameState.getBoard().getTile(tilex , tiley).getUnitOnTile() != null);
	}
	
	// Debug
	public void debugPrint() {
		
		// TileFlag
		System.out.println("TileFlag: " + tileFlag); 
		
		// Card
		if (loadedCard != null) {
			System.out.println("Card info:\n" + loadedCard.getCardname() + "\nHP: " + loadedCard.getBigCard().getHealth() + "\nAttack: " + loadedCard.getBigCard().getAttack());
			System.out.println("Card type: " + cardClasstype); 
		}
		
		// Add unit print
		
		// Tile print
		System.out.println("Tile (x,y) : (" + tilex + "," + tiley + ")");

	}
	
	
}
