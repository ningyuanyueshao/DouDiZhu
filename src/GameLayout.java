import javax.swing.*;
import java.awt.*;

public class GameLayout extends JPanel{
    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    public GameLayout(){
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0)); // 设置布局管理器为居中对齐的流布局
        ImageIcon backgroundImage = new ImageIcon("src/img/gamebg.png");
        Image scaledImage = backgroundImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        backgroundImage = new ImageIcon(scaledImage);
        JLabel backgroundLabel = new JLabel(backgroundImage);

        ImageIcon character = new ImageIcon("src/img/human1.png");

        add(backgroundLabel);
        setPreferredSize(new Dimension(width, height)); // 设置面板的首选大小为窗口的大小
        setOpaque(true); // 设置面板为不透明，以便显示背景图片
        setVisible(true);
    }
}
