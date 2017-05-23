package cc.icen.vochat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cc.icen.vochat.R;
import cc.icen.vochat.media.AudioEncoder;
import cc.icen.vochat.net.CallManager;
import cc.icen.vochat.net.UdpReceiver;

public class InCallActivity extends Activity implements View.OnClickListener, CallManager.CallAcceptListener {

    private static  final  String TAG = "InCallActivity";
    public static final String FRIEND_NAME = "friend_name";
    public static final String Call_UI_NAME = "start_call" ;
    private AudioEncoder aEncoder;
    private UdpReceiver receiver;
    private CallManager mCallManager;
    private TimerTask timerTask = null;
    private Timer timer;

    private ImageButton btnInCallStop;
    private ImageButton btnStartCall;
    private ImageButton btnStopCall;
    private RelativeLayout layoutCall;
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

        Intent intent = getIntent();
        String friendName = intent.getStringExtra(FRIEND_NAME);
        String CallUiName = intent.getStringExtra(Call_UI_NAME);

        btnInCallStop = (ImageButton) findViewById(R.id.im_button_stop_in_call);
        btnStartCall = (ImageButton) findViewById(R.id.im_button_start_call);
        btnStopCall = (ImageButton) findViewById(R.id.im_button_stop_call);
        layoutCall = (RelativeLayout) findViewById(R.id.layout_call);
        tvTimer = (TextView) findViewById(R.id.tv_timer);

        btnInCallStop.setOnClickListener(this);
        btnStartCall.setOnClickListener(this);
        btnStopCall.setOnClickListener(this);
        timer = new Timer();
        tvTimer.setVisibility(View.GONE);

        if(CallUiName.equals("receive_call")){
            initReceiveCallUI();
        }else if(CallUiName.equals("start_call")){
            initStartCallUI();
        }else{
            Log.e(TAG, "Wrong Parameter!");
            this.finish();
        }

        ((TextView) findViewById(R.id.tv_id)).setText(friendName);

        mCallManager = new CallManager(this, this);
        mCallManager.sendCallRequest("127.0.0.1", false);
        //startCall();

    }

    private void initReceiveCallUI(){
        btnInCallStop.setVisibility(View.GONE);
        layoutCall.setVisibility(View.VISIBLE);
    }

    private void initStartCallUI(){
        btnInCallStop.setVisibility(View.VISIBLE);
        layoutCall.setVisibility(View.GONE);

    }

    private void startCall() {

        tvTimer.setVisibility(View.VISIBLE);
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
                try {
                    stopRecording();
                    this.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.im_button_start_call:
                initStartCallUI();
                break;
            case R.id.im_button_stop_call:
                this.finish();
                break;
            default:
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
                    message.what = time_sec;
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            tvTimer.setText(String.format("%02d:%02d:%02d", msg.what / 3600, (msg.what % 3600) / 60, msg.what % 60));
            super.handleMessage(msg);
        }

    };

    @Override
    public void onCallAccept() {

    }
}
