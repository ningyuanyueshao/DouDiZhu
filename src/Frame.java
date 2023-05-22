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
    public void showOnePLayout(){//展现单机游戏布局
        OnePLayout onePLayout = new OnePLayout();//gameLayout panel容器
        getContentPane().removeAll(); // 从 frame 的 contentPane 中移除所有现有组件
        getContentPane().add(onePLayout); // 将 onePLayout 面板添加到 contentPane 中
        revalidate(); // 刷新布局
        repaint(); // 重绘窗口
        //Todo 发牌动画显示
        try {
            Thread.sleep(2000); //延迟，可自己设置
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onePLayout.Order();//对牌进行排序
    }
}
