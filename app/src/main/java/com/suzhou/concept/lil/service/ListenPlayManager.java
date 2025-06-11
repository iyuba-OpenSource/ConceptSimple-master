package com.suzhou.concept.lil.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * @title:
 * @date: 2023/10/18 14:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenPlayManager {
    private static ListenPlayManager instance;
    public ListenPlayService playService;
    public ServiceConnection conn;

    public static ListenPlayManager getInstance(){
        if (instance==null){
            synchronized (ListenPlayManager.class){
                if (instance==null){
                    instance = new ListenPlayManager();
                }
            }
        }
        return instance;
    }

    public ListenPlayManager(){
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ListenPlayService.MyBinder myBinder = (ListenPlayService.MyBinder) service;
                playService = myBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }
}
