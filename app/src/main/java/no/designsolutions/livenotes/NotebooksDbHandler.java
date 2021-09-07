package no.designsolutions.livenotes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import no.designsolutions.livenotes.util.NoteBook;

import static no.designsolutions.livenotes.DbCommon.*;

public class NotebooksDbHandler extends SQLiteOpenHelper {

    Context context;

    //Class constructor & implemented methods
    public NotebooksDbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(new DbCommon.DatabaseContext(context), DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_NOTEBOOKS);
        } catch (Exception e) {
            Message.message(context, "" + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTEBOOKS);
            onCreate(db);
        } catch (Exception e) {
            Message.message(context, "" + e);
        }
    }

    //Notebook Table Actions:
    public void addNotebook(NoteBook noteBook) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTEBOOK, noteBook.getName());
        values.put(COLUMN_DATE, System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTEBOOKS, null, values);
        db.close();
    }

    public void renameNotebook(NoteBook noteBook, String newName) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NOTEBOOK, newName);
            String[] whereArgs = {noteBook.getName()};
            db.update(TABLE_NOTEBOOKS, contentValues, COLUMN_NOTEBOOK + " = ?", whereArgs);
        }

    public String[] getNotebooks() {
        StringBuffer buffer = new StringBuffer();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTEBOOKS;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (buffer.length() != 0) {
                buffer.append("|");
            }
            buffer.append(c.getString(c.getColumnIndex(COLUMN_NOTEBOOK)));
        }
        return buffer.toString().split("|");
    }

    public void deleteNotebook(NoteBook noteBook) {
        String notebook = noteBook.getName();
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTEBOOKS + " WHERE " + COLUMN_NOTEBOOK + "=\"" + notebook + "\";");
    }

    //Print out Database as a String
    public String databaseToString() {
        return DbCommon.databaseToString(this);
    }


}



