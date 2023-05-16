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
    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private String playerName = "";
    public int[] roomsCanPlay = null;
    public int choseRoom = -1;
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
            JOptionPane.showMessageDialog(this, "欢迎进入联机游戏！");
            Object[] options = {"注册", "登录"};
            int choice = JOptionPane.showOptionDialog(this, "请选择操作：", "联机游戏", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                // 用户选择注册
                String userTempName = JOptionPane.showInputDialog(this, "请输入用户名，只能包含英文字母、数字、下划线：");
                while (true) {
                    if (containsOnlyAllowedCharacters(userTempName)) {
                        String userName = userTempName;
                        break;
                    }
                    else {
                        userTempName = JOptionPane.showInputDialog(this,"输入非法，请重新输入用户名：");
                    }
                }
                String password = JOptionPane.showInputDialog(this, "请输入密码：");
                JOptionPane.showMessageDialog(this, "注册成功！");
            } else if (choice == 1) {
                // 用户选择登录
                String username = JOptionPane.showInputDialog(this, "请输入用户名：");
                String password = JOptionPane.showInputDialog(this, "请输入密码：");
                //TODO:服务器判断是否登录成功
                JOptionPane.showMessageDialog(this, "登录成功！");
            }
            wantGetConnected = true;
            playerName = JOptionPane.showInputDialog(this,"请输入用户昵称：");
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
            int temp;
            boolean validInput = false;
            while (choseRoom == -1)
            {
                try {
                    Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
                } catch (InterruptedException f) {
                    f.printStackTrace();
                }
                temp = Integer.parseInt(JOptionPane.showInputDialog(this,"请用户选择房间号,从"+ Arrays.toString(roomsCanPlay)+"中选择"));
                for (int j : roomsCanPlay)
                {
                    if (temp == j)
                    {
                        choseRoom = temp;
                        break;
                    }
                }
                if (choseRoom==-1)
                {
                    JOptionPane.showMessageDialog(this,"输入的房间号不符合规定，请重新输入");
                }
            }
            JOptionPane.showMessageDialog(this,"欢迎用户"+playerName+"！正在匹配玩家...");
        } else if (e.getSource() == exitButton) {
            // 处理退出按钮被按下的事件
            int choice = JOptionPane.showConfirmDialog(this, "确定要退出吗？");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
    public String getPlayerName()
    {
        return playerName;
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
    public void changeToPlay(Background background){
        GameLayout gameLayout = new GameLayout();//gamelayout panel容器
        background.getContentPane().removeAll(); // 从 background 的 contentPane 中移除所有现有组件
        background.getContentPane().add(gameLayout); // 将 gameLayout 面板添加到 contentPane 中
        background.revalidate(); // 刷新布局
        background.repaint(); // 重绘窗口
    }
}