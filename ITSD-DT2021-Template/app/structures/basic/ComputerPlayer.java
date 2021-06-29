package structures.basic;
import commands.*;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import structures.GameState;
import events.*;
import structures.basic.*;

public class ComputerPlayer extends Player {
	
	private EndTurnClicked e;
	
	public ComputerPlayer(Deck deck) {
		super(deck);
		this.deck.deckTwo();

	}

	
	public void endTurn() {
		e.processEventComputer();
	}
	
	
//
//	// assign a fixed set of card for ComputerPlayer as deckTwo
//	@Override
//	public void assignDeck() {
//		this.deck.deckTwo();
//	}


}



	
