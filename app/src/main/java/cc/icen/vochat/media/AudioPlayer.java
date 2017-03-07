package cc.icen.vochat.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioPlayer {

    private final static String MINE_TYPE = "audio/mp4a-latm";
    private MediaCodec mediaCodec;
    private AudioTrack audiotrack;

    public AudioPlayer() {
        initDecoder();
        initAudioTrack();
    }

    private boolean initDecoder() {

        try {
            mediaCodec = MediaCodec.createDecoderByType("audio/mp4a-latm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createAudioFormat(MINE_TYPE, 44100, 1);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 24000);
        mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1);
        byte[] bytes = new byte[]{(byte) 0x11, (byte)0x90};
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        mediaFormat.setByteBuffer("csd-0", bb);
        mediaCodec.configure(mediaFormat, null, null, 0);
        mediaCodec.start();
        return true;

    }

    private void initAudioTrack() {
        int buffsize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audiotrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM);
        //启动AudioTrack
        audiotrack.play();
    }

    public void decodeAndPlay(byte[] data, int len) {
        Log.e("tian---player", "len: " + len);
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(data);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, len,
                    System.nanoTime(), 0);
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);
            audiotrack.write(outData, 0, bufferInfo.size);
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    public void stop() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }
        if (audiotrack != null) {
            audiotrack.stop();
            audiotrack.release();
        }
    }


}
