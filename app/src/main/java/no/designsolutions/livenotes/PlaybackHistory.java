package no.designsolutions.livenotes;

import android.text.format.DateUtils;

import java.io.File;
import java.util.Date;

/**
 * Created by Daniel on 16.03.2016.
 */
public class PlaybackHistory {

    private int _id;
    private String _title;
    private String _fileName;
   // private String _time;
    private int _duration;

    public PlaybackHistory() {
    }

    public PlaybackHistory(String fileName, String title, long duration) {
        _fileName = fileName;
        _title = title;
        _duration = (int) (duration / 1000);
    }

    public void set_id(int _id) {
        _id = _id;
    }

    public void set_title(String title) {
        _title = title;
    }

    public void set_fileName(String fileName) {
        _fileName = fileName;
    }

  //  public void set_time(String time) {
  //      _time = time;
  //  }

  //  public String get_time() {
  //      return _time;
  //  }

    public void set_duration(long duration) {
        _duration = (int) (duration / 1000);
    }

    public int get_duration() {
        return _duration;
    }

    public String get_title() {
        return _title;
    }

    public String get_fileName() {
        return _fileName;
    }
}

