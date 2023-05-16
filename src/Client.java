import java.awt.*;

/**
 * @author seaside
 * 2023-05-07 11:06
 */
public class Client {
    public static void main(String[] args){
        System.setProperty("sun.java2d.noddraw", "true");
        //这里调用图形化的主菜单界面
        Background background = new Background();
        background.setVisible(true);
        while(true){
            try {
                Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(background.wantGetConnected){
                ClientConnectThread clientConnectThread = new ClientConnectThread(background);
                clientConnectThread.start(); //额外创建一个线程用来网络连接，减少网络连接等带来的图形化界面停顿影响
                break;
            }
            if(background.wantSingleConnected){
                //Todo:调用单机游戏界面
                break;
            }
        }
    }
}
