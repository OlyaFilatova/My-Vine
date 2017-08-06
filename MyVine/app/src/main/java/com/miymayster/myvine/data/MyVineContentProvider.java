package com.miymayster.myvine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Olga on 29.07.2017.
 */

public class MyVineContentProvider extends ContentProvider {
    private static final int VINES = 100;
    private static final int VINE_BY_ID = 101;
    private static final int VINE_PHOTOS = 200;
    private static final int VINE_PHOTO_BY_ID = 201;
    private static final UriMatcher sUriMatcher = createUriMatcher();
    private MyVineDBHelper dbHelper;

    private static UriMatcher createUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MyVineContract.CONTENT_AUTHORITY, MyVineContract.PATH_VINE, VINES);
        uriMatcher.addURI(MyVineContract.CONTENT_AUTHORITY, MyVineContract.PATH_VINE + "/#", VINE_BY_ID);

        uriMatcher.addURI(MyVineContract.CONTENT_AUTHORITY, MyVineContract.PATH_VINE_PHOTO, VINE_PHOTOS);
        uriMatcher.addURI(MyVineContract.CONTENT_AUTHORITY, MyVineContract.PATH_VINE_PHOTO + "/#", VINE_PHOTO_BY_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyVineDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match){
            case VINES:
                cursor = db.query(MyVineContract.VineEntries.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VINE_BY_ID:
                long vineId = ContentUris.parseId(uri);
                selection = MyVineContract.VineEntries._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(vineId)
                };
                cursor = db.query(MyVineContract.VineEntries.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VINE_PHOTOS:
                cursor = db.query(MyVineContract.VinePhotoEntries.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VINE_PHOTO_BY_ID:
                long photoId = ContentUris.parseId(uri);
                selection = MyVineContract.VinePhotoEntries._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(photoId)
                };
                cursor = db.query(MyVineContract.VinePhotoEntries.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        if(cursor != null){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case VINES:
                return MyVineContract.VineEntries.CONTENT_LIST_TYPE;
            case VINE_BY_ID:
                return MyVineContract.VineEntries.CONTENT_ITEM_TYPE;
            case VINE_PHOTOS:
                return MyVineContract.VinePhotoEntries.CONTENT_LIST_TYPE;
            case VINE_PHOTO_BY_ID:
                return MyVineContract.VinePhotoEntries.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        Uri returnUri = null;
        switch (match){
            case VINES:
                id = db.insert(MyVineContract.VineEntries.TABLE_NAME, null, values);
                break;
            case VINE_PHOTOS:
                id = db.insert(MyVineContract.VinePhotoEntries.TABLE_NAME, null, values);
                break;
        }
        if(id != -1) {
            returnUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedCount = 0;
        switch (match){
            case VINE_BY_ID:
                long vineId = ContentUris.parseId(uri);
                selection = MyVineContract.VineEntries._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(vineId)
                };
                deletedCount = db.delete(MyVineContract.VineEntries.TABLE_NAME, selection, selectionArgs);
                if(deletedCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case VINE_PHOTO_BY_ID:
                long photoId = ContentUris.parseId(uri);
                selection = MyVineContract.VinePhotoEntries._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(photoId)
                };
                deletedCount = db.delete(MyVineContract.VinePhotoEntries.TABLE_NAME, selection, selectionArgs);
                if(deletedCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
        }
        return deletedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedCount = 0;
        switch (match){
            case VINE_BY_ID:
                long vineId = ContentUris.parseId(uri);
                selection = MyVineContract.VineEntries._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(vineId)
                };
                updatedCount = db.update(MyVineContract.VineEntries.TABLE_NAME, values, selection, selectionArgs);
                if(updatedCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case VINE_PHOTO_BY_ID:
                long photoId = ContentUris.parseId(uri);
                selection = MyVineContract.VinePhotoEntries._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(photoId)
                };
                updatedCount = db.update(MyVineContract.VinePhotoEntries.TABLE_NAME, values, selection, selectionArgs);
                if(updatedCount > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
        }
        return updatedCount;
    }
}
