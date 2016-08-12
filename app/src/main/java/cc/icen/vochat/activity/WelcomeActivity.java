package cc.icen.vochat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URLEncoder;

import cc.icen.vochat.util.Person;
import cc.icen.vochat.R;
import cc.icen.vochat.util.HttpCallbackListener;
import cc.icen.vochat.util.HttpUtil;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private String TAG = "tian-";

    protected static final int TIME_OUT = 5000;
    private EditText et_password;
    private EditText et_username;
    private Button btnSignUp;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //获取控件
        et_username = (EditText) findViewById(R.id.et_sign_in_account);
        et_password = (EditText) findViewById(R.id.et_sign_in_pwd);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
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


            String address = "http://192.168.1.2:8080/Login/Login";

            Gson gson = new Gson();
            Person person = new Person();
            person.setId(username);
            person.setPasswd(password);
            String json = gson.toJson(person);
            Log.d("tian", json);
            String param = "user=" + URLEncoder.encode(json);

            HttpUtil.sendHttpRequest(address, param, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {

                    if (response.toString().equals("登录成功")) {
                        // 通过runOnUiThread()方法回到主线程处理逻辑
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WelcomeActivity.this,
                                        "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(WelcomeActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WelcomeActivity.this,
                                    "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } else if (btnSignUp == v) {
            Log.d(TAG, "btnSignUp");
        }
    }
}
