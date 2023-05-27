import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SinglePoker extends JLabel implements MouseListener {
    String name;//图片url名字
    boolean up;//是否正反面
    boolean canClick=false;//是否可被点击
    boolean clicked=false;//是否点击过
    public SinglePoker(String name,boolean up){
        this.name=name;
        this.up=up;
        if(this.up)
            this.turnFront();
        else {
            this.turnRear();
        }
        this.setSize(71, 96);
        this.setVisible(true);
        this.addMouseListener(this);
    }
    public SinglePoker(SinglePoker singlePoker){
        this.name = singlePoker.name;
        this.up = singlePoker.up;
        if(this.up)
            this.turnFront();
        else {
            this.turnRear();
        }
        this.setSize(71, 96);
        this.setVisible(true);
    }
    public void turnFront() {
        this.setIcon(new ImageIcon("src/img/poker/" + name + ".png"));
//		D:\java\GitHub\Chinese-poker-game
//		D:\java\2\DouDiZhu\images
        this.up = true;

    }
    //反面
    public void turnRear() {
        this.setIcon(new ImageIcon("src/img/poker/card_back.png"));
        this.up = false;
    }
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        if(canClick)
        {
            Point from=this.getLocation();
            int step; //移动的距离
            if(clicked)
                step=-30;
            else {
                step=30;
            }
            clicked=!clicked; //反向
            //当被选中的时候，向前移动一步/后退一步
            Common.move(this,from,new Point(from.x,from.y-step),10);
        }
    }
    public void mouseEntered(MouseEvent arg0) {}
    public void mouseExited(MouseEvent arg0) {}
    public void mousePressed(MouseEvent arg0) {}
    public void mouseReleased(MouseEvent arg0) {}
}
