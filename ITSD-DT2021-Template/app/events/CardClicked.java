package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Hand;
import java.util.ArrayList;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = â€œcardClickedâ€�
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		int handPosition = message.get("position").asInt();
		ArrayList<Card> tempHand= new ArrayList<Card>;
		Card tempSelect= new Card;
		tempHand=gameState.getPlayerOne().getHand();
		tempSelect=tempHand.setSelectedCard(getCardFromHand(handPosition));
		
		
		//method for if no card is currently selected
		if
		(gameState.getTurnOwner()==gameState.getPlayerOne && gameState.getPlayerOne().getHand().getClicked==false)
		{
		tempHand.getCardFromHand(handPosition).setClicked(true);
		tempHand.setPlayingMode(true);
		
		ArrayList<Tile> display= GameState.getGameBoard().friendlyMonsterTiles(gameState.getPlayerOne);
		for(Tile t: display) {BasicCommands.DrawTile(out,t,2);}
		}
		
		
		// switch to new card when one already selected
		//else if{
		//(gameState.getTurnOwner()==playerOne && gameState.getPlayerOne().getHand().getClicked==true)
		//
		//
		// ArrayList<Tile> display= GameState.getGameBoard().friendlyMonsterTiles(gameState.getPlayerOne);
		//for(Tile t: display) {BasicCommands.DrawTile(out,t,2);}
        //}
	}

}
