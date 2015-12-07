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
import com.stevebrecher.HandEval.HandCategory;

import be.stilkin.HandParser;
import be.stilkin.StartingHands;
import poker.Card;
import poker.HandHoldem;
import poker.PokerMove;

/**
 * This class is the brains of your bot. Make your calculations here and return the best move with GetMove
 * 
 * http://www.holdemsecrets.com/startinghands.htm
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
    private static final float ODD_LOWER_BOUND = 0.55f;
    private final HashMap<String, Integer> roundMoneys = new HashMap<String, Integer>();
    private final HandParser myHandParser = new HandParser();
    private final HandParser tableHandParser = new HandParser();
    private String botName = "stilkin";
    private HandHoldem hand;
    private int lastRound = -1;
    private int minRaise;

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
	// set some round variables
	botName = state.getMyName();
	hand = state.getHand();
	minRaise = 2 * state.getBigBlind();
	final Card[] table = state.getTable();

	if (lastRound != state.getRound()) { // reset round counters
	    lastRound = state.getRound();
	    roundMoneys.clear();
	}

	if (table == null || table.length < 3) { // pre-flop
	    return preFlop(state);
	} else { // post-flop
	    return postFlop(table, state);
	}
    }

    // *****************
    // *** POST FLOP ***
    // *****************

    private PokerMove postFlop(final Card[] table, final BotState state) {
	// reset parsers
	tableHandParser.clear();
	myHandParser.clear();

	// init parser with this rounds' cards
	tableHandParser.addCards(table);
	myHandParser.addCards(table);
	myHandParser.addCards(state.getHand().getCards());

	// if the table cards are stronger or equally strong, we bail
	if (tableHandParser.getHandCategory().ordinal() >= myHandParser.getHandCategory().ordinal()) {
	    // TODO: check height of pairs and such on the table?
	    return preFlopCheck(state);
	}
	// TODO: check potential for flush or straight on the table

	// if we get here we have at least one of the cards in our hand, otherwise the table would be as good as our hand (see higher)
	final int callAmount = state.getAmountToCall();
	final HandEval.HandCategory myHand = getHandCategory(hand, table);

	// Get the ordinal values of the cards in your hand
	final int height1 = hand.getCard(0).getHeight().ordinal();
	final int height2 = hand.getCard(1).getHeight().ordinal();
	final int sum = height1 + height2;

	int odds = 1;
	// calculate som odds as multipliers
	switch (myHand) {
	    case STRAIGHT_FLUSH:
		odds = 72192;
		break;
	    case FOUR_OF_A_KIND:
		odds = 4164;
		break;
	    case FULL_HOUSE:
		odds = 693;
		break;
	    case FLUSH:
		odds = 508;
		break;
	    case STRAIGHT:
		odds = 254;
		break;
	    case THREE_OF_A_KIND:
		odds = 46;
		break;
	    case TWO_PAIR:
		odds = 20;
		break;
	    case PAIR:
		odds = 2;
		break;
	    case NO_PAIR:
		odds = 1;
		break;
	    default:
		odds = 1;
		break;
	}

	// determine right course of action
	switch (myHand) {
	    case STRAIGHT_FLUSH:
	    case FOUR_OF_A_KIND:
	    case FULL_HOUSE:
	    case FLUSH:
	    case STRAIGHT:
		final PokerMove oddRaise = raiseWithOdds(state, odds);
		if (oddRaise != null) {
		    return oddRaise; // we raise
		} else { // we are being re-raised
		    return loggedAction(botName, CALL_ACTION, 0);
		}
	    case THREE_OF_A_KIND:
		odds = 46;
		boolean trips = hand.getCard(0).getHeight() == hand.getCard(1).getHeight();
		if (trips) {
		    final PokerMove tripsOddRaise = raiseWithOdds(state, odds / 2);
		    if (tripsOddRaise != null) {
			return tripsOddRaise; // we raise
		    } else { // we are being re-raised
			return loggedAction(botName, CALL_ACTION, 0);
		    }
		} else if (sum > 15) { // TODO: find out which card is in the THREE OF A KIND
		    return loggedAction(botName, CALL_ACTION, callAmount);
		}
		break;
	    case TWO_PAIR: // TODO: find out which card is in the TWO PAIR
		odds = 20;
		boolean pairOnTable = tableHandParser.getHandCategory().ordinal() >= HandCategory.PAIR.ordinal();
		if (!pairOnTable && sum > 10) {
		    final PokerMove twoPairOddRaise = raiseWithOdds(state, odds / 2);
		    if (twoPairOddRaise != null) {
			return twoPairOddRaise; // we raise
		    } else { // we are being re-raised
			return loggedAction(botName, CALL_ACTION, 0);
		    }
		} else if (sum > 15) {
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
     * What do we do pre-flop? We get the odds and raise according to any odds over 50%
     */
    private PokerMove preFlop(final BotState state) {
	final float winOdds = StartingHands.getOdds(hand.getCard(0), hand.getCard(1));

	final PokerMove oppAction = state.getOpponentAction();
	boolean oppRaise = false;
	if (oppAction != null) {
	    oppRaise = RAISE_ACTION.equals(oppAction.getAction());
	}

	final PokerMove oddRaise = raiseWithOdds(state, winOdds);
	if (winOdds > ODD_LOWER_BOUND) { // over 55%
	    if (oddRaise != null) {
		return oddRaise; // we raise
	    } else { // we are being re-raised
		System.err.println("Pre-flop, BANZAII scenario.");
		return loggedAction(botName, CALL_ACTION, 0);
	    }
	} else if (winOdds > 0 && !oppRaise) { // between 50% and 55%
	    if (oddRaise != null) {
		return oddRaise; // we raise
	    }
	}
	// poor starting hand, or average hand was re-raised
	return preFlopCheck(state);
    }

    /**
     * Raises up to a specific amount specified by the odds. Will return null if we cannot raise
     */
    private PokerMove raiseWithOdds(final BotState state, final float winOdds) {
	final int raisedSoFar = roundMoneys.getOrDefault(RAISE_ACTION, 0);
	final int calledSoFar = roundMoneys.getOrDefault(CALL_ACTION, 0);
	final int spentSoFar = raisedSoFar + calledSoFar;
	final int maxRaise = (int) (winOdds * state.getmyStack());
	if (spentSoFar < maxRaise || spentSoFar < minRaise) {
	    final int raisePart = maxRaise / 2; // we raise in 2 steps
	    final int raise = Math.max(minRaise, raisePart);
	    return loggedAction(botName, RAISE_ACTION, raise);
	} else {
	    return null;
	}
    }

    /**
     * Calls up to big blind, otherwise checks (pre-flop)
     */
    private PokerMove preFlopCheck(final BotState state) {
	final int blindDiff = state.getBigBlind() - state.getSmallBlind();
	final int callAmount = state.getAmountToCall();
	final float costRatio = (float) blindDiff / state.getmyStack();
	// when the blind is too big compared to our stack, we don't peek // TODO: is this smart?
	if (costRatio < CURIOSITY && callAmount <= blindDiff) {
	    return loggedAction(botName, CALL_ACTION, callAmount);
	} else {
	    return loggedAction(botName, CHECK_ACTION, 0);
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
