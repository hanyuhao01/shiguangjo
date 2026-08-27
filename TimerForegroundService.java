package com.shiguangju.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * 前台计时服务 —— 保证应用后台不被杀掉
 * 即使应用被切到后台，计时器依然持续运行
 */
public class TimerForegroundService extends Service {

    private static final String CHANNEL_ID = "shiguangju_timer_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String TAG = "TimerFGSvc";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification("拾光橘正在计时中..."));
        Log.d(TAG, "Foreground service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("STOP".equals(action)) {
                stopSelf();
            } else if ("UPDATE".equals(action)) {
                String text = intent.getStringExtra("text");
                if (text != null) {
                    updateNotification(text);
                }
            }
        }
        // 服务被杀死后自动重启
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Foreground service destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // 用户从最近任务移除应用时，通知用户计时仍在后台运行
        Log.d(TAG, "Task removed - service continues in background");
        // 不调用 super，保持服务存活
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "拾光橘计时通知",
                    NotificationManager.IMPORTANCE_LOW  // LOW = 不打扰用户
            );
            channel.setDescription("后台计时运行时显示的通知");
            channel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification(String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        return builder
                .setContentTitle("🍊 拾光橘")
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_LOW)
                .build();
    }

    private void updateNotification(String text) {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(text));
        }
    }

    /**
     * 静态方法：启动前台服务
     */
    public static void startService(Context context, String text) {
        Intent intent = new Intent(context, TimerForegroundService.class);
        if (text != null) {
            intent.putExtra("text", text);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * 静态方法：停止前台服务
     */
    public static void stopService(Context context) {
        Intent intent = new Intent(context, TimerForegroundService.class);
        intent.setAction("STOP");
        context.startService(intent);
    }

    /**
     * 静态方法：更新通知文本
     */
    public static void updateService(Context context, String text) {
        Intent intent = new Intent(context, TimerForegroundService.class);
        intent.setAction("UPDATE");
        intent.putExtra("text", text);
        context.startService(intent);
    }
}
