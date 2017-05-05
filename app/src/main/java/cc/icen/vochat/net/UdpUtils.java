package cc.icen.vochat.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class UdpUtils implements Runnable {

    public static final int MTU = 1300;
    private DatagramSocket mSocket;
    private DatagramPacket[] mPackets;
    private byte[][] mBuffers;
    private int mBufferCount, mBufferIn, mBufferOut;
    private int mCount = 0;

    private Semaphore mBufferRequested, mBufferCommitted;
    private Thread mThread;

    public UdpUtils() {
        // TODO: readjust that when the FIFO is full
        mBufferCount = 300;
        mBuffers = new byte[mBufferCount][];
        mPackets = new DatagramPacket[mBufferCount];
        resetFifo();
        for (int i = 0; i < mBufferCount; i++) {

            mBuffers[i] = new byte[MTU];
            mPackets[i] = new DatagramPacket(mBuffers[i], 1);

            //TODO 自定义UDP包的第一个字节的各个位的含义（一帧开始时某一位置位）
            mBuffers[i][0] = (byte) Integer.parseInt("10000000", 2);


        }

        try {
            mSocket = new DatagramSocket();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void resetFifo() {
        mBufferIn = 0;
        mBufferOut = 0;
        mBufferRequested = new Semaphore(mBufferCount);
        mBufferCommitted = new Semaphore(0);
    }

    //关闭Socket
    public void close() {
        mSocket.close();
    }

    //为每一个UDP包设置目的地址和端口
    public void setDestination(InetAddress dest, int dport) {
        if (dport != 0 ) {
            for (int i = 0; i < mBufferCount; i++) {
                mPackets[i].setPort(dport);
                mPackets[i].setAddress(dest);
            }
        }
    }

    /**
     * Returns an available buffer from the FIFO, it can then be modified.
     * Call {@link #commitBuffer(int)} to send it over the network.
     *
     * @throws InterruptedException
     **/
    public byte[] requestBuffer() throws InterruptedException {
        mBufferRequested.acquire();
        mBuffers[mBufferIn][1] &= 0x7F;
        return mBuffers[mBufferIn];
    }

    /**
     * Sends the RTP packet over the network.
     */
    public void commitBuffer(int length) throws IOException {

        mPackets[mBufferIn].setLength(length);

        if (++mBufferIn >= mBufferCount) mBufferIn = 0;
        mBufferCommitted.release();

        if (mThread == null) {
            mThread = new Thread(this);
            mThread.start();
        }

    }

    //设置每一个UDP包的标志位
    public void markNextPacket() {
        //TODO 暂时设置 |= 0x80 后面按照自己定义好的每一位的含义再修改
        mBuffers[mBufferIn][0] |= 0x80;
    }

    /**
     * The Thread sends the packets in the FIFO one by one at a constant rate.
     */
    @Override
    public void run() {

        try {
            while (mBufferCommitted.tryAcquire(4, TimeUnit.SECONDS)) {

                if (mCount++ > 30) {
                    mSocket.send(mPackets[mBufferOut]);
                }
                if (++mBufferOut >= mBufferCount) mBufferOut = 0;
                mBufferRequested.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mThread = null;
        resetFifo();

    }
}
