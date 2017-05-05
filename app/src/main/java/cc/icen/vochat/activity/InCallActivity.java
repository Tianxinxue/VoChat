package cc.icen.vochat.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cc.icen.vochat.R;
import cc.icen.vochat.media.AudioEncoder;
import cc.icen.vochat.net.UdpReceiver;

public class InCallActivity extends Activity implements View.OnClickListener {

    private AudioEncoder aEncoder;
    private UdpReceiver receiver;
    private TimerTask timerTask = null;
    private Timer timer;

    private ImageButton btnInCallStop;
    private TextView tvTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*设置Activity无标题全屏*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_in_call);
        btnInCallStop = (ImageButton) findViewById(R.id.im_button_stop_in_call);
        btnInCallStop.setOnClickListener(this);
        tvTimer = (TextView) findViewById(R.id.tv_timer);
        timer = new Timer();

        startRecording();
        receiver = new UdpReceiver();
        try {
            receiver.fillBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_button_stop_in_call:
                Log.d("tian---", "stop in call");
                try {
                    stopRecording();
                    this.finish();
                } catch (IOException e) {
                    //  TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }
    }

    private void startRecording() {

        aEncoder = new AudioEncoder(0, 0, 0, 0);
        aEncoder.StartEncoderThread();
        startTimer();
    }


    private void stopRecording() throws IOException {

        if (null != aEncoder) {

            stopTimer();
            aEncoder.StopThread();
        }
        if (receiver != null)
            receiver.close();

    }

    private void startTimer() {

        if (timerTask == null) {
            timerTask = new TimerTask() {
                int time_sec = 0;
                @Override
                public void run() {
                    time_sec++;
                    Message message = new Message();
                    message.what = time_sec ;
                    handler.sendMessage(message);
                }
            };
            // 设置scedule，开始时间以及时间间隔，间隔此处为1秒
            timer.schedule(timerTask, 1000, 1000);
        }
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg) {
            tvTimer.setText(String.format("%02d:%02d:%02d",msg.what/3600,(msg.what%3600)/60,msg.what%60));
            super.handleMessage(msg);
        }

    };
}
