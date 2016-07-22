package cc.icen.vochat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private String TAG = "tian-";

    protected static final int TIME_OUT = 5000;
    private EditText et_password;
    private EditText et_username;
    private Button btnSignUp;
    private Button btnSignIn;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RESULT_OK:
                    if(msg.obj.toString().equals("登录成功")) {
                        Toast.makeText(WelcomeActivity.this, "成功:"
                                + msg.obj.toString(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        intent.setClass(WelcomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(WelcomeActivity.this, "失败:"
                                + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(WelcomeActivity.this, "失败:"
                            + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };

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

            //开启子线程
            new Thread(new Runnable() {

                @Override
                public void run() {


                    Gson gson = new Gson();
                    Person person = new Person();
                    person.setId(username);
                    person.setPasswd(password);
                    String json = gson.toJson(person);
                    Log.d("tian", json);
                    String path = "http://192.168.1.3:8080/Login/Login";
                    try

                    {
                        URL url = new URL(path);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        //配置参数
                        connection.setRequestMethod("POST");
                  /*
                   * 设置该参数,才能以流的形式提交数据
                   * 需要将要提交的数据转换为字节输出流
                   */
                        connection.setDoOutput(true);
                        connection.setConnectTimeout(TIME_OUT);
                        connection.setReadTimeout(TIME_OUT);
                        //将提交的参数进行 URL 编码
                        String param = "user=" + URLEncoder.encode(json);
                   /*
                    * 设置请求属性，相当于封装 http 的请求头参数
                    */
                        //设置请求体的的长度
                        connection.setRequestProperty("Content-Length", param.length() + "");
                        //设置请求体的类型
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        //打开链接
                        connection.connect();
                        //获取输出流
                        OutputStream os = connection.getOutputStream();
                        //通过输出流将要提交的数据提交出去
                        os.write(param.getBytes());
                        //关闭输出流
                        os.close();
                        //判断状态码
                        int responseCode = connection.getResponseCode();
                        if (200 == responseCode) {
                            //获取返回值
                            InputStream inputStream = connection.getInputStream();
                            //将字节流转换为字符串
                            String data = StreamUtils.inputStream2String(inputStream);
                            handler.obtainMessage(RESULT_OK, data).sendToTarget();
                        } else {
                            handler.obtainMessage(RESULT_CANCELED, responseCode).sendToTarget();
                        }

                    } catch (
                            Exception e
                            )

                    {
                        e.printStackTrace();
                        handler.obtainMessage(RESULT_CANCELED, e).sendToTarget();
                    }
                }
            }).start();

        } else if (btnSignUp == v) {
            Log.d(TAG, "btnSignUp");
        }
    }
}
