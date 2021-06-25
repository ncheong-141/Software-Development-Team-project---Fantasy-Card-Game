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
	private Unit						loadedUnit; 
	private GameState					gameStateRef; 
	private Class<?> 					cardClasstype; 
	private String						tileFlag; 
	
	// Temp variables just to make code work
	int tilex;
	int tiley; 
	ActorRef out; 
	
	// Constructor
	public GameplayContext(GameState gameState, ActorRef out, int tilex, int tiley) {
		this.currentStates = null; 
		this.gameStateRef = gameState;

		// Temp variable setting 
		this.out = out; 
		this.tilex = tilex;
		this.tiley = tiley; 

	}
	
	// Class methods
	
	// State method (called from within Context) 
	public void executeAndCreateSubStates() {
		
		/*
		 * Combination of different user inputs to substates 
		 * 
		 *   Unit + card selected 	-> Cast spell (if spell card)
		 *   Unit only				-> Display unit actions 
		 *   Card + Empty 			-> SummonMonster (if Monster card) 
		 *   Unit + Empty 			-> Move unit
		 *   Unit + Unit			-> Attack Unit (if enemy) 
		 */
		
		// Execute state created from previous user input
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

	
	
	
	
}
