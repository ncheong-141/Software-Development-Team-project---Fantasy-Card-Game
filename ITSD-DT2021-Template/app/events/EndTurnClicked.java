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
		
		// Check if locked, dont not execute anything if so
		if (gameState.userinteractionLocked()) {
			return;
		}
		
		// Lock user interaction during action
		/**===========================**/
		gameState.userinteractionLock();
		/**===========================**/
		
		// End turn state change procedure
		endTurnStateChange(out, gameState);
		
		// End turn for computer player. Executes all determined moves
		if (gameState.getTurnOwner() == gameState.getPlayerTwo()) {
			
			ComputerPlayerTurn compTurn = new ComputerPlayerTurn(out, gameState);
			compTurn.processComputerActions();
			
			if(gameState.getTurnOwner().getDeck().getCardList().isEmpty() && gameState.getTurnOwner() == gameState.getPlayerTwo()) {
				System.out.println("Computer lose");
				BasicCommands.addPlayer1Notification(out, "You win!", 2);
				gameState.gameOver();
				return;
			}
			
			BasicCommands.addPlayer1Notification(out, "Your move!", 2);
			GeneralCommandSets.threadSleep();

		}
		
		/**===========================**/
		gameState.userinteractionUnlock();
		/**===========================**/
	}
		

	private void endTurnStateChange(ActorRef out, GameState gameState) {  


		/** End turn procedure **/
		gameState.emptyMana(); 										// Empty mana for player who ends the turn
		GeneralCommandSets.threadSleep();
		GeneralCommandSets.updatePlayerStats(out, gameState);		// Update player stats
		GeneralCommandSets.threadSleep();
		gameState.deselectAllEntities();							// Deselect all entities
		GeneralCommandSets.boardVisualReset(out, gameState);  		// Visual rest of the board

		// Check if the deck is empty, if so then gameover
		if (gameState.isDeckEmpty()) {  							//check if current player has enough card in deck left to be added into hand
			gameState.gameOver(); 
		}
		else {

			// If there are cards left in deck, get a card from deck (back end)
			gameState.getTurnOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());  
			
			//if it is human player getting a new card, re-display all card in hand after drawing 
			if(gameState.getTurnOwner() == gameState.getPlayerOne()) {
				Card card = gameState.getTurnOwner().getDeck().getCardList().get(0);
				int oldCardSize = (gameState.getTurnOwner().getHand().getHandList().size()) -1; 									//after get new one, get current handsize -1 for old size 
				GeneralCommandSets.drawCardsInHand(out, gameState, oldCardSize, gameState.getTurnOwner().getHand().getHandList()); 	//refresh hand ,show with one card added	
			}	
		}

		gameState.setMonsterCooldown(true);						// Hard set all monsters on turn enders turn to cooldown
		GeneralCommandSets.threadSleep();
		gameState.turnChange(); 								// turnOwner exchanged	
		GeneralCommandSets.threadSleep();
		gameState.giveMana();			 						// Give turnCount mana to the player in the beginning of new turn
		GeneralCommandSets.threadSleep();
		GeneralCommandSets.updatePlayerStats(out, gameState);	// Update player states
		GeneralCommandSets.threadSleep();
		gameState.setMonsterCooldown(false);					// Set all monster cooldowns to false
		GeneralCommandSets.threadSleep();
		
		// Debug mode
		if (gameState.isTwoPlayerMode()) {
			// redraw hand to humanplayer
			int oldCardListSize = gameState.getEnemyPlayer().getHand().getHandList().size(); 

			GeneralCommandSets.drawCardsInHand(out, gameState, oldCardListSize, gameState.getTurnOwner().getHand().getHandList());
		}
			 
//		// If there are cards left in deck, get a card from deck (back end)
//		if(gameState.isHumanCard()) {
//			gameState.getTurnOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());//if it is human player getting a new card, re-display all card in hand after drawing 
//			showNewCard(out,gameState);
//			gameState.endTurnStaticChange();
//		}
		
		
	}

//	//display all cards after new one added
//	private void showNewCard(ActorRef out, GameState gameState) {
//		ArrayList<Card> card = gameState.getTurnOwner().getDeck().getCardList();
//		int oldCardSize = (gameState.getTurnOwner().getHand().getHandList().size()) -1; //after get new one, get current handsize -1 for old size 
//		GeneralCommandSets.drawCardsInHand(out, gameState, oldCardSize, card); //refresh hand ,show with one card added	
//	}
//	
}


