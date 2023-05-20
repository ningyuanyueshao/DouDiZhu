import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    SetupLayout SpLayout;//初始界面面板 选择游戏模式 登录/注册账号面板
    JPanel OnePLayout;//单机游戏面板
    JPanel OnlineLayout;//联机游戏面板

    public Frame(){
        Init();
        SpLayout = new SetupLayout();//创建初始界面面板
        add(SpLayout);//将JPanel容器添加到JFrame窗口中
    }
    public void Init(){//frame窗口初始化
        int windowsWidth = Toolkit.getDefaultToolkit().getScreenSize().width;;
        int windowsHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        setTitle("斗地主游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage("src/img/icon.png");
        setIconImage(icon);
        setSize(windowsWidth,windowsHeight);
        setResizable(false);//让窗口大小固定，不让用户更改位置，更美观
    }
    public boolean IsOnlineGame(){//返回true代表要联机游戏,返回false代表单机游戏
        return SpLayout.wantGetConnected;
    }
    public boolean IsOnePGame(){
        return SpLayout.wantSingleConnected;
    }
}
