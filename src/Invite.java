import java.util.LinkedList;

/**
 * @author seaside
 * 2023-05-19 16:40
 */
//该类放在服务端
public class Invite {
    public static LinkedList<Message> messages = new LinkedList<>(); //存放消息，一有用户端登录就检索
    public static LinkedList<ClientThread> clientThreads = new LinkedList<>(); //存放所有线程，其他类其实也可以使用
    public static void newMessage(String sourceUsername,int roomID,String aimUsername){
        for (ClientThread clientThread : clientThreads) {
            String username = clientThread.username;
            if (username.equals(aimUsername))
                sendMessage(clientThread, sourceUsername, roomID);
        }//检查现在处于登录状态的客户端，若有匹配的，直接发送信息
        //若没有，新建一个Message对象，填入messages中等待处理
        messages.add(new Message(sourceUsername, roomID, aimUsername));
    }

    public static void checkMessages(ClientThread clientThread){
        for (Message temp : messages) {
            if (temp.aimUsername.equals(clientThread.username))
                sendMessage(clientThread, temp.sourceUsername, temp.roomID);
        }
    }//查看未处理的消息中是否有与新登录的用户所匹配

    public static void sendMessage(ClientThread clientThread,String sourceUsername,int roomID){
        //todo:最后要得到客户端是否同意邀请的结果
    }
}
