package no.designsolutions.livenotes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PlaybackHistory.db";
    public static final String TABLE_HISTORY = "History";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_TIME = "time";

    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_HISTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                COLUMN_TITLE + " TEXT " +
                COLUMN_FILENAME + " TEXT " +
                COLUMN_DURATION + " INTEGER " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    //Add a new row to the database
    public void addItem(PlaybackHistory item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILENAME, item.get_fileName());
        values.put(COLUMN_TITLE, item.get_title());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }


    //Delete a product from the database
    public void deleteItem(String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HISTORY + " WHERE " + COLUMN_TITLE + "=\"" + title + "\";");
    }

    //Print out Database as a String
    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String queary = "SELECT * FROM " + TABLE_HISTORY + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(queary, null);
        //Move to the first row in your results
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_TITLE)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_TITLE));
                dbString += "\n";
            }
        }
        c.close();
        db.close();
        return dbString;
    }
}
