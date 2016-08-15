package com.example.admin.servicetimer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.servicetimer.service.Constants;
import com.example.admin.servicetimer.service.TimerService;

public class MainActivity extends Activity {

    TextView timerView;
    Intent timerService;
    long currentTime, duration = 40000;

    @Override
    protected void onStart() {
        super.onStart();
        timerService = new Intent(this, TimerService.class);
        //Register broadcast if service is already running
        if(isMyServiceRunning(TimerService.class)){
            registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button startButton, stopButton;
        timerView = (TextView) findViewById(R.id.timerValue);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(!isMyServiceRunning(TimerService.class)) {
                    timerService.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    timerService.putExtra(Constants.TIMER.DURATION,duration);
                    startService(timerService);
                    registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(isMyServiceRunning(TimerService.class)) {
                    timerView.setText("0:00");
                    timerService.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    startService(timerService);
                    unregisterReceiver(broadcastReceiver);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isMyServiceRunning(TimerService.class)) {
            timerView.setText("0:00");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /******************** Broadcast Receiver **************************************/

    //Receives the broadcast sent out by the service and updates the UI accordingly.
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!updateUI(intent)){
                if(!updateUI(timerService)){
                    timerService.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    startService(timerService);
                    showTimerCompleteNotification();
                }
            }
        }
    };
    //Receives the extra to update current timer and then updates the textView.
    public boolean updateUI(Intent intent){
        if(!intent.hasExtra(Constants.TIMER.CURRENT_TIME)) return false;

        this.currentTime = intent.getLongExtra(Constants.TIMER.CURRENT_TIME, 0L);

        if(this.currentTime == duration){
            timerView.setText("0:00");
            Toast.makeText(this,"Timer done",Toast.LENGTH_SHORT).show();
            return false;
        }

        int secs = (int) (currentTime / 1000);
        int minutes = secs / 60;

        timerView.setText(Integer.toString(minutes) + ":" + String.format("%02d", secs%60));
        return true;
    }
    /******************************************************************************************/


    /************* Helper Methods ****************************/
    private void showTimerCompleteNotification() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Timer Done!")
                        .setContentText("Congrats")
                        .setContentIntent(resultPendingIntent)
                        .setColor(Color.BLACK)
                        .setLights(Color.BLUE, 500, 500)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.InboxStyle());

        // Gets an instance of the NotificationManager service
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());

        //Cancel the notification after a little while
        Handler h = new Handler();
        long delayInMilliseconds = 5000;

        h.postDelayed(new Runnable() {
            public void run() {
                mNotifyMgr.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
            }
        }, delayInMilliseconds);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
