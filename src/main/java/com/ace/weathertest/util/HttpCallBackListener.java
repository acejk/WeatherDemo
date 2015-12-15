package com.ace.weathertest.util;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public interface HttpCallBackListener {
    void onfinish(String response);

    void onError(Exception e);

}
