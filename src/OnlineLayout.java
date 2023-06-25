import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class OnlineLayout extends JPanel implements ActionListener {
    int playerNum =-1;//该客户端玩家座次号 0 1 2 可以根据座次号推断前面有多少人进去了
    int priorityNum =-1;//优先叫地主的座次号 最开始server会传给玩家自己的座次号
    boolean[] preFlag = new boolean[3];//默认值为false
    String[] playerNames = new String[3];
    PrintWriter printWriter;
    ImageIcon backgroundImage;//背景图片
    JLabel backgroundLabel;//背景图片对应的JLabel

    ImageIcon[] avatar = new ImageIcon[3];
    JLabel[] avatarLabel = new JLabel[3];

    List<SinglePoker> currentList;//玩家准备出的牌,判断是否合规应该在客户端判断就好了？
    List<SinglePoker> playerList[] = new ArrayList[3]; // 定义3个玩家表,即使客户端没有把牌的内容传过来 还是需要这些对象
//    注意自己的牌数组下标并不一定是1，而是座次号作为数组下标
//    因为其他两家要背面朝上
    List<SinglePoker> lordList;//地主牌
    List<SinglePoker> lordListCopy;//地主牌的备份 显示在页面顶端
    SinglePoker cards[] = new SinglePoker[56]; // 定义54张牌
    SinglePoker[] lordCardCopy = new SinglePoker[3];//三张备份的地主牌

    String[] player0CardsStr = new String[17];
    String[] player1CardsStr = new String[17];
    String[] player2CardsStr = new String[17];
    String[] lordCardsStr = new String[3]; //自己手牌以及地主牌字符串
    JButton landlord[] = new RoundRectButton[4];//抢地主按钮
    JButton publishCard[] = new RoundRectButton[2];//出牌按钮
    JButton prepare;//准备按钮
    JTextField time[] = new JTextField[3];//三个玩家的计时器
    JTextField namesJText[] = new JTextField[3];//三个玩家的名字显示
    JLabel dizhu; //地主图标

    OnlineTime t;
    public OnlineLayout(){
        Init();
        setLayout(null);
        SwingUtilities.invokeLater(new onLineNewTimer(this,10));//开启一个线程

//        getLord();
        setBackground();
        add(backgroundLabel);
//        startPre();//开始准备 等全部准备好了就开始
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
    public void Init(){
        prepare = new RoundRectButton("准 备");

        //初始化自己面前的四个按钮
        landlord[0]=new RoundRectButton("3 分");
        landlord[1]=new RoundRectButton("2 分");
        landlord[2]=new RoundRectButton("1 分");
        landlord[3]=new RoundRectButton("不 抢");
        publishCard[0]= new RoundRectButton("出 牌");
        publishCard[1]= new RoundRectButton("不 要");

        prepare.setBounds(1000,850,60,40);
        add(prepare);
        prepare.setVisible(true);
        prepare.addActionListener(this);

        for(int i=0;i<3;i++){
            avatar[i] = new ImageIcon("src/img/avatar"+i+".jpg");
            avatarLabel[i] = new JLabel(avatar[i]);
        }
        avatarLabel[0].setBounds(0,900,avatar[0].getIconWidth(),avatar[0].getIconHeight());
        avatarLabel[1].setBounds(0,400,avatar[1].getIconWidth(),avatar[1].getIconHeight());
        avatarLabel[2].setBounds(1650,400,avatar[2].getIconWidth(),avatar[2].getIconHeight());
        avatarLabel[1].setVisible(false);
        avatarLabel[2].setVisible(false);


        for(int i=0;i<2;i++)
        {
            publishCard[i].setBounds(820+i*100, 630, 60, 40);
            add(publishCard[i]);
            publishCard[i].setVisible(false);
            publishCard[i].addActionListener(this);
        }
        for(int i=0;i<4;i++){
            landlord[i].setBounds(820+i*100, 630,75,40);
            add(landlord[i]);
            landlord[i].addActionListener(this);
            landlord[i].setVisible(false);//已经添加了但是没有显示
        }
        //初始化其他玩家面前的信息展示 JTextField
//        注意 倒计时在本地，出与不出来自server的信息
        for(int i=0;i<3;i++){
            time[i]=new JTextField("倒计时:");
            time[i].setOpaque(false);  // 设置背景透明
            time[i].setForeground(new Color(255, 255, 149));  // 设置字体颜色为浅黄色
            time[i].setFont(new Font("华文琥珀", Font.PLAIN, 21));  // 设置粗体字体
            time[i].setBorder(null);  // 去掉边框
            time[i].setVisible(false);
            add(time[i]);
            namesJText[i]=new JTextField("游戏玩家");
            namesJText[i].setOpaque(false);// 设置背景透明
            namesJText[i].setForeground(new Color(255, 255, 149));  // 设置字体颜色为浅黄色
            namesJText[i].setFont(new Font("华文琥珀", Font.PLAIN, 21));  // 设置粗体字体
            namesJText[i].setBorder(null);
            namesJText[i].setVisible(false);
            add(namesJText[i]);
        }
        time[0].setBounds(350, 450, 100, 50);
        time[1].setBounds(850, 550, 150, 50);
        time[2].setBounds(1400, 450, 100, 50);
        namesJText[0].setBounds(300,300,100,50);
        namesJText[1].setBounds(800,400,100,50);
        namesJText[2].setBounds(1300,300,100,50);
        dizhu = new JLabel(new ImageIcon("src/img/dizhu.png"));
        dizhu.setVisible(false);
        dizhu.setSize(40,40);
        add(dizhu);

        currentList = new ArrayList<SinglePoker>();//初始化当前客户端用户要出的牌
        lordList = new ArrayList<>();//初始化地主牌 地主牌信息来自server
        lordListCopy = new ArrayList<>();//初始化地主牌复制品 一直放在桌面前的
        for(int i=0;i<3;i++){
            playerList[i] = new ArrayList<>();//初始化每个人的牌
        }
    }
    public void CardsInit(){
        SinglePoker tempCard;
        int count = 0;
        int index = -1;
//        先初始化52张
        for(int i=1;i<=4;i++){
            for(int j=1;j<=13;j++){
                cards[count] = new SinglePoker(i+"-"+j,false);
                count++;
            }
        }
//        再初始化双王
        for(int i=1;i<=2;i++){
            cards[count] = new SinglePoker("5-"+i,false);
            count++;
        }

        if(playerNum == 0){
            for (int i = 0; i < 17; i++) {
                index = (Character.getNumericValue(player0CardsStr[i].charAt(0))  -1)*13 +  Character.getNumericValue(player0CardsStr[i].charAt(2));
                System.out.println(index);
                playerList[0].add(cards[index]);
            }
            for (int i = 0; i < 17; i++) {
                tempCard = new SinglePoker("1-1",false);
                playerList[1].add(tempCard);
            }
            for (int i = 0; i < 17; i++) {
                tempCard = new SinglePoker("1-1",false);
                playerList[2].add(tempCard);
            }
        }
        else if(playerNum == 1){
            for (int i = 0; i < 17; i++) {
                index = (Character.getNumericValue(player1CardsStr[i].charAt(0)) -1)*13 +  Character.getNumericValue(player1CardsStr[i].charAt(2));
                playerList[1].add(cards[index]);
            }
            for (int i = 0; i < 17; i++) {
                tempCard = new SinglePoker("1-1",false);
                playerList[0].add(tempCard);
            }
            for (int i = 0; i < 17; i++) {
                tempCard = new SinglePoker("1-1",false);
                playerList[2].add(tempCard);
            }
        }
        else{
            for (int i = 0; i < 17; i++) {
                index = (Character.getNumericValue(player2CardsStr[i].charAt(0)) -1)*13 +  Character.getNumericValue(player2CardsStr[i].charAt(2));
                playerList[2].add(cards[index]);
            }
            for (int i = 0; i < 17; i++) {
                tempCard = new SinglePoker("1-1",false);
                playerList[0].add(tempCard);
            }
            for (int i = 0; i < 17; i++) {
                tempCard = new SinglePoker("1-1",false);
                playerList[1].add(tempCard);
            }
        }
        for (int i = 0; i < 3; i++) {
            index = (Character.getNumericValue(lordCardsStr[i].charAt(0)) -1)*13 +  Character.getNumericValue(lordCardsStr[i].charAt(2));
            lordList.add(cards[index]);
            cards[index].turnRear();

            lordCardCopy[i] = new SinglePoker(cards[index]);
            lordListCopy.add(lordCardCopy[index]);
            lordCardCopy[i].turnRear();
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 17; j++) {
                if(playerNum == 0){
                    Common.move(playerList[0].get(j),playerList[0].get(j).getLocation(),new Point(550+((j-1)*3+2)*12,700),(j-1)*3+2);
                    playerList[0].get(j).turnFront();
                    setComponentZOrder(playerList[0].get(j),0);
                    Common.move(playerList[1].get(j),playerList[1].get(j).getLocation(),new Point(1550,280+((j-1)*3+3)*6),(j-1)*3+3);
                    setComponentZOrder(playerList[1].get(j),0);
                    Common.move(playerList[2].get(j),playerList[1].get(j).getLocation(),new Point(250,280+((j-1)*3+1)*6),(j-1)*3+1);
                    setComponentZOrder(playerList[2].get(j),0);
                }else if(playerNum == 1){
                    Common.move(playerList[1].get(j),playerList[1].get(j).getLocation(),new Point(550+((j-1)*3+2)*12,700),(j-1)*3+2);
                    setComponentZOrder(playerList[1].get(j),0);
                    playerList[1].get(j).turnFront();
                    Common.move(playerList[2].get(j),playerList[2].get(j).getLocation(),new Point(1550,280+((j-1)*3+3)*6),(j-1)*3+3);
                    setComponentZOrder(playerList[2].get(j),0);
                    Common.move(playerList[0].get(j),playerList[0].get(j).getLocation(),new Point(250,280+((j-1)*3+1)*6),(j-1)*3+1);
                    setComponentZOrder(playerList[0].get(j),0);
                }else{
                    Common.move(playerList[2].get(j),playerList[0].get(j).getLocation(),new Point(550+((j-1)*3+2)*12,700),(j-1)*3+2);
                    setComponentZOrder(playerList[2].get(j),0);
                    playerList[2].get(j).turnFront();
                    setComponentZOrder(playerList[0].get(j),0);
                    Common.move(playerList[0].get(j),playerList[1].get(j).getLocation(),new Point(1550,280+((j-1)*3+3)*6),(j-1)*3+3);
                    setComponentZOrder(playerList[0].get(j),0);
                    Common.move(playerList[1].get(j),playerList[1].get(j).getLocation(),new Point(250,280+((j-1)*3+1)*6),(j-1)*3+1);
                    setComponentZOrder(playerList[1].get(j),0);
                }
            }
        }
        for (int i = 0; i <=2 ; i++) {
            Common.move(lordList.get(i), lordList.get(i).getLocation(),new Point(300+i*80,50),53);
            Common.move(lordList.get(i), lordList.get(i).getLocation(),new Point(300+i*80,50),53);
            setComponentZOrder(lordCardCopy[i], 0);
            setComponentZOrder(cards[i], 0);
        }
        //    经过与server交互 得到了LordList和LordListcopy 以及其中一个playerCard[i] 其他玩家的手牌拿不到，名称随便赋，只要背面朝上即可
//    并且要设置每张牌的zOrder 因为server返回的牌值是已经排好序的，因此不需要像单机进行order
//    LordList和LordListcopy playerCard[i]
    }
//    public void getLord(){//开始抢地主，这时候就要开启线程
////        涉及到JTextField time[]的更新 一开始server就要给client传哪个客户端编号优先叫地主
//        SwingUtilities.invokeLater(new onLineNewTimer(this,10));
//
//    }
    public void startPre(){//开始准备
        //server先传给客户端已经做桌的玩家座次号，并且是否准备
    }
    public void actionPerformed(ActionEvent e){
//        if(e.getSource()==landlord[0])
//        {
//            time[playerNum].setText("3 分");
//            t.isRun=false; //时钟终结
//        }
//        else if(e.getSource()==landlord[1])
//        {
//            time[playerNum].setText("2 分");
//            t.isRun=false; //时钟终结
//        }
//        else if(e.getSource()==landlord[2]){
//            time[playerNum].setText("1 分");
//            t.isRun=false; //时钟终结
//        }
//        else {
//            time[playerNum].setText("不 抢");
//            t.isRun=false; //时钟终结
//        }
//
        if(e.getSource()==prepare){
            time[1].setText("已准备");//保证自己的编号是1
            time[1].setVisible(true);
            preFlag[playerNum] = true;
            String to = "9:";
            System.out.println("向服务器发送的准备就绪信息"+to);
            printWriter.println(to);
            prepare.setVisible(false);
        }
//        如果是不要
//        if(e.getSource()==publishCard[1])
//        {
//            this.nextPlayer=true;
//            currentList[1].clear();
//            time[1].setText("不要");
//        }
    }


}
class onLineNewTimer implements Runnable {

    OnlineLayout onlineLayout;
    int i;

    public onLineNewTimer(OnlineLayout onlineLayout, int i) {
        this.onlineLayout = onlineLayout;
        this.i = i;
    }

    @Override
    public void run() {
        onlineLayout.t = new OnlineTime(onlineLayout, 12);//从10开始倒计时
        onlineLayout.t.start();
    }
}
