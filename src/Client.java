import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author seaside
 * 2023-05-07 11:06
 */
public class Client {
    public static void main(String[] args) throws IOException {
        //这里调用图形化的初始界面
        Background background = new Background();
        while(true){
            if(background.wantGetConnected == true){
                connectedPlay(background);
                break;
            }
            if(background.wantSingleConnected == true){
                //Todo:单机游戏
                break;
            }
        }

    }
    public static void connectedPlay(Background background) throws IOException{
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
                //TODO:主要操作应该都在这里面
                System.out.println("得到服务器端给的信息"+ from);
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
            username = background.getPlayername();
            System.out.println(username);
            if(!username.equals(""))
                break;
        }
        Username = Username.concat(username);
        return Username;
    } //返回用户名
}
