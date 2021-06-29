package structures.basic;

import java.util.ArrayList;

public class CardCombo implements Comparable<CardCombo> {
	
	private int attackImpact;
	private int defenseImpact;
	private boolean specialSkill;
	private ArrayList<Card> cardCombo;
	protected double score;
	private double attackWeight, defenseWeight, skillWeight;
	
	public CardCombo() {
		cardCombo = new ArrayList<Card>();
	}
	
	public ArrayList<Card> getCardCombo(){
		return cardCombo;
	}
	
	public void add(Card c) {
		cardCombo.add(c);
	}
	
	public int calcComboScore() {
		
		
		return 0;
	}
	
	private void assignWeight() {
		
	}
	private int calcAttackImpact () {
		this.attackImpact = 0;
		
		
		return attackImpact;
	}
	
	public double getScore() {
		return score;
	}

	private int calcDefenseImpact() {
		this.defenseImpact = 0;
		
		return defenseImpact;
	}
	
	private int leftOverMana() {
		return 0;
	}

	@Override
	public int compareTo(CardCombo o) {
		if (this.getScore() > o.getScore()) return 1;
		else if (this.getScore()<o.getScore()) return -1;
		else return 0;
	}
	
	
}
