import java.io.*;
import java.net.Socket;


/**
 * @author seaside
 * 2023-05-08 10:46
 */
public class ClientThread extends Thread{

    private Socket clientSocket;
    //什么时候关闭这个线程呢？？？？？当客户端关闭连接时，套接字会得到通知，但通知的方式不同于服务器端收到客户端连接时的通知。
    //在客户端关闭连接时，InputStream.read() 方法会返回 -1，这表示已经到达了流的末尾。因此，您可以通过检查返回值来确定客户端是否关闭了连接。
    //在上面的代码中，当客户端关闭连接时，while 循环条件会变为 false，然后通过 socket.close() 关闭套接字。这将导致服务端的 InputStream.read() 方法返回 -1，从而结束服务端的读取循环。
    //在实际开发中，您可能需要处理客户端异常关闭连接的情况，例如在 IOException 异常中捕获客户端连接的关闭。您也可以在客户端发送特定的协议消息来显式地告知服务器端客户端已经关闭连接


    Room[] roomSet;
    int room;
    String from = "";
    String to = "";
    public ClientThread(String name, Socket clientSocket,Room[] roomSet) {
        super(name); //线程名称就是用户名
        this.clientSocket = clientSocket;
        this.roomSet = roomSet;
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
            to = "1:";
            for (Room room : roomSet) {
                if(room.playerSize != 3) //不等于3说明还没满，可以加入
                    to = to.concat(String.valueOf(room.roomNumber));
            }
            printWriter.println(to);
            while((from = bufferedReader.readLine()) != null){//从输入输出流获取交互信息
                System.out.println("客户端给的信息为"+ from);
                switch (from.charAt(0)){
                    case '2':
                        System.out.println("用户选择房间"+from.charAt(2));
                        room = from.charAt(2) - '0'; //确定该用户所处的房间
                        roomSet[room].setEveryClientThread(this);
                        try {
                            Thread.sleep(20000); //用于测试，后面有to的更新了就可以删掉
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                        break;
                    case '4':
                        System.out.println("用户准备就绪，可以发牌了");
                        break;
                }
                //根据交互信息的类型进行不同的操作
                System.out.println("服务端返回的信息为"+to);
                printWriter.println(to);
            }
        } catch (IOException e) {
            System.out.println("终止连接或数据传输出错");
            //出错应该尝试重连吧？？？？？
        }
    }
}
