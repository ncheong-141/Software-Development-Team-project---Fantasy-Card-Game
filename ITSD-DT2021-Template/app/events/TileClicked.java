package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.tileplaystates.*;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.BigCard;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Card;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import structures.basic.abilities.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Selected Tile coordinates
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		// Start the GameplayState (State Pattern for TileClicked control flow) 
		GameplayContext gameplayContext = new GameplayContext(gameState, out, tilex, tiley);
		
		System.out.println("In TileClicked.");
		
		/* --------------------------------------------------------------------------------
		 * Check previous User inputs (will be either Card Selected State or Unit Selected
		 * -------------------------------------------------------------------------------- */

		if (checkCardClicked(gameState)) {
			gameplayContext.addCurrentState(new CardPreviouslySelectedState());
		} 
		else if (gameState.getBoard().getUnitSelected() != null) {
			
			gameplayContext.addCurrentState(new UnitPreviouslySelectedState());
			
			// Set this now to false? 
		}
		else {			
			// If nothing is selected previously 
			gameplayContext.addCurrentState(new SingleSelectedState());
		}


		
		/*
		 * Execute State. Each state holds the game logic required to execute the desired functionality
		 * Note, some States here create sub-states. 
		 * E.g. CardSelectedState deals with the previous user input of a Card click and generates a new substate 
		 * based on what the user has currently clicked (a unit or empty tile) 
		 */
		gameplayContext.executeAndCreateSubStates();
		
		
	}

	
	
	/* Helper methods */ 
	
	private boolean checkCardClicked(GameState gameState) {
		return (gameState.getTurnOwner().getHand().isPlayingMode());
	}



}