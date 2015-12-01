package be.testing;

import be.stilkin.StartingHands;
import poker.Card;

/**
 * This class contains a main method. Do not include when packing bot.
 * @author stilkin
 *
 */
public class StartingHandsTest {

    public static void main(String[] args) {

	System.out.println("" + StartingHands.getOdds(new Card(12), new Card(8)));
	System.out.println("" + StartingHands.getOdds("TA="));

    }

}
