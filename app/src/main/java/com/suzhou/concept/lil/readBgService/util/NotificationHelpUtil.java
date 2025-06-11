package com.suzhou.concept.lil.readBgService.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;

/**
 * @title:
 * @date: 2023/7/21 15:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NotificationHelpUtil {

    public static void showNotification(Context context,boolean isPlaying,String types,String bookId,String voaId,String title){
        String playText = "正在播放";
        if (!isPlaying) {
            playText = "暂停播放";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT > 26) {
            notificationManager.createNotificationChannel(new NotificationChannel(String.valueOf(AppClient.appId), String.valueOf(AppClient.appId), NotificationManager.IMPORTANCE_LOW));
            Notification notification = new Notification.Builder(context, String.valueOf(AppClient.appId))
                    .setOngoing(true)
                    .setContentTitle(playText)
                    .setContentText(title)
                    .setContentIntent(null)
                    .setSmallIcon(R.mipmap.app_logo)
                    .setTicker(playText)
//                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
//                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
//                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent())
                    .build();
            notificationManager.notify(222,notification);
        } else {
            Notification.Builder localBuilder = new Notification.Builder(context);
            localBuilder.setOngoing(true).setContentTitle(playText)
                    .setContentText(title)
                    .setContentIntent(null)
                    .setSmallIcon(R.mipmap.app_logo)
                    .setTicker(playText);
//                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
//                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
//                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", receiveCloseIntent());
            notificationManager.notify(222,localBuilder.build());
        }
    }

    private static PendingIntent getIntent(Context context,String types,String bookId,String voaId){
        Intent intent = new Intent();
        return PendingIntent.getActivity(context, 0, intent, getPendFlag());
    }

    private static int getPendFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_IMMUTABLE;
        } else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }
}
