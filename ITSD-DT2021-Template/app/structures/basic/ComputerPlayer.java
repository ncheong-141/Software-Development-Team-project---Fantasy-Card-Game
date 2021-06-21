package structures.basic;

import commands.BasicCommands;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import events.CardClicked;
import events.EndTurnClicked;
import structures.GameState;



public class ComputerPlayer extends Player {
	
	
	public ComputerPlayer() {
		super();

	}

	

	// assign a fixed set of card for ComputerPlayer as deckTwo
	@Override
	public void assignDeck() {
		this.deck.deckTwo();
	}


}



	
