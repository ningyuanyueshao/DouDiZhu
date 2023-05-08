import javax.print.DocFlavor;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author seaside
 * 2023-05-07 11:06
 */
public class Server {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器端开始运行");
        Set<Room> roomSet = new HashSet<>();
        for (int roomNumber = 0; roomNumber < 10; roomNumber++) {
            roomSet.add(new Room(roomNumber)); //一开始先创建十个房间
        }
        int count = 1;
        while(true) {//这个循环按理来说要一直进行
            Socket clientSocket = serverSocket.accept(); //得到来自客户端的套接字连接。java中的socket默认使用TCP连接
            ClientThread clientThread = new ClientThread(String.valueOf(count), clientSocket,roomSet);
            clientThread.start();
        }
    }
}


