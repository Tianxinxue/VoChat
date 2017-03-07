package cc.icen.vochat.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


public class AudioGainer {
    private static final String TAG = "AudioGainer";
    private AudioRecord mAudioRecoder;
    //存放AudioRecoder采集的PCM数据的缓冲区
    private byte[] PCMbuffer;
    //AudioRecoder需要的最小的缓冲区大小
    private int bufferSize;

    //AudioRecoder参数
    private static final int PCM_SAMPLERATE = 44100;
    private static final int PCM_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int PCM_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    //音频数据源
    private static final int PCM_SOURCE = MediaRecorder.AudioSource.MIC;

    public AudioGainer() {
        this.bufferSize = AudioRecord.getMinBufferSize(PCM_SAMPLERATE, PCM_CHANNELS, PCM_AUDIO_FORMAT);
        mAudioRecoder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                PCM_SAMPLERATE, PCM_CHANNELS, PCM_AUDIO_FORMAT, bufferSize);

    }

    public void start() {
        if (mAudioRecoder == null) {
            Log.e(TAG, "start: mAudioRecoder is null !");
            return;
        }
        mAudioRecoder.startRecording();
    }

    //获取AudioRecoder需要的最小的缓冲区大小
    public int getBufferSize() {
        return this.bufferSize;
    }

    //获取一帧采集到的PCM数据
    public void getPcmFrame(byte[] data) {

        if (mAudioRecoder == null) {
            Log.e(TAG, "start: mAudioRecoder is null !");
            return;
        }
        int size = mAudioRecoder.read(data, 0, bufferSize);
        if (size <= 0) {
            Log.i(TAG, "getPcmFrame, no data to read.");
            return;
        }
    }

    public void stop() {
        mAudioRecoder.stop();
        mAudioRecoder.release();
        mAudioRecoder = null;
    }


}