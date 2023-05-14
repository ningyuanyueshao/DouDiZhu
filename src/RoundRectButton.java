import javax.swing.*;
import java.awt.*;
public class  RoundRectButton extends JButton
{
    public RoundRectButton(String s)
    {
        super(s);
        setMargin(new Insets(0,0,0,0));//去除文字与按钮的边沿
        setBorder(new RoundBorder());//圆角矩形边界
        setContentAreaFilled(false);//取消原先画矩形的设置
        //setBorderPainted(false);//会导致按钮没有明显边界
        setFocusPainted(false);//去除文字周围的虚线框
        setBackground(new Color(222,255,255));
    }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//开启抗锯齿

        if (getModel().isArmed()) {
            g.setColor(new Color(59, 173, 54));//按下后按钮变成绿色
        } else {
            g.setColor(getBackground());
        }
        g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1,35,35);//填充圆角矩形边界
        // 这个调用会画一个标签和焦点矩形。
        super.paintComponent(g);
    }
}
