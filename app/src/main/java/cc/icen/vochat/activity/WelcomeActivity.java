package cc.icen.vochat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.icen.vochat.R;
import cc.icen.vochat.net.FriendSearcher;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private String TAG = "tian-";

    private EditText et_password;
    private EditText et_username;
    private Button btnSignUp;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //获取控件
        et_username = (EditText) findViewById(R.id.et_sign_in_account);
        et_password = (EditText) findViewById(R.id.et_sign_in_pwd);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        new FriendSearcher();
    }

    @Override
    public void onClick(View v) {

        if (btnSignIn == v) {
            Log.d(TAG, "btnSignIn");
            //获取用户数据
            final String username = et_username.getText().toString().trim();
            final String password = et_password.getText().toString().trim();
            //校验数据
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                Toast.makeText(this, "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.equalsIgnoreCase("test") && password.equalsIgnoreCase("123")) {

                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        } else if (btnSignUp == v) {
            Log.d(TAG, "btnSignUp");
        }
    }
}
