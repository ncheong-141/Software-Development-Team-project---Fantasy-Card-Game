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
	public void processEvent (ActorRef out, GameState gameState, JsonNode message){
		
		//GeneralCommandSets.boardVisualReset(out, gameState);  //visual
		endTurnStateChange(out, gameState);
	}
		
	public void endTurnStateChange(ActorRef out, GameState gameState) {  

		// //check if current player has enough card in deck left to be added into hand
		if (gameState.isDeckEmpty()) { 
			gameState.gameOver(); 
			return;
		}
			 
		// If there are cards left in deck, get a card from deck (back end)
		if(gameState.isHumanCard()) {
			gameState.getTurnOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());//if it is human player getting a new card, re-display all card in hand after drawing 
			showNewCard(out,gameState);
			gameState.endTurnStaticChange();
		}	
		
//		// Debug mode
//		if (gameState.isTwoPlayerMode()) {
//			// redraw hand to humanplayer
//			int oldCardListSize = gameState.getEnemyPlayer().getHand().getHandList().size(); 
//
//			GeneralCommandSets.drawCardsInHand(out, gameState, oldCardListSize, gameState.getTurnOwner().getHand().getHandList());
//		}	
	}

	//display all cards after new one added
	private void showNewCard(ActorRef out, GameState gameState) {
		ArrayList<Card> card = gameState.getTurnOwner().getDeck().getCardList();
		int oldCardSize = (gameState.getTurnOwner().getHand().getHandList().size()) -1; //after get new one, get current handsize -1 for old size 
		GeneralCommandSets.drawCardsInHand(out, gameState, oldCardSize, card); //refresh hand ,show with one card added	
	}

}