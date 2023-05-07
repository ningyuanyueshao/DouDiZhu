package ddz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck()
    {
        String[] suits = {"♠", "♥", "♣", "♦"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        cards = new ArrayList<Card>();
        //添加每一张牌
        for(String suit : suits)
        {
            for (String rank : ranks)
            {
                Card card = new Card(suit, rank);
                cards.add(card);
            }
        }
    }

    public void shuffle()
    {
        Collections.shuffle(cards);//随机洗牌
    }

    public List<Card> deal(int count)
    {
        List<Card> hand = new ArrayList<>();//手牌
        //每次都把索引为0的牌（第一张牌）分给手牌，每次牌remove一张，总共发count张
        for (int i = 0; i < count; i++) {
            Card card = cards.remove(0);
            hand.add(card);
        }
        return hand;
    }
}
