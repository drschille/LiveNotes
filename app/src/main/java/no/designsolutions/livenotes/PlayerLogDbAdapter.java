package no.designsolutions.livenotes;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.os.IResultReceiver;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Daniel on 03.01.2020.
 */

public class PlayerLogDbAdapter {

    private MySQLiteOpenHelper myHelper;

    public PlayerLogDbAdapter(Context context) {
        myHelper = new MySQLiteOpenHelper(context);
    }

    public long insertData(String fileName, int timeStamp, long dateStamp) {
        SQLiteDatabase dbb = myHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteOpenHelper.MEDIA_FILE, fileName);
        contentValues.put(MySQLiteOpenHelper.TIMESTAMP, timeStamp);
        contentValues.put(MySQLiteOpenHelper.DATE, dateStamp);
        return dbb.insert(MySQLiteOpenHelper.LOG_TABLE_NAME, null, contentValues);
    }

    public long getData(String searchString) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        //String[] columns = {MySQLiteOpenHelper.UID, MySQLiteOpenHelper.MEDIA_FILE, MySQLiteOpenHelper.TIMESTAMP};
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            cursor = db.rawQuery("SELECT " + MySQLiteOpenHelper.TIMESTAMP + " FROM " + MySQLiteOpenHelper.LOG_TABLE_NAME + " WHERE " + MySQLiteOpenHelper.MEDIA_FILE + "=?", new String[]{searchString}, null);
        }

        long result = 0;
        if (cursor != null && cursor.moveToFirst()) {

            do {
                result = cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.TIMESTAMP));
            } while (cursor.moveToNext());
            cursor.close();

        }
        return result;
    }

    public long updateData(String fileName, int timeStamp, long dateStamp) {
        if (getData(fileName) == 0) {
            return insertData(fileName, timeStamp, dateStamp);
        } else {
            SQLiteDatabase db = myHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MySQLiteOpenHelper.TIMESTAMP, timeStamp);
            contentValues.put(MySQLiteOpenHelper.DATE, dateStamp);
            String[] whereArgs = {fileName};
            return db.update(MySQLiteOpenHelper.LOG_TABLE_NAME, contentValues, MySQLiteOpenHelper.MEDIA_FILE + " = ?", whereArgs);
        }
    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "liveNotesDatabase.db";
        private static final String LOG_TABLE_NAME = "logTable";
        private static final String NOTES_TABLE_NAME = "notesTable";
        private static final String NOTE_STRINGS = "noteStrings";
        private static final int DATABASE_Version = 2;   // Database Version
        private static final String UID = "_id"; // Column I (Primary Key)
        private static final String MEDIA_FILE = "mediaFilename";
        private static final String TIMESTAMP = "timestamp";
        private static final String DATE = "date";
        private static final String CREATE_LOG_TABLE = "CREATE TABLE " + LOG_TABLE_NAME +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MEDIA_FILE + " VARCHAR(255) ,"
                + TIMESTAMP + " INTEGER ,"
                + DATE + " LONG"
                + ");";
        private static final String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE_NAME +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MEDIA_FILE + " VARCHAR(255) ,"
                + NOTE_STRINGS + " TEXT ,"
                + TIMESTAMP + " INTEGER ,"
                + DATE + " LONG"
                + ");";
        private Context context;
        private static final String DROP_LOG_TABLE = "DROP TABLE IF EXISTS " + LOG_TABLE_NAME;
        private static final String DROP_NOTES_TABLE = "DROP TABLE IF EXISTS " + NOTES_TABLE_NAME;

        private MySQLiteOpenHelper(@Nullable Context context) {
            super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_NOTES_TABLE);
                db.execSQL(CREATE_LOG_TABLE);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "OnUpgrader");
                db.execSQL(DROP_LOG_TABLE);
                db.execSQL(DROP_NOTES_TABLE);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }
    }

    private static class DatabaseContext extends ContextWrapper {

        public DatabaseContext(Context base) {
            super(base);
        }

        @Override
        public File getDatabasePath(String name) {

            if (!name.endsWith(".db")) {
                name += ".db";
            }
            File externalFilesFolder = getBaseContext().getExternalFilesDir(null);
            File dbFile = new File(externalFilesFolder, name);

            if (!dbFile.getParentFile().exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return dbFile;
        }

        /* this version is called for android devices >= api-11. thank to @damccull for fixing this. */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            File dbFile = getDatabasePath(name);
            String newName = dbFile.getAbsolutePath();
            return openOrCreateDatabase(newName, mode, factory);
        }

        /* this version is called for android devices < api-11 */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            // SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
            return result;
        }
    }
}