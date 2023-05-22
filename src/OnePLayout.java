import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OnePLayout extends JPanel{
    ImageIcon backgroundImage;//背景图片
    JLabel backgroundLabel;//背景图片对应的JLabel
    List<SinglePoker> currentList[] =new ArrayList[3]; //  当前的出牌
    List<SinglePoker> playerList[] = new ArrayList[3]; // 定义3个玩家表
    List<SinglePoker> lordList;//地主牌
    SinglePoker card[] = new SinglePoker[56]; // 定义54张牌
    public OnePLayout(){
        setLayout(null);
        CardInit();
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
    public void CardInit(){
        int count =1;
//        初始化54张牌
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 13; j++) {
                if ((i == 5) && (j > 2))
                    break;
                else {
                    card[count] = new SinglePoker(i + "-" + j, false);
                    card[count].setLocation(300+(i*20), 50);
//                    add(card[count]);
                    count++;
                }
            }
        }
        //打乱顺序
        for(int i=0;i<200;i++){
            Random random=new Random();
            int a=random.nextInt(54)+1;
            int b=random.nextInt(54)+1;
            SinglePoker k=card[a];
            card[a]=card[b];
            card[b]=k;
        }
        for(int i=0;i<3;i++) playerList[i]=new ArrayList<SinglePoker>(); //玩家牌
        lordList=new ArrayList<SinglePoker>();//地主牌三张
        int t=0;
        for(int i=1;i<=54;i++)
        {
            if(i>=52)//地主牌
            {
                Common.move(card[i], card[i].getLocation(),new Point(300+(i-52)*80,16),t);
                lordList.add(card[i]);
                continue;
            }
            switch ((t++)%3) {
                case 0:
                    //左边玩家
                    Common.move(card[i], card[i].getLocation(),new Point(180,160+i*6),t);
                    playerList[0].add(card[i]);
//				card[i].turnFront(); //显示正面
                    break;
                case 1:
                    //我
                    Common.move(card[i], card[i].getLocation(),new Point(380+i*12,600),t);
                    playerList[1].add(card[i]);
                    card[i].turnFront(); //显示正面
                    break;
                case 2:
                    //右边玩家
                    Common.move(card[i], card[i].getLocation(),new Point(1300,160+i*6),t);
                    playerList[2].add(card[i]);
//				card[i].turnFront(); //显示正面
                    break;
            }
            //card[i].turnFront(); //显示正面
            setComponentZOrder(card[i], 0);
        }
    }
    public void Order() {
        for(int i=0;i<3;i++)
        {
            Common.order(playerList[i]);
            Common.rePosition(this,playerList[i],i);//重新定位
        }
    }
}
