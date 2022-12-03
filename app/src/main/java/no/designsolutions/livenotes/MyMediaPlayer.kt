package no.designsolutions.livenotes

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.TextView
import android.widget.SeekBar
import no.designsolutions.livenotes.PlayerLogDbAdapter
import android.content.ServiceConnection
import android.content.ComponentName
import android.os.IBinder
import no.designsolutions.livenotes.util.PlayerService.playerServiceBinder
import no.designsolutions.livenotes.util.PlayerService
import android.media.MediaPlayer.OnSeekCompleteListener
import android.media.MediaPlayer.OnCompletionListener
import no.designsolutions.livenotes.R
import android.widget.Toast
import android.widget.SeekBar.OnSeekBarChangeListener
import no.designsolutions.livenotes.MyMediaPlayer
import android.content.Intent
import android.os.Bundle
import no.designsolutions.livenotes.TimeSelect
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import no.designsolutions.livenotes.activities.NotebooksActivity2
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import android.os.Build
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MyMediaPlayer : AppCompatActivity() {
    private var play: ImageButton? = null
    private var TLTextView: TextView? = null
    private var timeText: TextView? = null
    private var seekBar: SeekBar? = null
    private val seekHandler = Handler()
    private var titleText: TextView? = null
    private var mPlayer: MediaPlayer? = null
    private var currentPlaying: String? = null
    private val playerAdapter = PlayerLogDbAdapter(this@MyMediaPlayer)
    private var mBound = false
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as playerServiceBinder
            val playerService = binder.service
            mBound = true
            mPlayer = playerService.getmPlayer()
            mPlayer?.setOnSeekCompleteListener { mp: MediaPlayer ->
                updateSeeker(
                    mp.isPlaying
                )
            }
            mPlayer?.setOnCompletionListener { mp: MediaPlayer? ->
                play!!.setImageResource(R.drawable.ic_play_button)
                play!!.setBackgroundColor(Color.TRANSPARENT)
            }
            play!!.setOnClickListener { v: View? ->
                val context: Context = this@MyMediaPlayer
                if (mPlayer?.isPlaying == true) {
                    mPlayer?.pause()
                    play!!.setImageResource(R.drawable.ic_play_button)
                    play!!.setBackgroundColor(Color.TRANSPARENT)
                    Toast.makeText(context, "Pause", Toast.LENGTH_LONG).show()
                    updateSeeker(mPlayer?.isPlaying == true)
                } else {
                    mPlayer?.start()
                    play!!.setImageResource(R.drawable.ic_pause_button)
                    play!!.setBackgroundColor(Color.TRANSPARENT)
                    Toast.makeText(context, "Play", Toast.LENGTH_LONG).show()
                    updateSeeker(mPlayer?.isPlaying == true)
                }
            }
            TLTextView = findViewById(R.id.textTotalLength)
            titleText = findViewById(R.id.textTitle)
            seekBar = findViewById(R.id.seekBar)
            seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                var progressChanged = 0
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    progressChanged = progress
                    if (fromUser) {
                        mPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    progressChanged = seekBar.progress
                    mPlayer?.seekTo(progressChanged)
                }
            })
            if (!playerService.isPrepared) {
                SelectFile()
            } else {
                seekBar?.max = mPlayer?.duration ?: 0
                titleText?.text = cTitle
                val duration = mPlayer?.duration?.toLong() ?: 0L
                val h = TimeUnit.MILLISECONDS.toHours(duration)
                val m = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
                val s = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
                val totalLengthString = String.format(formatString, h, m, s)
                TLTextView?.setText(totalLengthString)
                if (mPlayer?.isPlaying == true) {
                    play!!.setImageResource(R.drawable.ic_pause_button)
                    play!!.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    play!!.setImageResource(R.drawable.ic_play_button)
                    play!!.setBackgroundColor(Color.TRANSPARENT)
                }
                updateSeeker(mPlayer?.isPlaying == true)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
            Toast.makeText(this@MyMediaPlayer, "Service disconnected", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            val id = playerAdapter.updateData(
                currentPlaying,
                mPlayer!!.currentPosition,
                System.currentTimeMillis()
            )
            Log.d("DSDESIGN", "onActivityResult: $id")
        }
        unbindService(mConnection)
        mBound = false
        mPlayer = null
    }

    override fun onResume() {
        super.onResume()
        if (mBound) {
            if (mPlayer!!.isPlaying) {
                play!!.setImageResource(R.drawable.ic_pause_button)
                play!!.setBackgroundColor(Color.TRANSPARENT)
            } else {
                play!!.setImageResource(R.drawable.ic_play_button)
                play!!.setBackgroundColor(Color.TRANSPARENT)
            }
            val duration = mPlayer!!.duration.toLong()
            val h = TimeUnit.MILLISECONDS.toHours(duration)
            val m = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
            val s = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
            val totalLengthString = String.format(formatString, h, m, s)
            TLTextView!!.text = totalLengthString
            updateSeeker(mPlayer!!.isPlaying)
        } else {
            val intent = Intent(applicationContext, PlayerService::class.java)
            bindService(intent, mConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_media_player)
        setSupportActionBar(toolbar)

//        Intent serviceIntent = new Intent(getApplicationContext(), PlayerService.class);
//        startService(serviceIntent);
        if (!mBound) {
            val intent = Intent(this, PlayerService::class.java)
            bindService(intent, mConnection, BIND_AUTO_CREATE)
        }
        play = findViewById(R.id.playButton)
        val next = findViewById<ImageButton>(R.id.nextButton)
        val prev = findViewById<ImageButton>(R.id.previosuButton)
        play?.setBackgroundColor(Color.TRANSPARENT)
        next.setBackgroundColor(Color.TRANSPARENT)
        prev.setBackgroundColor(Color.TRANSPARENT)
        timeText = findViewById(R.id.playbackTime)
        timeText?.setOnClickListener {
            startActivity(
                Intent(
                    this@MyMediaPlayer,
                    TimeSelect::class.java
                )
            )
        }
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    var run = Runnable {
        if (mBound) {
            updateSeeker(mPlayer!!.isPlaying)
        }
    }

    private fun updateSeeker(isPlaying: Boolean) {
        val curPos = mPlayer!!.currentPosition
        val h = TimeUnit.MILLISECONDS.toHours(curPos.toLong())
        val m = TimeUnit.MILLISECONDS.toMinutes(curPos.toLong()) % 60
        val s = TimeUnit.MILLISECONDS.toSeconds(curPos.toLong()) % 60
        seekBar!!.progress = curPos
        val curPosString = String.format(formatString, h, m, s)
        timeText!!.text = curPosString
        if (isPlaying) {
            seekHandler.postDelayed(run, 250)
        }
    }

    private fun SelectFile() {
        val selectFileIntent =
            Intent(applicationContext, NotebooksActivity2::class.java) //SelectFileActivity.class);
        startActivity(selectFileIntent)
        //        startActivityForResult(selectFileIntent, FILE_SELECT_CODE);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.select_file) {
            SelectFile()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (checkPermissionWRITE_EXTERNAL_STORAGE(this)) {
            if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
                val cFile = data.getStringExtra("filepath")
                cTitle = data.getStringExtra("songtitle")
                titleText!!.text = cTitle
                val myFile = File(cFile)
                Log.d("DSDESIGN", "" + mPlayer!!.currentPosition)
                if (currentPlaying != null) {
                    val id = playerAdapter.updateData(
                        currentPlaying,
                        mPlayer!!.currentPosition,
                        System.currentTimeMillis()
                    )
                    Log.d("DSDESIGN", "onActivityResult: $id")
                }
                currentPlaying = cTitle
                val startPoint: Long
                startPoint = try {
                    playerAdapter.getData(currentPlaying)
                } catch (e: Exception) {
                    Log.e("DSDESIGN", "onActivityResult: ", e)
                    0
                }
                if (startPoint == 0L) {
                    Log.d("DSDESIGN", "No Records found")
                }
                val intent = Intent(applicationContext, PlayerService::class.java)
                startService(intent)
                try {
                    mPlayer!!.reset()
                    mPlayer!!.setDataSource(myFile.path)
                    mPlayer!!.prepare()
                    mPlayer!!.seekTo(startPoint.toInt())
                    mPlayer!!.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.v(R.string.app_name.toString(), e.message!!)
                }
                seekBar!!.max = mPlayer!!.duration
                updateSeeker(mPlayer!!.isPlaying)
            }
        }
    }

    fun showDialog(
        msg: String, context: Context?,
        permission: String
    ) {
        val alertBuilder = AlertDialog.Builder(
            context!!
        )
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("$msg permission is necessary")
        alertBuilder.setPositiveButton(
            android.R.string.yes
        ) { dialog, which ->
            ActivityCompat.requestPermissions(
                (context as Activity?)!!, arrayOf(permission),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
        val alert = alertBuilder.create()
        alert.show()
    }

    fun checkPermissionWRITE_EXTERNAL_STORAGE(
        context: Context?
    ): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    showDialog(
                        "Allow read and write to external storage", context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } else {
                    ActivityCompat
                        .requestPermissions(
                            (context as Activity?)!!,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    companion object {
        const val FILE_SELECT_CODE = 2
        private var cTitle: String? = null
        private const val formatString = "%02d:%02d:%02d"
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 124
    }
}