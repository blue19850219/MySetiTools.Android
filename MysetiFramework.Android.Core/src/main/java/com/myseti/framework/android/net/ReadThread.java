package com.myseti.framework.android.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.myseti.framework.android.core.HexTools;
import com.myseti.framework.android.net.ClientBase;
import com.myseti.framework.core.protocol.ProtocolEntity;

import java.io.BufferedInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by blue on 8/7/15.
 */
public class ReadThread extends Thread {
    public ReadThread(BufferedInputStream stream,Handler handler){
        ReadStream = stream;
        OutboxHandler = handler;
        MaxTimeOut = 30000;
        init();
    }
    public ReadThread(BufferedInputStream stream,Handler handler,int overTime){
        ReadStream = stream;
        OutboxHandler = handler;
        MaxTimeOut = overTime;
        init();
    }

    public void init(){
        OverTimeHandler.postDelayed(OverTimeRunnable,MaxTimeOut);
    }


    @Override
    public void run() {
        while (!Thread.interrupted()) {

            Log.i(Tag, Thread.currentThread().getName() + " 接收网关消息");
            try {
                byte[] b4 = recBytes(4);
                // Thread.sleep(100);
                if (b4 == null) {
                    continue;
                }
                //package length
                int length = HexTools.byte2Int(b4);
                Log.d(Tag, "Get protocol length:" + length);
                Log.i(Tag, "收到帧时间" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()))
                        + "收到帧长度：" + length);
                ProtocolEntity protocol = new ProtocolEntity(recBytes(length));
                Message msg = OutboxHandler.obtainMessage();
                msg.what = 0;
                msg.obj = protocol;
                OutboxHandler.sendMessage(msg);
                Thread.sleep(200);
                CurrentTimeMillis = System.currentTimeMillis();
                Log.d("OverTime", "重置超时时间" + CurrentTimeMillis);
            } catch (Exception ex) {
                Log.e(Tag, "监听接收时出错：" + (null == ex ? "" : ex.getMessage()));
                break;
            }
        }
        Log.e(Tag, "接收线程中断！");
        OutboxHandler.sendEmptyMessage(ClientBase.SOCKET_OVER_TIME);
    }


    public byte[] recBytes(int length) throws Exception {
        byte[] result = new byte[length];
        int hasRec = 0;
        int isRead = 0;
        do {
            byte[] buffer = new byte[length - hasRec];
            isRead = ReadStream.read(buffer);
            if (isRead != -1) {
                System.arraycopy(buffer, 0, result, hasRec, isRead);
                hasRec += isRead;
                if (isRead == 0)
                    Thread.sleep(100);
            } else {
                if (hasRec > 0) {
                    return null;
                }
            }
        } while (hasRec < length);
        return result;
    }

    // 超时验证：
    private long CurrentTimeMillis = System.currentTimeMillis();
    private Handler OverTimeHandler = new Handler();
    private Runnable OverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d("Gateway OverTime", "检查超时:" + (System.currentTimeMillis() - CurrentTimeMillis));
            if (System.currentTimeMillis() - CurrentTimeMillis > MaxTimeOut) {
                Log.e("Gateway OverTime", "服务器连接超时！重新连接！");
                OutboxHandler.sendEmptyMessage(ClientBase.SOCKET_OVER_TIME);
            } else {
                CurrentTimeMillis = System.currentTimeMillis();
                OverTimeHandler.removeCallbacks(this);
                OverTimeHandler.postDelayed(this, MaxTimeOut);
            }

        }
    };

    BufferedInputStream ReadStream = null;
    Handler OutboxHandler = null;
    String Tag = this.getClass().getSimpleName();
    int MaxTimeOut = 0;
}
