package com.jle.josh.simplecountdown;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private long millisecondsLeft=0;
    private CountDownTimer timer;

    private MediaPlayer beep1, beep2, expl;
    private int beepNum =1;

    private long minutes=0, seconds =0;

    EditText minutesText, secondsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set user interface layout for this activity.
        setContentView(R.layout.activity_main);

        //Initialize MediaPlayers
        beep1 = MediaPlayer.create(this, R.raw.beep1);
        beep2 = MediaPlayer.create(this, R.raw.beep2);
        expl = MediaPlayer.create(this, R.raw.done);

        //Get EditTexts
        minutesText = (EditText) findViewById(R.id.minutesText);
        secondsText = (EditText) findViewById(R.id.secondsText);

        //Add TextWatchers to EditTexts
        minutesText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Set minutes if applicable
                if(minutesText.getText().length() > 0){
                    minutes = Long.parseLong(minutesText.getText().toString());
                    millisecondsLeft = (minutes*60+seconds) * 1000;
                }
                //Reset to double zeroes if both texts empty
                else if (secondsText.getText().length()==0){
                    minutesText.setText("00");
                    secondsText.setText("00");
                }
            }
        });

        secondsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Set seconds if applicable
                if(secondsText.getText().length() > 0){
                    seconds = Long.parseLong(secondsText.getText().toString());
                    millisecondsLeft = (minutes*60+seconds) * 1000;
                }
                //Reset to double zeroes if both texts empty
                else if (minutesText.getText().length()==0){
                    minutesText.setText("00");
                    secondsText.setText("00");
                }
            }
        });
    }


    //Release mediaplayer resources
    @Override
    protected void onDestroy(){
        beep1.release();
        beep2.release();
        expl.release();
        super.onDestroy();
    }


    //Allow time texts to be focused and edited
    private void activateEditTexts(){
        minutesText.setFocusableInTouchMode(true);
        minutesText.setFocusable(true);
        secondsText.setFocusableInTouchMode(true);
        secondsText.setFocusable(true);
    }



    //Starts timer, or stops if already running
    public void clickStartStop(View v) {
        //Do nothing if millisecondsLeft is 0
        if (millisecondsLeft == 0)
            return;

        if (timer == null) {
            //Disable clicking or editing texts while timer is running
            minutesText.setFocusable(false);
            minutesText.setClickable(false);
            secondsText.setFocusable(false);
            secondsText.setClickable(false);
            timer = new CountDownTimer(millisecondsLeft, 1000) {
                //Called each tick of the interval
                @Override
                public void onTick(long remainingTimeMillis) {
                    millisecondsLeft = remainingTimeMillis;
                    displayTime(millisecondsLeft);

                    //Play correct beep sound
                    if (beepNum == 1) {
                        beep1.start();
                        beepNum = 2;
                    } else if (beepNum == 2) {
                        beep2.start();
                        beepNum = 1;
                    }
                }

                //Called at end of time
                @Override
                public void onFinish() {
                    displayTime(0);
                    millisecondsLeft = 0;
                    expl.start();
                    activateEditTexts();
                    timer = null;
                }
            };
            timer.start();
            Log.i(LOG_TAG, "Timer started");
        }
        else{
            timer.cancel();
            timer = null;
            activateEditTexts();
            Log.i(LOG_TAG, "Timer stopped");
        }

    }


    //Restarts millisecondsLeft and displays 00:00
    public void clickReset(View v) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        displayTime(0);
        millisecondsLeft = 0;
        activateEditTexts();
    }

    //Displays the current time in minutes:seconds left
    private void displayTime(long milliseconds){
        long seconds = milliseconds/1000;
        long minutes = seconds / 60;
        seconds -= minutes *60;
        
        String secondsString = formatTime(seconds);
        String minuteString = formatTime(minutes);
        minutesText.setText(minuteString);
        secondsText.setText(secondsString);
    }

    //Format seconds/minutes with leading zero, double zeroes, or no zeroes
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

}
