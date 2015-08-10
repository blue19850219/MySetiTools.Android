package com.myseti.framework.android.log;

/**
 * Created by blue on 8/7/15.
 */
public abstract class LogUtils {

    public static boolean enable = false;
    public void e(String tag,String value){
        if(enable){
            showError(tag,value);
        }
    }
    public void d(String tag,String value){
        if(enable){
            showDebug( tag, value);
        }
    }
    public void w(String tag,String value){
        if(enable){
            showWarn( tag, value);
        }
    }
    public void i(String tag,String value){
        if(enable){
            showInfo( tag, value);
        }
    }



    public abstract void showError(String Tag,String value);
    public abstract void showWarn(String Tag,String value);
    public abstract void showDebug(String Tag,String value);
    public abstract void showInfo(String Tag,String value);
}
