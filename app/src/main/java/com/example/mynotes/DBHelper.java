package com.example.mynotes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyNotes.db";
    public static final String Notes_TABLE_NAME = "Notes";
    public static final String Notes_COLUMN_NOTE_ID = "note_id";
    public static final String Notes_COLUMN_NOTE_CONTENT = "note_content";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists  Notes " +
                        "(id integer primary key, note_content text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Notes");
        onCreate(db);
    }

    public boolean insertNotes (String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("note_content", note_content);
        db.insert("Notes", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Notes where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, Notes_TABLE_NAME);
        return numRows;
    }

    public boolean updateNote (Integer id, String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("note_content", note_content);
        db.update("Notes", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteNote (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Notes",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }


    

}
