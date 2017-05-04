package cc.icen.vochat.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

import cc.icen.vochat.R;

public class RingPlayer {

    private MediaPlayer mPlayer;

    public RingPlayer(final Context context) {


        try {
            //mPlayer = MediaPlayer.create(context, R.raw.ring);
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mPlayer.setDataSource( context.getResources().openRawResourceFd(R.raw.ring).getFileDescriptor());
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play(context);
            }
        });
    }


    private void play(Context context) {
        try {
            mPlayer.start();
            mPlayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
    }

}