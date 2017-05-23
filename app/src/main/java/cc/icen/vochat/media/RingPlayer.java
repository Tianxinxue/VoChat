package cc.icen.vochat.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

import cc.icen.vochat.R;

public class RingPlayer implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mPlayer;

    public RingPlayer(final Context context) {
        AssetFileDescriptor mFileDescriptor = context.getResources().openRawResourceFd(R.raw.ring);

        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            }
            /*这个方法中的三个参数都要设置，只设置一个参数会报异常*/
            mPlayer.setDataSource( mFileDescriptor.getFileDescriptor(), mFileDescriptor.getStartOffset(),
                    mFileDescriptor.getLength());
            mPlayer.setOnCompletionListener(this);
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start(){
        mPlayer.start();
    }


    private void play() {
        try {
            mPlayer.start();
            mPlayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        mPlayer = null;
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        play();
    }
}