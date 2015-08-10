package com.myseti.framework.android.net;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by blue on 8/7/15.
 */
public abstract class ClientBase extends Service implements Runnable {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(null != bundle){
            if(null != bundle.getString(IP)){
                ip = bundle.getString(IP);
            }
            if(null != bundle.getString(DOMAIN)){
                domain = bundle.getString(DOMAIN);
            }
            if(0 != bundle.getInt(PORT)){
                port = bundle.getInt(PORT);
            }
        }
        if(!ip.equals("") && (!domain.equals("") || 0!=port)){

        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void createConnection(){
        try {
            Socket so = new Socket(ip, port);
            mSocket = new WeakReference<Socket>(so);
            WriteStream = new BufferedOutputStream(socket.getOutputStream());
            ReadStream = new BufferedInputStream(socket.getInputStream());
            ReadThread thread = new ReadThread(ReadStream,InBoxHandler);
            thread.start();
            SendAliveHandler.postDelayed(AliveRunnable, AliveSplit);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    private void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler InBoxHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SOCKET_OVER_TIME:{

                    break;
                }case PROCOTOL_ERROR:{
                    break;
                }
                case PROCOTOL_SUCCESS:{
                    handlerMessage(msg);
                    break;
                }
            }
        }
    };

    Handler SendAliveHandler = new Handler();
    Runnable AliveRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                Message aliveMsg = InBoxHandler.obtainMessage();
                aliveMsg.what = 0x0000;
                InBoxHandler.sendMessage(aliveMsg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    abstract void handlerMessage(Message msg);

    private String ip = "";
    private int port = 0;
    private String domain = "";

    public static final String IP = "IP";
    public static final String DOMAIN = "DOMAIN";
    public static final String PORT = "PORT";

    private BufferedInputStream ReadStream = null;
    private BufferedOutputStream WriteStream = null;
    private Socket socket = null;
    private WeakReference<Socket> mSocket;

    public static final int SOCKET_OVER_TIME = 0xffff;
    public static final int PROCOTOL_SUCCESS = 0X0000;
    public static final int PROCOTOL_ERROR = 0X0001;

    public static final int AliveSplit = 30000;
}
