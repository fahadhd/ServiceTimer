package com.example.admin.servicetimer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.admin.servicetimer.MainActivity;
import com.example.admin.servicetimer.R;

//Starts a service which sends 1000 interval queues to the messagequeue via handler acting as a timer.
//Also broadcasts this information via broacster so other activities like main can receive it and updateUI.
public class TimerService extends Service{
    Intent intent;
    public static final String TAG = TimerService.class.getSimpleName();
    //Intent filter used to receive the broadcast




    private final Handler handler = new Handler();
    long currentTime, duration;

    @Override
    public void onCreate() {
        super.onCreate();
        currentTime = 0L;
        duration = 0L;
        intent = new Intent(Constants.ACTION.BROADCAST_ACTION);
        handler.removeCallbacks(timerThread);
        handler.postDelayed(timerThread,0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                if (intent.hasExtra(Constants.TIMER.DURATION)) {
                    duration = intent.getLongExtra(Constants.TIMER.DURATION, 0);
                }
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, showNotification());
            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    //Thread handler uses to push to messagequeue. This creates a timer effect.
    private Runnable timerThread = new Runnable() {
        @Override
        public void run() {
            if(currentTime == duration){
                stopSelf();
                return;
            }
            currentTime += 1000;
            sendTimerInfo();
            handler.postDelayed(this,1000);
        }
    };

    private void sendTimerInfo(){
        Log.d(TAG,"timer running: tick is "+currentTime);
        intent.putExtra(Constants.TIMER.CURRENT_TIME,currentTime);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"timer service finished");

        handler.removeCallbacks(timerThread);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Notification showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Service Timer")
                .setTicker("Count up timer")
                .setContentText("timer")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        return notification;
    }
}
