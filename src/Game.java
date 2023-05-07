
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private Deck deck;
    private int turn;

    public Game(List<String> playerNames)
    {
        players = new ArrayList<Player>();
        for (String name : playerNames) {
            Player player = new Player(name);
            players.add(player);
        }
        deck = new Deck();
        deck.shuffle();
        turn = 0;
    }
    public void dealCards(int count)
    {
        for(Player player : players)
        {
            List<Card> hand = deck.deal(count);
            player.addCards(hand);
        }
    }
    public void playGame()
    {
        int numPlayers = players.size();
        int cardsPerPlayer = 17;
        dealCards(cardsPerPlayer);
        //这里原本是while(true)，出牌还未完成
        for (int i = 0; i < players.size(); i++)
        {
            Player currentPlayer = players.get(turn);
            System.out.println("当前出牌者：" + currentPlayer.getName());
            currentPlayer.printHand();
            // TODO: 出牌功能未完成
            turn = (turn + 1) % numPlayers;
        }
    }
}
