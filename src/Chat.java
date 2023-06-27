/**
 * @author seaside
 * 2023-05-21 23:38
 */
//该类放在服务端
public class Chat {
    public static void giveChatStrings(String fromUsername,String toUsername,String chatItems){
        ClientThread to = null;
        ClientThread temp;
        for (int i = 0; i < Invite.clientThreads.size(); i++) {
            temp = Invite.clientThreads.get(i);
            if(temp.username!=null && temp.username.equals(toUsername)){
                to = Invite.clientThreads.get(i);
            }
        }
        to.giveChatItemsToClient(fromUsername,chatItems);
    }
}
