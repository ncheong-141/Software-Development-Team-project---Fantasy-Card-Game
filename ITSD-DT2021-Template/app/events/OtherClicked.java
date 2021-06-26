package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		// Reset selected entities 
		gameState.getBoard().setUnitSelected(null);
		gameState.getTurnOwner().getHand().setPlayingMode(false);
		gameState.getTurnOwner().getHand().setSelectedCard(null);

		/*
		 * Will hold:
		 * Deselect Monster
		 * Deselect Card
		 */
		
		// Deselect any Monsters that are selected
		// Could put this in a method stored somewhere else, which can just be called wherever in program
		
	}

}


