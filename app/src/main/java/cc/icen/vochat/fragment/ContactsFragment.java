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


public class ContactsFragment extends Fragment {

    private AudioEncoder aEncoder;
    private UdpReceiver receiver;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, null);
        ((Button) view.findViewById(R.id.start_button)).setOnClickListener(btnClick);
        ((Button) view.findViewById(R.id.stop_button)).setOnClickListener(btnClick);
        enableButtons(false);

        return view;
    }


    private void enableButton(int id, boolean isEnable) {
        ((Button) view.findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.start_button, !isRecording);
        enableButton(R.id.stop_button, isRecording);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_button: {
                    enableButtons(true);
                    startRecording();
                    receiver = new UdpReceiver();
                    try {
                        receiver.fillBuffer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.stop_button: {
                    enableButtons(false);

                    try {
                        stopRecording();
                    } catch (IOException e) {
                        //  TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };

    private void startRecording() {

        aEncoder = new AudioEncoder(0, 0, 0, 0);
        aEncoder.StartEncoderThread();
    }


    private void stopRecording() throws IOException {
        //  stops the recording activity

        if (null != aEncoder) {

            aEncoder.StopThread();
        }
        if (receiver != null)
            receiver.close();

    }

}
