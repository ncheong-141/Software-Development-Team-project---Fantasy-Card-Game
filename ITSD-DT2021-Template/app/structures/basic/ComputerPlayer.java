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
	
	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	private Board board;
	private Deck deck;
	
	public ComputerPlayer(Deck deck) {
		super(deck);
	
	}

	
	// after all actions, ComputerPlayer call computerEndTurn() to ends the turn
	@Override
	public void endTurn() {
		gameState.computerEnd();
	}


//	@Override  give 3 card in the first round from deckTwo
	
//	public void firstThreeCards() {
//		// TODO Auto-generated method stub
//		super.firstThreeCards();
//	}


//	@Override assign deckOne for HumanPlayer
//	public void setDeck(ArrayList<Card> d) {
//		// TODO Auto-generated method stub
//		this.d = deck.deckTwo(); // need Deck to return deckTwo list
//	}

	
	
	
	
/*	public void selectUnit() {
		ArrayList<Monster> toPlay= board.friendlyUnitList(playerTwo);
		for (Monster m : toPlay) {
			m.getAttacksLeft();
			m.getHP();
			m.getAttackRange();
			}
	}
*/	
//
//	// assign a fixed set of card for ComputerPlayer as deckTwo
//	@Override
//	public void setDeck() {
//		this.deck.deckTwo();
//	}


}



	
