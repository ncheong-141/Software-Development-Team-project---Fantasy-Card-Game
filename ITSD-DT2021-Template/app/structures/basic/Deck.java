package structures.basic;

import java.util.ArrayList;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Deck{
	private ArrayList<Card> deck;
	
	public Deck() {
		this.deck = null;
	}
	
	private ArrayList<Card> deckOne() {
		Card card;
		String[] cardList= {
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
		for (int i=0; i<=9; i++) {
			card = BasicObjectBuilders.loadCard(cardList[i], i+1, Card.class);
			deck.add(card);
			card = BasicObjectBuilders.loadCard(cardList[i], i+11, Card.class);
			deck.add(card);
			}
		return deck;
		}
		
	private ArrayList<Card> deckTwo() {
		Card card;
		String[] cardList= {
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
		for (int i=0; i<=9; i++) {
			card = BasicObjectBuilders.loadCard(cardList[i], i+1, Card.class);
			deck.add(card);
			card = BasicObjectBuilders.loadCard(cardList[i], i+11, Card.class);
			deck.add(card);
			}
		return deck;
		}
		
	public void delCard(int i){
		deck.remove(i);
	}
	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}
	public ArrayList<Card> getDeck() {
		return deck;
	}
	
}
