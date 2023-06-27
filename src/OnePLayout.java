import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OnePLayout extends JPanel implements ActionListener {
    ImageIcon backgroundImage;//背景图片
    JLabel backgroundLabel;//背景图片对应的JLabel
    List<SinglePoker> currentList[] =new ArrayList[3]; //  当前的出牌 为后面比对时服务
    List<SinglePoker> playerList[] = new ArrayList[3]; // 定义3个玩家表
    List<SinglePoker> lordList;//地主牌
    List<SinglePoker> lordListCopy;//地主牌
    SinglePoker card[] = new SinglePoker[56]; // 定义54张牌
    SinglePoker lordCardCopy[] = new SinglePoker[3];//定义三张放在顶部的地主牌

    JButton landlord[] = new RoundRectButton[2];//抢地主按钮
    JButton publishCard[]=new RoundRectButton[2];//出牌按钮
    int dizhuFlag;//地主标志
    JLabel dizhu; //地主图标
    JTextField time[]=new JTextField[3]; //计时器
    Time t; //定时器（线程）
    int turn;//轮到谁
    boolean nextPlayer=false; //转换角色

    public OnePLayout(){
        Init();//创建功能按钮 计时器等
        setLayout(null);
        CardInit();
        Order();
        SetLordLabel();
        getLord();
        time[1].setVisible(true);
        SwingUtilities.invokeLater(new NewTimer(this,10));
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
//                    add(card[count]); //为什么不把card加入呢
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
        lordListCopy= new ArrayList<SinglePoker>();//copy版的地主牌
        int t=0,countLord=0;
        for(int i=1;i<=54;i++)
        {
            if(i>=52)//地主牌
            {
//                lordCardCopy[countLord] = card[i];
                lordCardCopy[countLord] = new SinglePoker(card[i]);
                Common.move(card[i], card[i].getLocation(),new Point(300+(i-52)*80,50),t);
                Common.move(lordCardCopy[countLord], lordCardCopy[countLord].getLocation(),new Point(300+(i-52)*80,50),t);
                lordList.add(card[i]);
                lordListCopy.add(lordCardCopy[countLord]);
                card[i].turnRear();
                lordCardCopy[countLord].turnRear();
                setComponentZOrder(lordCardCopy[countLord], 0);
                setComponentZOrder(card[i], 0);
                countLord++;
                continue;
            }
            switch ((t++)%3) {
                case 0:
                    //左边玩家
                    Common.move(card[i], card[i].getLocation(),new Point(250,280+i*6),t);
                    playerList[0].add(card[i]);
//				card[i].turnFront(); //显示正面
                    break;
                case 1:
                    //我
                    Common.move(card[i], card[i].getLocation(),new Point(550+i*12,700),t);
                    playerList[1].add(card[i]);
                    card[i].turnFront(); //显示正面
                    break;
                case 2:
                    //右边玩家
                    Common.move(card[i], card[i].getLocation(),new Point(1550,280+i*6),t);
                    playerList[2].add(card[i]);
//				card[i].turnFront(); //显示正面
                    break;
            }
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
    public void SetLordLabel() {
        ImageIcon originalIcon = new ImageIcon("src/img/dizhu.png");
        Image originalImage = originalIcon.getImage();

        // 计算缩放后的宽度和高度
        int scaledWidth = 50;
        int scaledHeight = 50;

        // 创建缩放后的图像
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        // 创建缩放后的图标
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        dizhu = new JLabel(scaledIcon);
        dizhu.setVisible(false);
        dizhu.setSize(scaledWidth, scaledHeight);
        add(dizhu);
    }

    public void getLord(){//开始抢地主
        for(int i=0;i<2;i++)
            landlord[i].setVisible(true);
    }
    public void Init(){
        landlord[0]=new RoundRectButton("抢地主");
        Font font = new Font ("微软雅黑",Font.PLAIN,16);
        landlord[0].setFont(font);
        landlord[1]=new RoundRectButton("不  抢");
        landlord[1].setFont(font);

        publishCard[0]= new RoundRectButton("出牌");
        publishCard[0].setFont(font);
        publishCard[1]= new RoundRectButton("不要");
        publishCard[1].setFont(font);
        for(int i=0;i<2;i++)
        {
            publishCard[i].setBounds(865+i*100, 630, 75, 40);
            landlord[i].setBounds(865+i*100, 630,75,40);
            add(landlord[i]);
            landlord[i].addActionListener(this);
            landlord[i].setVisible(false);//已经添加了但是没有显示
            add(publishCard[i]);
            publishCard[i].setVisible(false);
            publishCard[i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            time[i] = new JTextField("倒计时:");
            time[i].setOpaque(false);  // 设置背景透明
            time[i].setForeground(new Color(255, 255, 149));  // 设置字体颜色为浅黄色
            time[i].setFont(new Font("华文琥珀", Font.PLAIN, 21));  // 设置粗体字体
            time[i].setBorder(null);  // 去掉边框
            time[i].setVisible(false);
            add(time[i]);
        }

//        显示倒计时与出或不出
        time[0].setBounds(350, 450, 100, 50);
        time[1].setBounds(900, 550, 150, 50);
        time[2].setBounds(1400, 450, 100, 50);

        for(int i=0;i<3;i++)
        {
            currentList[i]=new ArrayList<SinglePoker>();//初始化要出的牌
        }
        // 添加退出单机按钮
        JButton exitButton = new RoundRectButton("退出游戏");
        exitButton.setFont(font);
        exitButton.setBounds(1800, 10, 75, 40); // 设置按钮位置和大小
        add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "确认要退出游戏吗？", "确认退出", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0); // 退出程序
                }
            }
        });
        // 添加再来一局按钮
        JButton againButton = new RoundRectButton("再来一局");
        againButton.setFont(font);
        againButton.setBounds(1700, 10, 75, 40); // 设置按钮位置和大小
        add(againButton);
        againButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "确认要再来一局吗？", "再来一局", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    SwingUtilities.getWindowAncestor(exitButton).dispose();
                    Frame a = new Frame();
                    a.showOnePLayout();
                    a.setVisible(true);
                    a.SpLayout.clip.close();
                }
            }
        });
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==landlord[0])
        {
            time[1].setText("抢地主");
            t.isRun=false; //时钟终结
        }
        if(e.getSource()==landlord[1])
        {
            time[1].setText("不抢");
            t.isRun=false; //时钟终结
        }
