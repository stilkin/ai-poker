package be.stilkin;

import java.util.Arrays;

import poker.Card;

/**
 * SPADES / HEARTS / CLUBS / DIAMONDS DEUCE, THREE, ..., QUEEN, KING, ACE
 * 
 * @author stilkin
 *
 */
public class HandParser {
    public static final int MAX_SUIT = 4;
    public static final int MAX_VALUE = 13;
    private final int[][] decks = new int[MAX_SUIT][MAX_VALUE];
    private final int[] suitCount = new int[MAX_SUIT];
    private final int[] valueCount = new int[MAX_VALUE];

    public HandParser() {}

    public HandParser(Card[] cards) {
	addCards(cards);
    }

    /**
     * Add a single card to this set. The hand parser will keep track of the suit and value.
     * 
     * @param card
     */
    public void addCard(Card card) {
	final int suit = card.getSuit().ordinal();
	final int height = card.getHeight().ordinal();
	decks[suit][height]++;
	suitCount[suit]++;
	valueCount[height]++;
    }

    /**
     * At an array of cards
     */
    public void addCards(Card[] cards) {
	for (Card c : cards) {
	    addCard(c);
	}
    }

    /**
     * Empty the state of this hand parser. Use if you want to reuse object.
     */
    public void clear() {
	for (int d = 0; d < decks.length; d++) {
	    Arrays.fill(decks[d], 0);
	}
    }

    /**
     * Will return true if this hand has at least one flush
     */
    public boolean hasFlush() {
	for (int s = 0; s < MAX_SUIT; s++) {
	    if (suitCount[s] >= 5) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Will return a positive number if this hand has at least one multiple
     * 
     * @param n
     *            pass 2 for pair, 3 for three-of-a-kind, 4 for four-of-a-kind, 5 for poker
     * @return the value of the highest multiple, or -1 if none was found
     */
    public int hasMultiple(final int n) {
	for (int v = MAX_VALUE - 1; v >= 0; v--) {
	    if (valueCount[v] >= n) {
		return v;
	    }
	}
	return -1;
    }

    /**
     * Will return a positive number if this hand has at least one straight
     * 
     * @param l
     *            the lenght of the straight (pass 5 for a normal straight)
     * @return the value of the hihghest card in the straight, or -1 if none was found
     */
    public int hasStraight(final int l) {
	int continuous = 0;
	for (int v = 0; v < MAX_VALUE; v++) {
	    int valCount = 0;
	    for (int s = 0; s < MAX_SUIT; s++) {
		if (decks[s][v] > 0) {
		    valCount++;
		    break;
		}
	    }
	    if (valCount < 1) { // at least one
		continuous = 0;
	    } else {
		continuous++;
		if (continuous >= l) {
		    return v;
		}
	    }
	}
	return -1;
    }

    /**
     * @param suit
     *            0 1 2 3 for SPADES / HEARTS / CLUBS / DIAMONDS
     * @return amount of cards with this suit
     */
    public int getSuitCount(int suit) {
	return suitCount[suit];
    }

    /**
     * @param value
     *            0 - 13 for DEUCE, THREE, ..., QUEEN, KING, ACE
     * @return amount of cards with this value
     */
    public int getValueCount(int value) {
	return valueCount[value];
    }

}
