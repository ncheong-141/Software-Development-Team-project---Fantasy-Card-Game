package structures.basic.abilities;

import java.util.ArrayList;
import java.util.HashMap;

import structures.basic.Avatar;
import structures.basic.Monster;

/*
 * This class holds the information about the relationship between Units (Monsters and Spells) and what abilities they possess. 
 */

public class AbilityToUnitLinkage {

	// Attributes. Hash map which contains a String for a key (Unit name, list of abilities the unit has) 
	public static HashMap<String, ArrayList<Ability>> UnitAbility = new HashMap<String, ArrayList<Ability>>(); 

	// Initialise the linkage data between Units and abilities 
	// If can read a full list of abilities from json, can automate this. 
	public static void initialiseUnitAbilityLinkageData() {
		

		/* Initiailising abilities with String name keys*/

		// Can maybe grab this info from a file instead if dont want to hard code it in. 
		
		/*** Spells ***/
		UnitAbility.put("Truestrike", 		constructArrayListAbility(new A_Truestrike(true,Monster.class))); 			// Truestrike 
		UnitAbility.put("Sundrop Elixir", 	constructArrayListAbility(new A_SundropElixir(false, Monster.class)));		// Sundrop Elixir
		UnitAbility.put("Staff of Y'Kir", 	constructArrayListAbility(new A_StaffofYkir(false, Avatar.class)));			// Staff of Y'kir
		UnitAbility.put("Entropic Decay", 	constructArrayListAbility(new A_EntropicDecay(true, Monster.class)));		// Entropic Decay

		
		/*** Units ***/ 
		UnitAbility.put("Windshrike", 		constructArrayListAbility(new A_U_DrawCardOnUnitDeath(false, Monster.class))				// Windshrike
											/*, + another ability*/);																
		UnitAbility.put("Blaze Hound", 		constructArrayListAbility(new A_U_PlayersDrawCardOnUnitSummon(false, Monster.class)));		// Blaze Hound
		UnitAbility.put("Azurite Lion", 	constructArrayListAbility(new A_U_DoubleAttacker(false, Monster.class)));					// Azurite Lion
		UnitAbility.put("Serpenti", 		constructArrayListAbility(new A_U_DoubleAttacker(false, Monster.class)));					// Serpenti
		UnitAbility.put("Fire Spitter", 	constructArrayListAbility(new A_U_RangedAttacker(false, Monster.class)));					// Fire Spitter
		UnitAbility.put("Pyromancer", 		constructArrayListAbility(new A_U_RangedAttacker(false, Monster.class)));					// Pyromancer
		
	}
	
	/* Helper methods */
	
	// For constructing an array list to input into the HashMap 
	// Overloaded method to account for 1,2 or 3 skills
	
	private static ArrayList<Ability> constructArrayListAbility(Ability ... args){
		ArrayList<Ability> abilityContainer = new ArrayList<Ability>();
		for(Ability a : args) {
			abilityContainer.add(a);
		}
		return abilityContainer; 
	}
	
//	private static ArrayList<Ability> constructArrayListAbility(Ability ability){
//		ArrayList<Ability> abilityContainer = new ArrayList<Ability>(10);
//		abilityContainer.add(ability);
//		
//		return abilityContainer; 
//	}
	
//	private static ArrayList<Ability> constructArrayListAbility(Ability ability1, Ability ability2){
//		ArrayList<Ability> abilityContainer = new ArrayList<Ability>(10);
//		abilityContainer.add(ability1);
//		abilityContainer.add(ability2);
//		
//		return abilityContainer; 
//	}
	
	
}
