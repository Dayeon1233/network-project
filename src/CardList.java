import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class CardList {
	
	private LinkedList<Card> cardList;
	private int userID;
	public CardList(){
		cardList = new LinkedList<Card>();
	}
	public LinkedList GetCardList(){
		return cardList;
	}
	public void add(Card card) {
		cardList.add(card);
	}
	public Card getBeginiIter() {
		return cardList.getFirst();
	}
	public int cardListSize() {
		return cardList.size();
	}

	public void removeFirst() {
		cardList.removeFirst();
	}
	public void shuffle(int pre, int pos)
	{
		if (pos<pre)
		{
			System.out.println("pos must smaller than pre");
		}

		if (pos > this.cardListSize())
		{
			System.out.println("Out of Index");
		}

		Random random = new Random();

		for (int i = 0; i<100; i++)
		{
			int n =	random.nextInt((pos - pre) + pre);
			int m =	random.nextInt((pos - pre) + pre);

			Collections.swap(cardList, n, m);
		}

	}

	public Card get(int i)
	{	
		return cardList.get(i);
	}
	
	public int getUserID()
	{
		return userID;
	}

	public void setUserID(int ID)
	{
		this.userID = ID;
	}
	
}
