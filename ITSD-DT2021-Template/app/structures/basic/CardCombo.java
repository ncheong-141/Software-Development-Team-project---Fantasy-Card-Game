package structures.basic;

import java.util.ArrayList;
import java.util.HashSet;

public class CardCombo implements Comparable<CardCombo> {
	
	private HashSet <Card> cardCombo;
	
	//class variables holding information on the specific combination obj
	//based on cards contained in cardCombo list
	
	//integer representing magnitude of card combo impact on enemy
	//for example if combination is one spell card (attacking enemy) and one monster
	//attackImpact will be calculated as the sum of spell's damage + monster attack points
	private int attackImpact;
	
	//same logic applies to defenseImpact
	//calculating magnitude of any impact on the player itself or any of their units
	private int defenseImpact;
	
	//boolean to track whether the card combo contains at least one card with a special skill
	private boolean specialSkill;
	
	//weight given to the parameters above will vary depending on player's "stance"
	//for example with low player's health a combination with a higher defense impact will be given a higher score
	private double attackWeight, defenseWeight, skillWeight;
	
	//overall score given to the specific card combination
	//score is calculated within this class and retrieved by Computer Player to make decisions
	protected double score;
	
	
	public CardCombo() {
		cardCombo = new HashSet<Card>();
	}
	
	public HashSet<Card> getCardCombo(){
		return cardCombo;
	}
	
	public void add(Card c) {
		cardCombo.add(c);
	}
	
	public int calcComboScore() {
		
		
		return 0;
	}
	
	private void assignWeights() {
		
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
	
	// To do: add to ComputerPlayer package (?)
}
