

public class Card {
    private String suit;//花色
    private String rank;//大小

    public Card(String suit, String rank)
    {
        this.suit = suit;
        this.rank = rank;
    }
    //setter and getter
    public String getsuit()
    {
        return suit;
    }
    public void setSuit(String suit) 
    {
        this.suit = suit;
    }

    public String getRank()
    {
        return rank;
    }
    public void setRank(String rank) 
    {
        this.rank = rank;
    }
    // 构造方法
    public Card(){

    }


    public boolean equals(Card card)
    {
        return this.suit==suit&&this.rank==rank;
    }

    public String toString()
    {
        return suit + rank;
    }
}
