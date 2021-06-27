package events.tileplaystates;

import structures.basic.Monster;
import structures.basic.Spell;

public class CardPreviouslySelectedState implements GameplayStates {

	// State attributes
	GameplayStates subState; 
	
	// Constructor
	public CardPreviouslySelectedState() {
		subState = null; 
	}
	
	// State methods
	public void execute(GameplayContext context) {
	
		// Debug section 
		System.out.println("In CardPreviouslySelectedState.");
		context.debugPrint();
		
		
		// If a card is selected as previous user input, get card for use in the Sub state (summon monster or Cast spell) 
		context.setLoadedCard( context.getGameStateRef().getTurnOwner().getHand().getSelectedCard() );
		
		// Determine its class type of the loaded card
		// Check if a Spell card 
		if (context.getLoadedCard().getBigCard().getAttack() < 0) {	
			context.setCardClasstype(Spell.class);
		}
		// else, Monster
		else {
			context.setCardClasstype(Monster.class);
		}
		
		// Determine the substate (SummonMonster or Cast Spell)  (to lower case just so case isnt a problem ever) 
		switch (context.getTileFlag().toLowerCase()) {
		
		
		
		case("unit"): {
			// Add check for card type
			if (context.getCardClasstype() == Spell.class) {
				subState = new CastSpellSubState();
				break; 
			}
			else {
				System.out.println("Can't summon Monster on occupied tile.");
				break;
			}

		}
		
		case("empty"): {
			if (context.getCardClasstype() == Monster.class) {
				subState = new SummonMonsterSubState();
				break;
			}
			else {
				System.out.println("Can't play Spell on empty tile.");
				break;
			}

		}
		}
		
		// Execute sub-state
		if (subState != null ) {
			subState.execute(context);
			
			// Deselect after action
			context.deselectAllAfterActionPerformed();
		}
		else {
			System.out.println("Substate = null.");
		}
	}
	
	
}
