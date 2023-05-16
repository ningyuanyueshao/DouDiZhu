import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck()
    {
        String[] suits = {"黑桃", "红心", "梅花", "方块"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        cards = new ArrayList<Card>();
        for(String suit : suits)
        {
            for (String rank : ranks)
            {
                Card card = new Card(suit, rank);
                cards.add(card);
            }
        }
        Card bigCard = new Card("大王","0");
        cards.add(bigCard);
        Card smallCard = new Card("小王","0");
        cards.add(smallCard);
    }

    public void shuffle()
    {
        Collections.shuffle(cards);//随机洗牌
    }

    public ArrayList<Card> deal(int count)
    {
        ArrayList<Card> hand = new ArrayList<>();//手牌
        //每次都把索引为0的牌（第一张牌）分给手牌，每次牌remove一张，总共发count张
        for (int i = 0; i < count; i++) {
            Card card = cards.remove(0);
            hand.add(card);
        }
        return hand;
    }
}
