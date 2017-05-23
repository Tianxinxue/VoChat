package cc.icen.vochat.net;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import cc.icen.vochat.activity.InCallActivity;
import cc.icen.vochat.media.RingPlayer;

public class CallManager {

    private static final String TAG = "CallManager";


    private final static int cmdPort = 9999;

    private ServerSocket mServer;
    private InetAddress remoteAddress;
    private Context mContext;
    private RingPlayer mRingPlayer;
    private CallAcceptListener mListener;

    private final byte[] CMD_Hangup = {0, 1, 0, 0}; //挂断
    private final byte[] CMD_Request = {0, 1, 0, 1}; //请求
    private final byte[] CMD_Accept = {0, 1, 1, 1}; //接听

    public CallManager(Context context, CallAcceptListener listener) {
        mContext = context;
        mListener = listener;
        new Thread() {
            @Override
            public void run() {
                try {
                    mServer = new ServerSocket(cmdPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                listen();
            }
        }.start();

    }

    private void listen() {
        Socket socket;
        try {
            socket = mServer.accept();
            remoteAddress = socket.getInetAddress();

            Log.i(TAG, remoteAddress.getHostAddress() + ": Connected");
            InputStream is = socket.getInputStream();
            byte[] data = new byte[4];
            is.read(data);
            parseCMD(data);
            Log.i(TAG, "#################################################" + data[0] + " " + data[1] + " " + data[2] + " " + data[3]);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendCallRequest(final String ip, boolean stopRing) {
        if (stopRing && mRingPlayer != null && mRingPlayer.isPlaying()) {
            mRingPlayer.stop();
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    Socket socket = new Socket(ip, cmdPort);
                    byte[] cmd = CMD_Request;
                    OutputStream os = socket.getOutputStream();
                    os.write(cmd);
                    // 一定要加上这句，否则收不到来自服务器端的消息返回 ，意思就是结束msg信息的写入
                    socket.shutdownOutput();
                    os.flush();
                    os.close();
                    socket.close();
                } catch (Exception e) {
                    Log.i(TAG, "Exception in sendCallRequest");
                }
            }
        }.start();
    }

    private void parseCMD(byte[] cmd) {

        if (Arrays.equals(CMD_Hangup, cmd)) { //挂断

        } else if (Arrays.equals(CMD_Request, cmd)) { //请求
            mRingPlayer = new RingPlayer(mContext);
            mRingPlayer.start();

        } else if (Arrays.equals(CMD_Accept, cmd)) { //接听
            mListener.onCallAccept();
        } else {//错误命令
            Log.e(TAG, "parseCMD: Wrong cmd!");
        }
    }

    public void stopRing() {
        if (mRingPlayer.isPlaying()) {
            mRingPlayer.stop();
        }
    }

    private void startInCallActivity(Context context, String friend_name, String call_ui_name) {
        Intent intent = new Intent(context, InCallActivity.class);
        intent.putExtra(InCallActivity.FRIEND_NAME, friend_name);
        intent.putExtra(InCallActivity.Call_UI_NAME, call_ui_name);
        context.startActivity(intent);
    }

    public interface CallAcceptListener {
        void onCallAccept();
    }


}