//        如果是不要
        if(e.getSource()==publishCard[1])
        {
            this.nextPlayer=true;
            currentList[1].clear();
            time[1].setText("不要");
        }

        //如果是出牌按钮
        if(e.getSource()==publishCard[0]){
            List<SinglePoker> c = new ArrayList<SinglePoker>();
            //点选出牌
            for(int i=0;i<playerList[1].size();i++)
            {
                SinglePoker card=playerList[1].get(i);
                if(card.clicked)
                {
                    c.add(card);//把点中的牌放入新集合
                }
            }
            /** 给点选的牌排序 */
            /*for(int i=0;i<c.size();i++){
				System.out.println("点选的牌是："+c.get(i).name);
			}*/
            int flag=0;
            //如果我主动出牌
            if(time[0].getText().equals("不要")&&time[2].getText().equals("不要"))
            {
                if(Common.jugdeType(c)!=CardType.c0)
                    flag=1;//表示可以出牌
            }//如果我跟牌
            else{
                flag=Common.checkCards(c,currentList);
            }
            //判断是否符合出牌
            if(flag==1)
            {
                currentList[1]=c;
                playerList[1].removeAll(currentList[1]);//移除走的牌
                //定位出牌
                Point point=new Point();
                point.x=900-(currentList[1].size()+1)*15/2;;
                point.y=550;
                for(int i=0,len=currentList[1].size();i<len;i++)
                {
                    SinglePoker card=currentList[1].get(i);
                    Common.move(card, card.getLocation(), point,10);
                    point.x+=25;
                }
                //抽完牌后重新整理牌
                Common.rePosition(this, playerList[1], 1);
                time[1].setVisible(false);
                this.nextPlayer=true;//??????????
            }
        }
    }
}
class NewTimer implements Runnable {

    OnePLayout onePLayout;
    int i;

    public NewTimer(OnePLayout onePLayout, int i) {
        this.onePLayout = onePLayout;
        this.i = i;
    }

    @Override
    public void run() {
        onePLayout.t = new Time(onePLayout, 12);//从10开始倒计时
        onePLayout.t.start();
    }
}