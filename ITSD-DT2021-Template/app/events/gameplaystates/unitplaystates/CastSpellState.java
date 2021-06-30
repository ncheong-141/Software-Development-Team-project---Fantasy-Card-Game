package events.gameplaystates.unitplaystates;

import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.basic.EffectAnimation;
import structures.basic.Spell;
import structures.basic.abilities.AbilityToUnitLinkage;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class CastSpellState implements ITilePlayStates {

	public void execute(GameplayContext context) {
		
		System.out.println("In CastSpellSubState.");

		// Create spell from the Card in context
		// StaticConfFiles needs to be able to be linked to Card. 
		Spell spellToCast = (Spell) BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 0, Spell.class); 
		GeneralCommandSets.threadSleep();

		// Set ability to Spell
		spellToCast.setAbility("Truestrike",AbilityToUnitLinkage.UnitAbility.get(context.getLoadedCard().getCardname()).get(0), "Description");
		
		// Cast the Spell on the Unit on tile selected


		boolean successfulFlag = spellToCast.getAbility().execute( context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley()).getUnitOnTile() , context.getGameStateRef());
		
		// Need to try and get Spell effect animation, for Truestrike its immolation in the card file but how to link it to the static conf file?
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
		BasicCommands.playEffectAnimation(context.out, ef, context.getGameStateRef().getBoard().getTile(context.getTilex(), context.getTiley()));

		GeneralCommandSets.threadSleep();
		
		if (successfulFlag) {
			
			System.out.println("Sucessfully cast spell."); 
						
			/** Reset entity selection and board **/  
			// Deselect after action finished *if* not in the middle of move-attack action
			context.deselectAllAfterActionPerformed();
		
			// Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
		}
		else {
			System.out.println("Spell cast unsucessful, please select another Unit"); 
		}
		
		

	}
}
