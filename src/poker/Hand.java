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

public abstract class Hand
{
	protected Card[] cards;
	
	
	/**
	* Returns a specific card of this hand
	*/
	public Card getCard(int index)
	{
		if(index >= 0 && index < cards.length)
			return cards[index];
		else
			return null;
	}
	
	
	/**
	 * Returns the number of cards in the hand/
	 */
	public int getNumberOfCards()
	{
		return cards.length;
	}
	
	
	/**
	* Returns an array of the four hand cards
	*/
	public Card[] getCards()
	{
		return cards;
	}
	
	
	/**
	 * Returns a string representation of the hand
	 */
	public String toString()
	{
		String str = "[";
		for(int i = 0; i < cards.length - 1; i++)
			str += cards[i].toString() + ",";
		
		str += cards[cards.length - 1].toString() + "]";
		return str;
	}
}
