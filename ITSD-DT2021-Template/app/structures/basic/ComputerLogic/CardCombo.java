package structures.basic.ComputerLogic;
import structures.basic.*; 
import java.util.ArrayList;
import java.util.HashSet;

public class CardCombo implements Comparable<CardCombo> {
	
	private HashSet <Card> cardCombo;
	
	//class variables holding information on the specific combination obj
	//based on cards contained in cardCombo list
	
	//integer representing magnitude of card combo impact on enemy
	//for example if combination is one spell card (attacking enemy) and one monster
	//attackImpact will be calculated as the sum of spell's damage + monster attack points
	private int attackScore;
	
	//same logic applies to defenseImpact
	//calculating magnitude of any impact on the player itself or any of their units
	private int defenseScore;
	
	//boolean to track whether the card combo contains at least one card with a special skill
	private boolean specialSkill;
	
	//weight given to the parameters above will vary depending on player's "stance"
	//for example with low player's health a combination with a higher defense impact will be given a higher score
	
	//overall score given to the specific card combination
	//score is calculated within this class and retrieved by Computer Player to make decisions
	protected int Attackscore, DefenseScore, score;
	
	
	public CardCombo() {
		cardCombo = new HashSet<Card>();
	}
	
	public HashSet<Card> getCardCombo(){
		return cardCombo;
	}
	
	public void add(Card c) {
		cardCombo.add(c);
	}
	
	public boolean isEmpty() {
		return cardCombo.isEmpty();
	}
	
	public int getScore() {
		return this.score;
	}
	
	private void setSpecialAbility() {
		for (Card c : this.cardCombo) {
			if (c.hasAbility()) {
				this.specialSkill = true;
				break;
			}
		}
	}
	

	public void calcAttackScore () {
		for (Card c : this.cardCombo) {
			if (c.getCardAttack() >0) this.attackScore += c.getCardAttack();
			else if (c.getCardAttack()<= 0) {
				if (c.targetEnemy()) {
//					this.attackScore += c.getAbilityEffect();
					// Commented to allow compile
				}
			}
		}
		this.setSpecialAbility();
		if (this.specialSkill) attackScore++;
		this.score = attackScore;
		
	}
	

	public void calcDefenseScore() {
		for (Card c: this.cardCombo) {
			if (c.getCardHP()>0) this.defenseScore += c.getCardHP();
			else if(c.getCardHP()<=0){
				if (!c.targetEnemy()) {
//					this.defenseScore += c.getAbilityEffect();
					// Commented to allow compile
				}
			}
		}
		this.setSpecialAbility();
		if (this.specialSkill) defenseScore++;
		this.score = defenseScore;
		
	}
	
	public int totalManaCost() {
		int totManaCost = 0;
		for (Card c : this.cardCombo) {
			totManaCost += c.getManacost();
		}
		 return totManaCost;
	
	}

	
	@Override
	public int compareTo(CardCombo o) {
		if (this.getScore() > o.getScore()) return 1;
		else if (this.getScore()<o.getScore()) return -1;
		else return 0;
	}
	
	
}
