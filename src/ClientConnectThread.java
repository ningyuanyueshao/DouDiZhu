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
    //Online
    GameLayout gameLayout;
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
    public ClientConnectThread(SetupLayout setupLayout) {
        this.setupLayout = setupLayout;
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
        System.out.println("连接成功，等待用户操作");
        String from = "";
        String to = "";
        try{
            //TODO 现在是有收才有发，最好是没收也能发,有收可以不发
            while((from = bufferedReader.readLine())!=null){//在这里使用输入输出流与服务器端进行交互。
                //TODO:和服务器的主要操作应该都在这里面
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
                //TODo：此时可以调用图形化界面显示游戏内部房间信息,第一位为房间号的长度n，后面n位为房间号，再后面若为9，说明房间没人
                int roomIDLength = from.charAt(2) - '0';
                String roomID = from.substring(3,3+roomIDLength);
                setupLayout.roomID = Integer.parseInt(roomID);
//                TODO 显示OnlinePanel
//                gameLayout = setupLayout.changeToPlay(setupLayout);
                System.out.println("加入房间并显示当前房间内有多少人");
                to = null;
                // 调用聊天和邀请窗口
                mainChatInviteFrame = new ChatInviteFrame(printWriter);
                mainChatInviteFrame.getInfo(this, setupLayout.getPlayerName(), Integer.parseInt(roomID));
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
            while(setupLayout.roomChoice == -1){
                try
                {
                    Thread.sleep(10);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            to = "4:"+ setupLayout.roomChoice;
        } else if (all.equals("用户列表")) {
            setupLayout.setChoice(-1);
            to = getUserInform(setupLayout); //让用户再次操作
            //todo:用户列表就给图形化一个string数组吧,然后还是进行getUserInform？
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
        int length = temp.length();
        int[] rooms = new int[length];
        for (int i = 0; i < length; i++) {
            rooms[i] = temp.charAt(i) - '0';
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
            if(setupLayout.roomID != -1)
                break;
        }
        chosenRoom = chosenRoom.concat(String.valueOf(setupLayout.roomID));
        return chosenRoom;
    }

    public void playerReady(){
        //todo:可能还得while循环不断读取GameLayOut界面的准备就绪标志
        String to = "9:";
        printWriter.println(to);
    }//若有人点击了准备就绪按钮，则告知服务端该用户准备就绪

    public void getCards(String cards) {
        //格式例子为"1-1、2-2、······;2-1、3-4、······;1-1、2-2、······;地主牌"
        int firstSemicolon = cards.indexOf(';');
        int secondSemicolon = cards.indexOf(';', firstSemicolon);
        int thirdSemicolon = cards.indexOf(';', secondSemicolon);
        String position0CardsString = cards.substring(0, firstSemicolon);
        String[] position0Cards = position0CardsString.split("、");
        String position1CardsString = cards.substring(firstSemicolon + 1, secondSemicolon);
        String[] position1Cards = position1CardsString.split("、");
        String position2CardsString = cards.substring(secondSemicolon + 1, thirdSemicolon);
        String[] position2Cards = position2CardsString.split("、");
        String landlordCardsString = cards.substring(thirdSemicolon + 1);
        String[] landlordCards = landlordCardsString.split("、");
        //public void getCards(String[] playerCards,String[] landlordCards){
        //
    //}
        //发牌只需要发某个人的牌和其他人的牌数就行了
    }

    public void giveChatItemsToServer(String username,String items){
        String to = "s:"+setupLayout.getPlayerName() + "-" +username+"-" + items;
        System.out.println("客户端要给"+username+"的信息为"+items);
        printWriter.println(to);
    }

    public void giveChatItemsToWindow(String all){
        String fromUsername = all.substring(0,all.indexOf('-'));
        String items = all.substring(all.indexOf('-')+1);
        //todo:和聊天窗口衔接
        mainChatInviteFrame.receiveMessage(fromUsername, items);
    }

    public void giveAllUsernamesToFrame(String all){
        String[] users = all.split(";");
        int length = users.length;
        String[] allUsernames = new String[length-1];
        int k = 0;
        String username = setupLayout.getPlayerName();
        for (int i = 0; i < length; i++) {
            if(users[i].equals(username)){
                continue;//把自己给剔除掉
            }
            allUsernames[k] = users[i];
        }
        mainChatInviteFrame.allUsernames = allUsernames;
    }

}
