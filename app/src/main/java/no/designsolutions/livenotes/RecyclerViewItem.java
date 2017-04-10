package no.designsolutions.livenotes;


import android.media.MediaMetadataRetriever;


/**
 * Created by Daniel on 28.08.2015.
 */
public class RecyclerViewItem {
    String filename;
    String title;
    String path;
    String artist;
    String album;

    MediaMetadataRetriever metadataRetriever;

    RecyclerViewItem(String sourcePath) {
        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(sourcePath);
        path = sourcePath;
        filename = sourcePath.split("\\/")[sourcePath.split("\\/").length -1]
                .split("\\.")[0];
        getMetadata();
    }

    private void getMetadata() {

        artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if (title == null){
            title = filename;
        }
    }
}
