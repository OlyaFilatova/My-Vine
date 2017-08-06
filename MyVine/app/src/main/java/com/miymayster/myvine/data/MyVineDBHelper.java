package com.miymayster.myvine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Olga on 29.07.2017.
 */

public class MyVineDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyVine.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_VINE_TABLE = "CREATE TABLE " + MyVineContract.VineEntries.TABLE_NAME + " (" +
            MyVineContract.VineEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MyVineContract.VineEntries.COLUMN_NAME + " TEXT, "+
            MyVineContract.VineEntries.COLUMN_KIND + " TEXT, "+
            MyVineContract.VineEntries.COLUMN_PLANTED + " TEXT, "+
            MyVineContract.VineEntries.COLUMN_LAST_IMAGE_PATH + " TEXT)";

    private static final String CREATE_VINE_PHOTO_TABLE = "CREATE TABLE " + MyVineContract.VinePhotoEntries.TABLE_NAME + " (" +
            MyVineContract.VinePhotoEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MyVineContract.VinePhotoEntries.COLUMN_PATH + " TEXT, "+
            MyVineContract.VinePhotoEntries.COLUMN_ABOUT + " TEXT, "+
            MyVineContract.VinePhotoEntries.COLUMN_VINE_ID + " INTEGER)";

    public MyVineDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VINE_TABLE);
        db.execSQL(CREATE_VINE_PHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + MyVineContract.VineEntries.TABLE_NAME);
        db.execSQL("DROP TABLE " + MyVineContract.VinePhotoEntries.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }
}
