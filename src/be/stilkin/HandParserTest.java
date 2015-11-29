package be.stilkin;


import java.util.ArrayList;
import java.util.List;

import poker.Card;

/**
 * 
 * @author stilkin
 *
 */
public class HandParserTest {

    public static void main(String[] args) {
	final List<Card> cards = new ArrayList<>();
	// flush
	cards.add(new Card(2));
	cards.add(new Card(4));
	cards.add(new Card(6));
	cards.add(new Card(8));
	cards.add(new Card(10));
	
	// pair / 3oak
	cards.add(new Card(11+13));
	cards.add(new Card(11+26));
	cards.add(new Card(11));
	
	// complete the straight
	cards.add(new Card(9+13));
	cards.add(new Card(7+26));

	cards.add(new Card(2+26));
	
	HandParser hp = new HandParser();
	hp.addCards(cards.toArray(new Card[]{}));
	
	System.err.println("Pair: " + hp.hasMultiple(2));
	System.err.println("3OAK: " + hp.hasMultiple(3));
	System.err.println("4OAK: " + hp.hasMultiple(4));
	System.err.println("Flush: " + hp.hasFlush());
	System.err.println("Straight: " + hp.hasStraight(5));
	System.err.println("Straight flush: " + hp.hasStraightFlush(5));
	System.err.println("Amount of pairs: " + hp.countMultiples(2));

    }

}
