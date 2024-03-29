import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * @author seaside
 * 2023-05-08 10:46
 */
public class ClientThread extends Thread{

    public String username = null;
    public String password = null;
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
    ArrayList<Room> roomArrayList; //要能够创建房间，意味着要往房间号序列里添加新房间，就不能用数组了（不过客户端的线程与图形化的传递还是可以用数组，不影响）
    int room;
    String from = "";
    String to = "";
    //代码中出现的while和wait的结合都是在等待消息传递，即线程通信
    public ClientThread(String name, Socket clientSocket,ArrayList<Room> roomArrayList) {
        super(name); //线程名称就是用户序号，TODO：感觉没什么用
        this.clientSocket = clientSocket;
        //this.roomSet = roomSet;
        this.roomArrayList = roomArrayList;
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
            printWriter.println(to);
            System.out.println("服务端一开始给予的信息为"+to);
            //TODO 现在是有收才有发，最好是没收也能发,有收可以不发
            while((from = bufferedReader.readLine()) != null){//从输入输出流获取交互信息
                //主要操作应该都在这里面
                System.out.println("客户端给的信息为"+ from);
                to = serverOperators(from,roomArrayList);
                if(to == null)
                    continue; //若为空，说明不用发
                System.out.println("服务端返回的信息为"+to);
                printWriter.println(to);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("终止连接或数据传输出错");
            if(DataBase.usr_name_isExist(username)){
                DataBase.update_usr_playtime(username,System.currentTimeMillis()); //记录时间给数据库
            }
            roomArrayList.get(room).deletePlayer(this);
            Invite.clientThreads.remove(this);
            if(username != null)
                Invite.onlineUsers.remove(username);
        }
    }

