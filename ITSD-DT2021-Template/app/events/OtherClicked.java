package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		// >>> Deselect all
		
		// Card selected
		if(gameState.getTurnOwner().getHand().isPlayingMode()) {
			
			Card cSelected = gameState.getTurnOwner().getHand().getSelectedCard();
			
			// Visual update while references intact	--- Need position info from Noah somehow
			//BasicCommands.drawCard(out, cSelected, position, 0);
			//GeneralCommandSets.threadSleep();
			
			// Update game variables
			gameState.getTurnOwner().getHand().setSelectedCard(null);
			//gameState.getTurnOwner().getHand().getSelectedCard().setClicked(false);
			gameState.getTurnOwner().getHand().setPlayingMode(false);
			
		}
		
		// Unit selected
		if(gameState.getBoard().getUnitSelected() != null) {
			
			Monster mSelected = gameState.getBoard().getUnitSelected();
			
			// Visual update while references intact
			ArrayList <Tile> actionRange = gameState.getBoard().unitMovableTiles(mSelected.getPosition().getTilex(), mSelected.getPosition().getTiley(), mSelected.getMovesLeft());
			ArrayList <Tile> attackRange = new ArrayList <Tile> (gameState.getBoard().unitAttackableTiles(mSelected.getPosition().getTilex(), mSelected.getPosition().getTiley(), mSelected.getAttackRange(), mSelected.getMovesLeft()));
			actionRange.addAll(attackRange);
			
			for(Tile t : actionRange) {
				BasicCommands.drawTile(out, t, 0);
				GeneralCommandSets.threadSleep();
			}
			
			// + Unit tile
			BasicCommands.drawTile(out, gameState.getBoard().getTile(mSelected.getPosition().getTilex(), mSelected.getPosition().getTiley()), 0);
			GeneralCommandSets.threadSleep();
			
			// Update game variables
			gameState.getBoard().setUnitSelected(null);
			mSelected.toggleSelect();
			
			System.out.println("Monster selected: " + mSelected.isSelected());
			
		}

		/*
		 * Will hold:
		 * Deselect Monster
		 * Deselect Card
		 */
		
		
	}

}


