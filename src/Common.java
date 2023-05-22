import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Common {

    //移动效果的函数,用于发牌
    public static void move(SinglePoker card,Point from,Point to,int t){
        if(to.x!=from.x){
            double k=(1.0)*(to.y-from.y)/(to.x-from.x);
            double b=to.y-to.x*k;
            int flag=0;//判断向左还是向右移动步幅
            if(from.x<to.x){

                if(t%3==2){
                    flag=3;
                }else{
                    flag=10;
                }
            }else {
                if(t%3==2){
                    flag=-3;
                }else{
                    flag=-10;
                }
            }
            for(int i=from.x;Math.abs(i-to.x)>20;i+=flag)
            {
                double y=k*i+b;//这里主要用的数学中的线性函数
                System.out.println(y+"="+k+"*"+i+"+"+b);
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
    //对list排序
//    public static void order(List<Card> list){
//
//        Collections.sort(list,new Comparator<Card>() {
//
//            public int compare(Card o1, Card o2) {
//
//                // TODO Auto-generated method stub
//                int a1=Integer.parseInt(o1.name.substring(0, 1));//花色
//
//                int a2=Integer.parseInt(o2.name.substring(0,1));
//                int b1=Integer.parseInt(o1.name.substring(2,o1.name.length()));//数值
//                int b2=Integer.parseInt(o2.name.substring(2,o2.name.length()));
//                int flag=0;
//                //如果是王的话
//                if(a1==5) b1+=100;
//                if(a1==5&&b1==1) b1+=50;
//
//                if(a2==5) b2+=100;
//                if(a2==5&&b2==1) b2+=50;
//                //如果是A或者2
//                if(b1==1) b1+=20;
//                if(b2==1) b2+=20;
//                if(b1==2) b1+=30;
//                if(b2==2) b2+=30;
//                flag=b2-b1;
//                if(flag==0){
//
//                    return a2-a1;
//                }
//                else {
//
//                    return flag;
//                }
//
//            }
//        });
//
//
//    }
//    //重新定位 flag代表电脑1 ,2 或者是我
//    public static void rePosition(Main m,List<Card> list,int flag){
//        Point p=new Point();
//        if(flag==0)
//        {
//            p.x=50;
//            p.y=(450/2)-(list.size()+1)*15/2;
//        }
//        if(flag==1)
//        {//我的排序 _y=450 width=830
//            p.x=(800/2)-(list.size()+1)*21/2;
//            p.y=450;
//        }
//        if(flag==2)
//        {
//            p.x=700;
//            p.y=(450/2)-(list.size()+1)*15/2;
//        }
//        int len=list.size();
//        for(int i=0;i<len;i++){
//            Card card=list.get(i);
//            Common.move(card, card.getLocation(), p,10);
//            m.container.setComponentZOrder(card, 0);
//            if(flag==1)p.x+=21;
//            else p.y+=15;
//        }
//    }
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
    }}

class Card_index{
    List a[]=new ArrayList[4];//单张
}