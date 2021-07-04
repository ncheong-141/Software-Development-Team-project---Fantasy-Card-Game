package events.gameplaystates.unitplaystates;

import java.util.ArrayList;
import java.util.List;

import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.EffectAnimation;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.abilities.AbilityToUnitLinkage;
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
		Spell spellToCast = (Spell) BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 0, Spell.class); 
		GeneralCommandSets.threadSleep();

		// Set ability to Spell
		spellToCast.setAbility("Truestrike",AbilityToUnitLinkage.UnitAbility.get(context.getLoadedCard().getCardname()).get(0), "Description");
		
		// Cast the Spell on the Unit on tile selected


		boolean successfulFlag = spellToCast.getAbility().execute(targetTile.getUnitOnTile() , context.getGameStateRef());
		
		// Play effect animation associated with ability (if present)
		if (spellToCast.getAbility().getEffectAnimation() != null) {
			
//			List<String> temp = new ArrayList<String>();
//			for (String s : context.getLoadedCard().getMiniCard().getAnimationFrames()) {
//				temp.add(s);
//				System.out.println(s);
//			}
//			
//			EffectAnimation ef = new EffectAnimation();
//			ef.setAnimationTextures(temp);
//			ef.setFps(context.getLoadedCard().getMiniCard().getFps());
//			
					
			BasicCommands.playEffectAnimation(context.out, spellToCast.getAbility().getEffectAnimation(), targetTile);
			GeneralCommandSets.threadSleep();
		}
		
		if (successfulFlag) {
			
			System.out.println("Sucessfully cast spell."); 
						
			/** Reset entity selection and board **/  
			// Deselect after action finished *if* not in the middle of move-attack action
			context.deselectAllAfterActionPerformed();
						
			/** Delete card from Hand + update visual **/
			int cardIndexInHand = context.getGameStateRef().getTurnOwner().getHand().getSelCarPos(); 
			context.getGameStateRef().getTurnOwner().getHand().removeCard(cardIndexInHand);

			// Update UI 
			BasicCommands.deleteCard(context.out, cardIndexInHand);
		
			// Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
		}
		else {
			System.out.println("Spell cast unsucessful, please select another Unit"); 
		}
		
		

	}
}
