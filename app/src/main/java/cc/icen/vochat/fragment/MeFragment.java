package cc.icen.vochat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import cc.icen.vochat.R;
import cc.icen.vochat.media.AudioEncoder;
import cc.icen.vochat.media.UdpReceiver;

/**
 * Created by Tian on 2016/7/2.
 */
public class MeFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, null);

        return view;
    }
}