import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.PortUnreachableException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class SetupLayout extends JPanel implements ActionListener {
    private RoundRectButton singlePlayerButton;
    private RoundRectButton multiPlayerButton;
    private RoundRectButton exitButton;
    public boolean wantGetConnected = false;
    public boolean wantSingleConnected = false;
    public boolean wantPlay = false;
    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    private String username = "";
    private String password = "";
    private int choice = -1; // 注册是0，登录是1，查看所有用户2
    public int isOK = -1; //判断是否注册或登录成功，0表示成功，1表示失败；-1是默认值，每次用完要回归默认值
    public int roomChoice = -1; //判断用户对房间的选择，0表示创建默认房间，1表示创建私人房间，2表示加入房间
    public int[] roomsCanPlay = null;
    public int choseRoom = -1;//todo:这个应该用不到了
    public int roomID = -1;
    public String[] userNames = null;

    ImageIcon backgroundImage;//背景图片
    JLabel backgroundLabel;//背景图片对应的JLabel

    public SetupLayout(){
        setLayout(null);
        setBackground();//JLabel要在之后才添加进SetupLayout这个panel中
        addButton();// 添加三个按钮到JPanel容器中
        add(backgroundLabel);//添加背景图片至panel
        Music();
    }

    public void setBackground(){//设置背景图片
        backgroundImage = new ImageIcon("src/img/bgd2.png");
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
    }
    public void addButton(){
        int windowsWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int windowsHeight = Toolkit.getDefaultToolkit().getScreenSize().height;//不知道在panel中是否能够获取到屏幕大小?
        int buttonWidth = windowsWidth/11;
        int buttonHeight = windowsHeight/11;

        singlePlayerButton = new RoundRectButton("单机游戏");
        singlePlayerButton.setBounds(windowsWidth/2-buttonWidth/2, windowsHeight/2+buttonHeight, buttonWidth, buttonHeight);
        Font font = new Font("华文新魏", Font.PLAIN, 26); // 创建字体对象
        singlePlayerButton.setFont(font); // 设置按钮字体
        add(singlePlayerButton);

        multiPlayerButton = new RoundRectButton("联机游戏");
        multiPlayerButton.setBounds(windowsWidth/2-buttonWidth/2, windowsHeight /2+2*buttonHeight+20, buttonWidth, buttonHeight);
        multiPlayerButton.setFont(font);
        add(multiPlayerButton);

        exitButton = new RoundRectButton("退出");
        exitButton.setBounds(windowsWidth/2-buttonWidth/2, windowsHeight /2+3*buttonHeight+40, buttonWidth, buttonHeight);
        exitButton.setFont(font);
        add(exitButton);

        singlePlayerButton.addActionListener(this);
        multiPlayerButton.addActionListener(this);
        exitButton.addActionListener(this);
    }


    public void actionPerformed(ActionEvent e) {
        // 确定是哪个按钮被按下
        if (e.getSource() == singlePlayerButton) {
            // 处理单机游戏按钮被按下的事件
            System.out.println("欢迎进入单机游戏！");
            JOptionPane.showMessageDialog(this, "欢迎进入单机游戏！");
            wantSingleConnected = true;
        } else if (e.getSource() == multiPlayerButton) {
            // 处理连接游戏按钮被按下的事件
            wantGetConnected = true;
            JOptionPane.showMessageDialog(this, "欢迎进入联机游戏！");
            while (!wantPlay) {
                Object[] options = {"注册", "登录", "用户列表"};
                choice = JOptionPane.showOptionDialog(this, "请选择操作：", "联机游戏", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 0) {
                    // 用户选择注册
                    sign();
                    while(isOK == -1){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(isOK == 0) {
                        JOptionPane.showMessageDialog(this, "注册成功！");
                    }
                    else{
                        JOptionPane.showMessageDialog(this,"注册失败，用户名已被使用");
                    }
                    clearUserData();
                } else if (choice == 1) {
                    // 用户选择登录
                    sign();
                    while(isOK == -1){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(isOK == 0)
                        JOptionPane.showMessageDialog(this, "登录成功！");
                    else{
                        JOptionPane.showMessageDialog(this,"登录失败！请重新尝试。");
                        return;
                    }
                    wantPlay = true;
                }
                else if (choice == 2) {
                    // 显示用户列表
                    while (userNames==null) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    StringBuilder userList = new StringBuilder();
                    for (String userName : userNames) {
                        userList.append(userName).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, "用户列表:\n" + userList.toString());
                }
                else {
                    return;
                }
            }
            if (wantPlay) {
                Object[] roomOptions = {"创建房间","创建私人房间", "加入房间"};
                roomChoice = JOptionPane.showOptionDialog(this, "请选择操作：", "选房间", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, roomOptions, roomOptions[0]);
                if (roomChoice == 0) {
                    // 创建房间
                    while (roomID == -1) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    JOptionPane.showMessageDialog(this,"分配的房间号是"+roomID);
                } else if (roomChoice == 1) {
                    //TODO:创建私人房间

                } else if (roomChoice == 2) {
                    // 加入房间
                    while(true){
                        try
                        {
                            Thread.sleep(10);
                        } catch (InterruptedException ex)
                        {
                            ex.printStackTrace();
                        }
                        if(roomsCanPlay != null)
                            break;
                    }
                    int tempNumber;
                    while (roomID == -1)
                    {
                        try {
                            Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
                        } catch (InterruptedException f) {
                            f.printStackTrace();
                        }
                        tempNumber = Integer.parseInt(JOptionPane.showInputDialog(this,"请用户"+username+"选择房间号,从"+ Arrays.toString(roomsCanPlay)+"中选择"));
                        for (int j : roomsCanPlay)
                        {
                            if (tempNumber == j)
                            {
                                roomID = tempNumber;
                                break;
                            }
                        }
                        if (roomID==-1)
                        {
                            JOptionPane.showMessageDialog(this,"输入的房间号不符合规定，请重新输入");
                        }
                    }
                    JOptionPane.showMessageDialog(this, "成功加入房间！");
                }
            }
            JOptionPane.showMessageDialog(this,"欢迎用户"+username+"！等待开始游戏...");
        } else if (e.getSource() == exitButton) {
            // 处理退出按钮被按下的事件
            int choice = JOptionPane.showConfirmDialog(this, "确定要退出吗？");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
    private void clearUserData() {
        username = "";
        password = "";
    }
    private void sign() {
        String TempUsername = JOptionPane.showInputDialog(this, "请输入用户名(6-20位)，只能包含英文字母、数字、下划线：");
        while (true) {
            if (containsOnlyAllowedCharacters(TempUsername)&&TempUsername.length()>=6&&TempUsername.length()<=20) {
                username = TempUsername;
                break;
            }
            else {
                TempUsername = JOptionPane.showInputDialog(this,"输入非法，请重新输入：");
            }
        }
        String TempPassword = JOptionPane.showInputDialog(this, "请输入密码(6-20位)，只能包含英文字母、数字、下划线：");
        while (true) {
            if (containsOnlyAllowedCharacters(TempPassword)&&TempPassword.length()>=6&&TempPassword.length()<=20) {
                password = TempPassword;
                break;
            }
            else {
                TempPassword = JOptionPane.showInputDialog(this,"输入非法，请重新输入：");
            }
        }
    }

    public String getPlayerName()
    {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public void setRoomsCanPlay(int[] rooms){
        roomsCanPlay = rooms;
    }
    public static boolean containsOnlyAllowedCharacters(String str) {//判断字符串是否符合要求
        // 定义匹配要求字符的正则表达式
        String pattern = "^[a-zA-Z0-9_]+$";
        // 判断是否匹配
        return Pattern.matches(pattern, str);
    }
    public void Music(){
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("src/sound/纯音乐 - 欢乐斗地主.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
