
import java.util.Arrays;
import java.util.List;

public class PlayGame {
    public static void main(String[] args) {
        List<String> playerNames = Arrays.asList("玩家1", "玩家2", "玩家3");
        Game game = new Game(playerNames);
        game.playGame();
    }
}