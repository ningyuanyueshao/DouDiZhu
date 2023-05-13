import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author seaside
 * 2023-05-13 20:38
 */
public class ClientConnectThread extends Thread{
    Background background; //该线程有时要获取图形化界面得到的信息，所以需要此对象。
    @Override
    public void run() {
        try {
            connectedPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientConnectThread(Background background) {
        this.background = background;
    }

    public void connectedPlay()throws IOException {
        //若用户选择了在线游戏，再进行连接
        //需要主机IP地址和端口
        String hostIPAddress = "10.128.199.86"; //这个是打开wifi的属性中得到的
        int serverPort = 8080;
        System.out.println("该客户端开始连接服务器");
        Socket clientSocket = new Socket(hostIPAddress,serverPort);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
        System.out.println("连接成功，等待用户操作");
        String from = "";
        String to = "";
        try{
            //TODO 现在是有收才有发，最好是没收也能发
            while((from = bufferedReader.readLine())!=null){//在这里使用输入输出流与服务器端进行交互。
                //TODO:和服务器的主要操作应该都在这里面
                System.out.println("得到服务器端给的信息为"+ from);
                //根据交互信息的类型进行不同的操作
                to = clientOperators(from,background);
                printWriter.println(to);
                System.out.println("客户端返回的信息为"+ to);
            }
        } catch (Exception e) {
            System.out.println("连接中断或出错");
        }
    }

    public static String clientOperators(String from,Background background){
        String to = "";
        switch (from.charAt(0)){
            case '1': //”连接成功“
                to = getUsername(background);
                break;
            case '3':
        }
        return to;
    }//根据收到的字符串的第一位执行相应操作

    public static String getUsername(Background background){
        String Username = "2:";
        //如果要在while中读取图形化的getUsername，感觉会很浪费性能，但这段时间内又只能干这件事，好像浪费也没啥？？？？？
        String username;
        while(true){
            try {
                Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            username = background.getPlayername();
            if(!username.equals(""))
                break;
        }
        Username = Username.concat(username);
        return Username;
    } //返回用户名
}
