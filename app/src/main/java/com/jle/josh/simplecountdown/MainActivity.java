package com.jle.josh.simplecountdown;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private long millisecondsLeft;
    private CountDownTimer timer;

    private MediaPlayer beep1, beep2, expl;
    private int beepNum =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set user interface layout for this activity.
        setContentView(R.layout.activity_main2);

        beep1 = MediaPlayer.create(this, R.raw.beep1);
        beep2 = MediaPlayer.create(this, R.raw.beep2);
        expl = MediaPlayer.create(this, R.raw.done);



        millisecondsLeft = 0;
        timer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        millisecondsLeft = 0;
        timer = null;
    }

    @Override
    protected void onPause() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        beep1.release();
        beep2.release();
        expl.release();
        super.onDestroy();
    }

    //starts timer , or stops if already running
    public void clickStartStop(View v) {
        if (millisecondsLeft != 0){
            if (timer != null) {
                Log.i(LOG_TAG, "timer stopped");
                timer.cancel();
                timer = null;
            }
            else {
                timer = new CustomCountDownTimer(millisecondsLeft, 1000);
                timer.start();
            }
        }
    }

    //adds 60 seconds to millisecondsLeft and starts timer with new millisecondsLeft
    public void clickPlus(View v) {
        millisecondsLeft += 60 * 1000;
        if (timer != null){
            timer.cancel();
            timer = new CustomCountDownTimer(millisecondsLeft, 1000);
            timer.start();
        }
        displayCount(millisecondsLeft);
    }

    //subtracts 60 seconds from millisecondsLeft and starts timer with new millisecondsLeft
    public void clickMinus(View v) {
        millisecondsLeft -= 60 * 1000;
        millisecondsLeft = Math.max(0, millisecondsLeft);
        if (millisecondsLeft == 0 && timer!=null){
            displayCount(0);
            timer.cancel();
            timer = null;
            expl.start();
        }
        else if (timer != null){
            timer.cancel();
            timer = new CustomCountDownTimer(millisecondsLeft, 1000);
            timer.start();
        }
        displayCount(millisecondsLeft);
    }

    //restarts millisecondsLeft and displayed 0
    public void clickReset(View v) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        displayCount(0);
        millisecondsLeft = 0;
    }

    //displays the current time in millisecondsLeft
    private void displayCount(long milliseconds){
        TextView timeText = (TextView) findViewById(R.id.timeText);

        long seconds = milliseconds/1000;
        long minutes = seconds / 60;
        seconds -= minutes *60;


        String secondsText = formatTime(seconds);
        String minuteText = formatTime(minutes);

        String timeString = minuteText +":"+ secondsText;

        timeText.setText(timeString);
    }

    private String formatTime(long t){
        String text = "";
        if (t==0)
            text = "00";
        else if(t<10)
            text = "0" + String.valueOf(t);
        else
            text = String.valueOf(t);

        return text;

    }

    private class CustomCountDownTimer extends CountDownTimer{
        public CustomCountDownTimer(long duration, long interval){
            super(duration, interval);
        }

        @Override
        public void onTick(long remainingTimeMillis) {
            millisecondsLeft = remainingTimeMillis;
            displayCount(millisecondsLeft);
            if (beepNum==1){
                beep1.start();
                beepNum = 2;
            }
            else if (beepNum==2){
                beep2.start();
                beepNum=1;
            }
        }
        @Override
        public void onFinish() {
            displayCount(0);
            millisecondsLeft = 0;
            expl.start();
        }

    }
}
