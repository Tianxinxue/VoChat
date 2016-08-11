package cc.icen.vochat.util;

/**
 * Created by Tian on 2016/8/11.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}