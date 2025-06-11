package com.suzhou.concept.lil.readBgService;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.suzhou.concept.AppClient;

/**
 * @title:
 * @date: 2023/10/16 17:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadServiceManager {

    private static ReadServiceManager instance;
    public static Context mContext;
    public ReadService bindService;
    public ServiceConnection conn;

    private ReadServiceManager() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ReadService.MyBinder binder = (ReadService.MyBinder) service;
                bindService = binder.getService();
                bindService.init(mContext);
            }
        };
    }

    public static synchronized ReadServiceManager Instace() {
        mContext = AppClient.context;
        if (instance == null) {
            instance = new ReadServiceManager();
        }
        return instance;
    }
}
