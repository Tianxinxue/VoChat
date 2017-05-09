package cc.icen.vochat.net;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import cc.icen.vochat.R;
import cc.icen.vochat.utils.Person;

public class FriendSearcher {


    private static final int BROADCAST_PORT = 9898;
    private static final String BROADCAST_IP = "224.255.0.1";

    private Thread senderThread = null;
    private Thread recverThread = null;
    private static String localIP;
    private static List<Person> friendList;


    public FriendSearcher() {

        friendList = new ArrayList<Person>();
        new Sender();
        new Receiver();
    }

    //获取本机ip地址
    private String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("tian--", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }

    public List<Person> getFriendList(){
        return friendList;
    }


    private class Sender implements Runnable {

        InetAddress inetAddress = null;
        /*发送广播端的socket*/
        MulticastSocket multicastSocket = null;
        private volatile boolean isRuning = true;

        public Sender() {
            try {
                inetAddress = InetAddress.getByName(BROADCAST_IP);
                multicastSocket = new MulticastSocket(BROADCAST_PORT);
                multicastSocket.setTimeToLive(1);
                multicastSocket.joinGroup(inetAddress);

            } catch (Exception e) {
                e.printStackTrace();

            }
            senderThread = new Thread(this);
            senderThread.start();
        }

        @Override
        public void run() {

            DatagramPacket dataPacket ;
            byte[] data;

            data = getHostIP().getBytes();

            dataPacket = new DatagramPacket(data, data.length, inetAddress, BROADCAST_PORT);
            while (true) {
                if (isRuning) {
                    try {
                        multicastSocket.send(dataPacket);
                        Thread.sleep(3000);
                        System.out.println("再次发送ip地址广播:.....");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    private class Receiver implements Runnable {

        InetAddress inetAddress = null;
        /*发送广播端的socket*/
        MulticastSocket multicastSocket = null;


        public Receiver() {

            try {
                multicastSocket = new MulticastSocket(BROADCAST_PORT);
                inetAddress = InetAddress.getByName(BROADCAST_IP);
                multicastSocket.joinGroup(inetAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }

            localIP = getHostIP();
            recverThread = new Thread(this);
            recverThread.start();
        }

        @Override
        public void run() {

            byte buf[] = new byte[1024];
            DatagramPacket dp;
            dp = new DatagramPacket(buf, buf.length, inetAddress, BROADCAST_PORT);
            friendList.add(new Person(localIP,R.mipmap.call_usr));

            while (true) {
                try {
                    int score = 0;
                    multicastSocket.receive(dp);
                    Thread.sleep(3000);
                    String ip_recv = new String(buf, 0, dp.getLength());
                    System.out.println("检测到服务端IP : " + ip_recv);
                    if(!ip_recv.equals(localIP) ) {
                        for(Person person: friendList){
                            if(ip_recv.equals(person.getName())) {
                                score ++;
                            }
                        }
                        if(score == 0){
                            friendList.add(new Person(ip_recv,R.mipmap.call_usr));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