    public String serverOperators(String from,ArrayList<Room> roomArrayList){
        String to = "";
        switch (from.charAt(0)){
            case '2':
                to = checkUser(from.substring(from.indexOf(':')+1));
                break;
            case '4':
                to = getRoomChoice(from.substring(from.indexOf(':')+1));
                break;
            case '6':
                room = Integer.parseInt(from.substring(2));
                to = roomArrayList.get(room).getPlayersNow();//先确定返回的人数和用户名
                roomArrayList.get(room).setEveryClientThread(this);//然后在房间中添加该线程
                break;
            case '9':
                roomArrayList.get(room).setPlayerReady(this);//告知房间该玩家准备就绪
                to = null;
                break;
            case 'c':
                roomArrayList.get(room).giveScores(from.substring(2));
                to=null;
                break;
            case 'e':
                roomArrayList.get(room).giveActionCardsToOthers(this,from.substring(2));
                to=null;
                break;
            case 'g':
                roomArrayList.get(room).recordDataBase(from.substring(2));
                to =null;
                break;
            case 'o':
                String fromAll = from.substring(from.indexOf(':')+1);
                String[] strings = fromAll.split("-");
                Invite.newMessage(strings[0],Integer.parseInt(strings[1]),strings[2]);
                to = null;
                break;
            case 'q':
                String string = from.substring(2);
                String[] str = string.split("-");
                position = Integer.parseInt(str[3]);
                for (ClientThread clientThread:Invite.clientThreads) {
                    if(clientThread.username!= null && clientThread.username.equals(str[1])) {
                        if(str[0].equals("同意"))
                            clientThread.printWriter.println("r:" + str[0] + "-" + str[2] + "-" + str[3]);
                        else if (str[0].equals("拒绝"))
                            clientThread.printWriter.println("r:" + str[0]);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                to = null;
                break;
            case 's':
                String fromUsername = from.substring(from.indexOf(':')+1,from.indexOf('-'));
                String temp = from.substring(from.indexOf('-')+1);
                String targetUsername = temp.substring(0,temp.indexOf('-'));
                String chatItems = temp.substring(temp.indexOf('-')+1);
                Chat.giveChatStrings(fromUsername,targetUsername,chatItems);
                to = null;
                break;
            case 'u':
                getAllOnlineUsernames();
                to = null;
                break;
        }
        return to;
    } //根据收到的字符串的第一位执行相应操作

    public String checkUser(String all){
        String to = "3:";
        switch (all.charAt(0)){
            case '0':
                username = all.substring(1,all.indexOf('-'));
                password = all.substring(all.indexOf('-')+1);
                Boolean ret = DataBase.Add_usr(username,password);
                if(ret)
                    to = to.concat("注册成功");
                else
                    to = to.concat("失败");
                //用户注册，数据库检验用户名是否重复即可,若重复要返回失败
                break;
            case '1':
                if(!Invite.isLogIn(all.substring(1,all.indexOf('-')))){
                    to = to.concat("登录失败");
                    break;
                }//与在线玩家冲突
                username = all.substring(1,all.indexOf('-'));
                password = all.substring(all.indexOf('-')+1);
                String pass = DataBase.get_usr_pwd(username);
                if(password.equals(pass)){
                    to = to.concat("登录成功");
                    Invite.onlineUsers.add(username);
                }
                else {
                    to = to.concat("登录失败");
                }
                //用户登录，数据库检验用户名与密码是否匹配
                break;
            case '2':
                to = to.concat("用户列表");
                to = to.concat(DataBase.get_All_user_name());
                break;
        }
        return to;
    }//对用户的注册/登录/查看用户列表的选择做出反应

    public String getRoomChoice(String all){
        String to = "5:";
        switch (all.charAt(0)){
            case '0'://创建默认房间
                room = roomArrayList.size();
                roomArrayList.add(new Room(room));
                to = roomArrayList.get(room).getPlayersNow();
                roomArrayList.get(room).setEveryClientThread(this);
                break;
            case '1'://创建私密房间
                room = roomArrayList.size();
                roomArrayList.add(new Room(room));
                roomArrayList.get(room).isPrivate = true;
                to = roomArrayList.get(room).getPlayersNow();
                roomArrayList.get(room).setEveryClientThread(this);
                break;
            case '2'://加入房间
                to = getSpareRooms(roomArrayList);
                break;
        }
        return to;
    }
    public static String getSpareRooms(ArrayList<Room> roomArrayList){
        String to = "5:";
        for(Room room:roomArrayList){
            if(!room.isPrivate && room.playerSize!=3)
                to = to.concat(room.roomNumber + "-");
        }
        return to;
    }//得到空闲的房间号

    public void informClientRoomNewPlayer(int position,String username){
        String to1 = "8:"+position+"-"+username;
        System.out.println("服务端返回的玩家信息为"+to1);
        printWriter.println(to1);//这样单独的输出就实现了没收也能发
    }//通知客户端房间内新加入的玩家，position对应房间几号位

    public void informClientRoomNewReady(int position){ //number表示几号位置
        String to2 = "a:"+position;
        System.out.println("服务器返回的玩家准备信息为"+to2);
        printWriter.println(to2);
    }//通知客户端房间内其他玩家准备的信息
    public void giveCards(String cards){
        String to3 = "b:" + cards;
        System.out.println("服务端返回的卡牌信息为"+to3);
        printWriter.println(to3);
    }
    public void giveScore(String string){
        printWriter.println("d:"+string);
    }
    public void giveInviteMessage(String sourceUsername,int roomID){
        String to = "p:"+sourceUsername+"-"+roomID;
        Room temp = roomArrayList.get(roomID);
        for (int i = 0; i < temp.clientThreads.length; i++) {
            if (temp.clientThreads[i] != null) {
                to = to.concat("-" + temp.clientThreads[i].username); //i表示几号位
                if(temp.positionReady[i])
                    to = to.concat("、1");
                else
                    to = to.concat("、0");
            }
        }
        System.out.println("服务器返回的邀请信息为"+to);
        printWriter.println(to);
    }
    public void giveChatItemsToClient(String fromUsername,String items){
        String to = "t:" + fromUsername + "-" + items;
        System.out.println("服务器返回的聊天信息为"+to);
        printWriter.println(to);
    }
    public void giveActionCardsToClient(String cards){
        String to = "f:"+ cards;
        System.out.println("服务器返回的卡牌信息为"+to);
        printWriter.println(to);
    }
    public void getAllOnlineUsernames(){
        String to = "v:"+Invite.getAllUsernames();
        System.out.println("服务器返回的所有在线玩家信息为"+to);
        printWriter.println(to);
        printWriter.flush();
    }
    public void updateScoreToDatabase(int score){
        DataBase.update_usr_score(this.username,score);
    }
}
