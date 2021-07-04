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
		
		// For Abilities constructor (targets enemies?, Class type of target, EffectAnimation)
		
		/*** Spells ***/
		UnitAbility.put("Truestrike", 		constructArrayListAbility(new A_Truestrike(true,Monster.class, null))); 			// Truestrike 
		UnitAbility.put("Sundrop Elixir", 	constructArrayListAbility(new A_SundropElixir(false, Monster.class, null)));		// Sundrop Elixir
		UnitAbility.put("Staff of Y'Kir", 	constructArrayListAbility(new A_StaffofYkir(false, Avatar.class, null)));			// Staff of Y'kir
		UnitAbility.put("Entropic Decay", 	constructArrayListAbility(new A_EntropicDecay(true, Monster.class, null)));			// Entropic Decay

		
		/*** Units ***/ 
		UnitAbility.put("Windshrike", 		constructArrayListAbility(new A_U_DrawCardOnUnitDeath(false, Monster.class, null))				// Windshrike
											/*, + another ability*/);																
		UnitAbility.put("Blaze Hound", 		constructArrayListAbility(new A_U_PlayersDrawCardOnUnitSummon(false, Monster.class, null)));	// Blaze Hound
		UnitAbility.put("Azurite Lion", 	constructArrayListAbility(new A_U_DoubleAttacker(false, Monster.class, null)));					// Azurite Lion
		UnitAbility.put("Serpenti", 		constructArrayListAbility(new A_U_DoubleAttacker(false, Monster.class, null)));					// Serpenti
		UnitAbility.put("Fire Spitter", 	constructArrayListAbility(new A_U_RangedAttacker(false, Monster.class, null)));					// Fire Spitter
		UnitAbility.put("Pyromancer", 		constructArrayListAbility(new A_U_RangedAttacker(false, Monster.class, null)));					// Pyromancer
		
	}
	
	/* Helper methods */
	
	// For constructing an array list to input into the HashMap 	
	private static ArrayList<Ability> constructArrayListAbility(Ability ... args){
		ArrayList<Ability> abilityContainer = new ArrayList<Ability>();
		for(Ability a : args) {
			abilityContainer.add(a);
		}
		return abilityContainer; 
	}
	
}
