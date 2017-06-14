public class Card {

	private char c_color;
	private int c_number;

	public Card(char color, int number){
		c_color = color;
		c_number = number;
	}
	
	public char get_color(){
		return c_color;
	}
	
	public int get_number(){
		return c_number;
	}
	
	public void set_card(Card card){
		this.c_color = card.get_color();
		this.c_number = card.get_number();
	}

}

