import java.io.*;
import java.net.Socket;

/**
 * @author seaside
 * 2023-05-13 20:38
 */
public class ClientConnectThread extends Thread{
    Background background; //该线程有时要获取图形化界面得到的信息，所以需要此对象。
    Socket clientSocket;
    BufferedReader bufferedReader;
    PrintWriter printWriter;
    GameLayout gameLayout;
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
        clientSocket = new Socket(hostIPAddress,serverPort);
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
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

        public String clientOperators(String from,Background background){
        String to = "";
        switch (from.charAt(0)){
            case '1': //”连接成功“
                to = getUserInform(background);
                break;
            case '3':
                to = getRoomChoice(from.substring(from.indexOf(':') + 1),background);
                break;
            case '5':
                if(background.roomChoice == 2)
                    to = getRoomChosen(from,background); //只有加入房间的情况才让用户选择房间号
                else{
                    background.roomID = from.charAt(2) - '0'; //其他情况就让房间号默认分配了
                    to = null;
                }
                break;
            case '7':
                //TODo：此时可以调用图形化界面显示游戏内部房间信息,第一位为房间号的长度n，后面n位为房间号，再后面若为9，说明房间没人
                int roomIDLength = from.charAt(2) - '0';
                String roomID = from.substring(3,3+roomIDLength);
                background.roomID = Integer.parseInt(roomID);
                gameLayout = background.changeToPlay(background);
                System.out.println("加入房间并显示当前房间内有多少人");
                to = null;
                break;
            case '8':
                //TODo：此时可以调用图形化界面显示有人加入房间到position位置
                System.out.println("有新用户加入房间");
                to = null;
                break;
            case 'a':
                //todo:此时可以调用图形化界面显示position位置的人准备就绪
                System.out.println("有人准备就绪");
                to = null;
                break;
            case 'b':
                getCards(from.substring(from.indexOf(':')+1));
                break;
        }
        return to;
    }//根据收到的字符串的第一位执行相应操作

    public String getUserInform(Background background){
        String to = "2:";
        int choice = -1;  //若用户选择注册，则为0；若用户选择登录，则为1；若用户选择用户列表，则为2；
        String username = "";
        String password = "";
        while(true){
            try {
                Thread.sleep(10); //让主线程停顿，使得能够接收background线程中值的变化
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            username = background.getPlayerName();
            password = background.getPassword();
            choice = background.getChoice();
            if(choice == 2 || (choice != -1 && !username.equals("") && !password.equals("") ))
                break;
        }
        return to.concat(choice + username + "-" + password);
    }//返回用户信息

    public String getRoomChoice(String all,Background background){
        String to = "";
        if(all.equals("注册成功")){
            background.setChoice(-1);
            background.isOK = 0;
            to = getUserInform(background); //注册成功后还得让用户登录一遍
            background.isOK = -1; //isOK的值也要变回去，让图形化界面不会出错
        }
        else if(all.equals("登录成功")){
            background.isOK = 0;
            while(background.roomChoice == -1){
                try
                {
                    Thread.sleep(10);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            to = "4:"+ background.roomChoice;
        } else if (all.equals("用户列表")) {
            background.setChoice(-1);
            to = getUserInform(background); //让用户再次操作
            //todo:用户列表就给图形化一个string数组吧,然后还是进行getUserInform？
        } else {
            background.setChoice(-1);
            background.isOK = 1;
            to = getUserInform(background); //失败后还得让用户再次操作
            background.isOK = -1; //isOK的值也要变回去，让图形化界面不会出错
        }
        return to;
    }

    public static String getRoomChosen(String from,Background background){
        String chosenRoom = "6:";
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
            if(background.roomID != -1)
                break;
        }
        chosenRoom = chosenRoom.concat(String.valueOf(background.roomID));
        return chosenRoom;
    }

    public void playerReady(){
        //todo:可能还得while循环不断读取GameLayOut界面的准备就绪标志
        String to = "9:";
        printWriter.println(to);
    }//若有人点击了准备就绪按钮，则告知服务端该用户准备就绪

    public void getCards(String cards){
        //格式例子为"黑桃A、红桃2、······;黑桃A、红桃2、······;黑桃A、红桃2、······;地主牌"
        int firstSemicolon = cards.indexOf(';');
        int secondSemicolon = cards.indexOf(';',firstSemicolon);
        int thirdSemicolon = cards.indexOf(';',secondSemicolon);
        String position0CardsString = cards.substring(0,firstSemicolon);
        String[] position0Cards = position0CardsString.split("、");
        String position1CardsString = cards.substring(firstSemicolon+1,secondSemicolon);
        String[] position1Cards = position1CardsString.split("、");
        String position2CardsString = cards.substring(secondSemicolon+1,thirdSemicolon);
        String[] position2Cards = position2CardsString.split("、");
        String landlordCardsString = cards.substring(thirdSemicolon+1);
        String[] landlordCards = landlordCardsString.split("、");
        //发牌只需要发某个人的牌和其他人的牌数就行了
    }
}
