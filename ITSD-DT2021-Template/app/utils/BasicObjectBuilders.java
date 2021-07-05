package utils;

import java.io.File;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import structures.basic.Avatar;
import structures.basic.BigCard;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.abilities.Ability;
import structures.basic.abilities.AbilityToUnitLinkage;
import structures.basic.abilities.Call_IDs;
import utils.StaticConfFiles;

/**
 * This class contains methods for producing basic objects from configuration files
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class BasicObjectBuilders {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	/**
	 * This class produces a Card object (or anything that extends Card) given a configuration
	 * file. Configuration files can be found in the conf/gameconfs directory. The card should
	 * be given a unique id number. The classtype field specifies the type of Card to be
	 * constructed, e.g. Card.class will create a default card object, but if you had a class
	 * extending card, e.g. MyAwesomeCard that extends Card, you could also specify
	 * MyAwesomeCard.class here. If using an extending class you will need to manually set any
	 * new data fields. 
	 * @param configurationFile
	 * @param id
	 * @param classtype
	 * @return
	 */
	
	// ObjectBuilder for Unit cards; mapper uses the cardConfig file to make the Card,
	// and the resulting Card object stores the unitConfig file for later use in summoning
	public static Card loadCard(String cardConfigurationFile, String unitConfig, int id, Class<? extends Card> classtype) {
		try {
			Card card = mapper.readValue(new File(cardConfigurationFile), classtype);
			card.setId(id);
			card.setConfigFile(unitConfig);
			return card;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	// Alternative Builder for non-unit cards, overloaded method signature for easy use
	// Config file here stores the card's config file (for Spell card use)
	public static Card loadCard(String cardConfigurationFile, int id, Class<? extends Card> classtype) {
		try {
			Card card = mapper.readValue(new File(cardConfigurationFile), classtype);
			card.setId(id);
			card.setConfigFile(cardConfigurationFile);
			return card;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	/**
	 * This class produces a EffectAnimation object given a configuration
	 * file. Configuration files can be found in the conf/gameconfs directory.
	 * @param configurationFile
	 * @return
	 */
	public static EffectAnimation loadEffect(String configurationFile) {
		try {
			EffectAnimation effect = mapper.readValue(new File(configurationFile), EffectAnimation.class);
			return effect;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	/**
	 * Loads a unit from a configuration file. Configuration files can be found 
	 * in the conf/gameconfs directory. The unit needs to be given a unique identifier
	 * (id). This method requires a classtype argument that specifies what type of
	 * unit to create. 
	 * @param configFile
	 * @return
	 */
	// Leave this intact to support game loading code ---?? Reconsider
	public static Unit loadUnit(String configFile, int id,  Class<? extends Unit> classType) {
		
		try {
			Unit unit = mapper.readValue(new File(configFile), classType);
			unit.setId(id);
			return unit;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Alternative ObjectBuilder that uses the Monster constructor
	public static Monster loadMonsterUnit(String configFile, Card statsRef, Player p, Class<? extends Monster> classType) {

		try {
			System.out.println("configFile name in objectbuilder is: "+ configFile);
			Monster mUnit = mapper.readValue(new File(configFile), classType);
			
			// Set monster attributes from reference Card info
			mUnit.setId(statsRef.getId());
			mUnit.setName(statsRef.getCardname());				// Check mapper doesn't do this?
			mUnit.setHP(statsRef.getBigCard().getHealth());
			mUnit.setMaxHP(statsRef.getBigCard().getHealth());
			mUnit.setAttackValue(statsRef.getBigCard().getAttack());
			
			// 
			mUnit.setOwner(p);
			
			System.out.println("mUnit has name " + mUnit.getName());
			System.out.println("mUnit has ID " + mUnit.getId());
			
			// Ability setting
			if(AbilityToUnitLinkage.UnitAbility.containsKey(mUnit.getName())) {
//				if(mUnit.getAbility() == null) {	mUnit.setAbility(new ArrayList <Ability> ());	}
				mUnit.setAbility(AbilityToUnitLinkage.UnitAbility.get(mUnit.getName()));
			}	
			
			return mUnit; 
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		
		return null;
	}
	
	// Alt method signature with no Player, currently in place for easier development (final submission will use Player signature)
	public static Monster loadMonsterUnit(String configFile, Card statsRef, Class<? extends Monster> classType) {

		try {
			Monster mUnit = mapper.readValue(new File(configFile), classType);
			
			// Set monster attributes from reference Card
			mUnit.setId(statsRef.getId());
			
			mUnit.setName(statsRef.getCardname());
			mUnit.setHP(statsRef.getBigCard().getHealth());
			mUnit.setMaxHP(statsRef.getBigCard().getHealth());
			mUnit.setAttackValue(statsRef.getBigCard().getAttack());
			
//			mUnit.setOwner(p);
			
			System.out.println("mUnit has ID " + mUnit.getId());
			
			return mUnit; 
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		
		return null;
	}
	
	
	public static Avatar loadAvatar(String configFile, int id, Player p, Board b, Class<? extends Avatar> classType) {
		
		try {
			Avatar unit = mapper.readValue(new File(configFile), classType);
			unit.setId(id);
			unit.setOwner(p,b);

			if(p instanceof HumanPlayer) {
				unit.setName("Human Avatar");
			} else {
				unit.setName("AI Avatar");
			}
			
			return unit;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
		
	}

	/**
	 * Generates a tile object with x and y indices
	 * @param x
	 * @param y
	 * @return
	 */
	public static Tile loadTile(int x, int y) {
		int gridmargin = 5;
		int gridTopLeftx = 410;
		int gridTopLefty = 280;
		
		Tile tile = Tile.constructTile(StaticConfFiles.tileConf);
		tile.setXpos((tile.getWidth()*x)+(gridmargin*x)+gridTopLeftx);
		tile.setYpos((tile.getHeight()*y)+(gridmargin*y)+gridTopLefty);
		tile.setTilex(x);
		tile.setTiley(y);
		
		return tile;
		
	}
	
}
