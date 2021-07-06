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
		int start=2;//takes into account 0,1 being reserved for avatar ids
		
		
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
		int monsters= unitConfigNames.length;
		int spells= cardConfigNames.length-unitConfigNames.length;
		
		
		for (int i=0;i<= cardConfigNames.length-spells; i++) {// cycles through the list and creates two instances of each card
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i], i+start, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i],i+monsters+start, Card.class);
			cardList.add(card);
			}
		for(int j=cardConfigNames.length-spells+1; j<=cardConfigNames.length; j++) {//cycles through spells in list and creates cards for each
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+monsters+start, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+monsters+spells+start, Card.class);
			cardList.add(card);
		}
		}
		
	public void deckTwo() {// creates AI player deck
		Card card;
		Unit unit;
		int start=2;//takes into account 0,1 being reserved for avatar ids
		
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
		int monsters= unitConfigNames.length;
		int spells= cardConfigNames.length-unitConfigNames.length;
		int deckLength= cardConfigNames.length*2;
		
		for (int i=0; i<=cardConfigNames.length-spells; i++) {// cycles through the list and creates two instances of each card
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i], i+deckLength+start, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[i], unitConfigNames[i],i+deckLength+monsters+start, Card.class);
			cardList.add(card);
			}
		for(int j=cardConfigNames.length-spells+1; j<=cardConfigNames.length; j++) {//cycles through spells in list and creates cards for each
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+deckLength+monsters+start, Card.class);
			cardList.add(card);
			card = BasicObjectBuilders.loadCard(cardConfigNames[j],j+deckLength+monsters+spells+start, Card.class);
			cardList.add(card);
		}
		
		}
		
	public void delCard(int i){// removes card from the deck and corresponding unit
		cardList.remove(i);
		//unitDeck.remove(i);
	}
	
	
	public void shuffleDeck() {
		Collections.shuffle(cardList);
	}

	//getters and setters
	public void setCardList(ArrayList<Card> deck) {
		this.cardList = deck;
	}
	public ArrayList<Card> getCardList() {
		return cardList;
	}
}
