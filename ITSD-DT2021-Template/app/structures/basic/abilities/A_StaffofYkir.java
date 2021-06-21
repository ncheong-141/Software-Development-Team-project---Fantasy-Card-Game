package structures.basic.abilities;

import structures.basic.Avatar;
import structures.basic.Monster;

public class A_StaffofYkir implements Ability {

	// Add + 2 attack to avatar
	public boolean execute(Monster targetMonster) {
		
		int additionalAttackValue = 2; 
		
		// Add two attack to avatar
		if (targetMonster instanceof Avatar) {
			System.out.println("Avatar attack: " + targetMonster.getAttackValue());
			targetMonster.setAttackValue(targetMonster.getAttackValue() + additionalAttackValue);
			System.out.println("Avatar attack after: " + targetMonster.getAttackValue());

			return true; 
		}
		else {
			System.out.println("Not instance of avatar");
			return false; 
		}
	}
}
