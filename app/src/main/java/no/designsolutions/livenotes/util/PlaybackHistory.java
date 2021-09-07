package no.designsolutions.livenotes.util;

/**
 * Created by Daniel on 16.03.2016.
 */
public class PlaybackHistory {

    private int _id;
    private String _title;
    private String _filename;
    private String _date;
    private int _duration;

    public PlaybackHistory() {
    }

    public PlaybackHistory(String filename, String title, long duration) {
        _filename = filename;
        _title = title;
        _duration = (int) (duration / 1000);
    }

    public void setId(int _id) {
        _id = _id;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void setFilename(String filename) {
        _filename = filename;
    }

    public void setDate(String date) {
        _date = date;
    }

    public String getDate() {
        return _date;
    }

    public void setDuration(long duration) {
        _duration = (int) (duration / 1000);
    }

    public int getDuration() {
        return _duration;
    }

    public String getTitle() {
        return _title;
    }

    public String getFilename() {
        return _filename;
    }
}

