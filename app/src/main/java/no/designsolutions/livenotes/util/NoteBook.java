package no.designsolutions.livenotes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;

import no.designsolutions.livenotes.R;

public class NoteBook {

    private int _id;
    private String _name;
    private String _date;
    private Bitmap _thumbnail;


    public NoteBook() {
    }

    public NoteBook(String title, Context context) {
        _name = title;
        _thumbnail = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_select_file);
        //TODO: Make a nice default Notebook Drawable
    }

    public NoteBook(String title, Bitmap thumbnail) {
        _name = title;
        _thumbnail = thumbnail;
    }

    public void setId(int _id) {
        _id = _id;
    }

    public void setName(String title) {
        _name = title;
    }

    public void setDate(String date) {
        _date = date;
    }

    public void setThumbnail(Bitmap thumbnail) {
        _thumbnail = getResizedBitmap(thumbnail, 96);
    }

    public void setThumbnail(File f) {
        Bitmap buffer = BitmapFactory.decodeFile(f.getPath());
        _thumbnail = getResizedBitmap(buffer, 96);
    }

    public String getDate() {
        return _date;
    }

    public String getName() {
        return _name;
    }

    public Bitmap getThumbnail() {
        return _thumbnail;
    }

    private Bitmap cropQuadratic(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int offset = 0;

        int shorterSide = Math.min(width, height);
        int longerSide = Math.max(width, height);
        boolean portrait = width < height; //find out the image orientation
        //number array positions to allocate for one row of the pixels (+ some blanks - explained in the Bitmap.getPixels() documentation)
        int stride = shorterSide + 1;
        int lengthToCrop = (longerSide - shorterSide) / 2; //number of pixel to remove from each side
        //size of the array to hold the pixels (amount of pixels) + (amount of strides after every line)
        int pixelArraySize = (shorterSide * shorterSide);
        int[] pixels = new int[pixelArraySize];

        //now fill the pixels with the selected range
        bm.getPixels(pixels, 0, stride, portrait ? 0 : lengthToCrop, portrait ? lengthToCrop : 0, shorterSide, shorterSide);

        //save memory
        bm.recycle();
        Bitmap croppedBitmap = Bitmap.createBitmap(shorterSide, shorterSide, Bitmap.Config.ARGB_4444);
        croppedBitmap.setPixels(pixels, offset, 0, 0, 0, shorterSide,shorterSide);

        return  croppedBitmap;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newSize) {

        Bitmap croppedBitmap = cropQuadratic(bm);
        int oldSize = Math.min(croppedBitmap.getWidth(), croppedBitmap.getHeight());
        float scale = ((float) newSize) / oldSize;
        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // Resize the bit map
        matrix.postScale(scale, scale);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, newSize, newSize, matrix, false);
        bm.recycle();
        return resizedBitmap;

    }

}
