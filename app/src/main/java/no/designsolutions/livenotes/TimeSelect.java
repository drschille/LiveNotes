package no.designsolutions.livenotes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import no.designsolutions.livenotes.util.PlayerService;

/**
 * Created by Daniel on 13.10.2015.
 */
public class TimeSelect extends Activity {

    private android.media.MediaPlayer mPlayer;
    private PlayerService playerService;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.playerServiceBinder binder = (PlayerService.playerServiceBinder) service;
            playerService = binder.getService();
            mBound = true;
            mPlayer = playerService.getmPlayer();

            int duration = mPlayer.getDuration();
            int currentPosition = mPlayer.getCurrentPosition();



            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width = (int) (dm.widthPixels * 0.8);
            int height = (int) (dm.heightPixels * 0.3);
            getWindow().setLayout(width, height);


            hourPicker = (NumberPicker) findViewById(R.id.hourPicker);
            minutePicker = (NumberPicker) findViewById(R.id.minutePicker);
            secondPicker = (NumberPicker) findViewById(R.id.secondPicker);

            maxhours = (int) timeUnit.toHours(duration);
            maxmins = (int) timeUnit.toMinutes(duration) % 60;
            maxseconds = (int) timeUnit.toSeconds(duration) % 60;

            String durText = String.valueOf(maxhours) + " Hours, " + String.valueOf(maxmins) + " Minutes and " + maxseconds + " seconds!";
            Toast.makeText(getApplicationContext(), durText, Toast.LENGTH_LONG).show();

            curSec = (int) timeUnit.toSeconds(currentPosition) % 60;
            curMin = (int) timeUnit.toMinutes(currentPosition) % 60;
            curHour = (int) timeUnit.toHours(currentPosition);

            hourPicker.setMinValue(minVal);
            hourPicker.setMaxValue(maxhours);
            minutePicker.setMinValue(minVal);
            minutePicker.setMaxValue(maxVal);
            secondPicker.setMinValue(minVal);
            secondPicker.setMaxValue(maxVal);

            hourPicker.setValue(curHour);
            minutePicker.setValue(curMin);
            secondPicker.setValue(curSec);

            fixMaxMinValues();

            secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    if (oldVal == minVal && newVal == maxVal) {
                        if (minutePicker.getValue() == minVal && hourPicker.getValue() != minVal) {
                            hourPicker.setValue(hourPicker.getValue() - 1);
                        }
                        minutePicker.setValue(minutePicker.getValue() - 1);

                    } else if (oldVal == maxVal && newVal == minVal) {
                        if (minutePicker.getValue() == maxVal && hourPicker.getValue() != maxhours) {
                            hourPicker.setValue(hourPicker.getValue() + 1);
                        }
                        minutePicker.setValue(minutePicker.getValue() + 1);
                    }

                    fixMaxMinValues();
                    setTime();

                }
            });

            minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    if (oldVal == minVal && newVal == maxVal) {
                        if (hourPicker.getValue() != 0) {
                            hourPicker.setValue(hourPicker.getValue() - 1);
                        }

                    } else if (oldVal == maxVal && newVal == minVal) {
                        if (hourPicker.getValue() != maxhours) {
                            hourPicker.setValue(hourPicker.getValue() + 1);
                        }
                    }

                    fixMaxMinValues();
                    setTime();

                }
            });

            hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    fixMaxMinValues();
                    setTime();

                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    private int curSec;
    private int curMin;
    private int curHour;
    private int maxhours = 0;
    private int maxmins = 0;
    private int maxseconds = 0;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    final int minVal = 0;
    final int maxVal = 59;
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    NumberPicker secondPicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_select);

        // Bind to PlayerService
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);



    }

    private void setTime() {
        int newPos;

        newPos = (int) timeUnit.convert(hourPicker.getValue(), TimeUnit.HOURS)
                + (int) timeUnit.convert(minutePicker.getValue(), TimeUnit.MINUTES)
                + (int) timeUnit.convert(secondPicker.getValue(), TimeUnit.SECONDS);

        mPlayer.seekTo(newPos);

    }

    void fixMaxMinValues() {

        curHour = hourPicker.getValue();
        curMin = minutePicker.getValue();
        curSec = secondPicker.getValue();

        if (maxhours == minVal) {

            minutePicker.setMinValue(minVal);
            minutePicker.setMaxValue(maxmins);
            minutePicker.setWrapSelectorWheel(false);

            if (maxmins == minVal) {

                secondPicker.setMinValue(minVal);
                secondPicker.setMaxValue(maxseconds);
                secondPicker.setWrapSelectorWheel(false);

            } else if (curMin == maxmins) {

                lastMinute();

            } else {

                middleMinute();
            }

        } else if (curHour == maxhours) {

            lastHour();
        } else if (curHour == minVal) {
            firsthour();
        } else {
            middleHour();
        }


    }

    private void firsthour() {

        if (curMin == minVal) {
            firstMinute();
        } else {
            middleHour();
        }
    }

    private void firstMinute() {
        minutePicker.setMinValue(minVal);
        minutePicker.setMaxValue(minVal + 1);
        minutePicker.setWrapSelectorWheel(false);

        if (curSec == minVal) {
            secondPicker.setMinValue(minVal);
            secondPicker.setMaxValue(minVal + 1);
            secondPicker.setWrapSelectorWheel(false);
        } else {
            middleMinute();
        }
    }

    private void lastHour() {

        if (curMin > maxmins) {
            minutePicker.setValue(maxmins);
            curMin = maxmins;
        }

        if (curMin == maxmins) {
            lastMinute();
        } else {
            middleMinute();
        }
    }


    private void lastMinute() {

        if (curSec > maxseconds) {
            secondPicker.setValue(maxseconds);
            curSec = maxseconds;
        }

        if (curSec == maxseconds && maxseconds != minVal) {
            secondPicker.setMaxValue(maxseconds);
            secondPicker.setMinValue(minVal);
            secondPicker.setWrapSelectorWheel(false);
        } else {
            secondPicker.setMaxValue(maxVal);
            secondPicker.setMinValue(minVal);
            secondPicker.setWrapSelectorWheel(true);
        }
    }

    private void middleMinute() {

        secondPicker.setMinValue(minVal);
        secondPicker.setMaxValue(maxVal);
        secondPicker.setWrapSelectorWheel(true);
    }

    private void middleHour() {

        minutePicker.setMinValue(minVal);
        minutePicker.setMaxValue(maxVal);
        minutePicker.setWrapSelectorWheel(true);

        middleMinute();
    }
}

