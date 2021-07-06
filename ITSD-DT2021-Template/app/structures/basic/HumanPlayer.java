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

//	private SummonMonsterSubState summonM;

	
	public HumanPlayer() {
		super();
		this.deck = new Deck();
		this.deck.deckOne();
	}
		
	//call execute method to play game in summon object
	public void summonMonster() {
//		this.summonM.execute(context);
	}
	
	public String toString() {
		return "Player One";
	}
	



	
	
	
	
}



	
