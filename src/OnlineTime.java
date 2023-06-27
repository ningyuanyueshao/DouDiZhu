import java.util.Arrays;

public class OnlineTime extends Thread{
    boolean mustPlay = false;
    boolean isFirst = false;
    boolean isRun= false;
    int lordIndex = -1;//地主的下标
    int[] score = new int[3];//三个人所叫的分数 每个人的玩家编号对应数组下标
//    todo 每个client端叫分后，server就要修改其他两个client的score数组

    OnlineLayout onlineLayout;
    int timeLeft;

    public OnlineTime(OnlineLayout onlineLayout,int timeLeft){
        this.onlineLayout = onlineLayout;
        this.timeLeft = timeLeft;
    }
    public void run(){
        waitForPlayNum();
        initData();
        beginPrepare();//进入各玩家的准备阶段,直到三个玩家均进入房间 并且 每个玩家都准备了 就结束该函数
        beginGetPokers();//从server端接受牌
        onlineLayout.CardsInit();//先初始化cards数组
        onlineLayout.addCardsToList();//把cards添加到每个玩家的手牌List 以及 地主牌List中去
        onlineLayout.setLocationAndZorder();
        callPoints();//叫分环节
//        allocateLord();//分配地主

    }
    public void second(int i){
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void waitForPlayNum(){
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(onlineLayout.playerNum != -1)    break;//使进程等待.直到系统给了client 的playerNum值
        }
    }
    public void initData(){
        System.out.println("当前玩家您的编号是"+onlineLayout.playerNum);
        System.out.println("当前房间人数为"+ (onlineLayout.playerNum+1));

//        把出自己之外的名字的 String 置空
//        并且把其余三个置为未准备状态
//        todo 要与后端交换信息，（bug待修复：当玩家进入时已经有其他玩家准备了，client无法获知）
        onlineLayout.time[0].setText("未准备");
        onlineLayout.time[1].setText("未准备");
        onlineLayout.time[2].setText("未准备");

        score[0] = -1;
        score[1] = -1;
        score[2] = -1;
    }
    public void beginPrepare(){//进入各玩家的准备阶段
        while(true){
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            进行一定时间的等待
            System.out.println("------------");
            System.out.println("输出现在该时刻 除自己以外的其他玩家的姓名");
            for (int j = 0; j < 3; j++) {
                if(onlineLayout.playerNames[j] != null){
                    System.out.println(onlineLayout.playerNames[j]);
                }
            }
            if(onlineLayout.playerNames[(onlineLayout.playerNum-1 + 3)%3] != null){//左侧（本client视角）有玩家进来
                onlineLayout.namesJText[0].setText(onlineLayout.playerNames[(onlineLayout.playerNum-1+3)%3]);
                onlineLayout.avatarLabel[0].setVisible(true);
                onlineLayout.namesJText[0].setVisible(true);
//                todo 显示头像
            }
            if(onlineLayout.playerNames[(onlineLayout.playerNum+1)%3] != null){//右侧（本client视角）有玩家进来
                onlineLayout.namesJText[2].setText(onlineLayout.playerNames[(onlineLayout.playerNum+1)%3]);
                onlineLayout.avatarLabel[2].setVisible(true);
                onlineLayout.namesJText[2].setVisible(true);
//                todo 显示头像
            }
//      据自己的playerNum不同，去讨论 preFlag为true时，对应哪个time元素需要被写出“已准备”
            if(onlineLayout.preFlag[0]){//0号的玩家准备好
                if(onlineLayout.playerNum == 0){
                    onlineLayout.time[1].setText("已准备");
                    onlineLayout.time[1].setVisible(true);
                }else if(onlineLayout.playerNum == 1) {
                    onlineLayout.time[0].setText("已准备");
                    onlineLayout.time[0].setVisible(true);
                }else{
                    onlineLayout.time[2].setText("已准备");
                    onlineLayout.time[2].setVisible(true);
                }
            }
            if(onlineLayout.preFlag[1]){//1号的玩家准备好
                if(onlineLayout.playerNum == 0){
                    onlineLayout.time[2].setText("已准备");
                    onlineLayout.time[2].setVisible(true);
                }else if(onlineLayout.playerNum == 1) {
                    onlineLayout.time[1].setText("已准备");
                    onlineLayout.time[1].setVisible(true);
                }else{
                    onlineLayout.time[0].setText("已准备");
                    onlineLayout.time[0].setVisible(true);
                }
            }
            if(onlineLayout.preFlag[2]){//2号
                if(onlineLayout.playerNum == 0){
                    onlineLayout.time[0].setText("已准备");
                    onlineLayout.time[0].setVisible(true);
                }else if(onlineLayout.playerNum == 1) {
                    onlineLayout.time[2].setText("已准备");
                    onlineLayout.time[2].setVisible(true);
                }else{
                    onlineLayout.time[1].setText("已准备");
                    onlineLayout.time[1].setVisible(true);
                }
            }
//            检测是否玩家的准备，若准备好则显示已准备字样
//            System.out.println("玩家0的准备状态"+ onlineLayout.preFlag[0]);
//            System.out.println("玩家1的准备状态"+ onlineLayout.preFlag[1]);
//            System.out.println("玩家2的准备状态"+ onlineLayout.preFlag[2]);

            if(onlineLayout.preFlag[0]&&onlineLayout.preFlag[1]&&onlineLayout.preFlag[2]){
                break;//全部都准备好了
            }
        }
        System.out.println("各个玩家都准备好了，可以发牌");
        for(int i=0;i<3;i++){
            onlineLayout.time[i].setVisible(false);
        }
    }
    public void beginGetPokers(){
        while(true){
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(onlineLayout.playerNum == 0){
                if(onlineLayout.player0CardsStr != null) {
                    for (int j = 0; j < 17; j++) {
                        System.out.println(onlineLayout.player0CardsStr[j]);
                    }
                    break;
                }
            }
            else if(onlineLayout.playerNum == 1){
                if(onlineLayout.player1CardsStr != null) {
                    for (int j = 0; j < 17; j++) {
                        System.out.println(onlineLayout.player1CardsStr[j]);
                    }
                    break;
                }
            }
            else{
                if(onlineLayout.player2CardsStr != null) {
                    for (int j = 0; j < 17; j++) {
                        System.out.println(onlineLayout.player2CardsStr[j]);
                    }
                    break;
                }
            }
            System.out.println("服务器还没有发给我牌");
//            如果本客户端拿到了自己的牌与地主牌，那么就可以退出循环
        }
        System.out.println("客户端收到了牌string");
    }
    public void callPoints(){
        while(onlineLayout.priorityNum == -1){//若priority不为-1则退出循环
            second(1);
        }
        System.out.println("priorityNum是"+onlineLayout.priorityNum);
        for (int j = 0; j < 3; j++) {
            timeLeft = 10;//叫分计时为10秒
            if(onlineLayout.priorityNum == onlineLayout.playerNum){
                //显现四个按钮
                for(int i=0;i<4;i++)
                    onlineLayout.landlord[i].setVisible(true);//把四个按钮打开
                while(timeLeft>=0) {
                    second(1);
                    if(score[onlineLayout.priorityNum] != -1){
                        //通知服务端
                        for(int i=0;i<4;i++)
                            onlineLayout.landlord[i].setVisible(false);
                        System.out.println("本客户端叫了"+score[onlineLayout.priorityNum]);
                        onlineLayout.printWriter.println("c:"+onlineLayout.playerNum+"-"+score[onlineLayout.priorityNum]);
                        break;
                    }
                    else{
                        showTimeText(onlineLayout.priorityNum);
                    }
                }
                if(timeLeft<0){
                    score[onlineLayout.priorityNum] = 0;
                    System.out.println("已超时");
                    onlineLayout.printWriter.println("c:"+onlineLayout.playerNum+"-"+score[onlineLayout.priorityNum]);
                }
            }
            else{
                while(timeLeft>=0){
                    second(1);
                    if(score[onlineLayout.priorityNum] != -1){
                        //当前该叫分的人叫分了
                        System.out.println("玩家编号为"+onlineLayout.priorityNum+"的人叫了"+score[onlineLayout.priorityNum]);
                        showScoreText(onlineLayout.priorityNum);
                        break;
                        //onlineLayout.time[onlineLayout.priorityNum].setText(String.valueOf(score[onlineLayout.priorityNum]));
                    }
                    else{
                        showTimeText(onlineLayout.priorityNum);
                    }
                }

                if(timeLeft<0){ //超时，当前行动玩家的分数被置为0
                    score[onlineLayout.priorityNum] = 0;
                    showScoreText(onlineLayout.priorityNum);
                }
            }
            if(score[onlineLayout.priorityNum] == 3){
                //有人叫三分,onlineLayout.priorityNum不能变
                break;
            }
            else{
                onlineLayout.priorityNum = (onlineLayout.priorityNum+1 )%3;
                //没人叫三分，进入下一个循环
            }
        }



        /*while(onlineLayout.priorityNum == -1){//若priority不为-1则退出循环
            second(1);
        }
        int currentIndex = -1;//当前叫分的玩家下标
        for(int count=0;count<3;count++){
            if(lordIndex != -1){//地主是否已经确定 也就是是否有人直接叫了三分
                return;
            }
            currentIndex = (count+onlineLayout.priorityNum)%3;//当前叫分的玩家下标 priorityNum是server端给
            if(currentIndex == onlineLayout.playerNum){//轮到自己了
                for(int i=0;i<4;i++)
                    onlineLayout.landlord[i].setVisible(true);//把四个按钮打开
            }
            i=10;//每个人叫分有十秒时间
            isRun = true;
            while(i>-1 && isRun){
                showTimeText(currentIndex);//每一秒钟展现该玩家面前的time的text文字 在此函数中i--
                second(1);//等一秒
                showScoreText(currentIndex);//如果叫分了，展现该玩家面前的time的分数
            }
//            叫完分之后
            if(score[onlineLayout.playerNum]!=-1){//本客户端玩家叫完分
                for(int i=0;i<4;i++)
                    onlineLayout.landlord[i].setVisible(false);//把四个按钮关闭
                //todo 把分数值传给server
            }
            if(i==-1){//说明超时
                showNoNeedText(currentIndex);
            }
            else{//非正常终结 说明是有叫分
                if(score[onlineLayout.playerNum] == 3){//叫了三分
                    lordIndex = onlineLayout.playerNum;
                    for (int j = 0; j < 4; j++) {
                        onlineLayout.landlord[j].setVisible(false);//打分按钮全部不可见 因为已经分配了地主 该客户端叫分已经没有意义
                    }
                    isRun = false;
                }
                else{//叫了其他分数
                    onlineLayout.landlord[score[onlineLayout.playerNum]].setVisible(false);//不让这个按钮可见 不能打相同的分
                }
            }
        }
        */
    }
    public void allocateLord(){
        if(score[0] > score[1] && score[0] > score [2]){
            lordIndex = 0;
        }
        else if(score[1] > score[0] && score[1] > score [2]){
            lordIndex = 1;
        }
        else if(score[2] > score[0] && score[2] > score [1]){
            lordIndex = 2;
        }
    }
    public void showTimeText(int currentIndex){
        if(currentIndex == 0){//如果是轮到0号
            if(onlineLayout.playerNum == 0){
                onlineLayout.time[1].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[1].setVisible(true);
            }
            else if(onlineLayout.playerNum == 1){
                onlineLayout.time[0].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[0].setVisible(true);
            }
            else {
                onlineLayout.time[2].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[2].setVisible(true);
            }
        }
        else if(currentIndex == 1){
            if(onlineLayout.playerNum == 0){
                onlineLayout.time[2].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[2].setVisible(true);
            }
            else if(onlineLayout.playerNum == 1){
                onlineLayout.time[1].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[1].setVisible(true);
            }
            else {
                onlineLayout.time[0].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[0].setVisible(true);
            }
        }
        else{//轮到2号在叫分
            if(onlineLayout.playerNum == 0){
                onlineLayout.time[0].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[0].setVisible(true);
            }
            else if(onlineLayout.playerNum == 1){
                onlineLayout.time[2].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[2].setVisible(true);
            }
            else {
                onlineLayout.time[1].setText("倒计时:"+ timeLeft--);
                onlineLayout.time[1].setVisible(true);
            }
        }
    }
    public void showScoreText(int currentIndex) {
            if (currentIndex == 0) {
                if (onlineLayout.playerNum == 0) {
                    onlineLayout.time[1].setText(score[currentIndex] + " 分");
                    onlineLayout.time[1].setVisible(true);
                } else if (onlineLayout.playerNum == 1) {
                    onlineLayout.time[0].setText(score[currentIndex] + " 分");
                    onlineLayout.time[0].setVisible(true);
                } else {
                    onlineLayout.time[2].setText(score[currentIndex] + " 分");
                    onlineLayout.time[2].setVisible(true);
                }
            } else if (currentIndex == 1) {
                if (onlineLayout.playerNum == 0) {
                    onlineLayout.time[2].setText(score[currentIndex] + " 分");
                    onlineLayout.time[2].setVisible(true);
                } else if (onlineLayout.playerNum == 1) {
                    onlineLayout.time[1].setText(score[currentIndex] + " 分");
                    onlineLayout.time[1].setVisible(true);
                } else {
                    onlineLayout.time[0].setText(score[currentIndex] + " 分");
                    onlineLayout.time[0].setVisible(true);
                }
            } else {//currentIndex 为2
                if (onlineLayout.playerNum == 0) {
                    onlineLayout.time[0].setText(score[currentIndex] + " 分");
                    onlineLayout.time[0].setVisible(true);
                } else if (onlineLayout.playerNum == 1) {
                    onlineLayout.time[2].setText(score[currentIndex] + " 分");
                    onlineLayout.time[2].setVisible(true);
                } else {
                    onlineLayout.time[0].setText(score[currentIndex] + " 分");
                    onlineLayout.time[0].setVisible(true);
                }
            }
    }
    public void showNoNeedText(int currentIndex){
        if(currentIndex == 0){
            if(onlineLayout.playerNum == 0){
                onlineLayout.time[1].setText("不 抢");
            }
            else if(onlineLayout.playerNum == 1){
                onlineLayout.time[0].setText("不 抢");
            }
            else {
                onlineLayout.time[2].setText("不 抢");
            }
        }
        else if(currentIndex == 1){
            if(onlineLayout.playerNum == 0){
                onlineLayout.time[2].setText("不 抢");
            }
            else if(onlineLayout.playerNum == 1){
                onlineLayout.time[1].setText("不 抢");
            }
            else {
                onlineLayout.time[0].setText("不 抢");
            }
        }
        else{//currentIndex 为2
            if(onlineLayout.playerNum == 0){
                onlineLayout.time[0].setText("不 抢");
            }
            else if(onlineLayout.playerNum == 1){
                onlineLayout.time[2].setText("不 抢");
            }
            else {
                onlineLayout.time[0].setText("不 抢");
            }
        }
    }
}
