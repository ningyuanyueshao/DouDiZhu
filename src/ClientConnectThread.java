import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * @author seaside
 * 2023-05-13 20:38
 */
public class ClientConnectThread extends Thread{
    SetupLayout setupLayout;//该线程有时要获取图形化界面得到的信息，所以需要此对象。
    Socket clientSocket;
    BufferedReader bufferedReader;
    PrintWriter printWriter;
    Frame frame;
    //Online
    OnlineLayout onlineLayout;
    ChatInviteFrame mainChatInviteFrame;
    @Override
    public void run() {
        try {
            connectedPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //代码中出现的while和wait的结合都是在等待消息传递，即线程通信
    public ClientConnectThread(SetupLayout setupLayout,Frame frame) {
        this.setupLayout = setupLayout;
        this.frame = frame;
    }

    public void connectedPlay()throws IOException {
        //若用户选择了在线游戏，再进行连接
        //需要主机IP地址和端口
        String hostIPAddress = "10.28.159.5"; //这个是打开wifi的属性中得到的
        int serverPort = 8080;
        System.out.println("该客户端开始连接服务器");
        clientSocket = new Socket(hostIPAddress,serverPort);
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
        setupLayout.printWriter = printWriter;//让窗口也能向服务端发消息
        System.out.println("连接成功，等待用户操作");
        setupLayout.wantGetConnected2 = true;
        String from = "";
        String to = "";
        try{
            //TODO 现在是有收才有发，最好是没收也能发,有收可以不发
            while((from = bufferedReader.readLine())!=null){//在这里使用输入输出流与服务器端进行交互。
                //TODO :和服务器的主要操作应该都在这里面
                System.out.println("得到服务器端给的信息为"+ from);
                //根据交互信息的类型进行不同的操作
                to = clientOperators(from,setupLayout);
                if(to == null)
                    continue; //若为空，说明不用发，就实现有收可以不发
                printWriter.println(to);

                System.out.println("客户端返回的信息为"+ to);
            }
        } catch (Exception e) {
            System.out.println("连接中断或出错");
        }
    }

    public String clientOperators(String from,SetupLayout setupLayout){
        String to = "";
        switch (from.charAt(0)){
            case '1': //”连接成功“
                to = getUserInform(setupLayout);
                break;
            case '3':
                to = getRoomChoice(from.substring(from.indexOf(':') + 1),setupLayout);
                break;
            case '5':
                if(setupLayout.roomChoice == 2)
                    to = getRoomChosen(from,setupLayout); //只有加入房间的情况才让用户选择房间号
                else{
                    setupLayout.roomID = from.charAt(2) - '0'; //其他情况就让房间号默认分配了
                    to = null;
                }
                break;
            case '7':
                //此时调用图形化界面显示游戏内部房间信息,第一位为房间号的长度n，后面n位为房间号，再后面若为9，说明房间没人
                showPlayRoom(from);
                to = null;
                break;
            case '8':
                String[] strings1 = from.substring(2).split("-");
                int newPlayerPosition = Integer.parseInt(strings1[0]);
                frame.onlineLayout.playerNames[newPlayerPosition] = strings1[1];
                System.out.println("有新用户加入房间");
                to = null;
                break;
            case 'a':
                givePrepareToFrame(from.charAt(2));
                System.out.println("有人准备就绪");
                to = null;
                break;//
            case 'b':
                getCards(from.substring(from.indexOf(':')+1));
                to = null;
                break;
            case 'd':
                giveScoreToFrame(from.charAt(2));
                to=null;
                break;
            case 'p':
                giveInviteMessageToWindow(from.substring(from.indexOf(':')+1));
                to = null;
                break;
            case 't':
                giveChatItemsToWindow(from.substring(from.indexOf(':')+1));
                to = null;
                break;
            case 'v':
                giveAllUsernamesToFrame(from.substring(from.indexOf(':')+1));
                to = null;
                break;
        }
        return to;
    }//根据收到的字符串的第一位执行相应操作

    public String getUserInform(SetupLayout setupLayout){
        String to = "2:";
        int choice = -1;  //若用户选择注册，则为0；若用户选择登录，则为1；若用户选择用户列表，则为2；
        String username = "";
        String password = "";
        while(true){
            /*synchronized (password){
                while(true){
                    password.wait();
                }
            }*/
            try {
                Thread.sleep(10); //让主线程停顿，使得能够接收setupLayout线程中值的变化
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            username = setupLayout.getPlayerName();
            password = setupLayout.getPassword();
            choice = setupLayout.getChoice();
            if(choice == 2 || (choice != -1 && !username.equals("") && !password.equals("") ))
                break;
        }
        return to.concat(choice + username + "-" + password);
    }//返回用户信息

    public String getRoomChoice(String all,SetupLayout setupLayout){
        String to = "";
        if(all.equals("注册成功")){
            setupLayout.setChoice(-1);
            setupLayout.isOK = 0;
            to = getUserInform(setupLayout); //注册成功后还得让用户登录一遍
            setupLayout.isOK = -1; //isOK的值也要变回去，让图形化界面不会出错
        }
        else if(all.equals("登录成功")){
            setupLayout.isOK = 0;
            // 调用聊天和邀请窗口
            mainChatInviteFrame = new ChatInviteFrame(printWriter);
            mainChatInviteFrame.getInfo(this, setupLayout.getPlayerName(), -1);
            to = null;
        } else if (all.length() > 5 && all.substring(0,4).equals("用户列表")) {
            setupLayout.userNames = all.substring(4).split("-");
            System.out.println("修改用户"+all.substring(4).split("-"));
            setupLayout.setChoice(-1);
            to = getUserInform(setupLayout); //让用户再次操作
        } else {
            setupLayout.setChoice(-1);
            setupLayout.isOK = 1;
            to = getUserInform(setupLayout); //失败后还得让用户再次操作
            setupLayout.isOK = -1; //isOK的值也要变回去，让图形化界面不会出错
        }
        return to;
    }

    public static String getRoomChosen(String from,SetupLayout setupLayout){
        String chosenRoom = "6:";
        String temp = from.substring(from.indexOf(':')+1);
        String[] roomIDs = temp.split("-");
        int length = roomIDs.length;
        int[] rooms = new int[length];
        for (int i = 0; i < length; i++) {
            rooms[i] = Integer.parseInt(roomIDs[i]);
        }
        setupLayout.setRoomsCanPlay(rooms);
        while(true){
            try
            {
                Thread.sleep(10);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if(setupLayout.roomID != -1 || setupLayout.roomChoice == -1)
                break;
        }
        if(setupLayout.roomChoice == -1)
            return null;
        chosenRoom = chosenRoom.concat(String.valueOf(setupLayout.roomID));
        return chosenRoom;
    }

    public void showPlayRoom(String from){
        String string = from.substring(2);
        String[] strings = string.split("-");
        setupLayout.roomID = Integer.parseInt(strings[0]);
        mainChatInviteFrame.roomID = Integer.parseInt(strings[0]);
        frame.showOnlineLayout();//展现onlinePanel
        frame.onlineLayout.printWriter = printWriter;
        if(strings[1].equals("9")){
            frame.onlineLayout.playerNum = 0;//房间没人，第一个进去的座位为0；
            frame.onlineLayout.playerNames[1] = null;
            frame.onlineLayout.playerNames[2] = null;
        }
        else{
            if(strings.length == 2) {
                frame.onlineLayout.playerNum = 1;//房间内已经有一个人
                int index = strings[1].indexOf('、');
                frame.onlineLayout.playerNames[0] = strings[1].substring(0,index);
                frame.onlineLayout.playerNames[2] = null;
                if(strings[1].charAt(index+1) == '1')
                    frame.onlineLayout.preFlag[0] = true;
            }
            else if(strings.length == 3){
                frame.onlineLayout.playerNum = 2;//房间内已经有两个人
                int index = strings[1].indexOf('、');
                frame.onlineLayout.playerNames[0] = strings[1].substring(0,index);
                if(strings[1].charAt(index+1) == '1')
                    frame.onlineLayout.preFlag[0] = true;

                index = strings[2].indexOf('、');
                frame.onlineLayout.playerNames[1] = strings[2].substring(0,index);
                if(strings[2].charAt(index+1) == '1')
                    frame.onlineLayout.preFlag[1] = true;
            }
        }
        frame.onlineLayout.playerNames[frame.onlineLayout.playerNum] = setupLayout.getPlayerName();
        System.out.println("加入房间并显示当前房间内有多少人");
    }
    public void getCards(String cards) {
        //格式例子为"1-1、2-2、······;2-1、3-4、······;1-1、2-2、······;地主牌"
        String[] ss = cards.split(";");
        String[] position0Cards = ss[0].split("、");
        String[] position1Cards = ss[1].split("、");
        String[] position2Cards = ss[2].split("、");
        String[] landlordCards = ss[3].split("、");
        if(frame.onlineLayout.playerNum == 0){
            frame.onlineLayout.player0CardsStr = position0Cards;
        }
        else if(frame.onlineLayout.playerNum == 1){
            frame.onlineLayout.player1CardsStr = position1Cards;
        }
        else {
            frame.onlineLayout.player2CardsStr = position2Cards;
        }
        frame.onlineLayout.lordCardsStr = landlordCards;

    }

    public void giveChatItemsToServer(String username,String items){
        String to = "s:"+setupLayout.getPlayerName() + "-" +username+"-" + items;
        System.out.println("客户端要给"+username+"的信息为"+items);
        printWriter.println(to);
    }

    public void giveInviteMessageToWindow(String string) {
        System.out.println("服务器给的信息为" + string);
        String[] str = string.split("-");
        // 创建对话框
        int choice = JOptionPane.showConfirmDialog(null, str[0]+"邀请您进入"+str[1]+"号房间，是否加入？", "邀请", JOptionPane.YES_NO_OPTION);
        // 处理用户选择
        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("用户选择加入房间");
            setupLayout.roomID = Integer.parseInt(str[1]);
            mainChatInviteFrame.roomID = Integer.parseInt(str[1]);
            printWriter.println("r:同意");
            //todo: 显示OnlinePanel
        } else {
            System.out.println("用户选择不加入房间");
            printWriter.println("r:拒绝");
        }
    }

    public void giveChatItemsToWindow(String all){
        String fromUsername = all.substring(0,all.indexOf('-'));
        String items = all.substring(all.indexOf('-')+1);
        mainChatInviteFrame.receiveMessage(fromUsername, items);
    }

    public void giveAllUsernamesToFrame(String all){
        String[] users = all.split(";");
        mainChatInviteFrame.allUsernames = users;
    }

    public void givePrepareToFrame(char positionChar){
        switch (positionChar){
            case'0':
                frame.onlineLayout.preFlag[0] = true;
                break;
            case'1':
                frame.onlineLayout.preFlag[1] = true;
                break;
            case'2':
                frame.onlineLayout.preFlag[2] = true;
                break;
        }
    }
    public void giveScoreToFrame(char score){
        switch (score){
            case '0':
                frame.onlineLayout.onlineTime.score[frame.onlineLayout.priorityNum] = 0;
                break;
            case '1':
                frame.onlineLayout.onlineTime.score[frame.onlineLayout.priorityNum] = 1;
                break;
            case '2':
                frame.onlineLayout.onlineTime.score[frame.onlineLayout.priorityNum] = 2;
                break;
            case '3':
                frame.onlineLayout.onlineTime.score[frame.onlineLayout.priorityNum] = 3;
                break;
        }
    }
}
