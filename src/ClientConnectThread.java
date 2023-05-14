import java.io.*;
import java.net.Socket;

/**
 * @author seaside
 * 2023-05-13 20:38
 */
public class ClientConnectThread extends Thread{
    Background background; //该线程有时要获取图形化界面得到的信息，所以需要此对象。
    @Override
    public void run() {
        try {
            connectedPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientConnectThread(Background background) {
        this.background = background;
    }

    public void connectedPlay()throws IOException {
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
            //TODO 现在是有收才有发，最好是没收也能发,有收可以不发
            while((from = bufferedReader.readLine())!=null){//在这里使用输入输出流与服务器端进行交互。
                //TODO:和服务器的主要操作应该都在这里面
                System.out.println("得到服务器端给的信息为"+ from);
                //根据交互信息的类型进行不同的操作
                to = clientOperators(from,background);
                if(to == null)
                    continue; //若为空，说明不用发，就实现有收可以不发
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
                to = getRoomChoosed(from,background);
                break;
            case '5':
                //TODO：此时可以调用图形化界面显示游戏内部房间信息
//                创建界面类，按background模板写，放到这里
//                case5是进桌等人的状态
                System.out.println("加入房间并显示当前房间内有多少人");
                to = null;
                break;
            case '6':
                //TODO：此时可以调用图形化界面显示有人加入房间
                System.out.println("有新用户加入房间");
                to = null;
                break;
        }
        return to;
    }//根据收到的字符串的第一位执行相应操作

    public static String getUsername(Background background){
        String Username = "2:";
        String username;
        while(true){
            try {
                Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            username = background.getPlayerName();
            if(!username.equals(""))
                break;
        }
        Username = Username.concat(username);
        return Username;
    } //返回用户名

    public static String getRoomChoosed(String from,Background background){
        String chosenRoom = "4:";
        String temp = from.substring(from.indexOf(':')+1);
        int length = temp.length();
        int[] rooms = new int[length];
        for (int i = 0; i < length; i++) {
            rooms[i] = temp.charAt(i) - '0';
        }
        background.setRoomsCanPlay(rooms);
        while(true){
            try
            {
                Thread.sleep(10);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if(background.choseRoom != -1)
                break;
        }
        chosenRoom = chosenRoom.concat(String.valueOf(background.choseRoom));
        return chosenRoom;
    }
}
