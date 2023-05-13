import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class Background extends JFrame implements ActionListener{
    private RoundRectButton singlePlayerButton;
    private RoundRectButton multiPlayerButton;
    private RoundRectButton exitButton;
    public boolean wantGetConnected = false;
    public boolean wantSingleConnected = false;
    private String playername = "";
    public int[] roomsCanPlay = null;
    public int choseRoom = -1;
    public Background() {
        setTitle("斗地主游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建一个JPanel容器并设置布局为null
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // 创建一个JLabel，并将图片作为背景添加到JPanel容器中
        ImageIcon backgroundImage = new ImageIcon("src//img//bgd2.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(16, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        //这里注意，要后放背景图片，先放的会覆盖后放的


        // 添加三个按钮到JPanel容器中
        singlePlayerButton = new RoundRectButton("单机游戏");
        singlePlayerButton.setBounds(860, 700, 200, 80);
        Font font = new Font("华文新魏", Font.PLAIN, 26); // 创建字体对象
        singlePlayerButton.setFont(font); // 设置按钮字体
        panel.add(singlePlayerButton);

        multiPlayerButton = new RoundRectButton("联机游戏");
        multiPlayerButton.setBounds(860, 800, 200, 80);
        multiPlayerButton.setFont(font);
        panel.add(multiPlayerButton);

        exitButton = new RoundRectButton("退出");
        exitButton.setBounds(860, 900, 200, 80);
        exitButton.setFont(font);
        panel.add(exitButton);

        panel.add(backgroundLabel);
        // 将JPanel容器添加到JFrame窗口中
        getContentPane().add(panel);
        setSize(1920, 1080);


        singlePlayerButton.addActionListener(this);
        multiPlayerButton.addActionListener(this);
        exitButton.addActionListener(this);

        setVisible(true);
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
            wantGetConnected = true;
            playername = JOptionPane.showInputDialog(this,"请输入用户昵称：");
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
            //Todo：这边让用户选择房间号，可以采用输入的方式，选择好就改变chosenRoom的值，网络连接处会循环检查
            int temp = -1;
            while (choseRoom == -1)
            {
                try {
                    Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
                } catch (InterruptedException f) {
                    f.printStackTrace();
                }
                temp = Integer.parseInt(JOptionPane.showInputDialog(this,"请用户选择房间号,从"+ Arrays.toString(roomsCanPlay)+"中选择"));
                for (int i = 0; i < roomsCanPlay.length; i++)
                {
                    if (temp == roomsCanPlay[i])
                    {
                        choseRoom = temp;
                    }
                }
                if (choseRoom==-1)
                {
                    JOptionPane.showMessageDialog(this,"输入的房间号不符合规定，请重新输入");
                }
            }
            JOptionPane.showMessageDialog(this,"欢迎用户"+playername+"！正在匹配玩家...");
            System.out.println(getPlayername());
        } else if (e.getSource() == exitButton) {
            // 处理退出按钮被按下的事件
            int choice = JOptionPane.showConfirmDialog(this, "确定要退出吗？");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

    }
    public String getPlayername()
    {
        return playername;
    }

    public void setRoomsCanPlay(int[] rooms){
        roomsCanPlay = rooms;
    }
}