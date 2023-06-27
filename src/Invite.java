import java.util.LinkedList;

/**
 * @author seaside
 * 2023-05-19 16:40
 */
//该类放在服务端
public class Invite {
    public static LinkedList<Message> messages = new LinkedList<>(); //存放消息，一有用户端登录就检索
    public static LinkedList<ClientThread> clientThreads = new LinkedList<>(); //存放所有在线的线程，其他类其实也可以使用
    public static boolean isLogIn(String usernameNow){
        for (ClientThread clientThread : clientThreads) {
            String username = clientThread.username;
            if (username != null &&username.equals(usernameNow)){
                System.out.print(username+"----");
                return false;
            }
        }//检查现在处于登录状态的客户端
        return true;
    }
    public static void newMessage(String sourceUsername,int roomID,String aimUsername){
        for (ClientThread clientThread : clientThreads) {
            String username = clientThread.username;
            if (username!=null && username.equals(aimUsername))
                clientThread.giveInviteMessage(sourceUsername,roomID);
        }//检查现在处于登录状态的客户端，若有匹配的，直接发送信息
        //若没有，新建一个Message对象，填入messages中等待处理
        messages.add(new Message(sourceUsername, roomID, aimUsername));
    }

    public static String getAllUsernames(){
        String usernames = clientThreads.get(0).username;
        for (int i = 1; i < clientThreads.size(); i++) {
            usernames = usernames.concat(";"+clientThreads.get(i).username);
        }
        return usernames;
    }
    public static void checkMessages(ClientThread clientThread){
        for (Message temp : messages) {
            if (temp.aimUsername.equals(clientThread.username))
                clientThread.giveInviteMessage(temp.sourceUsername, temp.roomID);
        }
    }//查看未处理的消息中是否有与新登录的用户所匹配(好像用不上，因为都是给在线用户发邀请)
}
