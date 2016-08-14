package com.example.admin.servicetimer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
    boolean receiverRegistered;
    long currentTime, duration = 20000;

    @Override
    protected void onStart() {
        super.onStart();
        //Register broadcast if service is already running
        if(isMyServiceRunning(TimerService.class)){
            registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
            receiverRegistered = true;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerService = new Intent(this, TimerService.class);


        Button startButton, stopButton;
        timerView = (TextView) findViewById(R.id.timerValue);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(!isMyServiceRunning(TimerService.class)) {
                    timerService.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(timerService);
                    registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
                    receiverRegistered = true;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(isMyServiceRunning(TimerService.class)) {
                    timerView.setText("0:00");
                    timerService.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    timerService.putExtra(Constants.TIMER.DURATION,duration);
                    startService(timerService);
                    unregisterReceiver(broadcastReceiver);

                    receiverRegistered = false;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(receiverRegistered){
            unregisterReceiver(broadcastReceiver);
        }
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

    //Checks if the service is already running.
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
