/**
 * @author seaside
 * 2023-05-07 11:06
 */
public class Client {
    public static void main(String[] args){
        //这里调用图形化的初始界面
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
                clientConnectThread.start(); //调用网络连接线程，减少网络连接等带来的图形化界面停顿影响
                break;
            }
            if(background.wantSingleConnected){
                //Todo:单机游戏
                break;
            }
        }
    }
}
