import javax.swing.*;

public class SinglePoker extends JLabel {
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
}
