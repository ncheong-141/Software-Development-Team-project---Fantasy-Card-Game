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

	private HumanPlayer playerOne;
	private ComputerPlayer playerTwo;
	private GameState gameState;
	private Board board;
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {  //for HumanPlayer
			
			if (isDeckEmpty()) {  //check if both players have enought card in deck left for new turn
				gameState.gameOver();  // if not, gameover(?)
			}
			gameState.getTurnOwner().drawFromDeck(); //draw a card from deck for current turnOwner
			emptyMana(); //empty mana for player who ends the turn
			toCoolDown(); //switch avatars status for current turnOwner
			gameState.deselectAllEntities();
			GeneralCommandSets.boardVisualReset(out, gameState);
			gameState.getTurnOwner().getHand().setPlayingMode(false); //current turnOwner hand turn off
			gameState.turnChange(); // turnOwner exchanged	
			giveMana(); //give turnCount mana to the player in the beginning of new turn
			toCoolDown(); //switch avatars status for new turnOwner in the beginning of new turn
			gameState.getTurnOwner().getHand().setPlayingMode(true); //current turnOwner hand turn on
		
	}
			
	
	
	
	
	// check if players decks are are empty 
	public boolean isDeckEmpty() {
		ArrayList<Card> humanDeck = playerOne.getDeck().getDeck();
		int humanCardLeft = humanDeck.size();
		ArrayList<Card> computerDeck = playerTwo.getDeck().getDeck();
		int computerCardLeft = computerDeck.size();
		
		if(( humanCardLeft < 1) ||(computerCardLeft < 1)) {
			return true;
		}
		return false;
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
		ArrayList<Monster> toCool = board.friendlyUnitList(gameState.getTurnOwner());	
		for(Monster m : toCool){
				m.toggleCooldown();				
		}
	}
}
