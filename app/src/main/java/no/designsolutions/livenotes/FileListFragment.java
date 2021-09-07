package no.designsolutions.livenotes;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Daniel on 31.08.2015.
 */
public class FileListFragment extends Fragment {

    private static LruCache<String, Bitmap> mMemoryCache;
    private static final int numberOfColumns = 3;

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
        //RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.myRecyclerView);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MyViewAdapter adapter = new MyViewAdapter(getActivity(), getData());
        RecyclerView recyclerView = getActivity().findViewById(R.id.myRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),numberOfColumns));

        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemorySize / 10;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };

    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public List<RecyclerViewMediaItem> getData() {
        List<RecyclerViewMediaItem> myList = new ArrayList<>();

        if (checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {


            // do your stuff..


            String[] Fields = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE};
            Cursor cursor = this.getActivity().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Fields,
                    null,
                    null,
                    MediaStore.Audio.Media.TITLE);


            if (cursor.moveToFirst()) {
                int filename_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int title_index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                String sourcePath = cursor.getString(filename_index);
                RecyclerViewMediaItem myItem = new RecyclerViewMediaItem(sourcePath);
                myItem.title = cursor.getString(title_index);
                myList.add(myItem);

                while (cursor.moveToNext()) {
                    sourcePath = cursor.getString(filename_index);
                    RecyclerViewMediaItem newItem = new RecyclerViewMediaItem(sourcePath);
                    newItem.title = cursor.getString(title_index);
                    myList.add(newItem);
                }

            }


        }
        return myList;
    }


}

