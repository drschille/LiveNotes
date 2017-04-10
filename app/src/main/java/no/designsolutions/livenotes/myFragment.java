package no.designsolutions.livenotes;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Daniel on 31.08.2015.
 */
public class myFragment extends Fragment {

    private static LruCache<String, Bitmap> mMemoryCache;


    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public static void setBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View layout = inflater.inflate(R.layout.my_fragment_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.myRecyclerView);
        myAdapter adapter = new myAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemorySize / 10;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };

        return layout;
    }

    public List<RecyclerViewItem> getData() {

        List<RecyclerViewItem> myList = new ArrayList<>();

        String[] Fields = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE};
        Cursor cursor = this.getActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Fields,
                null,
                null,
                MediaStore.Audio.Media.TITLE);

        if (cursor.moveToFirst()) {
            if (cursor.moveToFirst()) {
                int filename_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int title_index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                String sourcePath = cursor.getString(filename_index);
                RecyclerViewItem myItem = new RecyclerViewItem(sourcePath);
                myItem.title = cursor.getString(title_index);
                myList.add(myItem);

                while (cursor.moveToNext()) {
                    sourcePath = cursor.getString(filename_index);
                    RecyclerViewItem newItem = new RecyclerViewItem(sourcePath);
                    newItem.title = cursor.getString(title_index);
                    myList.add(newItem);
                }

            }
        }


        return myList;
    }


}

