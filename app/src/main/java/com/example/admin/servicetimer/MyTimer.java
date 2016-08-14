package com.example.admin.servicetimer;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
/**From docs the message queue is a Low-level class holding the list of messages to be dispatched by a Looper.
 * Messages are not added directly to a MessageQueue, but rather through Handler objects associated with the Looper.
 *You can retrieve the MessageQueue for the current thread with Looper.myQueue().**/

/**In this class the handler just pushes the current thread (this ie MyTimer class) into the
 * message queue. The second paramater is simily how long to delay before putting it into the queue.
 * I put this delay at 1 second to give it a countdown effect. Remeber it is the same thread/class so
 * all info such as current time is updated. The looper takes care of the work i think of processing the queue.**/
//Remember all the work for the thread takes place in run() so only this code gets looped by the handler.

public class MyTimer implements Runnable {
    MainActivity activity;
    Handler handler;
    TextView timerView;
    long current_time,duration;

    public MyTimer(MainActivity activity){
        this.activity = activity;
        this.handler = new Handler();
        this.current_time = 0L;
        timerView = (TextView) activity.findViewById(R.id.timerValue);
    }

    public MyTimer startTimer(int duration){
        this.current_time = 0L;
        this.duration = duration;
        this.resetTimer();
        handler.postDelayed(this,0);
        return this;
    }
    public MyTimer resetTimer(){
        timerView.setText("0:00");
        handler.removeCallbacks(this);
        return this;
    }

    @Override
    public void run() {
        if(current_time == duration){
            Toast.makeText(activity,"Timer done",Toast.LENGTH_SHORT).show();
            resetTimer();
            return;
        }
        current_time += 1000;
        int secs = (int) (current_time / 1000);
        int minutes = secs / 60;

        timerView.setText(Integer.toString(minutes) + ":" + String.format("%02d", secs%60));
        handler.postDelayed(this, 100);
    }
}