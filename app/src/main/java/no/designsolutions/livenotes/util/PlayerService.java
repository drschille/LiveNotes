package no.designsolutions.livenotes.util;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Daniel on 30.08.2016.
 */
public class PlayerService extends Service {

    public boolean isPrepared = false;
    public MediaPlayer mPlayer = null;

    public MediaPlayer getmPlayer(){
        return mPlayer;
    }

    private final IBinder mBinder = new playerServiceBinder();

    public class playerServiceBinder extends Binder {

        public PlayerService getService() {
            // Return this instance of PlayerService so clients can call public methods
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            }
            return PlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new MediaPlayer();

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
                isPrepared = false;
            }
        });

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                isPrepared = true;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service
        Toast.makeText(getApplicationContext(),"The Service is binding",Toast.LENGTH_LONG);
        return mBinder;
    }


}
