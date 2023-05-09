import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class A extends JFrame implements ActionListener {
    private JButton singlePlayerButton;
    private JButton multiplayerButton;
    private JButton exitButton;

    public A() {
        super("游戏主菜单");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // 创建按钮
        singlePlayerButton = new JButton("单机游戏");
        multiplayerButton = new JButton("连接游戏");
        exitButton = new JButton("退出");

        // 添加按钮到面板
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(singlePlayerButton);
        panel.add(multiplayerButton);
        panel.add(exitButton);
        add(panel);

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
