

/**
 * @author seaside
 * 2023-05-08 10:47
 */
public class Room{
    int roomNumber;//房间号

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    ClientThread clientThread1;
    ClientThread clientThread2;
    ClientThread clientThread3;
    ClientThread tempClientThread;//tempClientThread表示要被调用的线程
    public void setEveryClientThread(ClientThread clientThread){
        switch (clientThread.getName()){
            case "1":
                clientThread1 = clientThread;
                System.out.println("该房间内的用户1的线程已被获取");
                break;
            case "2":
                clientThread2 = clientThread;
                System.out.println("该房间内的用户2的线程已被获取");
                break;
            case "3":
                clientThread3 = clientThread;
                System.out.println("该房间内的用户3的线程已被获取");
                break;
        }
    } //线程通过调用该方法来为房间里的三个线程类变量赋值，这样之后就可以利用该房间访问到其他线程


    //可以在这个房间里调用每个线程类的方法，就可以修改每个线程里的from和to了

}
