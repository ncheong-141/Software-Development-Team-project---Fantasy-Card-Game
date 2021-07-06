package structures.basic.abilities;

import java.util.ArrayList;
import java.util.HashMap;

import structures.basic.Avatar;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/*
 * This class holds the information about the relationship between Units (Monsters) and Spells and what abilities they possess. 
 * Containing data here as dont want to edit the JSON files. 
 */

public class AbilityToUnitLinkage {

	// Attributes. Hash map which contains a String for a key (Unit name, list of abilities the unit has) 
	public static HashMap<String, ArrayList<Ability>> UnitAbility = new HashMap<String, ArrayList<Ability>>(); 

	// Initialise the linkage data between Units and abilities 
	// If can read a full list of abilities from json, can automate this. 
	public static void initialiseUnitAbilityLinkageData() {
		

		/* Initiailising abilities with String name keys*/
		// Can maybe grab this info from a file instead if dont want to hard code it in. 
		
		// For Abilities constructor (execution targets enemies?, Class type of target, EffectAnimation)
		
						/*** Spells ***/
		// Deck 1
	/* Truestrike */		UnitAbility.put("Truestrike", 		constructArrayListAbility(new A_S_Truestrike(true,Monster.class, (BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation))))); 	
	/* Sundrop Elixir */	UnitAbility.put("Sundrop Elixir", 	constructArrayListAbility(new A_S_SundropElixir(false, Monster.class, (BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff)))));
		// Deck 2
	/* Staff of Y'Kir */	UnitAbility.put("Staff of Y'Kir'", 	constructArrayListAbility(new A_S_StaffofYkir(false, Avatar.class, (BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff)))));	
	/* Entropic Decay */	UnitAbility.put("Entropic Decay", 	constructArrayListAbility(new A_S_EntropicDecay(true, Monster.class, (BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom)))));	
			
					
						/*** Units ***/ 
		/***	Deck 1		***/
	/* Comodo Charger */	UnitAbility.put("Comodo Charger", 		constructArrayListAbility());
	/* Hailstone Golem */	UnitAbility.put("Hailstone Golem", 		constructArrayListAbility());
//	/* Pureblade Enforcer */UnitAbility.put("Pureblade Enforcer", 	constructArrayListAbility(new A_U_BuffAttackHPIfEnemySpellCast()));
//	/* Azure Herald */		UnitAbility.put("Azure Herald", 		constructArrayListAbility(new A_U_HealAvatarHPIfSummoned()));
//	/* Silverguard Knight */UnitAbility.put("Silverguard Knight", 	constructArrayListAbility((new A_U_Provoke()), (new A_U_BuffAttackIfAvatarTakesDamage()));
	/* Azurite Lion */		UnitAbility.put("Azurite Lion", 		constructArrayListAbility(new A_U_DoubleAttacker(false, Monster.class, null)));	
	/* Fire Spitter */		UnitAbility.put("Fire Spitter", 		constructArrayListAbility(new A_U_RangedAttacker(false, Monster.class, (BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles)))));	
//	/* Ironcliff Guardian */UnitAbility.put("Ironcliff Guardian", 	constructArrayListAbility((new A_U_SummonAnywhere()), (new A_U_Provoke());
	
		/***	Deck 2		***/
//	/* Planar Scout */		UnitAbility.put("Planar Scout", 		constructArrayListAbility(new A_U_SummonAnywhere());
//	/* Rock Pulveriser */	UnitAbility.put("Rock Pulveriser",		constructArrayListAbility(new A_U_Provoke());
	/* Pyromancer */		UnitAbility.put("Pyromancer", 			constructArrayListAbility(new A_U_RangedAttacker(false, Monster.class, (BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles)))));	
	/* Bloodshard Golem */	UnitAbility.put("Bloodshard Golem", 	constructArrayListAbility());
	/* Blaze Hound */		UnitAbility.put("Blaze Hound", 			constructArrayListAbility(new A_U_PlayersDrawCardOnUnitSummon(false, Monster.class, null)));
//	/* Windshrike */		UnitAbility.put("Windshrike", 			constructArrayListAbility((new A_U_Flying()), 
	//																(new A_U_DrawCardOnUnitDeath(false, Monster.class, null))));																
	/* Hailstone Golem 		--- identical to Deck 1	*/
	/* Serpenti */			UnitAbility.put("Serpenti", 			constructArrayListAbility(new A_U_DoubleAttacker(false, Monster.class, null)));	
	
	
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
	// To do:
	// Put in gamestate after integrating changes with Card
	
}
