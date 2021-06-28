package structures.basic;

import java.util.ArrayList;

public class CardCombo {
	
	private int attackImpact;
	private int defenseImpact;
	private boolean specialSkill;
	private ArrayList<Card> cardCombo;
	
	public CardCombo() {
		cardCombo = new ArrayList<Card>();
	}
	
	public ArrayList<Card> getCardCombo(){
		return cardCombo;
	}
	
	public void add(Card c) {
		cardCombo.add(c);
	}
	
	public int calcAttackImpact () {
		this.attackImpact = 0;
		
		
		return attackImpact;
	}
	
	public int calcDefenseImpact() {
		this.defenseImpact = 0;
		
		return defenseImpact;
	}
	
	
	
}
