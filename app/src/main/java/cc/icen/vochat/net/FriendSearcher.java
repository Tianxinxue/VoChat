package cc.icen.vochat.net;

import android.widget.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by cent on 17-3-14.
 */

public class FriendSearcher {

    public FriendSearcher(){

        new Sender();
        new Receiver();
    }

    private static final int BROADCAST_PORT = 9898;
    private static final String BROADCAST_IP = "224.224.224.224";

    private Thread senderThread = null;
    private Thread recverThread = null;

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

            DatagramPacket dataPacket = null;
            //将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
            byte[] data = new byte[0];
            try {
                data = InetAddress.getLocalHost().getHostAddress().getBytes();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
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
            recverThread = new Thread(this);
            recverThread.start();
        }

        @Override
        public void run() {

            byte buf[] = new byte[1024];
            DatagramPacket dp = null;
            dp = new DatagramPacket(buf, buf.length, inetAddress, BROADCAST_PORT);

            while (true) {
                try {
                    multicastSocket.receive(dp);
                    Thread.sleep(3000);
                    String ip_recv = new String(buf, 0, dp.getLength());
                    System.out.println("检测到服务端IP : " + ip_recv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

