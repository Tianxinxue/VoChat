package cc.icen.vochat.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import cc.icen.vochat.net.UdpUtils;

public class AudioEncoder {
    private final static String TAG = "AudioEncoder";

    private int TIMEOUT_USEC = 12000;
    int KEY_CHANNEL_COUNT = 1;
    int KEY_SAMPLE_RATE = 44100;
    int KEY_BIT_RATE = 24000;
    int KEY_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;

    private int m_framerate = 30;

    private MediaCodec mediaCodec;
    private AudioGainer aGainer;

    private UdpUtils udpSocket;

    private static final String DEST_ADDRESS = "127.0.0.1";
    private static final int    DEST_PORT = 8800;


    public AudioEncoder(int sample_rate, int channel_count, int framerate, int bitrate) {


        MediaFormat mediaFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, KEY_AAC_PROFILE);
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        try {
            mediaCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //配置编码器参数
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        udpSocket = new UdpUtils();
        try {
            InetAddress address = InetAddress.getByName(DEST_ADDRESS);
            udpSocket.setDestination(address,DEST_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        aGainer = new AudioGainer();
        aGainer.start();

        //启动编码器
        mediaCodec.start();
        //创建保存编码后数据的文件
        createfile();
    }

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.aac";
    private BufferedOutputStream outputStream;

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

    private void StopEncoder() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRuning = false;

    public void StopThread() {
        isRuning = false;
        try {
            StopEncoder();
            outputStream.flush();
            outputStream.close();
            udpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartEncoderThread() {
        Thread EncoderThread = new Thread(new Runnable() {

            @Override
            public void run() {
                isRuning = true;
                byte[] input = new byte[aGainer.getBufferSize()];
                long pts = 0;
                long generateIndex = 0;

                while (isRuning) {

                    if (input != null) {
                        try {
                            aGainer.getPcmFrame(input);
                            long startMs = System.currentTimeMillis();
                            //编码器输入缓冲区
                            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                            //编码器输出缓冲区
                            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                            if (inputBufferIndex >= 0) {
                                pts = computePresentationTime(generateIndex);
                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                                inputBuffer.clear();
                                inputBuffer.put(input);
                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                                generateIndex += 1;
                            }

                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                            while (outputBufferIndex >= 0) {
                                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                                //给adts头字段空出7的字节
                                Log.d(TAG,"length = " + bufferInfo.size);
                                int length = bufferInfo.size + 7;
                                byte[] outData = new byte[length];
                                addADTStoPacket(outData, length);
                                outputBuffer.get(outData, 7, bufferInfo.size);
                                //写到文件中
                                outputStream.write(outData, 0, length);

                                byte[] send_buffer = udpSocket.requestBuffer();
                                System.arraycopy(outData,0,send_buffer,0,length);
                                udpSocket.commitBuffer(length);

                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        EncoderThread.start();

    }

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / m_framerate;
    }

    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}
