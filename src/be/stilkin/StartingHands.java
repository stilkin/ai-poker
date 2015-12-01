package be.stilkin;

import java.util.Arrays;
import java.util.HashMap;

import poker.Card;

/**
 * This static class contains the odds for all starting hands with a bigger than 50% win chance
 * @author stilkin
 *
 */
public class StartingHands {
    public static final HashMap<String, Float> handMap = new HashMap<String, Float>();
    static { 
	// put this in the static initializer
	StartingHands.addStartingHands();
	System.err.println("Loaded starting hand odds.");
    }

    /**
     * Get the winning odds for a specific starting hand
     * 
     * @param cardA
     *            cannot be null
     * @param cardB
     *            cannot be null
     * @return
     */
    public static float getOdds(final Card cardA, final Card cardB) {
	String letters = "" + cardA.toString().charAt(0) + cardB.toString().charAt(0);
	if (cardA.getSuit().ordinal() == cardB.getSuit().ordinal()) {
	    letters += "=";
	}
	return getOdds(letters);
    }

    /**
     * Get the winning odds for a specific starting hand
     * 
     * @param handStr
     *            cannot be null
     * @return
     */
    public static float getOdds(final String handStr) {
	char[] chars = handStr.toCharArray();
	Arrays.sort(chars);
	final Float odds = handMap.get(new String(chars));
	if (odds == null) {
	    return 0f;
	} else {
	    return odds;
	}
    }

    /**
     * Add a starting hand to the set, with a string
     * 
     * @param handStr
     *            a string indicating the starting hand, e.g. QK= (queen king suited) or AA (ace ace unsuited)
     * @param odds
     */
    public static void addStartingHand(final String handStr, final float odds) {
	char[] chars = handStr.toCharArray();
	Arrays.sort(chars);
	handMap.put(new String(chars), odds);
    }

    /**
     * Hardcoded rubbish, copied off the internet http://wizardofodds.com/games/texas-hold-em/2-player-game/
     */
    private static void addStartingHands() {
	addStartingHand("AA", 84.93f / 100);
	addStartingHand("KK", 82.12f / 100);
	addStartingHand("QQ", 79.63f / 100);
	addStartingHand("JJ", 77.15f / 100);
	addStartingHand("TT", 74.66f / 100);
	addStartingHand("99", 71.67f / 100);
	addStartingHand("88", 68.72f / 100);
	addStartingHand("KA=", 66.22f / 100);
	addStartingHand("77", 65.73f / 100);
	addStartingHand("QA=", 65.31f / 100);
	addStartingHand("JA=", 64.40f / 100);
	addStartingHand("KA", 64.47f / 100);
	addStartingHand("TA=", 63.49f / 100);
	addStartingHand("QA", 63.51f / 100);
	addStartingHand("JA", 62.54f / 100);
	addStartingHand("QK=", 62.41f / 100);
	addStartingHand("66", 62.70f / 100);
	addStartingHand("9A=", 61.51f / 100);
	addStartingHand("TA", 61.57f / 100);
	addStartingHand("JK=", 61.48f / 100);
	addStartingHand("8A=", 60.51f / 100);
	addStartingHand("TK=", 60.59f / 100);
	addStartingHand("QK", 60.43f / 100);
	addStartingHand("7A=", 59.39f / 100);
	addStartingHand("9A", 59.45f / 100);
	addStartingHand("JK", 59.44f / 100);
	addStartingHand("55", 59.64f / 100);
	addStartingHand("JQ=", 59.07f / 100);
	addStartingHand("9K=", 58.64f / 100);
	addStartingHand("5A=", 58.06f / 100);
	addStartingHand("6A=", 58.18f / 100);
	addStartingHand("8A", 58.37f / 100);
	addStartingHand("TK", 58.49f / 100);
	addStartingHand("TQ=", 58.17f / 100);
	addStartingHand("4A=", 57.14f / 100);
	addStartingHand("7A", 57.17f / 100);
	addStartingHand("8K=", 56.79f / 100);
	addStartingHand("3A=", 56.34f / 100);
	addStartingHand("JQ", 56.91f / 100);
	addStartingHand("9K", 56.41f / 100);
	addStartingHand("5A", 55.74f / 100);
	addStartingHand("6A", 55.87f / 100);
	addStartingHand("9Q=", 56.22f / 100);
	addStartingHand("7K=", 55.85f / 100);
	addStartingHand("TJ=", 56.15f / 100);
	addStartingHand("2A=", 55.51f / 100);
	addStartingHand("TQ", 55.95f / 100);
	addStartingHand("44", 56.26f / 100);
	addStartingHand("4A", 54.73f / 100);
	addStartingHand("6K=", 54.80f / 100);
	addStartingHand("8K", 54.43f / 100);
	addStartingHand("8Q=", 54.42f / 100);
	addStartingHand("3A", 53.86f / 100);
	addStartingHand("5K=", 53.83f / 100);
	addStartingHand("9J=", 54.11f / 100);
	addStartingHand("9Q", 53.86f / 100);
	addStartingHand("TJ", 53.83f / 100);
	addStartingHand("7K", 53.42f / 100);
	addStartingHand("2A", 52.95f / 100);
	addStartingHand("4K=", 52.89f / 100);
	addStartingHand("7Q=", 52.52f / 100);
	addStartingHand("6K", 52.30f / 100);
	addStartingHand("3K=", 52.07f / 100);
	addStartingHand("9T=", 52.38f / 100);
	addStartingHand("8J=", 52.31f / 100);
	addStartingHand("33", 52.84f / 100);
	addStartingHand("6Q=", 51.68f / 100);
	addStartingHand("8Q", 51.93f / 100);
	addStartingHand("5K", 51.25f / 100);
	addStartingHand("9J", 51.64f / 100);
	addStartingHand("2K=", 51.24f / 100);
	addStartingHand("5Q=", 50.71f / 100);
	addStartingHand("8T=", 50.51f / 100);
	addStartingHand("4K", 50.23f / 100);
	addStartingHand("7J=", 50.45f / 100);
    }
}
