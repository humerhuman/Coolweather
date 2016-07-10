package com.theweather.app.theweather.util;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
