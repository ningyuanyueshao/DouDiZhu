import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Background extends JFrame implements ActionListener{
    private RoundRectButton singlePlayerButton;
    private RoundRectButton multiPlayerButton;
    private RoundRectButton exitButton;
    public Background() {
        setTitle("斗地主游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建一个JPanel容器并设置布局为null
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // 创建一个JLabel，并将图片作为背景添加到JPanel容器中
        ImageIcon backgroundImage = new ImageIcon("E:\\ideaworkspace\\1\\DouDiZhu\\src\\img\\bgd2.jpg");
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
        } else if (e.getSource() == multiPlayerButton) {
            // 处理连接游戏按钮被按下的事件
            JOptionPane.showMessageDialog(this, "欢迎进入联机游戏！");
            String playername = JOptionPane.showInputDialog(this,"请输入用户昵称：");
            JOptionPane.showMessageDialog(this,"欢迎用户"+playername+"！正在匹配玩家...");
        } else if (e.getSource() == exitButton) {
            // 处理退出按钮被按下的事件
            int choice = JOptionPane.showConfirmDialog(this, "确定要退出吗？");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }


    public static void main(String[] args) {
        new Background();
    }
}