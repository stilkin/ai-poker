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


package poker;

public class HandHoldem extends Hand
{		
	/**
	 * A hand containing two cards
	 * @param firstCard : the first card
	 * @param secondCard : the second card
	 */
	public HandHoldem(Card firstCard, Card secondCard)
	{
		cards = new Card[2];
		cards[0] = firstCard;
		cards[1] = secondCard;
	}
}