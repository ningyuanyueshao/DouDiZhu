/**
 * @author seaside
 * 2023-05-19 16:41
 */
public class Message {
    String sourceUsername;
    int roomID;
    String aimUsername;

    public Message(String sourceUsername, int roomID, String aimUsername) {
        this.sourceUsername = sourceUsername;
        this.roomID = roomID;
        this.aimUsername = aimUsername;
    }
}
