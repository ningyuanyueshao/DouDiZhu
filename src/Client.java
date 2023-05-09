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


        //若用户选择了在线游戏，再进行连接
        //需要主机IP地址和端口
        String hostIPAddress = "192.168.87.1"; //这个是打开wifi的属性中得到的，不知道能不能用？？？？？
        int serverPort = 8080;
        System.out.println("该客户端开始连接服务器");
        Socket clientSocket = new Socket(hostIPAddress,serverPort);
        //TODO:主要操作应该都在这里面
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
        System.out.println("连接成功，等待用户操作");
        String from = "";
        String to = "";
        try{
            while((from = bufferedReader.readLine())!=null){//在这里使用输入输出流与服务器端进行交互。
                System.out.println("得到服务器端给的信息"+ from);
                //根据交互信息的类型进行不同的操作
                switch (from.charAt(0)){
                    case '1':
                        System.out.println("令用户选择房间号");
                        to = "2:1";
                        break;
                    case '3':
                        System.out.println("房间人数已满，可以开始游戏");
                        break;
                }
                printWriter.println(to);
                System.out.println("客户端返回的信息为"+ to);
            }
        } catch (Exception e) {
            System.out.println("连接中断或出错");
        }
    }

}
