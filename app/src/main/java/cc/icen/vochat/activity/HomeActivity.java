package cc.icen.vochat.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import cc.icen.vochat.R;
import cc.icen.vochat.fragment.ContactsFragment;
import cc.icen.vochat.fragment.MeFragment;
import cc.icen.vochat.fragment.MessageFragment;

/**
 * Created by Tian on 2016/7/2.
 */
public class HomeActivity extends Activity implements RadioGroup.OnCheckedChangeListener {



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
        mMessageFragment = new MessageFragment();
        mContactsFragment = new ContactsFragment();
        mMeFragment = new MeFragment();
        mFrameLayout = (FrameLayout) findViewById(R.id.content);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_bottom);
        mRadioGroup.setOnCheckedChangeListener(this);
        //设置 RadioButton 默认选中的页面，这里默认短信页面
        mRadioGroup.check(R.id.rb_sms);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            //消息
            case R.id.rb_sms:
                transaction.replace(R.id.content, mMessageFragment);
                break;
            // 联系人
            case R.id.rb_phone:
                transaction.replace(R.id.content, mContactsFragment);
                break;
            // 我
            case R.id.rb_black:
                transaction.replace(R.id.content, mMeFragment);
                break;
        }
        //[6]最后一步 记得 提交事物
        transaction.commit();

    }

}
