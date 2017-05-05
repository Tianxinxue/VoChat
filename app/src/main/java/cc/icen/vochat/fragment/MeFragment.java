package cc.icen.vochat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cc.icen.vochat.R;
import cc.icen.vochat.media.RingPlayer;

/**
 * Created by Tian on 2016/7/2.
 */
public class MeFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, null);
        ((Button) view.findViewById(R.id.start_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RingPlayer(getActivity());
            }
        });

        return view;
    }
}