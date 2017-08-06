package com.miymayster.myvine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miymayster.myvine.data.MyVineContract;
import com.miymayster.myvine.photo.PhotoUtils;

import java.util.ArrayList;

public class VineInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_VINE_INFO = 1000;
    private static final int LOADER_VINE_PHOTOS = 2000;

    VineInfoPhotosAdapter adapter;
    Uri callUri;
    long mVineId;

    private TextView nameTextView;
    private TextView kindTextView;
    private TextView plantedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vine_info);

        if (getIntent() != null && getIntent().getData() != null) {
            callUri = getIntent().getData();
            mVineId = ContentUris.parseId(callUri);
        } else {
            Log.e(VineInfoActivity.class.getSimpleName(), "Can\'t get data.");
            finish();
            return;
        }

        nameTextView = (TextView) findViewById(R.id.vine_name);
        kindTextView = (TextView) findViewById(R.id.vine_kind);
        plantedTextView = (TextView) findViewById(R.id.vine_planted);

        RecyclerView photosRecyclerView = (RecyclerView) findViewById(R.id.vine_photos_recycler);

        adapter = new VineInfoPhotosAdapter();

        photosRecyclerView.setAdapter(adapter);

        int columnWidth = ((int) getResources().getDimension(R.dimen.vine_width)) + ((int) getResources().getDimension(R.dimen.vine_margin)) * 2;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        photosRecyclerView.setLayoutManager(new GridLayoutManager(this, width / columnWidth));

        findViewById(R.id.fab_add_vine_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_VINE_INFO, null, this);
        getSupportLoaderManager().initLoader(LOADER_VINE_PHOTOS, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vine_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent editIntent = new Intent(VineInfoActivity.this, VineEditActivity.class);
            editIntent.setData(callUri);
            startActivity(editIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private String mPhotoUrl;

    private void capturePhoto() {
        mPhotoUrl = PhotoUtils.capturePhoto(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PhotoUtils.REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                if(mPhotoUrl != null && !mPhotoUrl.isEmpty()){
                    ContentValues values = new ContentValues();
                    values.put(MyVineContract.VinePhotoEntries.COLUMN_VINE_ID, mVineId);
                    values.put(MyVineContract.VinePhotoEntries.COLUMN_PATH, mPhotoUrl);
                    Uri resultUri = getContentResolver().insert(MyVineContract.VinePhotoEntries.CONTENT_URI, values);
                    values.clear();
                    values.put(MyVineContract.VineEntries.COLUMN_LAST_IMAGE_PATH, mPhotoUrl);
                    getContentResolver().update(callUri, values, null, null);

                    Intent openPhoto = new Intent(this, VinePhotoActivity.class);
                    openPhoto.setData(resultUri);
                    startActivity(openPhoto);

                    mPhotoUrl = null;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_VINE_INFO:
                return new CursorLoader(this, callUri, null, null, null, null);
            case LOADER_VINE_PHOTOS:
                String selection = MyVineContract.VinePhotoEntries.COLUMN_VINE_ID + "=?";
                String[] selectionArgs = new String[]{
                        String.valueOf(mVineId)
                };
                return new CursorLoader(this, MyVineContract.VinePhotoEntries.CONTENT_URI, null, selection, selectionArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id){
            case LOADER_VINE_INFO:
                if(data.moveToFirst()) {
                    int nameColumnIndex = data.getColumnIndex(MyVineContract.VineEntries.COLUMN_NAME);
                    int kindColumnIndex = data.getColumnIndex(MyVineContract.VineEntries.COLUMN_KIND);
                    int plantedColumnIndex = data.getColumnIndex(MyVineContract.VineEntries.COLUMN_PLANTED);

                    String name = data.getString(nameColumnIndex);
                    String kind = data.getString(kindColumnIndex);
                    String planted = data.getString(plantedColumnIndex);

                    nameTextView.setText(name);
                    kindTextView.setText(kind);
                    plantedTextView.setText(planted);
                }
                break;
            case LOADER_VINE_PHOTOS:
                adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        switch (id){
            case LOADER_VINE_INFO:
                nameTextView.setText("");
                kindTextView.setText("");
                plantedTextView.setText("");
                break;
            case LOADER_VINE_PHOTOS:
                adapter.swapCursor(null);
                break;
        }
    }
}
