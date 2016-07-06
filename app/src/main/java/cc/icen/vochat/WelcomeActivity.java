package cc.icen.vochat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private String TAG = "tian-";

    private Button btnSignUp;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (btnSignIn == v) {
            Log.d(TAG, "btnSignIn");
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this,HomeActivity.class);
            startActivity(intent);
        } else if (btnSignUp == v) {
            Log.d(TAG, "btnSignUp");
        }
    }
}
