package events;
import structures.basic.*;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;


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

	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	private Board board;
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		
		gameState.getTurnOwner().drawFromDeck(); //draw a card from deck for current turnOwner
		emptyMana(); //empty mana for player who ends the turn
		toCoolDown(); //cool monsters
		gameState.deselectAllEntities();
		gameState.turnChange(); // turnOwner exchanged	
		giveMana(); //give turnCount mana to the player in the beginning of new turn
		
//		deactivateTileClicked();	
	}
	
	//give turnCount mana to the player just in the beginning of new turn	
	public void giveMana() {  
			gameState.getTurnOwner().setMana(gameState.getTurnCount());  
	}
	
	//empty mana for player who ends the turn
	public void emptyMana() {
		gameState.getTurnOwner().setMana(0);
	}
	
	//cooldown monsters
	public void toCoolDown() {
		
		ArrayList<Monster> toCool = board.coolDownCheck();	
		for(Monster m : toCool){
				m.toggleCooldown();				
		}
	}
	
	

	
}
