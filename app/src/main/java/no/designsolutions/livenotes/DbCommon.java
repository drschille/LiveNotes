package no.designsolutions.livenotes;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

public class DbCommon {

    public static final int version = 1;

    //Database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LiveNotes.db";

    //Tables
    public static final String TABLE_PLAYBACK_HISTORY = "History";
    public static final String TABLE_NOTES = "Notes";
    public static final String TABLE_NOTEBOOKS = "Notebooks";

    //Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MEDIA_FILENAME = "filename";
    public static final String COLUMN_MEDIA_TITLE = "title";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_NOTEBOOK = "notebook";
    public static final String COLUMN_THUMBNAIL = "thumbnail";

    //Queries
    public static final String CREATE_TABLE_HISTORY = new StringBuilder()
            .append("CREATE TABLE ").append(TABLE_PLAYBACK_HISTORY).append("(")
            .append(COLUMN_ID).append(" INT PRIMARY KEY AUTOINCREMENT,")
            .append(COLUMN_MEDIA_TITLE).append(" TEXT, ")
            .append(COLUMN_MEDIA_FILENAME).append(" VARCHAR(255), ")
            .append(COLUMN_DATE).append(" DATETIME DEFAULT CURRENT_TIMESTAMP, ")
            .append(COLUMN_TIMESTAMP).append(" INT")
            .append(")")
            .toString();
    public static final String CREATE_TABLE_NOTES = new StringBuilder()
            .append("CREATE TABLE ").append(TABLE_NOTES).append("(")
            .append(COLUMN_ID).append(" INT PRIMARY KEY AUTOINCREMENT,")
            .append(COLUMN_MEDIA_FILENAME).append(" VARCHAR(255), ")
            .append(COLUMN_DATE).append(" DATETIME DEFAULT CURRENT_TIMESTAMP, ")
            .append(COLUMN_TIMESTAMP).append(" INT, ")
            .append(COLUMN_NOTEBOOK).append(" VARCHAR, ")
            .append(COLUMN_NOTE).append(" TEXT")
            .append(")")
            .toString();
    public static final String CREATE_TABLE_NOTEBOOKS = new StringBuilder()
            .append("CREATE TABLE ").append(TABLE_NOTEBOOKS).append("(")
            .append(COLUMN_ID).append(" INT PRIMARY KEY AUTOINCREMENT,")
            .append(COLUMN_NOTEBOOK).append(" VARCHAR, ")
            .append(COLUMN_DATE).append(" DATETIME DEFAULT CURRENT_TIMESTAMP, ")
            .append(COLUMN_THUMBNAIL).append(" VARCHAR(255)")
            .append(")")
            .toString();

    //ContextWrapper:
    public static class DatabaseContext extends ContextWrapper {

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

    //Print out Database as a String
    public static String databaseToString(SQLiteOpenHelper helper) {
        String dbString = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] tables = {TABLE_NOTEBOOKS, TABLE_PLAYBACK_HISTORY, TABLE_NOTES};
        for (int i = 0; i <= tables.length; i++) {
            String table = tables[i];
            String query = "SELECT * FROM " + table;
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_MEDIA_TITLE)) != null) {
                    dbString += c.getString(c.getColumnIndex(COLUMN_MEDIA_TITLE));
                    dbString += "\n";
                }
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        return dbString;
    }

}
