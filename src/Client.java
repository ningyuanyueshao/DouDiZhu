/**
 * @author seaside
 * 2023-05-07 11:06
 */
public class Client {
    public static void main(String[] args){
        System.setProperty("sun.java2d.noddraw", "true");
        //这里调用图形化的主菜单界面
        Frame frame = new Frame();
        frame.setVisible(true);
         //todo:要不要设成static，因为ClientThread肯定要调用它。也可以在Invite里return它自己
        while(true){
            try {
                Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(frame.IsOnlineGame()){
                ClientConnectThread clientConnectThread = new ClientConnectThread(frame.SpLayout);
                clientConnectThread.start(); //额外创建一个线程用来网络连接，减少网络连接等带来的图形化界面停顿影响
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(frame.SpLayout.wantGetConnected2)
                    break;
            }
            if(frame.IsOnePGame()){
                //Todo:调用单机游戏界面
                frame.showOnePLayout();//调用单机游戏界面
                break;
            }
        }
    }
}
