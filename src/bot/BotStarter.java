/**
 * www.TheAIGames.com 
 * Heads Up Omaha pokerbot
 *
 * Last update: May 07, 2014
 *
 * @author Jim van Eeden, Starapple
 * @version 1.0
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package bot;

import java.util.HashMap;

import com.stevebrecher.HandEval;

import poker.Card;
import poker.HandHoldem;
import poker.PokerMove;

/**
 * This class is the brains of your bot. Make your calculations here and return the best move with GetMove
 * 
 * @author stilkin
 */
public class BotStarter implements Bot {
    public static final String CALL_ACTION = "call";
    public static final String RAISE_ACTION = "raise";
    public static final String CHECK_ACTION = "check";
    public static final String FOLD_ACTION = "fold";
    public static final float CURIOSITY = 0.05f;
    public static final float COCKYNESS = 0.025f;
    private HashMap<String, Integer> roundMoneys = new HashMap<String, Integer>();
    private String botName = "stilkin";
    private HandHoldem hand;
    private int lastRound = -1;

    /**
     * Implement this method to return the best move you can. Currently it will return a raise the ordinal value of one of our cards is higher than 9, a call when one of the cards
     * has a higher ordinal value than 5 and a check otherwise.
     * 
     * @param state
     *            : The current state of your bot, with all the (parsed) information given by the engine
     * @param timeOut
     *            : The time you have to return a move
     * @return PokerMove : The move you will be doing
     */
    @Override
    public PokerMove getMove(BotState state, Long timeOut) {
	botName = state.getMyName();
	hand = state.getHand();
	final Card[] table = state.getTable();

	if (lastRound != state.getRound()) { // reset round counters
	    lastRound = state.getRound();
	    roundMoneys.clear();
	}

	if (table == null || table.length < 3) { // pre-flop
	    return preFlop(table, state);
	} else { // post-flop
	    return postFlop(table, state);
	}
    }

    // *****************
    // *** POST FLOP ***
    // *****************

    private PokerMove postFlop(final Card[] table, final BotState state) {
	final int callAmount = state.getAmountToCall();
	HandEval.HandCategory botHand = getHandCategory(hand, table);
	final HandEval.HandCategory tableHand = getCardsCategory(table);

	// if the five table cards are stronger or equally strong // TODO: code for flop and river
	if (tableHand != null && tableHand.ordinal() >= botHand.ordinal()) {
	    // only look at our own cards for decision making
	    // botHand = getHandCategory(hand, null);
	    return preFlopCheck(state);
	}

	// Get the ordinal values of the cards in your hand
	final int height1 = hand.getCard(0).getHeight().ordinal();
	final int height2 = hand.getCard(1).getHeight().ordinal();
	final int sum = height1 + height2;

	int odds = 1;
	switch (botHand) {
	    case STRAIGHT_FLUSH:
		odds = 72192;
		return raiseWithOdds(state, odds);
	    case FOUR_OF_A_KIND:
		odds = 4164;
		return raiseWithOdds(state, odds);
	    case FULL_HOUSE:
		odds = 693;
		return raiseWithOdds(state, odds);
	    case FLUSH: // TODO: check flush height
		odds = 508;
		return raiseWithOdds(state, odds);
	    case STRAIGHT:
		odds = 254;
		return raiseWithOdds(state, odds);
	    case THREE_OF_A_KIND: // TODO: find out which card is in the THREE OF A KIND
		odds = 46;
		if (sum > 5) {
		    return loggedAction(botName, CALL_ACTION, callAmount);
		}
		break;
	    case TWO_PAIR: // TODO: find out which card is in the TWO PAIR
		odds = 20;
		if (sum > 10) {
		    return loggedAction(botName, CALL_ACTION, callAmount);
		}
		break;
	    case PAIR: // TODO: find out which card is in the PAIR
		odds = 2;
		if (sum > 20) {
		    return loggedAction(botName, CALL_ACTION, callAmount);
		}
		break;
	    case NO_PAIR:
		odds = 1;
		break;
	}
	return loggedAction(botName, CHECK_ACTION, 0);
    }

