package events.gameplaystates.tileplaystates;

import events.gameplaystates.GameplayContext;
import events.gameplaystates.unitplaystates.CastSpellState;
import events.gameplaystates.unitplaystates.IUnitPlayStates;
import events.gameplaystates.unitplaystates.SummonMonsterState;
import structures.basic.Monster;
import structures.basic.Spell;
import structures.basic.Tile;

public class CardPreviouslySelectedState implements ITilePlayStates {

	// State attributes
	IUnitPlayStates unitState; 
	Tile targetTile; 
	
	// Constructor
	public CardPreviouslySelectedState() {
		unitState = null; 
		targetTile = null; 
	}
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
	
		// Debug section 
		System.out.println("In CardPreviouslySelectedState.");
		context.debugPrint();
		
		/***	Set reference info for navigating state		***/
		// Could be it's own method
		
		// Get selected card for use in the Sub state (Monster or Spell use) 
		context.setLoadedCard( context.getGameStateRef().getTurnOwner().getHand().getSelectedCard() );
		
		// Determine class type of the loaded card
		// Check if a Spell card 
		if (context.getLoadedCard().getBigCard().getAttack() < 0) {	
			context.setCardClasstype(Spell.class);
		}
		// else, Monster
		else {
			context.setCardClasstype(Monster.class);
		}
		
		// Set targetTile
		targetTile = context.getClickedTile();
		
		
		
		// Determine the unit state (SummonMonster or Cast Spell)  (to lower case just so case isnt a problem ever) 
		switch (context.getTileFlag().toLowerCase()) {
		
		case("friendly unit"): {
			// Add check for card type
			if (context.getCardClasstype() == Spell.class) {
				unitState = new CastSpellState(targetTile);
				break; 
			}
			else {
				System.out.println("Can't summon Monster on occupied tile.");
				break;
			}

		}
		case("enemy unit"): {
			// Add check for card type
			if (context.getCardClasstype() == Spell.class) {
				unitState = new CastSpellState(targetTile);
				break; 
			}
			else {
				System.out.println("Can't summon Monster on occupied tile.");
				break;
			}
		}
		
		
		case("empty"): {
			if (context.getCardClasstype() == Monster.class) {
				unitState = new SummonMonsterState(targetTile);
				break;
			}
			else {
				System.out.println("Can't play Spell on empty tile.");
				break;
			}

		}
		}
		
		// Execute Unit state
		if (unitState != null ) {
			unitState.execute(context);
		}
		else {
			System.out.println("Substate = null.");
		}
	}
	
	
}
