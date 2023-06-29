import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author seaside
 * 2023-05-08 10:47
 */
public class Room{
    int roomNumber;//房间号
    int playerSize; //当前房间内玩家数量
    volatile int readySize = 0;
    boolean[] positionReady = new boolean[3];
    boolean isPrivate = false; //房间默认不是私密的
    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    ClientThread[] clientThreads = new ClientThread[3]; //存放3个用户线程，用这个下标表示座位


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
        String to = "7:";
        String temp = String.valueOf(roomNumber);
        to = to.concat(temp);
        if(clientThreads[0] == null)
            to = to.concat("-"+"9");//客户端收到9说明房间内一开始没有其他用户
        else {
            for (int i = 0; i < clientThreads.length; i++) {
                if (clientThreads[i] != null) {
                    to = to.concat("-" + clientThreads[i].username); //i表示几号位
                    if(positionReady[i])
                        to = to.concat("、1");
                    else
                        to = to.concat("、0");
                }
            }
        }
        return to;
    }//得到该房间当前人数和对应用户名

    public void setPlayerReady(ClientThread clientThread){
        readySize++;
        int temp = clientThread.position;
        positionReady[temp] = true;
        for (int i = 0; i < 3; i++) {
            if (i != temp && clientThreads[i]!=null)
                clientThreads[i].informClientRoomNewReady(temp);
        }
        System.out.println("当前房间内准备的玩家数量"+readySize);
        if(readySize >= 3){
            System.out.println("发牌");
            dealTheCards(); //若三个人都准备就绪，直接发牌
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
        int random = (int)(Math.random() * 3);
        for (int i = 0; i < 3; i++) {
            System.out.println("向座位"+i+"的玩家发牌");
            this.giveTheCards(clientThreads[i],tempList,landlordCards,random);
        }
    }//给所有线程发牌

    public void giveTheCards(ClientThread tempClient,List<ArrayList<Card>> tempList,List<Card> landlordCards,int firstActorPosition){
        String temp = "";
        Card tempCard;
        String[] colors = {"0","黑桃","红心","梅花","方块","王"};
        String[] values = {"0","A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        List<String> cardColors = Arrays.asList(colors);
        List<String> cardValues = Arrays.asList(values);
        //每个线程都要得到所有人的手牌信息，最好是按位置放
        for (ArrayList<Card> cards : tempList) {
            for (Card card : cards) {
                temp = temp.concat(cardColors.indexOf(card.getSuit()) + "-" + cardValues.indexOf(card.getRank())+"、");
            }
            temp = temp.concat(";");
        }//加入所有人的手牌，格式例子为"1-1、1-2、······;2-1、2-2、······;3-1、3-2、······;"
        //当客户端获取到该字符串的时候，就可以根据分号获取不同位置的人的手牌，根据顿号获取手牌数组
        //todo：这样会不会一次性传太多，一张牌大概需要8字节，总共就是两百多字节。如果太多，就分次发送
        for(Card card : landlordCards){
            tempCard = card;
            temp = temp.concat(cardColors.indexOf(tempCard.getSuit()) + "-" + cardValues.indexOf(tempCard.getRank())+"、"); //加入地主牌
        }
        temp = temp.concat(";"+firstActorPosition);
        tempClient.giveCards(temp);
    } //给单个线程发牌，客户端要根据位置来知晓谁得到了哪些牌

    public void giveScores(String string){
        int position = string.charAt(0) - '0';
        int score = string.charAt(2) - '0';
        //座位号-分数。要通知该座位外的两个人抢的分数
        for (int i = 0; i < clientThreads.length; i++) {
            if(clientThreads[i].position != position){
                clientThreads[i].giveScore(String.valueOf(score));
            }
        }
    }

    public void deletePlayer(ClientThread clientThread){
        int temp = clientThread.position;
        clientThreads[temp] = null;
        positionReady[temp] = false;
        playerSize--;
        System.out.println("该房间内的用户"+temp+"的线程已被剔除");
    }//当用户退出的时候，从房间中剔除该用户

    public void giveActionCardsToOthers(ClientThread client,String cards){
        for (ClientThread clientThread:clientThreads) {
            if(clientThread != client)
                clientThread.giveActionCardsToClient(cards);
        }
    }
    //可以在这个房间里调用每个线程类的方法，就可以修改每个线程里的from和to了
    public void recordDataBase(String string){
        String[] strings = string.split("-");
        int score = Integer.parseInt(strings[0]);//叫的分
        if(strings.length ==2){
            //一个人获胜，那就是地主
            int position = Integer.parseInt(strings[1]);
            clientThreads[position].updateScoreToDatabase(score*2); //地主获胜为叫的分*2
        }
        else if(strings.length == 3){
            //两个人获胜，那就是农民
            int position = Integer.parseInt(strings[1]);
            clientThreads[position].updateScoreToDatabase(score); //农民获胜为叫的分
            position = Integer.parseInt(strings[2]);
            clientThreads[position].updateScoreToDatabase(score);
        }
    }
}
