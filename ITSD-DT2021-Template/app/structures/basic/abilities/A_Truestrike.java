package structures.basic.abilities;
import structures.basic.Monster;

public class A_Truestrike implements Ability {
	
	// Deal 2 damage to  an enemy Unit
	public boolean execute(Monster targetMonster) {
		
		// Reduce Monster HP by 2
		targetMonster.setHP(targetMonster.getHP() - 2);
		
		return true; 
	}

}
