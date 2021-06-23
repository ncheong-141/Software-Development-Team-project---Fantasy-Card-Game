package events;
import structures.basic.*;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */

//player draws a card and mana is drained
public class EndTurnClicked implements EventProcessor{

	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		gameState.getTurnOwner().drawFromDeck(); //draw a card from deck for current turnOwner
		gameState.turnChange();
		changeMana();  //turnOwner has been changed after turnChange()
		
		
	}

	
	
	//after turn is changed ,add mana for current turnOwner
	//empty mana for previous turnOwner
	public void changeMana() {  
		if (gameState.getTurnOwner() == playerOne) {
			playerOne.setMana(gameState.getTurnCount()); 
			playerTwo.setMana(0); 
		}		                           
	
		else {
			playerOne.setMana(0);
			playerTwo.setMana(gameState.getTurnCount());
		}
	}
	
	
	
	
}
