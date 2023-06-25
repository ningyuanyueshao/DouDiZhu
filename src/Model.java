import java.util.ArrayList;
import java.util.List;

public class Model {
	//一组牌
	int value; //权值
	int num;// 手数 (几次能够走完，没有挡的情况下)
	List<String> a1=new ArrayList<String>(); //单张
	List<String> a2=new ArrayList<String>(); //对子
	List<String> a3=new ArrayList<String>(); //3带
	List<String> a123=new ArrayList<String>(); //连子
	List<String> a112233=new ArrayList<String>(); //连牌
	List<String> a111222=new ArrayList<String>(); //飞机
	List<String> a4=new ArrayList<String>(); //炸弹
	
    public static Model getModel(List<SinglePoker> list){
        //先复制一个list
        List<SinglePoker> list2=new ArrayList<SinglePoker>(list);
        Model model=new Model();
        Model.getBoomb(list2, model); // 先拆炸弹
        Model.getThree(list2, model); // 拆3带
        Model.getPlane(list2, model); // 拆飞机
        Model.getTwo(list2, model); // 拆对子
        Model.getTwoTwo(list2, model); // 拆连队
        Model.get123(list2, model); // 拆顺子
        Model.getSingle(list2, model); // 拆单
        return model;
    }
	
    public static void getBoomb(List<SinglePoker> list,Model model){
        List<SinglePoker> del=new ArrayList<SinglePoker>();//要删除的Cards
        //王炸
        if(list.size()>=2 &&Common.getColor(list.get(0))==5 && Common.getColor(list.get(1))==5)
        {
            model.a4.add(list.get(0).name+","+list.get(1).name); //按名字加入
            del.add(list.get(0));
            del.add(list.get(1));
        }
        //如果王不构成炸弹咋先拆单
        if(Common.getColor(list.get(0))==5&&Common.getColor(list.get(1))!=5)
        {
            del.add(list.get(0));
            model.a1.add(list.get(0).name);
        }
        list.removeAll(del);
        //一般的炸弹
        for(int i=0,len=list.size();i<len;i++){
            if(i+3<len && Common.getValue(list.get(i))==Common.getValue(list.get(i+3)))
            {
                String s=list.get(i).name+",";
                s+=list.get(i+1).name+",";
                s+=list.get(i+2).name+",";
                s+=list.get(i+3).name;
                model.a4.add(s);
                for(int j=i;j<=i+3;j++)
                    del.add(list.get(j));
                i=i+3;
            }
        }
        list.removeAll(del);
    }

    public static void getThree(List<SinglePoker> list,Model model){
        List<SinglePoker> del=new ArrayList<SinglePoker>();//要删除的Cards
        //连续3张相同
        for(int i=0,len=list.size();i<len;i++){
            if(i+2<len&&Common.getValue(list.get(i))==Common.getValue(list.get(i+2)))
            {
                String s=list.get(i).name+",";
                s+=list.get(i+1).name+",";
                s+=list.get(i+2).name;
                model.a3.add(s);
                for(int j=i;j<=i+2;j++)
                    del.add(list.get(j));
                i=i+2;
            }
        }
        list.removeAll(del);
    }
    public static void getTwo(List<SinglePoker> list,Model model){
        List<SinglePoker> del=new ArrayList<SinglePoker>();//要删除的Cards
        //连续2张相同
        for(int i=0,len=list.size(); i < len - 1;i++){
            if(Common.getValue(list.get(i))==Common.getValue(list.get(i+1)))
            {
                String s=list.get(i).name+",";
                s+=list.get(i+1).name;
                model.a2.add(s);
                for(int j=i;j<=i+1;j++)
                    del.add(list.get(j));
                i=i+1;
            }
        }
        list.removeAll(del);
    }

    public static void getSingle(List<SinglePoker> list,Model model){
        List<SinglePoker> del=new ArrayList<SinglePoker>();//要删除的Cards
        //1
        for(int i=0,len=list.size();i<len;i++){
            model.a1.add(list.get(i).name);
            del.add(list.get(i));
        }
        list.removeAll(del);
    }
    public static void getPlane(List<SinglePoker> list,Model model){
        List<String> del=new ArrayList<String>();//要删除的Cards
        //从model里面的3带找
        List<String> l=model.a3;
        if(l.size()<2)
            return ;
        Integer s[]=new Integer[l.size()];
        for(int i=0,len=l.size();i<len;i++){
            String []name=l.get(i).split(",");
            s[i]=Integer.parseInt(name[0].substring(2,name[0].length()));
        }
        for(int i=0,len=l.size();i<len;i++){
            int k=i;
            for(int j=i;j<len;j++)
            {
                if(s[i]-s[j]==j-i)
                    k=j;
            }
            if(k!=i)
            {//说明从i到k是飞机
                String ss="";
                for(int j=i;j<k;j++) {
                    ss+=l.get(j)+",";
                    del.add(l.get(j));
                }
                ss+=l.get(k);
                model.a111222.add(ss);
                del.add(l.get(k));
                i=k;
            }
        }
        l.removeAll(del);
    }

    public static void getTwoTwo(List<SinglePoker> list, Model model){
        List<String> del=new ArrayList<String>();//要删除的Cards
        //从model里面的对子找
        List<String> l=model.a2;
        if(l.size()<3)
            return ;
        Integer s[] = new Integer[l.size()];
        for(int i=0,len=l.size();i<len;i++){
            String []name=l.get(i).split(",");
            s[i]=Integer.parseInt(name[0].substring(2,name[0].length()));
        }
        //s0,1,2,3,4  13,9,8,7,6
        for(int i=0,len=l.size();i<len;i++){
            int k=i;
            for(int j=i;j<len;j++)
            {
                if(s[i]-s[j]==j-i)
                    k=j;
            }
            if(k-i>=2)//k=4 i=1
            {//说明从i到k是连队
                String ss="";
                for(int j=i;j<k;j++)
                {
                    ss+=l.get(j)+",";
                    del.add(l.get(j));
                }
                ss+=l.get(k);
                model.a112233.add(ss);
                del.add(l.get(k));
                i=k;
            }
        }
        l.removeAll(del);
    }

    public static void get123(List<SinglePoker> list,Model model){
        List<SinglePoker> del=new ArrayList<SinglePoker>();//要删除的Cards
        if(list.size() > 0 && (Common.getValue(list.get(0)) < 7 
        		|| Common.getValue(list.get(list.size() - 1)) > 10))
            return;
        
        if(list.size()<5) return;
        
        for(int i=0,len=list.size();i<len;i++) {
            int k=i;
            for(int j=i;j<len;j++){
                if(Common.getValue(list.get(i))-Common.getValue(list.get(j))==j-i)
                {
                    k=j;
                }
            }
            if(k-i>=4) {
                String s="";
                for(int j=i;j<k;j++) {
                    s+=list.get(j).name+",";
                    del.add(list.get(j));
                }
                s+=list.get(k).name;
                del.add(list.get(k));
                model.a123.add(s);
                i=k;
            }
        }
        list.removeAll(del);
    }

}
