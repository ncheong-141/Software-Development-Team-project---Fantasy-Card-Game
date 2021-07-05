package structures.basic;

import java.util.ArrayList;
import java.util.Collections;

import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Deck{//class used to create and manage player and ai decks
	private ArrayList<Card> cardList;// array of card objects that comprise the deck
	
	public Deck() { //constructor for deck
		this.cardList = new ArrayList<Card>();
	}
	
	public void deckOne() {// creates an instance of the human player deck
		Card card;
		Unit unit;
		String[] cardConfigNames = {// list of cards in player deck
				StaticConfFiles.c_azure_herald,
				StaticConfFiles.c_azurite_lion,
				StaticConfFiles.c_comodo_charger,
				StaticConfFiles.c_fire_spitter,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_ironcliff_guardian,
				StaticConfFiles.c_pureblade_enforcer,
				StaticConfFiles.c_silverguard_knight,
				StaticConfFiles.c_sundrop_elixir,
				StaticConfFiles.c_truestrike};
		String[] unitConfigNames = {// list of units in player deck
				StaticConfFiles.u_azure_herald,
				StaticConfFiles.u_azurite_lion,
				StaticConfFiles.u_comodo_charger,
				StaticConfFiles.u_fire_spitter,
				StaticConfFiles.u_hailstone_golem,
				StaticConfFiles.u_ironcliff_guardian,
				StaticConfFiles.u_pureblade_enforcer,
				StaticConfFiles.u_silverguard_knight};
		
		
		for (int i=0; i<=7; i++) {// cycles through the list and creates two instances of each card
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i], i+2, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i],i+10, Card.class);
			cardList.add(card);
			}
		for(int j=8; j<=9; j++) {//cycles through spells in list and creates cards for each
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+10, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+12, Card.class);
			cardList.add(card);
		}
		}
		
	public void deckTwo() {// creates AI player deck
		Card card;
		Unit unit;
		String[] cardConfigNames= {//list of cards in AI player deck
				StaticConfFiles.c_blaze_hound,
				StaticConfFiles.c_bloodshard_golem,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_planar_scout,
				StaticConfFiles.c_pyromancer,
				StaticConfFiles.c_windshrike,
				StaticConfFiles.c_serpenti,
				StaticConfFiles.c_rock_pulveriser,
				StaticConfFiles.c_entropic_decay,
				StaticConfFiles.c_staff_of_ykir};
		String[] unitConfigNames= {//list of units in AI player deck
				StaticConfFiles.u_blaze_hound,
				StaticConfFiles.u_bloodshard_golem,
				StaticConfFiles.u_hailstone_golem,
				StaticConfFiles.u_planar_scout,
				StaticConfFiles.u_pyromancer,
				StaticConfFiles.u_windshrike,
				StaticConfFiles.u_serpenti,
				StaticConfFiles.u_rock_pulveriser};
		
		for (int i=0; i<=7; i++) {// cycles through the list and creates two instances of each card
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i], i+22, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i],i+30, Card.class);
			cardList.add(card);
			}
		for(int j=8; j<=9; j++) {//cycles through spells in list and creates cards for each
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+30, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+32, Card.class);
			cardList.add(card);
		}
		
		}
		
	public void delCard(int i){// removes card from the deck and corresponding unit
		cardList.remove(i);
		//unitDeck.remove(i);
	}
	
	//getters and setters
	public void setCardList(ArrayList<Card> deck) {
		this.cardList = deck;
	}
	public ArrayList<Card> getCardList() {
		return cardList;
	}

	//method to test deck creation in eclipse
	public void pod(Deck deck) {
		ArrayList<Card> check= new ArrayList<Card>();
		check=deck.getCardList();
		for(int i=0; i<check.size();i++) {
			System.out.print(check.get(i).getCardname());
		}
	}
	
	public void shuffleDeck() {
		Collections.shuffle(cardList);
	}

}
	
// To do:
// Less hard coding in the deck construction loops
// Adjust deck getter name to be more transparent
