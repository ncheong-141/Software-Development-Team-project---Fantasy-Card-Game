package events.gameplaystates;

import java.util.ArrayList;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;


// TEMP
import akka.actor.ActorRef;
import events.gameplaystates.tileplaystates.ITilePlayStates;


/*
 * GameplayContext holds the current state of the game and the behaviour of which varies depending on the current state
 * " The Context class doesn't implement state-specific behavior directly. Instead, Context refers to the State interface 
 *   for performing state-specific behavior (state.operation()), which makes Context independent of how state-specific behavior is implemented"
 */
public class GameplayContext {

	// Attributes
	private ITilePlayStates				currentStates;		// Current Tile state reference for executing

	private Card 						loadedCard; 		// Any Card in select mode from a previous action
	private Class<?> 					cardClasstype; 		// Class type of the Card
	private Unit						loadedUnit; 		// Any Unit that is currently in selected mode from previous action
	private GameState					gameStateRef; 		// GameState reference to use game variables when dealing with gameplay control flow
	private String						tileFlag; 			// Flag for detailing the status of the current tile clicked 
	private Tile						clickedTile;		// Current tile clicked reference
	private boolean						combinedActive;		// Reference for states to indicate multiple Unit states will occur
	public ActorRef 					out; 				// Front end reference			
	
	// Constructor
	public GameplayContext(GameState gameState, ActorRef out, int tilex, int tiley) {
		
		// Setting attributes 
		this.currentStates = null; 
		this.gameStateRef = gameState;
		this.clickedTile = gameState.getBoard().getTile(tilex , tiley); 
		this.out = out; 
	}
	
	
	// State method (called from within Context) 
	public void executeAndCreateUnitStates() {
		
		
		/* -----------------------------------------------------------------------------------------------
		 * Check if a (friendly/enemy) Unit has been clicked/is on the tile clicked (or if its empty)
		 * ----------------------------------------------------------------------------------------------- */
		
		// Flag needed to determine what what Unit state is required (e.g. SummonMonster or CastSpell)
		if (checkUnitPresentOnTile() && (isUnitFriendly())) {
			this.setTileFlag("friendly unit");
		}
		else if (checkUnitPresentOnTile() && (!isUnitFriendly())) {
			this.setTileFlag("enemy unit");
		}
		else {
			this.setTileFlag("empty"); 
		}
		
		
		
		/*
		 * Combination of different user inputs to substates 
		 * 
		 *   Card selected 	+ 	Unit target 	-> Cast spell (if spell card)
		 *   None selected	+	Unit target		-> Display unit actions 
		 *   Card selected 	+ 	Empty target	-> SummonMonster (if Monster card) 
		 *   Unit selected 	+ 	Empty target 	-> Move unit
		 *   Unit selected 	+ 	Unit target		-> Attack Unit (if enemy) or Move and Attack Unit (if enemy)
		 */
		System.out.println("In GameplayContext.");

		// Execute state created from previous user input (specified in TileClicked)
		currentStates.execute(this);
	}
	
	
	
	
	// Getters and setters
	public ITilePlayStates getCurrentStates() {
		return currentStates;
	}

	public void addCurrentState(ITilePlayStates state) {
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
	
	public Tile getClickedTile() {
		return clickedTile; 
	}
	
	public void setClickedTile(Tile newClicked) {
		this.clickedTile = newClicked; 
	}
	public boolean getCombinedActive() {
		return combinedActive;
	}
	
	public void setCombinedActive(boolean c) {
		combinedActive = c;
	}
	
	/* Helper methods */
	private boolean checkUnitPresentOnTile() {	
		return (clickedTile.getUnitOnTile() != null);
	}
	
	public void deselectAllAfterActionPerformed() { 
		
		gameStateRef.deselectAllEntities();
		this.loadedUnit = null;
		this.loadedCard = null;
	}
	
	
	private boolean isUnitFriendly() {
		return clickedTile.getUnitOnTile().getOwner() == gameStateRef.getTurnOwner();
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
		System.out.println("Tile (x,y) : (" + clickedTile.getTilex() + "," + clickedTile.getTiley() + ")");

	}
	
}
