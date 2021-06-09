package structures;


import akka.actor.ActorRef;				// TAKE OUT IF BOARD NO LONGER NEEDS ACTORREF
import structures.basic.Board;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	Board board; 
	
	public GameState(ActorRef out) {
		
		board = new Board(out); 
	}
	
	
	/* Getters*/ 
	public Board getBoard() {
		return board; 
	}
	
}
