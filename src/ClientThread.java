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
        super(name); //线程名称就是用户序号，TODO：感觉没什么用
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
            to = "1:连接成功";
            /*
            }*/
            printWriter.println(to);
            System.out.println("服务端一开始给予的信息为"+to);
            //TODO 现在是有收才有发，最好是没收也能发
            while((from = bufferedReader.readLine()) != null){//从输入输出流获取交互信息
                //TODO:主要操作应该都在这里面
                System.out.println("客户端给的信息为"+ from);
                to = serverOperators(from,roomSet,room);
                System.out.println("服务端返回的信息为"+to);
                printWriter.println(to);
            }
        } catch (IOException e) {
            System.out.println("终止连接或数据传输出错");
            //出错应该尝试重连吧？？？？？
        }
    }

    public static String serverOperators(String from,Room[] roomSet, int room){
        String to = "";
        switch (from.charAt(0)){
            case '2': //客户端给的是用户名
                //进行数据库操作
                //返回空余房间
                to = getSpareRooms(roomSet);
                break;
            case '4':
                System.out.println("用户准备就绪，可以发牌了");
                break;
        }
        return to;
    } //根据收到的字符串的第一位执行相应操作
    public static String getSpareRooms(Room[] roomSet){
        String temp = "3:";
        for (Room room : roomSet) {
            if (room.playerSize != 3) //不等于3说明还没满，可以加入
                temp = temp.concat(String.valueOf(room.roomNumber));
        }
        return temp;
    } //得到空闲的房间号
}
