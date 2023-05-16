import java.util.ArrayList;
import java.util.List;

/**
 * @author seaside
 * 2023-05-08 10:47
 */
public class Room{
    int roomNumber;//房间号
    int playerSize; //当前房间内玩家数量
    int readySize = 0;
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
                clientThread.position = i;
                this.playerSize++;
                System.out.println("该房间内的用户"+ i + "的线程已被获取");
                break;
            }
        }
        for (int j = 0; j < i; j++) {
            clientThreads[j].informClientRoomNewPlayer(i,clientThreads[i].username);//位置分为012号位，按照加入次序分配
        }
    } //线程通过调用该方法来为房间里的三个线程类变量赋值，这样之后就可以利用该房间访问到其他线程

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


    public void setPlayerReady(ClientThread clientThread){
        readySize++;
        if(readySize == 3)
            dealTheCards(); //若三个人都准备就绪，直接发牌
        else{
            int temp = clientThread.position;
            for (int i = 0; i < 3; i++) {
                if(i != temp)
                    clientThreads[i].informClientRoomNewReady(temp);
            }
        }
    }//将准备就绪状态告知其他线程，同时若三个人都准备就绪，就可以发牌

    public void dealTheCards(){
        Deck deck = new Deck();
        deck.shuffle();
        List<ArrayList<Card>> tempList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tempList.add(deck.deal(17));
        }
        List<Card> landlordCards = deck.deal(3);
        for (int i = 0; i < 3; i++) {
            this.giveTheCards(clientThreads[i],tempList,landlordCards);
        }
    }//给所有线程发牌

    public void giveTheCards(ClientThread tempClient,List<ArrayList<Card>> tempList,List<Card> landlordCards){
        String temp = "";
        Card tempCard;
        //每个线程都要得到所有人的手牌信息，最好是按位置放
        for (ArrayList<Card> cards : tempList) {
            for (Card card : cards) {
                temp = temp.concat(card.toString() + "、");
            }
            temp = temp.concat(";");
        }//加入所有人的手牌，格式例子为"黑桃A、红桃2、······;黑桃A、红桃2、······;黑桃A、红桃2、······;"
        //当客户端获取到该字符串的时候，就可以根据分号获取不同位置的人的手牌，根据顿号获取手牌数组
        //todo：这样会不会一次性传太多，一张牌大概需要8字节，总共就是两百多字节。如果太多，就分次发送
        for(Card card : landlordCards){
            tempCard = card;
            temp = temp.concat(tempCard.toString()+"、"); //加入地主牌
        }
        tempClient.giveCards(temp);
    } //给单个线程发牌



    public void deletePlayer(ClientThread clientThread){
        int temp = clientThread.position;
        clientThreads[temp] = null;
        playerSize--;
        System.out.println("该房间内的用户"+temp+"的线程已被剔除");
    }//当用户退出的时候，从房间中剔除该用户

    //可以在这个房间里调用每个线程类的方法，就可以修改每个线程里的from和to了


}
