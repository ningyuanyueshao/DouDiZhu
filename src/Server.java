import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @author seaside
 * 2023-05-07 11:06
 */

//该类用于获取与客户端的连接，应该没有什么要更改的了
public class Server {
    public static void main(String[] args) throws IOException {
        int roomSize = 10; //总共房间数
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器端开始运行");
        Room[] roomSet = new Room[roomSize];
        for (int roomNumber = 0; roomNumber < roomSize; roomNumber++) {
            roomSet[roomNumber] = new Room(roomNumber); //一开始先创建十个房间
        }
        int count = 1; //用户序号，拿来标识线程
        while(true) {//这个循环按理来说要一直进行
            Socket clientSocket = serverSocket.accept(); //得到来自客户端的套接字连接。java中的socket默认使用TCP连接
            ClientThread clientThread = new ClientThread(String.valueOf(count), clientSocket,roomSet);
            System.out.println("用户（序号"+ count +"）连接成功");
            count = count + 1;
            clientThread.start();
        }
    }
}