    /**
     * We have a good hand, with how much do we raise?
     */
    private PokerMove raiseWithOdds(final BotState state, int odds) {
	final int multiplier = 2 + (odds / 120);
	int raise = multiplier * state.getBigBlind();
	final int stackDiff = state.getmyStack() - state.getOpponentStack();

	if (stackDiff > 0) { // we are ahead
	    raise += (int) (0.15f * stackDiff);
	}

	final int raisedSoFar = roundMoneys.getOrDefault(RAISE_ACTION, 0);
	final int calledSoFar = roundMoneys.getOrDefault(CALL_ACTION, 0);
	final int bothSoFar = raisedSoFar + calledSoFar;

	if (bothSoFar < raise) { // set to raise only once
	    return loggedAction(botName, RAISE_ACTION, raise);
	} else {
	    final int callAmount = state.getAmountToCall();
	    return loggedAction(botName, CALL_ACTION, callAmount);
	}
    }

    // ****************
    // *** PRE FLOP ***
    // ****************

    /**
     * What do we do pre-flop? Depends on pair / no-pair
     */
    private PokerMove preFlop(final Card[] table, final BotState state) {
	final HandEval.HandCategory category = getHandCategory(hand, table);

	switch (category) {
	    case PAIR: // they are identical
		return preFlopPair(state);
	    case NO_PAIR: // not identical
		return preFlopNoPair(state);
	    default:
		return preFlopCheck(state);
	}
    }

    /**
     * We are dealt a PAIR pre-flop. What do we do?
     */
    private PokerMove preFlopPair(final BotState state) {

	// get the ordinal values of the pair in your hand
	final int pairHeight = hand.getCard(0).getHeight().ordinal(); // should be identical

	if (pairHeight > 7) { // TEN or higher
	    return preFlopRaise(state, (pairHeight - 7) * 2);
	} else if (pairHeight > 5) { // EIGHT or higher
	    return preFlopCall(state, 4);
	    // return loggedAction(botName, CALL_ACTION, callAmount);
	}
	// else
	return preFlopCheck(state);
    }

    /**
     * We are dealt NO PAIR pre-flop. What do we do?
     */
    private PokerMove preFlopNoPair(final BotState state) {
	final Card cardX = hand.getCard(0);
	final Card cardY = hand.getCard(1);
	// get the ordinal values of the cards in your hand
	final int height1 = cardX.getHeight().ordinal();
	final int height2 = cardY.getHeight().ordinal();
	final int sum = height1 + height2;
	final int diff = Math.abs(height1 - height2);
	final boolean suited = cardY.getSuit().equals(cardX.getSuit());

	if (sum > 20) { // AK23 - AQ22 - KQ21 - AJ21 -
	    return preFlopRaise(state, (sum - 20)); // raises at least once
	} else if (height1 > 9 || height2 > 9) { // AKQ
	    if (diff < 3 || (diff < 4 && suited)) { // with semi-connected second card
		return preFlopCall(state, 3);
	    }
	}
	// else
	return preFlopCheck(state);
    }

    /**
     * Calls up to big blind, otherwise checks (pre-flop)
     */
    private PokerMove preFlopCheck(final BotState state) {
	final int callAmount = state.getAmountToCall();
	final float blindRatio = (float) state.getBigBlind() / state.getmyStack();
	// when the blind is too big compared to our stack, we don't peek as much // TODO: is this smart?
	if (blindRatio < CURIOSITY && callAmount < state.getBigBlind()) { // curiosity fee
	    return loggedAction(botName, CALL_ACTION, callAmount);
	} else {
	    return loggedAction(botName, CHECK_ACTION, 0);
	}
    }

