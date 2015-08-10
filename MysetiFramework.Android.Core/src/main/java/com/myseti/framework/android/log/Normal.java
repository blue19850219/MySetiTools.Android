package com.myseti.framework.android.log;

import android.util.Log;

/**
 * Created by blue on 8/7/15.
 * 调用Android自带的日志函数
 */
public class Normal extends LogUtils {
    private Normal(){}
    public static LogUtils getInstance(){
        if(null == log){
            log = new Normal();
        }
        return log;
    }

    @Override
    public void showError(String tag, String value) {
        Log.e(tag,value);
    }

    @Override
    public void showWarn(String tag, String value) {
        Log.w(tag,value);
    }

    @Override
    public void showDebug(String tag, String value) {
        Log.d(tag,value);
    }

    @Override
    public void showInfo(String tag, String value) {
        Log.i(tag,value);
    }

    public static LogUtils log = new Normal();
}
