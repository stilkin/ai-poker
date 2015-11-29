package be.stilkin;

import java.util.Arrays;

import com.stevebrecher.HandEval;
import com.stevebrecher.HandEval.HandCategory;

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
     * Will return a positive number if this hand has at least one multiple of the specified kind
     * 
     * @param n
     *            pass 2 for pair, 3 for three-of-a-kind, 4 for four-of-a-kind, 5 for poker
     * @return the amount of multiples of this kind, or 0 if none was found
     */
    public int countMultiples(final int n) {
	int count = 0;
	for (int v = MAX_VALUE - 1; v >= 0; v--) {
	    if (valueCount[v] >= n) {
		count++;
	    }
	}
	return count;
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
     * Will return a positive number if this hand has at least one straight
     * 
     * @param l
     *            the length of the straight (pass 5 for a normal straight)
     * @return the value of the lowest card in the straight found, or -1 if none was found
     */
    public int hasStraight(final int l) {
	int continuous = 0;
	for (int v = MAX_VALUE - 1; v >= 0; v--) {
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
     * Will return a positive number if this hand has at least one straight flush
     * 
     * @param l
     *            the lenght of the straight flush (pass 5 for a normal straight flush)
     * @return the value of the lowest card in the straight found, or -1 if none was found
     */
    public int hasStraightFlush(final int l) {
	for (int s = 0; s < MAX_SUIT; s++) {
	    int continuous = 0;
	    for (int v = MAX_VALUE - 1; v >= 0; v--) {
		if (decks[s][v] == 0) { // at least one
		    continuous = 0;
		} else {
		    continuous++;
		    if (continuous >= l) {
			return v;
		    }
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

    public HandEval.HandCategory getHandCategory() {
	// TODO: royal flush?
	if (hasStraightFlush(5) > 0) {
	    return HandCategory.STRAIGHT_FLUSH;
	}
	if (hasMultiple(4) > 0) {
	    return HandCategory.FOUR_OF_A_KIND;
	}
	if (hasFlush()) {
	    return HandCategory.FLUSH;
	}
	if (hasStraight(5) > 0) {
	    return HandCategory.STRAIGHT;
	}
	if (hasMultiple(3) > 0) {
	    return HandCategory.THREE_OF_A_KIND;
	}
	if (countMultiples(2) > 1) {
	    return HandCategory.TWO_PAIR;
	}
	if (hasMultiple(2) > 0) {
	    return HandCategory.PAIR;
	}

	return HandEval.HandCategory.NO_PAIR;
    }

}
