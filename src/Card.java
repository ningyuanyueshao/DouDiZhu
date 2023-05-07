package ddz;

public class Card {
    private String suit;
    private String rank;

    public Card(String suit, String rank)
    {
        this.suit = suit;
        this.rank = rank;
    }

    public String getsuit()
    {
        return suit;
    }

    public String getRank()
    {
        return rank;
    }

    public String toString()
    {
        return suit + rank;
    }
}
