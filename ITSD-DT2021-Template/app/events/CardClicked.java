package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import structures.basic.Hand;

import java.util.ArrayList;
import structures.basic.Tile;
import structures.basic.abilities.AbilityToUnitLinkage;
import utils.BasicObjectBuilders;
import structures.basic.Board;

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
		int handPosition = message.get("position").asInt();//gets position in hand of clicked card
		
		//checks if a card had previously been selected, if so it removes any traces of this
		if(gameState.getPlayerOne().getHand().isPlayingMode()==true){
		gameState.getPlayerOne().getHand().setPlayingMode(false);
		Hand tempHand= new Hand(gameState.getPlayerOne().getHand());
		tempHand= gameState.getPlayerOne().getHand();
		gameState.getPlayerOne().getHand().setSelectedCard(null);
		for(int i; i<=tempHand.; i++){
		    tempHand(i).setClicked(false);
		    }
		}
		
		
		//creates a placeholder for the clicked card
			Card clickedCard = gameState.getTurnOwner().getHand().getCardFromHand(handPosition);
			//sets clicked card and tells the game state that a card in hand is to be played
			clickedCard.setClicked(true);
			gameState.getPlayerOne().getHand().setPlayingMode(true);
			gameState.getPlayerOne().getHand().setSelectedCard(gameState.getTurnOwner().getHand().getCardFromHand(handPosition));
		//checks that the clicked card is a monster card using its attack value
		if (clickedCard.getBigCard().getAttack() > 0){ //for summoning monsters
			ArrayList<Tile> display= gameState.getGameBoard().allSummonableTiles(gameState.getPlayerOne());
				for(Tile t: display) {
					BasicCommands.drawTile(out,t,2);
			}		
		}//a loop which checks that a card is a spell, then displays playable tiles depending on spell target
		else if (clickedCard.getBigCard().getAttack() < 0) {
			//for spell targeting enemy units
			if(AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Monster.class
				&& clickedCard.targetEnemy()==true){
					ArrayList<Tile> display= gameState.getGameBoard().enemyTile(gameState.getPlayerOne());
						for(Tile t: display) {
							BasicCommands.drawTile(out,t,2);
							}
			}//for spell which targets enemy avatar
			else if (AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Avatar.class
				&& clickedCard.targetEnemy()==true){
					Tile display= gameState.getGameBoard().enemyAvatarTile(gameState.getPlayerOne(), gameState);
							BasicCommands.drawTile(out,display,2);
			}//for spell targeting friendly unit
			else if (AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Monster.class
				&& clickedCard.targetEnemy()==false){
					ArrayList<Tile> display= gameState.getGameBoard().friendlyTile(gameState.getPlayerOne());
						for(Tile t: display) {
							BasicCommands.drawTile(out,t,2);
							}
			}//for spell targeting friendly avatar
			else if (AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Avatar.class
				&& clickedCard.targetEnemy()==false){
						Tile display= gameState.getGameBoard().ownAvatarTile(gameState.getPlayerOne());
						BasicCommands.drawTile(out,display,2);						
						}
			}

	}
	
}



