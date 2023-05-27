import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineLayout extends JPanel {
    int playerNum;//该客户端玩家座次号
    ImageIcon backgroundImage;//背景图片
    JLabel backgroundLabel;//背景图片对应的JLabel
    List<SinglePoker> playerCard[] = new ArrayList[3]; // 定义3个玩家表
    List<SinglePoker> lordList;//地主牌
    List<SinglePoker> currentList;//当前想出的牌

    String playerStr,lordStr;

    public OnlineLayout(){
        setLayout(null);
//        getCard(playerStr,lordStr,playerNum);
        setBackground();
        add(backgroundLabel);
    }
    public void setBackground(){//设置背景图片
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        backgroundImage = new ImageIcon("src/img/gamebg.png");
        Image scaledBkImage = backgroundImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        backgroundImage = new ImageIcon(scaledBkImage);
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
    }
//    public void getCard(String playerStr,String lordStr,int i){//要传给后端玩家座次号
//        String[playerNum] playerCards = playerStr.split("、");
//        String[] loadCards = lordStr.split("、");
//        for(int count=0;count<17;i++){
//            playerCard[count] = new SinglePoker(playerCards[count],false);
//        }
//        for(int count=0;count<3;count++){
//            loadCard[count] = new SinglePoker(loadCards[count],false);
//        }
//    }

}
