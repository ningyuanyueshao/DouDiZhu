import java.awt.*;
import java.util.*;
import java.util.List;

public class Common {

    //移动效果的函数,用于发牌
    public static void move(SinglePoker card,Point from,Point to,int t){
        if(to.x!=from.x){
            double k=(1.0)*(to.y-from.y)/(to.x-from.x);
            double b=to.y-to.x*k;
            int flag=0;//判断向左还是向右移动步幅
            if(from.x<to.x) {
                if(t%3 == 2) { flag = 3; }
                else { flag = 10; }
            } else {
                if(t%3 == 2){ flag = -3; }
                else { flag = -10; }
            }
            for(int i=from.x;Math.abs(i-to.x)>20;i+=flag)
            {
                double y=k*i+b;//这里主要用的数学中的线性函数
                card.setLocation(i,(int)y);

//                try {
//                    Thread.sleep(5); //延迟，可自己设置
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
        //位置校准
        card.setLocation(to);
    }
//    对list排序
    public static void order(List<SinglePoker> list){

        Collections.sort(list,new Comparator<SinglePoker>() {

            public int compare(SinglePoker o1, SinglePoker o2) {

                // TODO Auto-generated method stub
                int a1 = Integer.parseInt(o1.name.substring(0, 1));//花色
                int a2 = Integer.parseInt(o2.name.substring(0, 1));
                int b1 = Integer.parseInt(o1.name.substring(2, o1.name.length()));//数值
                int b2 = Integer.parseInt(o2.name.substring(2, o2.name.length()));
                int flag = 0;
                //如果是王的话
                if(a1 == 5) b1 += 100;
                if(a1 == 5 && b1 == 1) b1 += 50;
                if(a2 == 5) b2 += 100;
                if(a2 == 5 && b2 == 1) b2 += 50;
                //如果是A或者2
                if(b1 == 1) b1 += 20;
                if(b2 == 1) b2 += 20;
                if(b1 == 2) b1 += 30;
                if(b2 == 2) b2 += 30;
                flag = b2-b1;
                if(flag == 0) { return a2 - a1; }
                else { return flag; }
            }
        });
    }
    //重新定位 flag代表电脑1 ,2 或者是我
    public static void rePosition(OnePLayout onePLayout,List<SinglePoker> list, int flag){
        Point p = new Point();
        if(flag == 0) {
            p.x = 250;
            p.y = (760 / 2) - (list.size() + 1) * 15 / 2;
        }
        if(flag==1) {
        //我的排序 _y=450 width=830
            p.x=(1600/2)-(list.size()+1)*21/2;
            p.y=700;
        }
        if(flag==2) {
            p.x=1550;
            p.y=(760/2)-(list.size()+1)*15/2;
        }
        int len=list.size();
        for(int i=0;i<len;i++){
            SinglePoker card=list.get(i);
            Common.move(card, card.getLocation(), p,10);
            onePLayout.setComponentZOrder(card, 0);
            if(flag==1)p.x+=34;
            else p.y+=20;
        }
    }
    //地主牌权值，看是否抢地主
    public static int getScore(List<SinglePoker> list){
        int count=0;
        for(int i=0,len=list.size();i<len;i++){
            SinglePoker card=list.get(i);
            if(card.name.substring(0, 1).equals("5"))
            {
                //System.out.println(card.name.substring(0, 1));
                count+=5;
            }
            if(card.name.substring(2, card.name.length()).equals("2"))
            {
                //System.out.println(2);
                count+=2;
            }
        }
        return count;
    }
    //返回花色
    public static int getColor(SinglePoker card){
        return Integer.parseInt(card.name.substring(0,1));
    }
    //返回值
    public static int getValue(SinglePoker card){
        int i= Integer.parseInt(card.name.substring(2,card.name.length()));
        if(card.name.substring(2,card.name.length()).equals("2"))
            i+=13;
        if(card.name.substring(2,card.name.length()).equals("1"))
            i+=13;
        if(Common.getColor(card)==5)
            i+=2;//是王
        return i;
    }
    //得到最大相同数
    public static void getMax(Card_index card_index,List<SinglePoker> list){
        int count[]=new int[14];//1-13各算一种,王算第14种
        for(int i=0;i<14;i++)
            count[i]=0;
        for(int i=0; i<list.size(); i++){
            if(Common.getColor(list.get(i))==5)
                count[13]++;
            else
                count[Common.getValue(list.get(i))-1]++;
        }
        for(int i=0;i<14;i++)
        {
            switch (count[i]) {
                case 1:
                    card_index.a[0].add(i+1);
                    break;
                case 2:
                    card_index.a[1].add(i+1);
                    break;
                case 3:
                    card_index.a[2].add(i+1);
                    break;
                case 4:
                    card_index.a[3].add(i+1);
                    break;
            }
        }
    }
    
    public static void hideCards(List<SinglePoker> list){
        for(int i=0,len=list.size();i<len;i++){
            list.get(i).setVisible(false);
        }
    }
    

    

    public static CardType jugdeType(List<SinglePoker> list) {
        //因为之前排序过所以比较好判断
        int len=list.size();
        //单牌,对子，3不带，4个一样炸弹
        if(len<=4)
        {	//如果第一个和最后个相同，说明全部相同
            if(list.size() > 0 
            		&& Common.getValue(list.get(0)) == Common.getValue(list.get(len - 1)))
            {
                switch (len) {
                    case 1:
                        return CardType.c1;
                    case 2:
                        return CardType.c2;
                    case 3:
                        return CardType.c3;
                    case 4:
                        return CardType.c4;
                }
            }
            //双王,炸弹
            if(len == 2 && Common.getColor(list.get(1)) == 5 
            		&& Common.getColor(list.get(0)) == 5)
                return CardType.c4;
            //当第一个和最后个不同时,3带1
            if(len==4 &&((Common.getValue(list.get(0))==Common.getValue(list.get(len-2)))||
                    Common.getValue(list.get(1))==Common.getValue(list.get(len-1))))
                return CardType.c31;
            else {
                return CardType.c0;
            }
        }
        //当5张以上时，连字，3带2，飞机，2顺，4带2等等
        if(len>=5)
        {//现在按相同数字最大出现次数
            Card_index card_index=new Card_index();
            for(int i=0;i<4;i++)
                card_index.a[i]=new ArrayList<Integer>();
            //求出各种数字出现频率
            Common.getMax( card_index,list); //a[0,1,2,3]分别表示重复1,2,3,4次的牌
            //3带2 -----必含重复3次的牌
            if(card_index.a[2].size()==1 &&card_index.a[1].size()==1 && len==5)
                return CardType.c32;
            //4带2(单,双)
            if(card_index.a[3].size()==1 && len==6)
                return CardType.c411;
            //4带2对
            if(card_index.a[3].size()==1 && card_index.a[1].size()==2 &&len==8)
                return CardType.c422;
            //顺子,保证不存在王
            if((Common.getColor(list.get(0))!=5)&&(card_index.a[0].size()==len) &&
                    (Common.getValue(list.get(0))-Common.getValue(list.get(len-1))==len-1))
                return CardType.c123;
            //连队
            if(card_index.a[1].size()==len/2 && len%2==0 && len/2>=3
                    &&(Common.getValue(list.get(0))-Common.getValue(list.get(len-1))==(len/2-1)))
                return CardType.c1122;
            //飞机
            if(card_index.a[2].size()==len/3 && (len%3==0) &&
                    (Common.getValue(list.get(0))-Common.getValue(list.get(len-1))==(len/3-1)))
                return CardType.c111222;

            //飞机带n单,n/2对
            if(card_index.a[2].size()==len/4 &&
                    ((Integer)(card_index.a[2].get(len/4-1))-(Integer)(card_index.a[2].get(0))==len/4-1)
            &&(len>=8))
                return CardType.c11122234;
//jjj43 len=5 a[2]=1 a[0]=2 会导致 (j-j)==5/4-1


            //飞机带n双
            if(card_index.a[2].size()==len/5 && card_index.a[2].size()==len/5 &&
                    ((Integer)(card_index.a[2].get(len/5-1))-(Integer)(card_index.a[2].get(0))==len/5-1)&&(len>=10))
                return CardType.c1112223344;

        }
        return CardType.c0;
    }
    public static List<SinglePoker> getOrder2(List<SinglePoker> list){
        List<SinglePoker> list2=new ArrayList<SinglePoker>(list);
        List<SinglePoker> list3=new ArrayList<SinglePoker>();
        //	List<Integer> list4=new ArrayList<Integer>();
        int len=list2.size();
        int a[]=new int[20];//记录数
        for(int i=0;i<20;i++)
            a[i]=0;
        for(int i=0;i<len;i++) {
            a[Common.getValue(list2.get(i))]++;
        }
        int max=0;
        for(int i=0;i<20;i++){
            max=0;
            for(int j=19;j>=0;j--){
                if(a[j]>a[max])
                    max=j;
            }

            for(int k=0;k<len;k++){
                if(Common.getValue(list2.get(k))==max){
                    list3.add(list2.get(k));
                }
            }
            list2.remove(list3);
            a[max]=0;
        }
        return list3;
    }

    public static int checkCards(List<SinglePoker> c,List<SinglePoker>[] current){
        // 找出当前最大的牌是哪个电脑出的,c是点选的牌
        List<SinglePoker> currentlist=(current[0].size()>0)?current[0]:current[2];
        CardType cType=Common.jugdeType(c);
        // 如果张数不同直接过滤 但是炸弹不会直接return 0
        if(cType!=CardType.c4&&c.size()!=currentlist.size())
            return 0;
        // 比较我的出牌类型
        if(Common.jugdeType(c)!=Common.jugdeType(currentlist)) {
        	// 两个类型不等的话
            if(cType!=CardType.c4)// 如果cType不是炸弹的话 直接不能出牌
                return 0;
            else{// 如果cType是炸弹，那么肯定可以出牌
                return 1;
            }
        }
        // 比较出的牌是否要大
        // 王炸弹
        if(cType==CardType.c4)
        {
            if(c.size()==2)
                return 1;
            if(currentlist.size()==2)
                return 0;
        }
        // 单牌,对子,3带,4炸弹
        if(cType==CardType.c1||cType==CardType.c2||cType==CardType.c3||cType==CardType.c4){
            if(Common.getValue(c.get(0))<=Common.getValue(currentlist.get(0))) {
                return 0;
            } else { return 1; }
        }
        // 顺子,连队，飞机裸
        if(cType==CardType.c123||cType==CardType.c1122||cType==CardType.c111222)
        {
            if(Common.getValue(c.get(0))<=Common.getValue(currentlist.get(0)))
                return 0;
            else
                return 1;
        }
        // 按重复多少排序
        // 3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的
        if(cType == CardType.c31 || cType == CardType.c32 
        		|| cType == CardType.c411 || cType == CardType.c422
                || cType == CardType.c11122234 || cType==CardType.c1112223344){
            List<SinglePoker> a1 = Common.getOrder2(c); // 我出的牌
            List<SinglePoker> a2 = Common.getOrder2(currentlist);// 当前最大牌
            if(Common.getValue(a1.get(0)) < Common.getValue(a2.get(0)))
                return 0;
        }
        return 1;
    }
}

class Card_index{
    List a[]=new ArrayList[4];//单张
}