    /**
     * Will raise the min raise up to a certain point, determined by factor value. After that, only calls.
     * 
     * @param factor
     *            Determines maximal raise. Is a factor of 2 * BIG BLIND. (Should be at least 1)
     */
    private PokerMove preFlopRaise(final BotState state, final int factor) {
	final int minRaise = 2 * state.getBigBlind();
	int max = factor * minRaise;
	final int stackDiff = state.getmyStack() - state.getOpponentStack();

	if (stackDiff > 0) { // we are ahead
	    max += (int) (0.1f * stackDiff);
	}

	final int raisedSoFar = roundMoneys.getOrDefault(RAISE_ACTION, 0);
	final int calledSoFar = roundMoneys.getOrDefault(CALL_ACTION, 0);
	final int bothSoFar = raisedSoFar + calledSoFar;

	if (bothSoFar < max) {
	    return loggedAction(botName, RAISE_ACTION, minRaise);
	} else {
	    return preFlopCall(state, factor);
	}
    }

    /**
     * Will call up to a certain point, determined by factor value. After that, checks.
     * 
     * @param factor
     *            Determines maximal call. Is a factor of BIG BLIND. (Should be at least 3 to be able to call one minimum raise)
     */
    private PokerMove preFlopCall(final BotState state, final int factor) {
	final int callAmount = state.getAmountToCall();
	final int max = factor * state.getBigBlind();

	final int raisedSoFar = roundMoneys.getOrDefault(RAISE_ACTION, 0);
	final int calledSoFar = roundMoneys.getOrDefault(CALL_ACTION, 0);
	final int bothSoFar = raisedSoFar + calledSoFar;

	if (bothSoFar + callAmount <= max) {
	    return loggedAction(botName, CALL_ACTION, callAmount);
	} else {
	    return preFlopCheck(state);
	}
    }

    /**
     * TODO: add more logging to this method
     */
    private PokerMove loggedAction(final String botName, final String action, final int amount) {
	final int currentAmount = roundMoneys.getOrDefault(action, 0);
	roundMoneys.put(action, currentAmount + amount);

	return new PokerMove(botName, action, amount);
    }

    /**
     * Calculates the hand strength, only works with 5 cards. This uses the com.stevebrecher package to get hand strength.
     * 
     * @param cardSet
     *            : a set of five cards
     * @return HandCategory with what the cardSet is worth
     */
    public HandEval.HandCategory getCardsCategory(final Card[] cardSet) {
	if (cardSet != null && cardSet.length == 5) {
	    long handCode = 0;
	    for (Card card : cardSet) {
		handCode += card.getNumber();
	    }
	    return rankToCategory(HandEval.hand5Eval(handCode));

	}
	return null;
    }

    // ***********************
    // *** UTILITY METHODS ***
    // ***********************

    /**
     * Calculates the bot's hand strength, with 0, 3, 4 or 5 cards on the table. This uses the com.stevebrecher package to get hand strength.
     * 
     * @param hand
     *            : cards in hand
     * @param table
     *            : cards on table
     * @return HandCategory with what the bot has got, given the table and hand
     */
    public HandEval.HandCategory getHandCategory(HandHoldem hand, Card[] table) {
	if (table == null || table.length == 0) { // there are no cards on the table
	    return hand.getCard(0).getHeight() == hand.getCard(1).getHeight() // return a pair if our hand cards are the same
		    ? HandEval.HandCategory.PAIR : HandEval.HandCategory.NO_PAIR;
	}
	long handCode = hand.getCard(0).getNumber() + hand.getCard(1).getNumber();

	for (Card card : table) {
	    handCode += card.getNumber();
	}

	if (table.length == 3) { // three cards on the table
	    return rankToCategory(HandEval.hand5Eval(handCode));
	}
	if (table.length == 4) { // four cards on the table
	    return rankToCategory(HandEval.hand6Eval(handCode));
	}
	return rankToCategory(HandEval.hand7Eval(handCode)); // five cards on the table
    }

    /**
     * small method to convert the int 'rank' to a readable enum called HandCategory
     */
    public HandEval.HandCategory rankToCategory(int rank) {
	return HandEval.HandCategory.values()[rank >> HandEval.VALUE_SHIFT];
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	final BotParser parser = new BotParser(new BotStarter());
	parser.run();
    }

}
