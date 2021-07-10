package events.gameplaystates.unitplaystates;

import java.util.ArrayList;
import java.util.List;

import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.EffectAnimation;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Position;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.A_U_RangedAttacker;
import structures.basic.abilities.Ability;
import structures.basic.abilities.AbilityToUnitLinkage;
import structures.basic.abilities.Call_IDs;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class CastSpellState implements IUnitPlayStates {

	/*** State attributes ***/
	private Tile targetTile; 
	
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public CastSpellState(Tile targetTile) {
		this.targetTile = targetTile; 
	}
	
	public void execute(GameplayContext context) {
		
		System.out.println("In CastSpellSubState.");

		// Create spell from the Card in context
		// StaticConfFiles needs to be able to be linked to Card. 
		//Spell spellToCast = (Spell) BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 0, Spell.class); 
		System.out.println("Static config file is:" + context.getLoadedCard().getConfigFile());
		
		Spell spellToCast = (Spell) BasicObjectBuilders.loadCard( context.getLoadedCard().getConfigFile(), context.getLoadedCard().getId(), Spell.class); 

		
		GeneralCommandSets.threadSleep();

		// Set ability to Spell
		spellToCast.setAbility("Truestrike",AbilityToUnitLinkage.UnitAbility.get(context.getLoadedCard().getCardname()).get(0));
		
		/* Cast the Spell on the Unit on tile selected */
		
		boolean successfulFlag = false;
		
		// If has enough mana 
		if (context.getGameStateRef().getTurnOwner().getMana() - context.getLoadedCard().getManacost() >= 0) {
			
			// Cast spell and return a flag to indicate if worked
			successfulFlag = spellToCast.getAbility().execute(targetTile.getUnitOnTile() , context.getGameStateRef());
			
			// Play effect animation associated with ability (if present)
			if (spellToCast.getAbility().getEffectAnimation() != null) {

				BasicCommands.playEffectAnimation(context.out, spellToCast.getAbility().getEffectAnimation(), targetTile);
				GeneralCommandSets.threadSleep();
			}
		}
		
		// Apply changes to gamestate if successful cast	
		if (successfulFlag) {
			
			System.out.println("Sucessfully cast spell."); 
					

			/** Delete card from Hand + update visual **/
			
			// Index variables
			int cardIndexInHand = context.getGameStateRef().getTurnOwner().getHand().getSelCarPos(); 
			int oldHandSize =  context.getGameStateRef().getTurnOwner().getHand().getHandList().size(); 	// How many UI cards to delete

			// Remove card
			context.getGameStateRef().getTurnOwner().getHand().removeCard(cardIndexInHand);
			
			/** Reset entity selection and board **/  
			// Deselect after action finished
			context.deselectAllAfterActionPerformed();
			
			
			// Only update Hand for Human player
			if (context.getGameStateRef().getTurnOwner() instanceof HumanPlayer) {
				GeneralCommandSets.drawCardsInHand(context.out, context.getGameStateRef(), oldHandSize, context.getGameStateRef().getTurnOwner().getHand().getHandList());
			}
		
			//  Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			
			// Check if Unit has been defeated, if so, destroy it. 
			if (targetTile.getUnitOnTile().getHP() <= 0) {
				
				BasicCommands.playUnitAnimation(context.out, targetTile.getUnitOnTile(), UnitAnimationType.death);
				GeneralCommandSets.threadSleepLong();
				BasicCommands.deleteUnit(context.out, targetTile.getUnitOnTile());
				GeneralCommandSets.threadSleep();
				
				// Update back end data
				unitDeath(context, targetTile);
			}
			
		}
		else {
			System.out.println("Spell cast unsucessful, please select another Unit"); 
		}
		
		

	}
	
	
	
	// Unit death method to update location data and delete a Unit from Board
	private void unitDeath(GameplayContext context, Tile grave) {
		
		Monster deadUnit = grave.getUnitOnTile();
		
		// Check for onDeath ability
		if(deadUnit.hasAbility()) {
			for(Ability a : deadUnit.getMonsterAbility()) {
				if(a.getCallID() == Call_IDs.onDeath) {	a.execute(deadUnit,context.getGameStateRef()); 
				break;}
			}
		}
		
		// Update internal Tile values
		grave.removeUnit();
		deadUnit.setPosition(new Position(-1,-1,-1,-1));
		
	}

}
