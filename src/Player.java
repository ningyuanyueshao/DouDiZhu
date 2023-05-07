

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;//玩家姓名
    private List<Card> hand;//玩家手牌

    public Player(String name)
    {
        this.name = name;
        hand = new ArrayList<Card>();
    }

    public String getName()
    {
        return name;
    }

    public void addCards(List<Card> cards)
    {
        hand.addAll(cards);
    }

    public void playCard(List<Card> cards)
    {
        hand.removeAll(cards);
    }

    public List<Card> getHand()
    {
        return hand;
    }

    public void printHand()
    {
        System.out.println(name + "的手牌：");
        for(Card card:hand)
        {
            System.out.print(card.toString() + " ");
        }
        System.out.println();
    }
}
//zxy ❥(^_-) gzl
