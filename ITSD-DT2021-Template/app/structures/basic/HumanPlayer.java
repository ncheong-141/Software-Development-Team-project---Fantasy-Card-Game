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
	}
		
	//call execute method to play game in summon object
	public void summonMonster() {
//		this.summonM.execute(context);
	}
	
	public String toString() {
		return "Player One";
	}
	



	
	
	
	
}



	
