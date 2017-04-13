package no.designsolutions.livenotes;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import no.designsolutions.livenotes.util.PlayerService;

public class myMediaPlayer extends AppCompatActivity {

    static final int FILE_SELECT_CODE = 2;
    private ImageButton play;
    private TextView TLTextView;
    private TextView timeText;
    private SeekBar seekBar;
    private Handler seekHandler = new Handler();
    private TextView titleText;
    private MediaPlayer mPlayer;
    private static String cTitle;

    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.playerServiceBinder binder = (PlayerService.playerServiceBinder) service;
            PlayerService playerService = binder.getService();
            mBound = true;
            mPlayer = playerService.getmPlayer();

            mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    updateSeeker(mp.isPlaying());
                }
            });

            mPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mp) {
                    play.setImageResource(R.drawable.ic_play_button);
                    play.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            play.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getApplicationContext();
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                        play.setImageResource(R.drawable.ic_play_button);
                        play.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(context, "Pause", Toast.LENGTH_LONG).show();
                        updateSeeker(mPlayer.isPlaying());
                    } else {
                        mPlayer.start();
                        play.setImageResource(R.drawable.ic_pause_button);
                        play.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(context, "Play", Toast.LENGTH_LONG).show();
                        updateSeeker(mPlayer.isPlaying());
                    }

                }
            });

            TLTextView = (TextView) findViewById(R.id.textTotalLength);
            titleText = (TextView) findViewById(R.id.textTitle);

            seekBar = (SeekBar) findViewById(R.id.seekBar);


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChanged = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                    if (fromUser) {
                        mPlayer.seekTo(progress);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    progressChanged = seekBar.getProgress();
                    mPlayer.seekTo(progressChanged);
                }
            });


            if (!playerService.isPrepared) {
                SelectFile();
            } else {
                seekBar.setMax(mPlayer.getDuration());
                titleText.setText(cTitle);

                long duration = mPlayer.getDuration();
                long h = TimeUnit.MILLISECONDS.toHours(duration);
                long m = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
                long s = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
                String totalLengthString = String.format("%02d:%02d:%02d", h, m, s);
                TLTextView.setText(totalLengthString);

                if (mPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.ic_pause_button);
                    play.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    play.setImageResource(R.drawable.ic_play_button);
                    play.setBackgroundColor(Color.TRANSPARENT);
                }
                updateSeeker(mPlayer.isPlaying());
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Toast.makeText(getApplicationContext(), "Service disconnected", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mBound = false;
        mPlayer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBound) {
            if (mPlayer.isPlaying()) {
                play.setImageResource(R.drawable.ic_pause_button);
                play.setBackgroundColor(Color.TRANSPARENT);
            } else {
                play.setImageResource(R.drawable.ic_play_button);
                play.setBackgroundColor(Color.TRANSPARENT);
            }


            long duration = mPlayer.getDuration();
            long h = TimeUnit.MILLISECONDS.toHours(duration);
            long m = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
            long s = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
            String totalLengthString = String.format("%02d:%02d:%02d", h, m, s);


            TLTextView.setText(totalLengthString);
            updateSeeker(mPlayer.isPlaying());
        } else {
            Intent intent = new Intent(getApplicationContext(), PlayerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Intent serviceIntent = new Intent(getApplicationContext(), PlayerService.class);
//        startService(serviceIntent);


        if (!mBound) {
            Intent intent = new Intent(this, PlayerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }


        play = (ImageButton) findViewById(R.id.playButton);
        ImageButton next = (ImageButton) findViewById(R.id.nextButton);
        ImageButton prev = (ImageButton) findViewById(R.id.previosuButton);

        play.setBackgroundColor(Color.TRANSPARENT);
        next.setBackgroundColor(Color.TRANSPARENT);
        prev.setBackgroundColor(Color.TRANSPARENT);

        timeText = (TextView) findViewById(R.id.playbackTime);

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(myMediaPlayer.this, TimeSelect.class));

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            if (mBound) {
                updateSeeker(mPlayer.isPlaying());
            }
        }
    };


    private void updateSeeker(boolean isPlaying) {

        int curPos = mPlayer.getCurrentPosition();
        long h = TimeUnit.MILLISECONDS.toHours(curPos);
        long m = TimeUnit.MILLISECONDS.toMinutes(curPos) % 60;
        long s = TimeUnit.MILLISECONDS.toSeconds(curPos) % 60;
        seekBar.setProgress(curPos);
        String curPosString = String.format("%02d:%02d:%02d", h, m, s);
        timeText.setText(curPosString);
        if (isPlaying) {
            seekHandler.postDelayed(run, 250);
        }
    }

    private void SelectFile() {

        Intent selectFileIntent = new Intent(getApplicationContext(), MainActivity.class);

        startActivityForResult(selectFileIntent, FILE_SELECT_CODE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.select_file) {

            SelectFile();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {

            String cFile = data.getStringExtra("filepath");
            cTitle = data.getStringExtra("songtitle");
            titleText.setText(cTitle);

            File myFile = new File(cFile);

            //Initialize mPlayer

            Intent intent = new Intent(getApplicationContext(), PlayerService.class);
            startService(intent);

            try {
                mPlayer.reset();
                mPlayer.setDataSource(myFile.getPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(String.valueOf(R.string.app_name), e.getMessage());
            }

            seekBar.setMax(mPlayer.getDuration());
            updateSeeker(mPlayer.isPlaying());
        }

    }

}


