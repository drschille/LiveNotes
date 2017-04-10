package no.designsolutions.livenotes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewHolder> {


    List<RecyclerViewItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private MainActivity myContext;

    public myAdapter(Context context, List<RecyclerViewItem> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;

        myContext = (MainActivity) context;
    }

    public static boolean checkBitmapWorkerTask(String imagePath, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String workTaskPath = bitmapWorkerTask.getPath();
            if (workTaskPath != null) {
                if (!workTaskPath.equals(imagePath)) {
                    bitmapWorkerTask.cancel(true);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof AsyncDrawable) {
            AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
            return asyncDrawable.getBitmapWorkerTask();
        }
        return null;
    }

    @Override
    public myAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_item, parent, false);
        return new myViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(myAdapter.myViewHolder holder, final int position) {
        final RecyclerViewItem current = data.get(position);
        holder.title.setText(current.title);
        // holder.icon.setImageResource(R.drawable.ic_launcher);

        Bitmap placeHolderBitmap;

        placeHolderBitmap = BitmapFactory.decodeResource(holder.icon.getResources(), R.drawable.ic_file_icon);

        Bitmap bitmap = myFragment.getBitmapFromMemoryCache(current.path);
        if (bitmap != null) {
            holder.icon.setImageBitmap(bitmap);
        } else if (checkBitmapWorkerTask(current.path, holder.icon)) {
            BitmapWorkerTask workerTask = new BitmapWorkerTask(holder.icon);
            AsyncDrawable asyncDrawable = new AsyncDrawable(holder.icon.getResources(), placeHolderBitmap, workerTask);
            holder.icon.setImageDrawable(asyncDrawable);
            workerTask.execute(current.path);
        }

        // BitmapWorkerTask workerTask = new BitmapWorkerTask(holder.icon);
        // workerTask.execute(current.path);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   MediaPlayer mp = myMediaPlayer.mPlayer;
                //   File myFile = new File(current.path);
                Intent resultsData = myContext.getIntent();
                resultsData.putExtra("songtitle", current.title);
                resultsData.putExtra("filepath", current.path);
                myContext.setResult(Activity.RESULT_OK, resultsData);
                myContext.finish();

//                try {
//                    mp.reset();
//                    mp.setDataSource(myFile.getPath());
//                    mp.prepare();
//                    mp.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.v(String.valueOf(R.string.app_name), e.getMessage());
//                }


            }
        });
    }

    @Override
    public int getItemCount() {

        return data.size();
    }


    public static class AsyncDrawable extends BitmapDrawable {
        final WeakReference<BitmapWorkerTask> taskReference;

        public AsyncDrawable(Resources resources,
                             Bitmap bitmap,
                             BitmapWorkerTask workerTask) {
            super(resources, bitmap);
            taskReference = new WeakReference(workerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return taskReference.get();
        }
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public myViewHolder(View itemView) {

            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listItemText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }
    }

}

