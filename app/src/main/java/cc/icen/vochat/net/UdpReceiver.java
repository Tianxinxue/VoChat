package cc.icen.vochat.net;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import cc.icen.vochat.media.AudioPlayer;

public class UdpReceiver implements Runnable {

    private static final String TAG = "UdpReceiver";

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recv.aac";
    public static final int MTU = 1300;
    public static final int localPort = 8800;
    private DatagramSocket mSocket;
    private DatagramPacket mPacket;
    private byte[] mBuffer;
    private boolean isStop;
    private AudioPlayer player;

    private BufferedOutputStream outputStream;

    private Thread mThread;

    public UdpReceiver() {

        mBuffer = new byte[MTU];
        mPacket = new DatagramPacket(mBuffer, 1024);
        try {
            mSocket = new DatagramSocket(localPort);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        player = new AudioPlayer();

    }


    //关闭Socket
    public void close() {
        isStop = true;
        mSocket.close();
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void fillBuffer() throws IOException {

        if (mThread == null) {
            mThread = new Thread(this);
            createfile();
            isStop = false;
            mThread.start();
        }

    }


    @Override
    public void run() {

        while (!isStop) {


            try {
                mSocket.receive(mPacket);
                Log.d(TAG, "receive buffer" + "len: " + mPacket.getLength());
                outputStream.write(mBuffer, 0, mPacket.getLength());
                player.decodeAndPlay(mBuffer, mPacket.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mThread = null;

        }
    }


    private void createfile() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
