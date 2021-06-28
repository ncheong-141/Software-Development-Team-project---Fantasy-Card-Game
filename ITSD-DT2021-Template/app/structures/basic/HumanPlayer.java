package structures.basic;

import commands.BasicCommands;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import events.CardClicked;
import events.EndTurnClicked;
import structures.GameState;



public class HumanPlayer extends Player {
	

	
	public HumanPlayer(Deck deck) {
		super(deck);
		this.deck.deckOne();
	}

	
	

//	// assign a fixed set of card for HumanPlayer as deckOne
//	@Override
//	public void assignDeck() {
//		this.deck.deckOne();
//	}


	
}



	
