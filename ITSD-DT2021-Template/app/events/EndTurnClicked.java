package events;
import structures.basic.*;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;
import commands.*;

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

	@Override
	public void processEvent (ActorRef out, GameState gameState, JsonNode message) {
		
		gameState.endTureStateChange(out);
//		if (gameState.getTurnOwner() == gameState.getPlayerTwo()) {
//			ComputerPlayerTurn compTurn = new ComputerPlayerTurn();
//			compTurn.processComputerActions(out, gameState);
//		}
	}
}
	

	
	
		
//	//give turnCount mana to the player just in the beginning of new turn	
//	public void giveMana(GameState gameState) {  
//			gameState.getTurnOwner().setMana(gameState.getTurnCount());  
//	}
//	
//	//empty mana for player who ends the turn
//	public void emptyMana(GameState gameState) {
//		gameState.getTurnOwner().setMana(0);
//	}
	
//	//cooldown monsters
//	public void toCoolDown(GameState gameState) {
//		ArrayList<Monster> toCool = gameState.getBoard().friendlyUnitList(gameState.getTurnOwner());	
//		for(Monster m : toCool){
//				m.toggleCooldown();				
//		}
//	}
	


// To do:
// Move extra methods to gameState
