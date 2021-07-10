package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import structures.GameState;
import structures.basic.*;
import structures.basic.Hand;

import java.util.ArrayList;
import structures.basic.Tile;
import structures.basic.abilities.Ability;
import structures.basic.abilities.AbilityToUnitLinkage;
import structures.basic.abilities.Call_IDs;
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
		 
		
		// Reset entity selection and board
        GeneralCommandSets.boardVisualReset(out, gameState);
        gameState.deselectAllEntities();
		
		// Stop user interacting with the UI when this is set
		if (!gameState.canInteract) {
			System.out.println("Cant interact.");
			return;
		}

		
		int handPosition = message.get("position").asInt();//gets position in hand of clicked card
		
		//creates a placeholder for the clicked card
			Card clickedCard = gameState.getTurnOwner().getHand().getCardFromHand(handPosition);
			
		//tells the game state that a card in hand is to be played
			gameState.getTurnOwner().getHand().setSelectedCard(gameState.getTurnOwner().getHand().getCardFromHand(handPosition));
			gameState.getTurnOwner().getHand().setSelCarPos(handPosition);
			

		//checks that the clicked card is a monster card using its attack value
			// Check if the card has an ability that affects before summoning

			// Boolean switch to check if the ability is applicable 
			boolean outputted = false; 

			if(clickedCard.hasAbility()) {			
				for(Ability a: clickedCard.getAbilityList()) {

					if (a.getCallID() == Call_IDs.onCardClicked) {			
						// Execute it (null for no target monster)
						a.execute(null, gameState); 
						
						System.out.println(a);
						// Draw the respective tiles (any ability like this will only affect tiles really unless its like, "if you have this card in your had then get 2 HP per turn but that would be weird"/
						GeneralCommandSets.drawBoardTiles(out, gameState.getTileAdjustedRangeContainer(), 2);
						outputted = true; 
						break; 
					}
				}
				
			}
			if (!outputted) {
				// Else, draw the summonable tiles as normal
				ArrayList<Tile> display= gameState.getBoard().allSummonableTiles(gameState.getTurnOwner());	
				GeneralCommandSets.drawBoardTiles(out, display, 2);	
			}
			

		//a loop which checks that a card is a spell, then displays playable tiles depending on spell target
		if (clickedCard.getBigCard().getAttack() < 0) {

			//for spell targeting enemy units
			if(AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Monster.class
				&& clickedCard.targetEnemy()==true){
					ArrayList<Tile> display= gameState.getBoard().enemyTile(gameState.getTurnOwner());
					GeneralCommandSets.drawBoardTiles(out, display, 2);	
			}//for spell which targets enemy avatar
			else if (AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Avatar.class
				&& clickedCard.targetEnemy()==true){
					Tile display= gameState.getBoard().enemyAvatarTile(gameState.getTurnOwner(), gameState);
							BasicCommands.drawTile(out,display,2);
			}//for spell targeting friendly unit
			else if (AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Monster.class
				&& clickedCard.targetEnemy()==false){
					ArrayList<Tile> display= gameState.getBoard().friendlyTile(gameState.getTurnOwner());
					GeneralCommandSets.drawBoardTiles(out, display, 2);	

			}//for spell targeting friendly avatar
			else if (AbilityToUnitLinkage.UnitAbility.get(""+clickedCard.getCardname()).get(0).getTargetType()==Avatar.class
				&& clickedCard.targetEnemy()==false){
						Tile display= gameState.getBoard().ownAvatarTile(gameState.getTurnOwner());
						BasicCommands.drawTile(out,display,2);						
						}
			}
	}
}