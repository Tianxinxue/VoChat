package cc.icen.vochat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.icen.vochat.R;
import cc.icen.vochat.activity.HomeActivity;
import cc.icen.vochat.activity.InCallActivity;
import cc.icen.vochat.media.AudioEncoder;
import cc.icen.vochat.media.UdpReceiver;


public class ContactsFragment extends Fragment {

    private View view;
    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, null);
        lv = (ListView) view.findViewById(R.id.lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), InCallActivity.class);
                startActivity(intent);
            }
        });
        //ArrayList就是动态数组
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("icon", R.mipmap.ic_launcher);
        map.put("text", "张三");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("icon", R.mipmap.ic_launcher);
        map.put("text", "李四");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("icon", R.mipmap.ic_launcher);
        map.put("text", "小明");
        list.add(map);
/**
 * 创建一个 SimpleAdapter 对象
 * 第一个参数 Context 上下文
 * 第二个参数 data 要显示的数据集合
 * 第三个参数 id 指定一个作为 ListView 的子条目的布局文件
 * 第四个参数 String[] 定义得 Map 中 key 组成的数组
 * 第五个参数 int[] 控件的 id 组成的数组,必须与第四个参数的 key 一
 一对应
 */
        SimpleAdapter myAdapter = new SimpleAdapter(getActivity(), list,
                R.layout.contacts_item, new String[] { "icon", "text" }, new int[]
                { R.id.iv_icon, R.id.tv_text });
        lv.setAdapter(myAdapter);

        return view;
    }

}
