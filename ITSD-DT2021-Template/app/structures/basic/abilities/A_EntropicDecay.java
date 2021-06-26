package structures.basic.abilities;

import structures.basic.Avatar;
import structures.basic.Monster;

public class A_EntropicDecay implements Ability {
	
	// Reduce non-avatar entity HP to 0
	public boolean execute(Monster targetMonster) {
		
		if (!(targetMonster instanceof Avatar)) {
			targetMonster.setHP(0);
			return true; 
		}
		else {
			return false;
		}	
	}
}
