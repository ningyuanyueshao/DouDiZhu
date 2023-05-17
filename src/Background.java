import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Background extends JFrame implements ActionListener{
    private final RoundRectButton singlePlayerButton;
    private final RoundRectButton multiPlayerButton;
    private final RoundRectButton exitButton;
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
    JPanel panel = new JPanel();
    public Background() {
        setTitle("斗地主游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowsWidth = width;
        int windowsHeight = height;
        int buttonWidth = windowsWidth/11;
        int buttonHeight = windowsHeight/11;

        // 加载图像文件并将其设置为窗口图标
        Image icon = Toolkit.getDefaultToolkit().getImage("src/img/icon.png");
        setIconImage(icon);

        // 创建一个JPanel容器并设置布局为null

        panel.setLayout(null);

        // 创建一个JLabel，并将图片作为背景添加到JPanel容器中。这里注意，要后放背景图片，先放的会覆盖后放的
        ImageIcon backgroundImage = new ImageIcon("src/img/bgd2.png");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());

        // 添加三个按钮到JPanel容器中
        singlePlayerButton = new RoundRectButton("单机游戏");
        singlePlayerButton.setBounds(windowsWidth/2-buttonWidth/2, windowsHeight/2+buttonHeight, buttonWidth, buttonHeight);
        Font font = new Font("华文新魏", Font.PLAIN, 26); // 创建字体对象
        singlePlayerButton.setFont(font); // 设置按钮字体
        panel.add(singlePlayerButton);

        multiPlayerButton = new RoundRectButton("联机游戏");
        multiPlayerButton.setBounds(windowsWidth/2-buttonWidth/2, windowsHeight /2+2*buttonHeight+20, buttonWidth, buttonHeight);
        multiPlayerButton.setFont(font);
        panel.add(multiPlayerButton);

        exitButton = new RoundRectButton("退出");
        exitButton.setBounds(windowsWidth/2-buttonWidth/2, windowsHeight /2+3*buttonHeight+40, buttonWidth, buttonHeight);
        exitButton.setFont(font);
        panel.add(exitButton);

        panel.add(backgroundLabel);

        // 将JPanel容器添加到JFrame窗口中
        getContentPane().add(panel);
        setSize(windowsWidth, windowsHeight);

        //能够播放音乐
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("src/sound/纯音乐 - 欢乐斗地主.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        singlePlayerButton.addActionListener(this);
        multiPlayerButton.addActionListener(this);
        exitButton.addActionListener(this);

        setVisible(true);
        setResizable(false);//让窗口大小固定，不让用户更改位置，更美观
    }

    public void actionPerformed(ActionEvent e) {
        // 确定是哪个按钮被按下
        if (e.getSource() == singlePlayerButton) {
            // 处理单机游戏按钮被按下的事件
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
                    //todo:用while循环读取是否注册成功
                    JOptionPane.showMessageDialog(this, "注册成功！");
                } else if (choice == 1) {
                    // 用户选择登录
                    sign();
                    //TODO:用while循环读取是否登录成功
                    JOptionPane.showMessageDialog(this, "登录成功！");
                    wantPlay = true;
                }
                else if (choice == 2) {
                    // 显示用户列表
                    String[] userNames = {"Alice", "Bob", "Charlie"};  // 假设这是一些用户的名字列表
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
                Object[] roomOptions = {"创建房间", "加入房间"};
                int roomChoice = JOptionPane.showOptionDialog(this, "请选择操作：", "选房间", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, roomOptions, roomOptions[0]);
                int roomID;
                if (roomChoice == 0) {
                    // 创建房间
                    roomID = Integer.parseInt(JOptionPane.showInputDialog(this, "请输入房间号(0-9)："));
                    // TODO: 用while循环读取是否创建房间成功
                    JOptionPane.showMessageDialog(this, "成功创建房间！");
                } else if (roomChoice == 1) {
                    // 加入房间
                    roomID = Integer.parseInt(JOptionPane.showInputDialog(this, "请输入房间号(0-9)："));
                    // TODO: 用while循环读取是否加入房间成功
                    JOptionPane.showMessageDialog(this, "成功加入房间！");
                }
            }
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
            while (choseRoom == -1)
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
                        choseRoom = tempNumber;
                        break;
                    }
                }
                if (choseRoom==-1)
                {
                    JOptionPane.showMessageDialog(this,"输入的房间号不符合规定，请重新输入");
                }
            }
            JOptionPane.showMessageDialog(this,"欢迎用户"+username+"！正在匹配玩家...");
        } else if (e.getSource() == exitButton) {
            // 处理退出按钮被按下的事件
            int choice = JOptionPane.showConfirmDialog(this, "确定要退出吗？");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
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

    public void setIsOK(int isOK){this.isOK = isOK;}

    public void setRoomsCanPlay(int[] rooms){
        roomsCanPlay = rooms;
    }
    public static boolean containsOnlyAllowedCharacters(String str) {//判断字符串是否符合要求
        // 定义匹配要求字符的正则表达式
        String pattern = "^[a-zA-Z0-9_]+$";
        // 判断是否匹配
        return Pattern.matches(pattern, str);
    }
    public void changeToPlay(Background background){
        GameLayout gameLayout = new GameLayout();//gamelayout panel容器
        background.getContentPane().removeAll(); // 从 background 的 contentPane 中移除所有现有组件
        background.getContentPane().add(gameLayout); // 将 gameLayout 面板添加到 contentPane 中
        background.revalidate(); // 刷新布局
        background.repaint(); // 重绘窗口
    }
}