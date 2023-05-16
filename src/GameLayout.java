import javax.swing.*;
import java.awt.*;

public class GameLayout extends JPanel{
    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    public GameLayout(){

        ImageIcon backgroundImage = new ImageIcon("src/img/gamebg.png");
        Image scaledBkImage = backgroundImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        backgroundImage = new ImageIcon(scaledBkImage);
        JLabel backgroundLabel = new JLabel(backgroundImage);

        ImageIcon characterImage = new ImageIcon("src/img/human1.png");
        Image scaledCrImage = characterImage.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        characterImage = new ImageIcon(scaledCrImage);
        JLabel characterLabel = new JLabel(characterImage);
        characterLabel.setBounds(100, 800, characterImage.getIconWidth(), characterImage.getIconHeight());

        backgroundLabel.add(characterLabel);
        add(backgroundLabel);

        setPreferredSize(new Dimension(width, height)); // 设置面板的首选大小为窗口的大小
        setOpaque(true); // 设置面板为不透明，以便显示背景图片
        setVisible(true);
    }
}
