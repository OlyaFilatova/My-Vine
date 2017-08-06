package com.miymayster.myvine.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Olga on 29.07.2017.
 */

public class MyVineContract {
    private MyVineContract(){}
    public static final String CONTENT_AUTHORITY = "com.miymayster.myvine";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_VINE = "vine";
    public static final String PATH_VINE_PHOTO = "vine_photo";

    public static final class VineEntries implements BaseColumns{
        private VineEntries(){}
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_VINE).build();
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_VINE;
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_VINE;
        public static final String TABLE_NAME = "vine";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_KIND = "kind";
        public static final String COLUMN_PLANTED = "planted";
        public static final String COLUMN_LAST_IMAGE_PATH = "last_image_path";
    }
    public static final class VinePhotoEntries implements BaseColumns{
        private VinePhotoEntries(){}
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_VINE_PHOTO).build();
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_VINE_PHOTO;
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_VINE+PATH_VINE;
        public static final String TABLE_NAME = "vine_photo";

        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_ABOUT = "about";
        public static final String COLUMN_VINE_ID = "vine_id";
    }
}
