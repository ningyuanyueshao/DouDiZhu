import java.util.ArrayList;
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
        switch (clientThread.getName()){
            case "1":
                clientThreads[0] = clientThread;
                this.playerSize++;
                System.out.println("该房间内的用户1的线程已被获取");
                break;
            case "2":
                clientThreads[1] = clientThread;
                this.playerSize++;
                System.out.println("该房间内的用户2的线程已被获取");
                break;
            case "3":
                clientThreads[2] = clientThread;
                this.playerSize++;
                System.out.println("该房间内的用户3的线程已被获取");
                break;
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

    //可以在这个房间里调用每个线程类的方法，就可以修改每个线程里的from和to了


}
