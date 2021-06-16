package structures.basic;

import java.util.ArrayList;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Deck{//class used to create and manage player and ai decks
	private ArrayList<Card> deck;// array of card objects that comprise the deck
	
	public Deck() { //constructor for deck
		this.deck = null;
	}
	
	private ArrayList<Card> deckOne() {// creates an instance of the human player deck
		Card card;
		String[] cardList= {// list of cards in player deck
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
		for (int i=0; i<=9; i++) {// cycles through the list and creates two instances of each card
			card = BasicObjectBuilders.loadCard(cardList[i], i+1, Card.class);
			deck.add(card);
			card = BasicObjectBuilders.loadCard(cardList[i], i+11, Card.class);
			deck.add(card);
			}
		return deck;
		}
		
	private ArrayList<Card> deckTwo() {// creates AI player deck
		Card card;
		String[] cardList= {//list of cards in AI player deck
				StaticConfFiles.c_blaze_hound,
				StaticConfFiles.c_bloodshard_golem,
				StaticConfFiles.c_entropic_decay,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_planar_scout,
				StaticConfFiles.c_pyromancer,
				StaticConfFiles.c_serpenti,
				StaticConfFiles.c_rock_pulveriser,
				StaticConfFiles.c_staff_of_ykir,
				StaticConfFiles.c_windshrike,};
		for (int i=0; i<=9; i++) {//cycles through card list twice and creates two of each card and adds to deck
			card = BasicObjectBuilders.loadCard(cardList[i], i+1, Card.class);
			deck.add(card);
			card = BasicObjectBuilders.loadCard(cardList[i], i+11, Card.class);
			deck.add(card);
			}
		return deck;
		}
		
	public void delCard(int i){// removes card from the deck
		deck.remove(i);
	}
	
	//getters and setters
	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}
	public ArrayList<Card> getDeck() {
		return deck;
	}
}
