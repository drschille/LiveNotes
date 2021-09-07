package no.designsolutions.livenotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;

import no.designsolutions.livenotes.util.PlaybackHistory;

import static no.designsolutions.livenotes.DbCommon.*;

public class PlaybackHistoryDbHandler extends SQLiteOpenHelper {

    Context context;

    //Class constructor & implemented methods
    public PlaybackHistoryDbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(new DbCommon.DatabaseContext(context), DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_HISTORY);
        } catch (Exception e) {
            Message.message(context, "" + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYBACK_HISTORY);
            onCreate(db);
        } catch (Exception e) {
            Message.message(context, "" + e);
        }
    }

    //Playback History Table Actions:
    public void addItem(PlaybackHistory item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDIA_FILENAME, item.getFilename());
        values.put(COLUMN_MEDIA_TITLE, item.getTitle());
        values.put(COLUMN_TIMESTAMP, item.getDuration());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PLAYBACK_HISTORY, null, values);
        db.close();
    }

    public void updateItem(PlaybackHistory item) {
        if (getPlaybackHistoryItem(item.getFilename()) == 0) {
            addItem(item);
        } else {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_TIMESTAMP, item.getDuration());
            contentValues.put(COLUMN_DATE, item.getDate());
            String[] whereArgs = {item.getFilename()};
            db.update(TABLE_PLAYBACK_HISTORY, contentValues, COLUMN_MEDIA_FILENAME + " = ?", whereArgs);
        }
    }

    private long getPlaybackHistoryItem(String filename) {
        SQLiteDatabase db = getWritableDatabase();
        //String[] columns = {MySQLiteOpenHelper.UID, MySQLiteOpenHelper.MEDIA_FILE, MySQLiteOpenHelper.TIMESTAMP};
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            cursor = db.rawQuery("SELECT " + COLUMN_TIMESTAMP + " FROM " + TABLE_PLAYBACK_HISTORY + " WHERE " + COLUMN_MEDIA_FILENAME + "=?", new String[]{filename}, null);
        }
        long result = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                result = cursor.getInt(cursor.getColumnIndex(COLUMN_TIMESTAMP));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public void deleteItem(PlaybackHistory item) {
        String filename = item.getFilename();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYBACK_HISTORY + " WHERE " + COLUMN_MEDIA_FILENAME + "=\"" + filename + "\";");
    }

    //Print out Database as a String
    public String databaseToString() {
        return DbCommon.databaseToString(this);
    }

}
