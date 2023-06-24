import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class OnlineLayout extends JPanel implements ActionListener {
    int playerNum;//该客户端玩家座次号 0 1 2//todo
    int priorityNum;//优先叫地主的座次号 最开始server会传给玩家自己的座次号
    boolean preOneFlag=false,preTwoFlag=false,preThrFlag=false;

    ImageIcon backgroundImage;//背景图片
    JLabel backgroundLabel;//背景图片对应的JLabel

    List<SinglePoker> currentList;//玩家准备出的牌,判断是否合规应该在客户端判断就好了？
    List<SinglePoker> playerList[] = new ArrayList[3]; // 定义3个玩家表,即使客户端没有把牌的内容传过来 还是需要这些对象
//    注意自己的牌数组下标并不一定是1，而是座次号作为数组下标
//    因为其他两家要背面朝上
    List<SinglePoker> lordList;//地主牌
    List<SinglePoker> lordListCopy;//地主牌的备份 显示在页面顶端
    SinglePoker cards[] = new SinglePoker[54]; // 定义54张牌

    String playerStr,lordStr;//?
    JButton landlord[] = new JButton[2];//抢地主按钮
    JButton publishCard[] = new JButton[2];//出牌按钮
    JButton prepare;//准备按钮
    JTextField time[] = new JTextField[3];//三个玩家的计时器
    JLabel dizhu; //地主图标

//    OnlineTime t;
    public OnlineLayout(){
        Init();
        setLayout(null);
//        startPre();//开始准备 等全部准备好了就开始
//        CardInit(playerStr);//先从服务端获取自己的手牌 其他的不要

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
    public void Init(){
        prepare = new JButton("准备");

        //初始化自己面前的四个按钮
        landlord[0]=new JButton("3 分");
        landlord[1]=new JButton("2 分");
        landlord[2]=new JButton("1 分");
        landlord[3]=new JButton("不 抢");
        publishCard[0]= new JButton("出牌");
        publishCard[1]= new JButton("不要");

        prepare.setBounds(1000,550,60,40);
        add(prepare);
        prepare.setVisible(true);
        prepare.addActionListener(this);

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
            time[i].setFont(new Font("Serif",Font.PLAIN,20));
            time[i].setVisible(false);
            add(time[i]);
        }
        time[0].setBounds(350, 450, 100, 50);
        time[1].setBounds(850, 550, 150, 50);
        time[2].setBounds(1400, 450, 100, 50);
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
    public void CardInit(){
        int count = 1;
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
        while(true){
            if(preOneFlag){//座位0的玩家准备好
                time[0].setText("已准备");
            }
            if(preTwoFlag){//座位1的玩家准备好
                time[1].setText("已准备");
            }
            if(preThrFlag){
                time[2].setText("已准备");
            }
            if(time[0].getText().equals("已准备")&&time[1].getText().equals("已准备")&&time[2].getText().equals("已准备")){
                break;//全部都准备好了
            }
        }
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
//        if(e.getSource()==prepare){
//            time[playerNum].setText("已准备");
//            prepare.setVisible(false);
//        }
//        如果是不要
//        if(e.getSource()==publishCard[1])
//        {
//            this.nextPlayer=true;
//            currentList[1].clear();
//            time[1].setText("不要");
//        }
    }


}
//class onLineNewTimer implements Runnable {
//
//    OnlineLayout onlineLayout;
//    int i;
//
//    public onLineNewTimer(OnlineLayout onlineLayout, int i) {
//        this.onlineLayout = onlineLayout;
//        this.i = i;
//    }
//
////    @Override
////    public void run() {
////        onlineLayout.t = new OnlineTime(onlineLayout, 12);//从10开始倒计时
////        onlineLayout.t.start();
////    }
//}
