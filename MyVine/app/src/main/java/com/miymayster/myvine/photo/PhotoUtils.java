package com.miymayster.myvine.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.miymayster.myvine.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Olga on 29.07.2017.
 */


public class PhotoUtils {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static String capturePhoto(Activity context) {
        String photoUrl = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            try {
                File f = createImageFile(context);
                photoUrl = f.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            } catch (IOException e) {
                e.printStackTrace();
                photoUrl = null;
            }
            context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        return photoUrl;
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir(context);
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }
    private static File getAlbumDir(Context context) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(
                    context.getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES
                    ),
                    getAlbumName(context)
            );
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.d("CameraSample", "failed to create directory");
                    return null;
                }
            }
        } else {
            Log.v(context.getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
    private static String getAlbumName(Context context) {
        return context.getString(R.string.photos_directory_name);
    }
}
