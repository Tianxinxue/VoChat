package cc.icen.vochat;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

/**
 * Created by Tian on 2016/7/2.
 */
public class HomeActivity extends Activity implements RadioGroup.OnCheckedChangeListener {


    // 单选按钮组
    private RadioGroup mRadioGroup;
    // 内容页面
    private FrameLayout mFrameLayout;
    private FragmentManager fragmentManager = getFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFrameLayout = (FrameLayout) findViewById(R.id.content);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_firewall);
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
                transaction.replace(R.id.content, new MessageFragment());
                break;
            // 联系人
            case R.id.rb_phone:
                transaction.replace(R.id.content, new ContactsFragment());
                break;
            // 我
            case R.id.rb_black:
                transaction.replace(R.id.content, new MeFragment());
                break;
        }
        //[6]最后一布 记得 提交事物
        transaction.commit();

    }

}
