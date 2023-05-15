import java.io.*;
import java.net.Socket;


/**
 * @author seaside
 * 2023-05-08 10:46
 */
public class ClientThread extends Thread{

    public String username;
    public int position;//在房间内是几号位，012三个值按进入房间顺序进行分配
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
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
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            printWriter = new PrintWriter(outputStream,true);//true表示自动刷新，即会立刻传输数据
            to = "1:连接成功";
            /*
            }*/
            printWriter.println(to);
            System.out.println("服务端一开始给予的信息为"+to);
            //TODO 现在是有收才有发，最好是没收也能发,有收可以不发
            while((from = bufferedReader.readLine()) != null){//从输入输出流获取交互信息
                //TODO:主要操作应该都在这里面
                System.out.println("客户端给的信息为"+ from);
                to = serverOperators(from,roomSet);
                if(to == null)
                    continue; //若为空，说明不用发
                System.out.println("服务端返回的信息为"+to);
                printWriter.println(to);
            }
        } catch (IOException e) {
            System.out.println("终止连接或数据传输出错");
            roomSet[room].deletePlayer(this);
            //出错应该尝试重连吧？？？？？还是说不会出错？？？？
        }
    }

    public String serverOperators(String from,Room[] roomSet){
        String to = "";
        switch (from.charAt(0)){
            case '2':
                username = from.substring(from.indexOf(':')+1); //得到用户名
                //TODO：进行数据库操作
                to = getSpareRooms(roomSet);//返回空余房间
                break;
            case '4':
                room = from.charAt(2) - '0';
                to = roomSet[room].getPlayersNow();//先确定返回的人数和用户名
                roomSet[room].setEveryClientThread(this); //然后在房间中添加该线程
                break;
            case '7':
                roomSet[room].setPlayerReady(this); //告知房间该玩家准备就绪
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

    public void informClientRoomNewPlayer(int position,String username){
        String to1 = "6:"+position+username;
        System.out.println("服务端返回的玩家信息为"+to1);
        printWriter.println(to1);//这样单独的输出就实现了没收也能发
    }//通知客户端房间内新加入的玩家，position对应房间几号位

    public void informClientRoomNewReady(int position){ //number表示几号位置
        String to2 = "8:"+position;
        System.out.println("服务器返回的玩家准备信息为"+to2);
        printWriter.println(to2);
    }//通知客户端房间内其他玩家准备的信息
    public void giveCards(String cards){
        String to3 = "9:" + cards;
        System.out.println("服务端返回的卡牌信息为"+to3);
        printWriter.println(to3);
    }

}
