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
                to = clientOperators(from);
                printWriter.println(to);
                System.out.println("客户端返回的信息为"+ to);
            }
        } catch (Exception e) {
            System.out.println("连接中断或出错");
        }
    }

    public static String clientOperators(String from){
        String to = "";
        switch (from.charAt(0)){
            case '1': //”连接成功“
                to = getUsername();
                break;
            case '3':
        }
        return to;
    }//根据收到的字符串的第一位执行相应操作


    public static String getUsername(){
        String Username = "2:";
        //这边就是图形化让用户输入的那些代码，可以再来个方法

        return Username;
    } //返回用户名
}
