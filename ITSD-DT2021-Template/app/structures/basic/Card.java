package structures.basic;

import java.util.ArrayList;
import structures.basic.Unit;
import structures.basic.abilities.AbilityToUnitLinkage;
import utils.BasicObjectBuilders;


/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card implements Comparable<Card> {
	
	int id;//unique identifier for each card
	
	String cardname;//name of card
	int manacost;//mana cost to play associated unit
	
	MiniCard miniCard;//display element for unselected cards
	BigCard bigCard;//display element for selected card
	
	String configFile;
	
	public Card() {};
	
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
		this.configFile="";
	}
	
	
	
	
	
	
	//shortcut methods for ability access
	
	//checks whether card targets spell or enemy
	public boolean targetEnemy() {
		boolean result= AbilityToUnitLinkage.UnitAbility.get(getCardname()).get(0).targetEnemy();
		return result;
	}
	
	//checks that monster/spell associated with card has an ability
	public boolean hasAbility(){
		boolean result= false;
		if(this.getBigCard().getAttack()>0) {//checks whether card is a monster
		//create a temporary	
			Monster mon = BasicObjectBuilders.loadMonsterUnit("StaticConfFiles.c_"+this.getCardname(), 99, this, Monster.class);
			if(mon.getMonsterAbility()!=null) {//if monster has ability, a true result is given
				result=true;
			}else {//if monster doesn't have an ability a false result is give
				result=false;
			}
		}else if(this.getBigCard().getAttack()<0) {//checks whether card is a spell
			Spell spell = (Spell)BasicObjectBuilders.loadCard("StaticConfFiles.c_"+this.getCardname(),99, Spell.class);
			if(spell.getAbility()!=null) {//if spell has an effect(should always have one) returns a true result
				result=true;
			}else {//if spell has no ability(should never happen) false result returned
				result=false;
			}
		}
		return result;
		}

	//special getter methods to aid with ai logic decisions getting card(if monster) health and attack
	public int getCardHP(){
		return this.getBigCard().getHealth();
	}
	public int getCardAttack() {
		return this.getBigCard().getAttack();
	}
	
	//helper method to show where card(if monster) is playable
	public boolean playableAnywhere() {
		if(this.getCardname().equals("Ironcliff Guardian") || this.getCardname().contentEquals("Planar Scout")) {
			return true;
		}else {
			return false;
		}
	}
	
	
	//method to return integer value of unit ability effect 
	//i.e if ability is +2 damage, the method would return 2
	public int getAbilityEffect() {
		int result=0;
		return result;
	}
	
	@Override
	//compares mana cost of two cards for ai logic
	public int compareTo(Card o) {
		if(this.manacost>o.getManacost()) {
			return 1;
		}else if(this.manacost<o.getManacost()){
			return -1;
		}else {
		return 0;
		}
	}
	
	
	
	//getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	public int getManacost() {
		return manacost;
	}
	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	public MiniCard getMiniCard() {
		return miniCard;
	}
	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}
	public BigCard getBigCard() {
		return bigCard;
	}
	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
	public String getConfigFile() {
		return this.configFile;
	}
}