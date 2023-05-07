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
        //需要主机IP地址和端口
        String hostIPAddress = "192.168.0.0";
        int serverPort = 8080;
        System.out.println("该客户端开始连接服务器");

        Socket clientSocket = new Socket(hostIPAddress,serverPort);
        //TODO:主要操作应该都在这里面
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
        String from = "";
        String to = "";
        try{
            while((from = bufferedReader.readLine())!=null){//在这里使用输入输出流与服务器端进行交互。
                System.out.println("得到服务器端给的信息"+ from);
                //根据交互信息的类型进行不同的操作

                System.out.println("客户端返回的信息为"+ to);
            }
        } catch (Exception e) {
            System.out.println("连接出错");
        }

    }
}
//四大皆空三