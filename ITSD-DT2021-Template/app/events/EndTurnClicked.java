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
//		if (gameState.getTurnOwner() == gameState.getPlayerTwo()) {
//			ComputerPlayerTurn compTurn = new ComputerPlayerTurn();
//			compTurn.processComputerActions(out, gameState);
//		}

	}
		

	public void endTurnStateChange(ActorRef out, GameState gameState) {  

		gameState.emptyMana(); 										// Empty mana for player who ends the turn
		gameState.deselectAllEntities();								// Deselect all entities
		GeneralCommandSets.boardVisualReset(out, gameState);  	// Visual rest

		// Check if the deck is empty, if so then gameover
		if (gameState.isDeckEmpty()) {  //check if current player has enough card in deck left to be added into hand
			gameState.gameOver(); 
		} else {

			// If holds enough card, get card from deck
			gameState.getTurnOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());  

			// Draw the card on last index
			Card card = gameState.getTurnOwner().getDeck().getCardList().get(0);
			int handPos = (gameState.getTurnOwner().getHand().getHandList().size())-1;
			BasicCommands.drawCard(out, card, handPos, 0);
			GeneralCommandSets.threadSleepLong();
		}

		//gameState.setMonsterCooldown(false);	// Hard set all monsters on turn enders turn to cooldown
		gameState.turnChange(); 				// turnOwner exchanged	
		gameState.giveMana();			 		// Give turnCount mana to the player in the beginning of new turn
		gameState.toCoolDown(); 				// Switch avatars status for current turnOwner
		//gameState.setMonsterCooldown(true);

		// Debug mode
		if (gameState.isTwoPlayerMode()) {
			// redraw hand to humanplayer
			int oldCardListSize = gameState.getEnemyPlayer().getHand().getHandList().size(); 

			GeneralCommandSets.drawCardsInHand(out, gameState, oldCardListSize, gameState.getTurnOwner().getHand().getHandList());
		}	
	}

}