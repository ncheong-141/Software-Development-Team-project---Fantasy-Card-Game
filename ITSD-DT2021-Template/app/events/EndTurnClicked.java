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
 *   messageType = 
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */

//player draws a card and mana is drained
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {  //for HumanPlayer
				
			emptyMana(gameState); //empty mana for player who ends the turn
			toCoolDown(gameState); //switch avatars status for current turnOwner
			gameState.deselectAllEntities();
			GeneralCommandSets.boardVisualReset(out, gameState);
			gameState.deselectAllEntities();
			gameState.turnChange(); // turnOwner exchanged	
			if (isDeckEmpty(gameState)) {  //check if both players have enought card in deck left for new turn
				gameState.gameOver();  // if not, gameover(?)
			}
			giveMana(gameState); //give turnCount mana to the player in the beginning of new turn
			toCoolDown(gameState); //switch avatars status for new turnOwner in the beginning of new turn

			gameState.getTurnOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());
	}
		
	// check if players decks are are empty 
	public boolean isDeckEmpty(GameState gameState) {
		ArrayList<Card> turnOwnerDeck = gameState.getTurnOwner().getDeck().getCardList();
		int deckCardLeft = turnOwnerDeck.size();
		
		if(deckCardLeft < 1) {
			return true;
		}
		return false;
	}
	
	//give turnCount mana to the player just in the beginning of new turn	
	public void giveMana(GameState gameState) {  
			gameState.getTurnOwner().setMana(gameState.getTurnCount());  
	}
	
	//empty mana for player who ends the turn
	public void emptyMana(GameState gameState) {
		gameState.getTurnOwner().setMana(0);
	}
	
	//cooldown monsters
	public void toCoolDown(GameState gameState) {
		ArrayList<Monster> toCool = gameState.getBoard().friendlyUnitList(gameState.getTurnOwner());	
		for(Monster m : toCool){
				m.toggleCooldown();				
		}
	}
	
	
	
}

// To do:
// Move extra methods to gameState