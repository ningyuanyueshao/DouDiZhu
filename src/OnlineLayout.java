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
    Frame frame;
    ImageIcon[] avatar = new ImageIcon[3];
    JLabel[] avatarLabel = new JLabel[3];

    List<SinglePoker> currentList;//玩家准备出的牌,判断是否合规应该在客户端判断就好了？
    List<SinglePoker> playerList[] = new ArrayList[3]; // 定义3个玩家表,即使客户端没有把牌的内容传过来 还是需要这些对象
//    注意自己的牌数组下标并不一定是1，而是座次号作为数组下标
//    因为其他两家要背面朝上
    List<SinglePoker> lordList;//地主牌
    List<SinglePoker> lordListCopy;//地主牌的备份 显示在页面顶端
    SinglePoker cards[] = new SinglePoker[54]; // 定义54张牌
    SinglePoker nullCardsOne[] = new SinglePoker[17];
    SinglePoker nullCardsTwo[] = new SinglePoker[17];
//    用来给其他两个玩家发牌，这些牌只需要背面朝上
    SinglePoker[] lordCardCopy = new SinglePoker[3];//三张备份的地主牌

    String[] player0CardsStr = null;
    String[] player1CardsStr = null;
    String[] player2CardsStr = null;
    String[] lordCardsStr = new String[3]; //自己手牌以及地主牌字符串
    JButton landlord[] = new RoundRectButton[4];//抢地主按钮
    JButton publishCard[] = new RoundRectButton[2];//出牌按钮
    JButton prepare;//准备按钮
    JTextField time[] = new JTextField[3];//三个玩家的计时器
    JTextField namesJText[] = new JTextField[3];//三个玩家的名字显示

    JTextField leftPokersNum = new JTextField();
    JTextField rightPokerNum = new JTextField();
    SinglePoker nullCardOne = new SinglePoker("1-1",false);//用来放在两侧
    SinglePoker nullCardTwo = new SinglePoker("1-1",false);

    JLabel dizhu; //地主图标

    OnlineTime onlineTime;
    public OnlineLayout(Frame frame){
        this.frame = frame;
        Init();
        setLayout(null);
        SwingUtilities.invokeLater(new onLineNewTimer(this,10));//开启新的线程 进行准备 发牌 叫分 游戏
        CardsInit();//不在进程中初始化cards 试试能不能在屏幕中显示 说明可以显示
//        getLord();
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
//            add(avatarLabel[i]);
        }
        avatarLabel[0].setBounds(0,900,avatar[0].getIconWidth(),avatar[0].getIconHeight());
        avatarLabel[0].setVisible(true);
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
        nullCardOne.setLocation(300,400);
        nullCardTwo.setLocation(1400,400);
        add(nullCardOne);
        add(nullCardTwo);
    }
    public void CardsInit(){//把cards数组进行初始化
        int count = 0;
//        先初始化52张
        for(int i=1;i<=4;i++){
            for(int j=1;j<=13;j++){
                cards[count] = new SinglePoker(i+"-"+j,false);
                cards[count].setLocation(300+(i*20),50);
                add(cards[count]);//会不会是因为cards没有加入到jpanel里面去导致显示不出来?
                cards[count].setVisible(false);//先不让所有的cards显示
                count++;
            }
        }
//        再初始化双王
        for(int i=1;i<=2;i++){
            cards[count] = new SinglePoker("5-"+i,false);
            cards[count].setLocation(400,50);
            add(cards[count]);
            cards[count].setVisible(false);
            count++;
        }
    }


    public void actionPerformed(ActionEvent e){
        if(e.getSource()==landlord[0])
        {
            time[playerNum].setText("3 分");
            onlineTime.score[playerNum] = 3;
            onlineTime.isRun=false; //时钟终结
        }
        else if(e.getSource()==landlord[1])
        {
            time[playerNum].setText("2 分");
            onlineTime.score[playerNum] = 2;
            onlineTime.isRun=false; //时钟终结
        }
        else if(e.getSource()==landlord[2]){
            time[playerNum].setText("1 分");
            onlineTime.score[playerNum] = 1;
            onlineTime.isRun=false; //时钟终结
        }
        else if(e.getSource()==landlord[3]){
            time[playerNum].setText("不 抢");
            onlineTime.score[playerNum] = 0;
            onlineTime.isRun=false; //时钟终结
        }
//          todo 叫分之后客户端要怎么传给server？然后server如何去通知其他的客户端？
//        客户端传给server：点击了按钮之后 ……
//        server传给其他两个客户端 更改他们的isCallScore 以及score数组

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
    public void addCardsToList(){//把Cards添加到List中去，
        int pokerColor,pokerNum;//临时存储每张牌的花色和权值
        int index = -1;
        if(playerNum == 0){
            for (int i = 0; i < 17; i++) {
                pokerColor = Integer.parseInt(player0CardsStr[i].substring(0,1));
                pokerNum = Integer.parseInt(player0CardsStr[i].substring(2));
                index = (pokerColor  -1)*13 + pokerNum -1;
                playerList[0].add(cards[index]);
            }
            for (int i = 0; i < 17; i++) {
                nullCardsOne[i] = new SinglePoker("1-1",false);
                playerList[1].add(nullCardsOne[i]);
            }
            for (int i = 0; i < 17; i++) {
                nullCardsTwo[i] = new SinglePoker("1-1",false);
                playerList[2].add(nullCardsTwo[i]);
            }
            leftPokersNum.setText(Integer.toString(playerList[(playerNum +2)%3].size()));
            leftPokersNum.setVisible(true);//显示牌张数
            rightPokerNum.setText(Integer.toString(playerList[(playerNum +1)%3].size()));
            rightPokerNum.setVisible(true);
        }
        else if(playerNum == 1){
            for (int i = 0; i < 17; i++) {
                pokerColor = Integer.parseInt(player1CardsStr[i].substring(0,1));
                pokerNum = Integer.parseInt(player1CardsStr[i].substring(2));
                index = (pokerColor -1)*13 + pokerNum -1 ;
                playerList[1].add(cards[index]);
            }
            for (int i = 0; i < 17; i++) {
                nullCardsOne[i] = new SinglePoker("1-1",false);
                playerList[0].add(nullCardsOne[i]);
            }
            for (int i = 0; i < 17; i++) {
                nullCardsTwo[i] = new SinglePoker("1-1",false);
                playerList[2].add(nullCardsTwo[i]);
            }
        }
        else{
            for (int i = 0; i < 17; i++) {
                pokerColor = Integer.parseInt(player2CardsStr[i].substring(0,1));
                pokerNum = Integer.parseInt(player2CardsStr[i].substring(2));
                index = (pokerColor  -1)*13 + pokerNum -1;
                playerList[2].add(cards[index]);
            }
            for (int i = 0; i < 17; i++) {
                nullCardsOne[i] = new SinglePoker("1-1",false);
                playerList[0].add(nullCardsOne[i]);
            }
            for (int i = 0; i < 17; i++) {
                nullCardsTwo[i] = new SinglePoker("1-1",false);
                playerList[1].add(nullCardsTwo[i]);
            }
        }
        for (int i = 0; i < 3; i++) {
            pokerColor = Integer.parseInt(lordCardsStr[i].substring(0, 1));
            pokerNum = Integer.parseInt(lordCardsStr[i].substring(2));
            index = (pokerColor - 1) * 13 + pokerNum - 1;
            lordCardCopy[i] = new SinglePoker(cards[index]);
            lordList.add(cards[index]);
            lordListCopy.add(lordCardCopy[i]);
            cards[index].turnRear();
            cards[index].setVisible(true);
            lordCardCopy[i].turnRear();
            lordCardCopy[i].setVisible(true);
        }

    }
    public void setLocationAndZorder(){//设置每张牌的位置和z轴顺序
            for (int j = 0; j < 17; j++) {//每个人的17张手牌
                System.out.println(j);
                if(playerNum == 0){//若本client的playerNum是0 则从左至右为201
                    Common.move(playerList[0].get(j),playerList[0].get(j).getLocation(),new Point(550+((j-1)*3+2)*12,700),(j-1)*3+2);
                    playerList[0].get(j).turnFront();
                    playerList[0].get(j).setVisible(true);
                    setComponentZOrder(playerList[0].get(j),0);
                }
                else if(playerNum == 1){//若本client的playerNum是1 则从左至右为012
                    Common.move(playerList[1].get(j),playerList[1].get(j).getLocation(),new Point(550+((j-1)*3+2)*12,700),(j-1)*3+2);

                    playerList[1].get(j).turnFront();
                    playerList[1].get(j).setVisible(true);
                    setComponentZOrder(playerList[1].get(j),0);
                }
                else{//若本client的playerNum是2 则从左至右为120
                    Common.move(playerList[2].get(j),playerList[2].get(j).getLocation(),new Point(550+((j-1)*3+2)*12,700),(j-1)*3+2);
                    playerList[2].get(j).turnFront();
                    playerList[2].get(j).setVisible(true);
                    setComponentZOrder(playerList[2].get(j),0);
                }
            }
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Common.order(playerList[playerNum]);
            rePosition(this,playerList[playerNum],1);
        //设置地主牌的位置和z轴顺序
        for (int i = 0; i <=2 ; i++) {
            Common.move(lordList.get(i), lordList.get(i).getLocation(),new Point(300+i*80,50),53);
            Common.move(lordListCopy.get(i), lordList.get(i).getLocation(),new Point(300+i*80,50),53);
            lordList.get(i).turnRear();
            lordList.get(i).setVisible(true);
            lordListCopy.get(i).turnRear();
            lordListCopy.get(i).setVisible(true);
//            setComponentZOrder(lordList.get(i), 0);
//            setComponentZOrder(lordListCopy.get(i), 0);
        }
        frame.getContentPane().removeAll();
        frame.getContentPane().add(this);
        frame.revalidate();
        frame.repaint();
        System.out.println("窗口已经刷新");
    }
    public void rePosition(OnlineLayout onlineLayout,List<SinglePoker> list, int flag){
        Point p = new Point();
        if(flag == 0) {
            p.x = 250;
            p.y = (760 / 2) - (list.size() + 1) * 15 / 2;
        }
        if(flag==1) {
            //我的排序 _y=450 width=830
            p.x=(1600/2)-(list.size()+1)*21/2;
            p.y=700;
        }
        if(flag==2) {
            p.x=1550;
            p.y=(760/2)-(list.size()+1)*15/2;
        }
        int len=list.size();
        for(int i=0;i<len;i++){
            SinglePoker card=list.get(i);
            Common.move(card, card.getLocation(), p,10);
            onlineLayout.setComponentZOrder(card, 0);
            if(flag==1)p.x+=34;
            else p.y+=20;
        }
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
        onlineLayout.onlineTime = new OnlineTime(onlineLayout, 12);//从10开始倒计时
        onlineLayout.onlineTime.start();
    }

}
