package no.designsolutions.livenotes.util;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Daniel on 18.03.2016.
 */
public class CustomMediaPlayer extends MediaPlayer {

    private String dataSourcePath;
    private String currentTitle;



    public java.lang.String getDataSourcePath() {
        return dataSourcePath;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        super.setDataSource(path);
        dataSourcePath = path;
    }
}