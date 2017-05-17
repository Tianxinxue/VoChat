package cc.icen.vochat.net;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import cc.icen.vochat.media.RingPlayer;

public class CallManager {

    private static final String TAG = "CallManager";
    private final static int cmdPort = 9999;

    private ServerSocket mServer;
    private InetAddress remoteAddress;
    private Context mContext;

    private final byte[] CMD_Hangup = {0, 1, 0, 0}; //挂断
    private final byte[] CMD_Request = {0, 1, 0, 1}; //请求
    private final byte[] CMD_Accept = {0, 1, 1, 1}; //接听

    public CallManager(Context context) {
        mContext = context;
        new Thread() {
            @Override
            public void run() {
                listen();
            }
        }.start();

    }

    public void listen() {
        Socket socket;
        try {
            mServer = new ServerSocket(cmdPort);
            socket = mServer.accept();
            remoteAddress = socket.getInetAddress();

            Log.i(TAG, remoteAddress.getHostAddress() + ": Connected");
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            byte[] data = new byte[4];
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //String data = reader.readLine();
            is.read(data);
            parseCMD(data);
            Log.i(TAG, "#################################################" + data[0] + " " + data[1] + " " + data[2] + " " + data[3]);

            os.write("From Server:hi,I am server .\n".getBytes());
            //reader.close();
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendCallRequest(final String ip, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    Socket socket = new Socket(ip, port);
                    byte[] cmd = CMD_Request;
                    OutputStream os = socket.getOutputStream();
                    os.write(cmd);
                    // 一定要加上这句，否则收不到来自服务器端的消息返回 ，意思就是结束msg信息的写入
                    socket.shutdownOutput();
                    InputStream is = socket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String data = br.readLine();
                    Log.i(TAG, data);

                    br.close();
                    is.close();
                    os.close();
                } catch (Exception e) {
                    Log.i(TAG, "Exception in sendCallRequest");
                }
            }
        }.start();
    }

    private void parseCMD(byte[] cmd) {

        if (Arrays.equals(CMD_Hangup, cmd)) { //挂断

        } else if (Arrays.equals(CMD_Request, cmd)) { //请求
            new RingPlayer(mContext);

        } else if (Arrays.equals(CMD_Accept, cmd)) { //接听

        } else {//错误命令
            Log.e(TAG, "parseCMD: Wrong cmd!");
        }
    }

}
