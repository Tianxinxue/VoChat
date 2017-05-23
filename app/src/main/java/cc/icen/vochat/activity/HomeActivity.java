package cc.icen.vochat.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cc.icen.vochat.R;
import cc.icen.vochat.fragment.ContactsFragment;
import cc.icen.vochat.fragment.MeFragment;
import cc.icen.vochat.fragment.MessageFragment;
import cc.icen.vochat.net.CallManager;


public class HomeActivity extends Activity implements RadioGroup.OnCheckedChangeListener , View.OnTouchListener {

    private static  final String TAG = "HomeActivity";

    private MessageFragment mMessageFragment;
    private ContactsFragment mContactsFragment;
    private MeFragment mMeFragment;

    // 单选按钮组
    private RadioGroup mRadioGroup;
    // 内容页面
    private FrameLayout mFrameLayout;
    private FragmentManager fragmentManager = getFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFrameLayout = (FrameLayout) findViewById(R.id.content);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_bottom);
        mRadioGroup.setOnCheckedChangeListener(this);
        findViewById(R.id.rb_contacts).setOnTouchListener(this);
        findViewById(R.id.rb_me).setOnTouchListener(this);
        findViewById(R.id.rb_message).setOnTouchListener(this);
        //设置 RadioButton 默认选中的页面
        mRadioGroup.check(R.id.rb_contacts);

    }
    private void hideAllFragment(FragmentTransaction transaction){
        if(mMessageFragment != null){
            transaction.hide(mMessageFragment);
        }
        if(mContactsFragment != null){
            transaction.hide(mContactsFragment);
        }
        if(mMeFragment != null){
            transaction.hide(mMeFragment);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId) {
            //消息
            case R.id.rb_message:
                if(mMessageFragment == null) {
                    mMessageFragment = new MessageFragment();
                    transaction.add(R.id.content, mMessageFragment);
                }else{
                    transaction.show(mMessageFragment);
                }
                break;
            // 联系人
            case R.id.rb_contacts:
                if(mContactsFragment == null) {
                    mContactsFragment = new ContactsFragment();
                    transaction.add(R.id.content, mContactsFragment);
                }else{
                    transaction.show(mContactsFragment);
                }
                break;
            // 我
            case R.id.rb_me:
                if(mMeFragment == null) {
                    mMeFragment = new MeFragment();
                    transaction.add(R.id.content, mMeFragment);
                }else{
                    transaction.show(mMeFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();

    }

    private void disableRadioGroup(RadioGroup mRadioGroup , RadioButton mRadioButton) {
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            if(mRadioGroup.getChildAt(i).getId() != mRadioButton.getId()) {
                mRadioGroup.getChildAt(i).setEnabled(false);
            }
        }
    }

    private void enableRadioGroup(RadioGroup mRadioGroup , RadioButton mRadioButton) {
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            if(mRadioGroup.getChildAt(i).getId() != mRadioButton.getId()) {
                mRadioGroup.getChildAt(i).setEnabled(true);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if((v.getId() == R.id.rb_me) || (v.getId() == R.id.rb_contacts) || (v.getId() == R.id.rb_message)){
            RadioButton rb = (RadioButton)findViewById(v.getId());
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                disableRadioGroup(mRadioGroup,(RadioButton) findViewById(v.getId()));
            }else if(event.getAction()==MotionEvent.ACTION_UP){
                enableRadioGroup(mRadioGroup,(RadioButton) findViewById(v.getId()));
            }
        }
        return false;
    }
}
