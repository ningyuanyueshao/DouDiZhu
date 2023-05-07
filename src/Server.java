import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author seaside
 * 2023-05-07 11:06
 */
public class Server {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器端开始运行");
        while(true) {//这个循环按理来说要一直进行

            Socket clientSocket = serverSocket.accept(); //得到来自客户端的套接字连接。java中的socket默认使用TCP连接
            //创建一个线程对应一个客户端。
            new ClientThread(clientSocket.getRemoteSocketAddress().toString(), clientSocket).start();

        }
    }
}

class ClientThread extends Thread{
    //有多个客户端一起游戏的话应该加点标识。注意共同操作的部分应该保证线程安全
    private Socket clientSocket;
    //什么时候关闭这个线程呢？？？？？当客户端关闭连接时，套接字会得到通知，但通知的方式不同于服务器端收到客户端连接时的通知。
    //在客户端关闭连接时，InputStream.read() 方法会返回 -1，这表示已经到达了流的末尾。因此，您可以通过检查返回值来确定客户端是否关闭了连接。
    //在上面的代码中，当客户端关闭连接时，while 循环条件会变为 false，然后通过 socket.close() 关闭套接字。这将导致服务端的 InputStream.read() 方法返回 -1，从而结束服务端的读取循环。
    //在实际开发中，您可能需要处理客户端异常关闭连接的情况，例如在 IOException 异常中捕获客户端连接的关闭。您也可以在客户端发送特定的协议消息来显式地告知服务器端客户端已经关闭连接
    public ClientThread(String name, Socket clientSocket) {
        super(name); //线程名称有什么用呢？？？？
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        super.run();
        //主要操作应该都在这个线程里面
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            //要用这个BufferedReader接收吗？？？看传输的是什么吧,如果用到序列化就不行
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //这个是字符流。要用这个发送吗？？？感觉信息不应该是简简单单的字符串，应该包含有 操作类型、信息、检验等吧
            PrintWriter printWriter = new PrintWriter(outputStream,true);//true表示自动刷新，即会立刻传输数据
            String from = "";
            String to = "";
            while((from = bufferedReader.readLine()) != null){//从输入输出流获取交互信息
                System.out.println("客户端给的信息为"+ from);

                //根据交互信息的类型进行不同的操作
                System.out.println("服务端返回的信息为"+to);
                printWriter.println(to);
            }
        } catch (IOException e) {
            System.out.println("数据传输出错");
            //出错应该尝试重连吧？？？？？
        }
    }
}
