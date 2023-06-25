import java.util.Arrays;

public class OnlineTime extends Thread{
    boolean mustPlay = false;
    boolean isFirst = false;
    boolean isRun= false;
    OnlineLayout onlineLayout;
    int i;
    int lordScore= -1 ;
    public OnlineTime(OnlineLayout onlineLayout,int i){
        this.onlineLayout = onlineLayout;
        this.i = i;
    }
    public void run(){
        boolean isSetLandlord = false;
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(onlineLayout.playerNum != -1)    break;//使进程等待
        }
        System.out.println("当前玩家您的编号是"+onlineLayout.playerNum);
        System.out.println("当前房间人数为"+ (onlineLayout.playerNum+1));
        for (int j = 0; j < 3; j++) {
            if(j != onlineLayout.playerNum)
                onlineLayout.playerNames[j] = null;
        }
//        把出自己之外的名字置空
        onlineLayout.time[0].setText("未准备");
        onlineLayout.time[1].setText("未准备");
        onlineLayout.time[2].setText("未准备");
        while(true){//进入各玩家的准备阶段
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            进行一定时间的等待
            for (int j = 0; j < 3; j++) {
                    if(onlineLayout.playerNames[j] != null){
                        System.out.println(onlineLayout.playerNames[j]);
                    }
            }
//            打印各玩家的姓名
            if(onlineLayout.playerNames[(onlineLayout.playerNum-1 + 3)%3] != null){//有玩家进来
                onlineLayout.namesJText[0].setText(onlineLayout.playerNames[(onlineLayout.playerNum-1+3)%3]);
                onlineLayout.avatarLabel[0].setVisible(true);
                onlineLayout.namesJText[0].setVisible(true);
            }
            if(onlineLayout.playerNames[(onlineLayout.playerNum+1)%3] != null){//有玩家进来
                onlineLayout.namesJText[2].setText(onlineLayout.playerNames[(onlineLayout.playerNum+1)%3]);
                onlineLayout.avatarLabel[2].setVisible(true);
                onlineLayout.namesJText[2].setVisible(true);
            }
//            检测是否有玩家进入，若有玩家进入则显示其名字以及头像

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
            System.out.println("玩家0的准备状态"+ onlineLayout.preFlag[0]);
            System.out.println("玩家1的准备状态"+ onlineLayout.preFlag[1]);
            System.out.println("玩家2的准备状态"+ onlineLayout.preFlag[2]);
            if(onlineLayout.preFlag[0]&&onlineLayout.preFlag[1]&&onlineLayout.preFlag[2]){
                break;//全部都准备好了
            }
        }
        System.out.println("各个玩家都准备好了，可以发牌");
        for(int i=0;i<3;i++){
            onlineLayout.time[i].setVisible(false);
        }

        while(true){
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1");
            if(onlineLayout.playerNum == 0){
                if(onlineLayout.player0CardsStr != null) break;
            }
            else if(onlineLayout.playerNum == 1){
                if(onlineLayout.player1CardsStr != null) break;
            }
            else{
                if(onlineLayout.player2CardsStr != null) break;
            }
//            如果本客户端拿到了自己的牌与地主牌，那么就可以退出循环
        }
        System.out.println("服务端发给了我牌");
        onlineLayout.CardsInit();//根据两个str数组来初始化所有的牌


//        客户端需要先随机一个priority值

//        for(int count=0;count<3;count++){
//            if((count+onlineLayout.priorityNum)%3 == onlineLayout.playerNum){//轮到自己了
//                for(int i=0;i<4;i++)
//                    onlineLayout.landlord[i].setVisible(true);//把四个按钮打开
//            }
//            while(i>-1 && isRun){
//                onlineLayout.time[(count+onlineLayout.priorityNum)%3].setText("倒计时:"+ i--);
//                second(1);//等一秒
//                if(gotScore() == true){//如果服务端发出该用户叫分信息则直接break
//                    isRun = false;
//                    onlineLayout.time[(count+onlineLayout.priorityNum)%3].setText(gotScore()+" 分");//显示几分
//                }
////                注意 本玩家的isRun的是在按钮处修改
//            }
//            if(i==-1){//正常终结，说明超时
//                onlineLayout.time[(count+onlineLayout.priorityNum)%3].setText("不 抢");
//            }
//            if((count+onlineLayout.priorityNum) % 3 == onlineLayout.playerNum){//是本客户端玩家在叫分
//                for(int i=0;i<4;i++)
//                    onlineLayout.landlord[i].setVisible(false);//把四个按钮关闭
//            }
//            if(isSetLandlord == true)   break;//如果有人直接抢地主，那就退出抢地主环节
//        }
//        int lordNum = getLordNum();//从 server 拿地主编号
//        for (SinglePoker card2 : onlineLayout.playerList[onlineLayout.playerNum])
//            card2.canClick = true;// 可被点击
//
//        if (onlineLayout.time[onlineLayout.playerNum].getText().equals("抢地主")) {
//            // 自己抢了地主 得到地主牌
//            onlineLayout.playerList[onlineLayout.playerNum].addAll(onlineLayout.lordList);
//            openlord(true);
//            second(2);// 等待五秒
//            Common.order(onlineLayout.playerList[onlineLayout.playerNum]);
//            Common.rePosition(onlineLayout, onlineLayout.playerList[onlineLayout.playerNum], 1);
//            //TODO Common仅支持OnePLayout的rePosition 需要添加
//            setlord(onlineLayout.playerNum);
//            mustPlay = true;//这是本玩家的牌权
//            isFirst = true;//这是玩家第一次出牌
//        }
//        else{
//            if(onlineLayout.time[(onlineLayout.playerNum+1)%3].getText().equals("抢地主")){
//
//            }
//            else{
//
//            }
//            // 电脑选地主
//            if (Common.getScore(onePLayout.playerList[0]) < Common
//                    .getScore(onePLayout.playerList[2])) {
//                onePLayout.time[2].setText("抢地主");
//                onePLayout.time[2].setVisible(true);
//                setlord(2);// 设定地主
//                openlord(true);//把地主牌翻开
//                second(3);
//                onePLayout.playerList[2].addAll(onePLayout.lordList);
//                Common.order(onePLayout.playerList[2]);
//                Common.rePosition(onePLayout, onePLayout.playerList[2], 2);
//                openlord(false);
//            }
//            else{
//                onePLayout.time[0].setText("抢地主");
//                onePLayout.time[0].setVisible(true);
//                setlord(0);// 设定地主
//                openlord(true);
//                second(3);
//                onePLayout.playerList[0].addAll(onePLayout.lordList);
//                Common.order(onePLayout.playerList[0]);
//                Common.rePosition(onePLayout, onePLayout.playerList[0], 0);
//                openlord(false);
//            }
//        }
    }
    public void second(int i){
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
