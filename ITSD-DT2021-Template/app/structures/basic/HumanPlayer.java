package structures.basic;

import commands.BasicCommands;

import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import actors.GameActor;
import akka.actor.ActorRef;
import events.CardClicked;
import events.EndTurnClicked;
//import events.tileplaystates.*;
import structures.GameState;



public class HumanPlayer extends Player {
	private GameState gameState;
	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
//	private SummonMonsterSubState summonM;
//	private GameplayContext context;
	
	
	public HumanPlayer() {
		super();
		this.deck.deckOne();
	}
		
	//call execute method to play game in summon object
	public void summonMonster() {
//		this.summonM.execute(context);
	}
	


//	@Override  give 3 card in the first round from deckOne
	
//	public void firstThreeCards() {
//		// TODO Auto-generated method stub
//		super.firstThreeCards();
//	}

//	@Override assign deckOne for HumanPlayer
//	public void setDeck(ArrayList<Card> d) {
//		// TODO Auto-generated method stub
//		this.d = deck.deckTwo(); // need Deck to return deckOne list
//	}

/*	
	public void firstThreeCards(ArrayList<Card> hand) {
//		this.hand = new Hand(this.deck.getDeck());	
				
		System.out.println("In setPlayerHand()");
		for (Card d : this.deck.getDeck()) {
			System.out.println(d.getCardname());
		}
	}
	
*/
	
	// To do:
	// Move hand instantiation/set up from gamestate into Player constructor
	
	
	
	
}



	
