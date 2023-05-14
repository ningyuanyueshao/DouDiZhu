import java.util.List;

/**
 * @author seaside
 * 2023-05-08 10:47
 */
public class Room{
    int roomNumber;//房间号
    int playerSize; //当前房间内玩家数量
    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    ClientThread[] clientThreads = new ClientThread[3]; //存放3个用户线程

    ClientThread tempClientThread;//tempClientThread表示要被调用的线程

    public void setEveryClientThread(ClientThread clientThread){
        int i;
        for (i = 0; i < 3; i++) {
            if(clientThreads[i] == null) {
                clientThreads[i] = clientThread;
                this.playerSize++;
                System.out.println("该房间内的用户"+ i + "的线程已被获取");
                break;
            }
        }
        for (int j = 0; j < i; j++) {
            clientThreads[j].informClientRoomNewPlayer(i,clientThreads[i].username);//位置分为012号位，按照加入次序分配
        }
    } //线程通过调用该方法来为房间里的三个线程类变量赋值，这样之后就可以利用该房间访问到其他线程

    public void dealTheCards(){
        Deck deck = new Deck();
        deck.shuffle();
        List<Card> tempList;
        for (int i = 0; i < 3; i++) {
            tempList = deck.deal(17);
            this.giveTheCards(clientThreads[i],tempList);
        }
    }//给所有线程发牌   //TODO:剩的三张牌要作为地主牌通知给所有线程

    public void giveTheCards(ClientThread tempClient,List<Card> list){
        String temp = "8:";
        Card tempCard;
        for (Card card : list) {
            tempCard = card;
            temp = temp.concat(tempCard.getSuit() + tempCard.getRank());
        }
        tempClient.to = temp;
    } //给单个线程发牌

    public String getPlayersNow(){
        String to = "5:";
        if(clientThreads[0] == null)
            to = to.concat("9");//客户端收到9说明房间内一开始没有其他用户
        else {
            for (int i = 0; i < clientThreads.length; i++) {
                if (clientThreads[i] != null) {
                    to = to.concat(i + clientThreads[i].username + ";"); //i表示几号位
                }
            }
        }
        return to;
    }//得到该房间当前人数和对应用户名

    public void deletePlayer(ClientThread clientThread){
        //如何判断线程相等
        for (int i = 0; i < clientThreads.length; i++) {
            if(clientThread.equals(clientThreads[i])){
                clientThreads[i] = null;
                playerSize--;
                System.out.println("该房间内的用户"+i+"的线程已被剔除");
            }
        }
    }//当用户退出的时候，从房间中剔除该用户

    //可以在这个房间里调用每个线程类的方法，就可以修改每个线程里的from和to了


}
