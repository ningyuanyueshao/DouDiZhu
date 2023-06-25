import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Time extends Thread{
    boolean mustPlay = false;// 是否是本人的牌权呢？
    boolean isFirst = false;// 如果玩家当地主且这次是第一手出牌 则为true
    OnePLayout onePLayout;
    boolean isRun = true;
    int i = 10;
    
    public Time(OnePLayout onePLayout, int i) {
        this.onePLayout = onePLayout;
        this.i = i;
    }
    
    public void run() {//自动执行run方法
        while(i>-1 && isRun){
            System.out.println("1");
            onePLayout.time[1].setText("倒计时:"+ i--);
            second(1);//等一秒
        }
        if(i == -1){//正常终结，说明超时
            onePLayout.time[1].setText("不抢");
        }
        onePLayout.landlord[0].setVisible(false);
        onePLayout.landlord[1].setVisible(false);
        for (SinglePoker card2 : onePLayout.playerList[1])
            card2.canClick = true;// 可被点击 但是点击牌前移动画还没做

        if (onePLayout.time[1].getText().equals("抢地主")) {
            // 得到地主牌
            onePLayout.playerList[1].addAll(onePLayout.lordList);
            openlord(true);
            second(2); // 等待五秒
            Common.order(onePLayout.playerList[1]);
            Common.rePosition(onePLayout, onePLayout.playerList[1], 1);
            setlord(1);
            mustPlay = true; // 这是玩家的牌权
            isFirst = true; // 这是玩家第一次出牌
        }
        else{
            // 电脑选地主
            if (Common.getScore(onePLayout.playerList[0]) < Common
                    .getScore(onePLayout.playerList[2])) {
                onePLayout.time[2].setText("抢地主");
                onePLayout.time[2].setVisible(true);
                setlord(2); // 设定地主
                openlord(true); // 把地主牌翻开
                second(3);
                onePLayout.playerList[2].addAll(onePLayout.lordList);
                Common.order(onePLayout.playerList[2]);
                Common.rePosition(onePLayout, onePLayout.playerList[2], 2);
                openlord(false);
            }
            else{
                onePLayout.time[0].setText("抢地主");
                onePLayout.time[0].setVisible(true);
                setlord(0);// 设定地主
                openlord(true);
                second(3);
                onePLayout.playerList[0].addAll(onePLayout.lordList);
                Common.order(onePLayout.playerList[0]);
                Common.rePosition(onePLayout, onePLayout.playerList[0], 0);
                openlord(false);
            }
        }
        turnOn(false);
        for (int i = 0; i < 3; i++)
        {
            onePLayout.time[i].setText("不要");
            onePLayout.time[i].setVisible(false);
        }
        onePLayout.turn = onePLayout.dizhuFlag;
        while(true){
            if(onePLayout.turn==1) //我
            {
                if(onePLayout.time[2].getText().equals("不要") && onePLayout.time[0].getText().equals("不要")){//说明这是自己的牌权
                    mustPlay = true;
                }
                else{
                    mustPlay = false;
                }
                if(isFirst == true) {
                    mustPlay = true;
                    isFirst = false;
                }
                turnOn(true);// 出牌按钮 --我出牌
                timeWait(30, 1);// 我自己的定时器
                System.out.println("我出牌");
                turnOn(false);//选完关闭出牌按钮
                onePLayout.turn=(onePLayout.turn+1)%3;
                if(win())//判断输赢
                    break;
            }
            if (onePLayout.turn==0)
            {
                computer0();
                onePLayout.turn=(onePLayout.turn+1)%3;
                if(win())//判断输赢
                    break;
            }
            if(onePLayout.turn==2)
            {
                computer2();
                onePLayout.turn=(onePLayout.turn+1)%3;
                if(win())//判断输赢
                    break;
            }
        }
    }
    
    public void second(int i){
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void openlord(boolean is) {
        for (int i = 0; i < 3; i++) {
            if (is) {
                onePLayout.lordListCopy.get(i).turnFront();
                onePLayout.lordList.get(i).turnFront(); // 地主牌翻看
            }
            else {
                onePLayout.lordListCopy.get(i).turnRear();
                onePLayout.lordList.get(i).turnRear(); // 地主牌闭合
            }
            onePLayout.lordList.get(i).canClick = true;// 可被点击
        }
    }
    
    public void setlord(int i) {
        Point point = new Point();
        if (i == 1)// 我是地主
        {
            point.x = 300;
            point.y = 650;
            onePLayout.dizhuFlag = 1;// 设定地主
        }
        if (i == 0) {
            point.x = 150;
            point.y = 130;
            onePLayout.dizhuFlag = 0;
        }
        if (i == 2) {
            point.x = 1600;
            point.y = 130;
            onePLayout.dizhuFlag = 2;
        }
        onePLayout.dizhu.setLocation(point);
        onePLayout.dizhu.setVisible(true);
    }
    public void turnOn(boolean flag) {//打开出牌按钮
        onePLayout.publishCard[0].setVisible(flag);
        onePLayout.publishCard[1].setVisible(flag);//第一次出牌的话 不出按钮不可见
        if(mustPlay == true) onePLayout.publishCard[1].setVisible(false);

    }
    public void timeWait(int n, int player) {

        if (onePLayout.currentList[player].size() > 0)
            Common.hideCards(onePLayout.currentList[player]);
        if (player == 1)
            // 如果是我，n秒到后直接出最小的牌
            // 如果是自己的牌权，超时则必须要自己出牌
        {
            int i = n;
            while (onePLayout.nextPlayer == false && i >= 0) {
                // main.container.setComponentZOrder(main.time[player], 0);
                onePLayout.time[player].setText("倒计时:" + i);
                onePLayout.time[player].setVisible(true);
                second(1);
                i--;
            }
            if (i == -1) {
                onePLayout.time[1].setText("超时");
            }
            onePLayout.nextPlayer = false;
        }
        else {//如果是其他两家,则等n秒
            for (int i = n; i >= 0; i--) {
                second(1);
                // main.container.setComponentZOrder(main.time[player], 0);
                onePLayout.time[player].setText("倒计时:" + i);
                onePLayout.time[player].setVisible(true);
            }
        }
        onePLayout.time[player].setVisible(false);
    }
    public boolean win(){
        for(int i=0;i<3;i++){
            if(onePLayout.playerList[i].size()==0) {
                for(int j=0;j<3;j++){
                    for(int k=0;k<onePLayout.playerList[j].size();k++){
                        onePLayout.playerList[j].get(k).turnFront();
                    }
                }
                String s;
                if(i==1) {
                    s="恭喜你，胜利了!";
                }else {
                    s="恭喜电脑"+i+",赢了! 你的智商有待提高哦";
                }
                JOptionPane.showMessageDialog(onePLayout, s);
                return true;
            }
        }
        return false;
    }
    public void computer0() {
        timeWait(3, 0); // 定时
        ShowCard(0); // 出牌

    }
    public void computer2() {
        timeWait(3, 2); // 定时
        ShowCard(2); // 出牌

    }
    public void ShowCard(int role) {
        Model model = Model.getModel(onePLayout.playerList[role]);
        // 待走的牌
        java.util.List<String> list = new ArrayList<String>();
        // 如果是主动出牌 即自己的牌权
        if (onePLayout.time[(role + 1) % 3].getText().equals("不要")
                && onePLayout.time[(role + 2) % 3].getText().equals("不要")) {
            // 有单出单 (除开3带，飞机能带的单牌)
            if (model.a1.size() > (model.a111222.size() * 2 + model.a3.size())) {
                list.add(model.a1.get(model.a1.size() - 1));
            }// 有对子出对子 (除开3带，飞机)
            else if (model.a2.size() > (model.a111222.size() * 2 + model.a3
                    .size())) {
                list.add(model.a2.get(model.a2.size() - 1));
            }// 有顺子出顺子
            else if (model.a123.size() > 0) {
                list.add(model.a123.get(model.a123.size() - 1));
            }// 有3带就出3带，没有就出光3
            else if (model.a3.size() > 0) {
                // 3带单,且非关键时刻不能带王，2
                if (model.a1.size() > 0) {
                    list.add(model.a1.get(model.a1.size() - 1));
                }// 3带对
                else if (model.a2.size() > 0) {
                    list.add(model.a2.get(model.a2.size() - 1));
                }
                list.add(model.a3.get(model.a3.size() - 1));
            }// 有双顺出双顺
            else if (model.a112233.size() > 0) {
                list.add(model.a112233.get(model.a112233.size() - 1));
            }// 有飞机出飞机
            else if (model.a111222.size() > 0) {
                String name[] = model.a111222.get(0).split(",");
                // 带单
                if (name.length / 3 <= model.a1.size()) {
                    list.add(model.a111222.get(model.a111222.size() - 1));
                    for (int i = 0; i < name.length / 3; i++)
                        list.add(model.a1.get(i));
                } else if (name.length / 3 <= model.a2.size())// 带双
                {
                    list.add(model.a111222.get(model.a111222.size() - 1));
                    for (int i = 0; i < name.length / 3; i++)
                        list.add(model.a2.get(i));
                }
                // 有炸弹出炸弹
            } else if (model.a4.size() > 0) {
                // 4带2,1
                int sizea1 = model.a1.size();
                int sizea2 = model.a2.size();
                if (sizea1 >= 2) {
                    list.add(model.a1.get(sizea1 - 1));
                    list.add(model.a1.get(sizea1 - 2));
                    list.add(model.a4.get(0));

                } else if (sizea2 >= 2) {
                    list.add(model.a2.get(sizea1 - 1));
                    list.add(model.a2.get(sizea1 - 2));
                    list.add(model.a4.get(0));

                } else {// 直接炸
                    list.add(model.a4.get(0));
                }

            }
        }// 如果是跟牌
        else {
            java.util.List<SinglePoker> player = onePLayout.currentList[(role + 2) % 3].size() > 0
                    ? onePLayout.currentList[(role + 2) % 3]
                    : onePLayout.currentList[(role + 1) % 3];//当前出的牌

            CardType cType=Common.jugdeType(player);//桌面别人出的牌
            //如果是单牌
            if(cType==CardType.c1)
            {
                AI_1(model.a1, player, list, role);
            }//如果是对子
            else if(cType==CardType.c2)
            {
                AI_1(model.a2, player, list, role);
            }//3带
            else if(cType==CardType.c3)
            {
                AI_1(model.a3, player, list, role);
            }//炸弹
            else if(cType==CardType.c4)
            {
                AI_1(model.a4, player, list, role);
            }//如果是3带1
            else if(cType==CardType.c31){
                //偏家 涉及到拆牌
                //if((role+1)%3==main.dizhuFlag)
                AI_2(model.a3, model.a1, player, list, role);
            }//如果是3带2
            else if(cType==CardType.c32){
                //偏家
                //if((role+1)%3==main.dizhuFlag)
                AI_2(model.a3, model.a2, player, list, role);
            }//如果是4带11
            else if(cType==CardType.c411){
                AI_5(model.a4, model.a1, player, list, role);
            }
            //如果是4带22
            else if(cType==CardType.c422){
                AI_5(model.a4, model.a2, player, list, role);
            }
            //顺子
            else if(cType==CardType.c123){
                AI_3(model.a123, player, list, role);
            }
            //双顺
            else if(cType==CardType.c1122){
                AI_3(model.a112233, player, list, role);
            }
            //飞机带单
            else if(cType==CardType.c11122234){
                AI_4(model.a111222,model.a1, player, list, role);
            }
            //飞机带对
            else if(cType==CardType.c1112223344){
                AI_4(model.a111222,model.a2, player, list, role);
            }
            //炸弹
            if(list.size()==0)
            {
                int len4=model.a4.size();
                if(len4>0)
                    list.add(model.a4.get(len4-1));
            }
        }

        // 定位出牌
        onePLayout.currentList[role].clear();
        if (list.size() > 0) {
            Point point = new Point();
            if (role == 0)
                point.x = 500;
            if (role == 2)
                point.x = 1400;
            point.y = 400 - (list.size() + 1) * 15 / 2;// 屏幕中部
            // 将name转换成Card
            for (int i = 0, len = list.size(); i < len; i++) {
                List<SinglePoker> cards = getCardByName(onePLayout.playerList[role],
                        list.get(i));
                for (SinglePoker card : cards) {
                    Common.move(card, card.getLocation(), point,10);
                    point.y += 30;
                    onePLayout.currentList[role].add(card);
                    onePLayout.playerList[role].remove(card);
                }
            }
            Common.rePosition(onePLayout, onePLayout.playerList[role], role);
        } else {
            onePLayout.time[role].setVisible(true);
            onePLayout.time[role].setText("不要");
        }
        for(SinglePoker card:onePLayout.currentList[role])
            card.turnFront();
    }
    public void AI_1(List<String> model,List<SinglePoker> player,List<String> list,int role){
        // 顶家
        if((role+1)%3==onePLayout.dizhuFlag) {
            for(int i=0,len=model.size();i<len;i++) {
                if(getValueInt(model.get(i))>Common.getValue(player.get(0))) {
                    list.add(model.get(i));
                    break;
                }
            }
        }else {
        	// 偏家
            for(int len=model.size(),i=len-1;i>=0;i--) {
                if(getValueInt(model.get(i))>Common.getValue(player.get(0))) {
                    list.add(model.get(i));
                    break;
                }
            }
        }
    }
    //3带1,2,4带1,2
    public void AI_2(List<String> model1,List<String> model2,List<SinglePoker> player,List<String> list,int role){
        //model1是主牌,model2是带牌,player是玩家出的牌,,list是准备回的牌
        //排序按重复数
        player=Common.getOrder2(player);
        int len1=model1.size();
        int len2=model2.size();
        //如果有王直接炸了
        if(len1>0&&model1.get(0).length()<10) {
            list.add(model1.get(0));
            System.out.println("王炸");
            return;
        }
        if(len1<1 || len2<1)
            return;
        for(int len=len1,i=len-1;i>=0;i--)
        {
            if(getValueInt(model1.get(i))>Common.getValue(player.get(0)))
            {
                list.add(model1.get(i));
                break;
            }
        }
        list.add(model2.get(len2-1));
        if(list.size()<2)
            list.clear();
    }
    public  int getValueInt(String n){
        String name[]=n.split(",");
        String s=name[0];
        int i=Integer.parseInt(s.substring(2, s.length()));
        if(s.substring(0, 1).equals("5"))
            i+=3;
        if(s.substring(2, s.length()).equals("1")||s.substring(2, s.length()).equals("2"))
            i+=13;
        return i;
    }
    public void AI_5(List<String> model1,List<String> model2,List<SinglePoker> player,List<String> list,int role){
        //排序按重复数
        player=Common.getOrder2(player);
        int len1=model1.size();
        int len2=model2.size();

        if(len1<1 || len2<2)
            return;
        for(int i=0;i<len1;i++){
            if(getValueInt(model1.get(i))>Common.getValue(player.get(0)))
            {
                list.add(model1.get(i));
                for(int j=1;j<=2;j++)
                    list.add(model2.get(len2-j));
            }
        }
    }
    public void AI_3(List<String> model,List<SinglePoker> player,List<String> list,int role){

        for(int i=0,len=model.size();i<len;i++)
        {
            String []s=model.get(i).split(",");
            if(s.length==player.size()&&getValueInt(model.get(i))>Common.getValue(player.get(0)))
            {
                list.add(model.get(i));
                return;
            }
        }
    }
    public void AI_4(List<String> model1,List<String> model2,List<SinglePoker> player,List<String> list,int role){
        //排序按重复数
        player=Common.getOrder2(player);
        int len1=model1.size();
        int len2=model2.size();

        if(len1<1 || len2<1)
            return;
        for(int i=0;i<len1;i++){
            String []s=model1.get(i).split(",");
            String []s2=model2.get(0).split(",");
            if((s.length/3<=len2)&&(s.length*(3+s2.length)==player.size())&&getValueInt(model1.get(i))>Common.getValue(player.get(0)))
            {
                list.add(model1.get(i));
                for(int j=1;j<=s.length/3;j++)
                    list.add(model2.get(len2-j));
            }
        }
    }
    
    public List<SinglePoker> getCardByName(List<SinglePoker> list, String n) {
        String[] name = n.split(",");
        List<SinglePoker> cardsList = new ArrayList<SinglePoker>();
        int j = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (j < name.length && list.get(i).name.equals(name[j])) {
                cardsList.add(list.get(i));
                i = 0;
                j++;
            }
        }
        return cardsList;
    }
}
