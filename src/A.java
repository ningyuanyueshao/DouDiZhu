import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class A extends JFrame implements ActionListener {
    private JButton singlePlayerButton;
    private JButton multiplayerButton;
    private JButton exitButton;
    private BufferedImage backgroundImage;

    public A() {
        super("游戏主菜单");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        // 读取背景图片
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // 创建按钮
        singlePlayerButton = new JButton("单机游戏");
        multiplayerButton = new JButton("连接游戏");
        exitButton = new JButton("退出");

        // 创建面板并添加按钮
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setOpaque(false); // 设置面板透明
        panel.add(singlePlayerButton);
        panel.add(multiplayerButton);
        panel.add(exitButton);

        // 添加面板到JFrame的ContentPane
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        });
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        // 添加按钮的ActionListener
        singlePlayerButton.addActionListener(this);
        multiplayerButton.addActionListener(this);
        exitButton.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        // 确定是哪个按钮被按下
        if (e.getSource() == singlePlayerButton) {
            // 处理单机游戏按钮被按下的事件
            JOptionPane.showMessageDialog(this, "你选择了单机游戏！");
        } else if (e.getSource() == multiplayerButton) {
            // 处理连接游戏按钮被按下的事件
            JOptionPane.showMessageDialog(this, "你选择了连接游戏！");
        } else if (e.getSource() == exitButton) {
            // 处理退出按钮被按下的事件
            int choice = JOptionPane.showConfirmDialog(this, "确定要退出吗？");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        new A();
    }
}
