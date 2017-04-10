package no.designsolutions.livenotes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Daniel on 15.09.2015.
 */
//comment

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    final static int TARGET_WIDTH = 60;
    final static int TARGET_HEIGHT = 60;
    WeakReference<ImageView> imageViewWeakReference;
    private String mPath;

    public BitmapWorkerTask(ImageView imageView) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mPath = params[0];
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(params[0]);
        byte[] ImageBytes = metadataRetriever.getEmbeddedPicture();

        Bitmap bitmap = null;

        if (ImageBytes != null) {
            bitmap = decodeBitmapFromBytes(ImageBytes);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        /*
        if (bitmap != null && imageViewWeakReference != null) {
            ImageView image = imageViewWeakReference.get();
            if (image != null) {
                image.setImageBitmap(bitmap);
            }

        }
        */
        if (isCancelled()) {
            bitmap = null;
        }
        if (bitmap != null && imageViewWeakReference != null) {
            ImageView imageView = imageViewWeakReference.get();
            BitmapWorkerTask bitmapWorkerTask = myAdapter.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
                myFragment.setBitmapToMemoryCache(mPath, bitmap);
            }
        }

    }

    private int calculateInSampleSize(BitmapFactory.Options bmOptions) {
        final int photoWidth = bmOptions.outWidth;
        final int photoHeight = bmOptions.outHeight;
        int scaleFactor = 1;

        if (photoWidth > TARGET_WIDTH || photoHeight > TARGET_HEIGHT) {
            final int halfPhotoWidth = photoWidth / 2;
            final int halfPhotoHeight = photoHeight / 2;
            while (halfPhotoHeight / scaleFactor > TARGET_HEIGHT ||
                    halfPhotoWidth / scaleFactor > TARGET_WIDTH) {
                scaleFactor *= 2;
            }
        }

        return scaleFactor;
    }

    private Bitmap decodeBitmapFromBytes(byte[] imageBytes) {
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, myOptions);
        myOptions.inSampleSize = calculateInSampleSize(myOptions);
        myOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, myOptions);
    }

    public String getPath() {
        return mPath;
    }
}

