package com.miymayster.myvine;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.miymayster.myvine.data.MyVineContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class VineEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener, VineEditPhotosAdapter.OnItemClickListener {
    private static final int LOADER_VINE_INFO = 1000;
    private static final int LOADER_VINE_PHOTOS = 2000;

    VineEditPhotosAdapter adapter;
    Uri callUri;
    long mVineId;

    private EditText mNameEditText;
    private EditText mKindEditText;
    private EditText mPlantedEditText;

    private CustomDialog mCustomDialog;
    DatePickerDialog datePickerDialog;
    Button mDeletePhotosButton;

    private Set<Long> photosToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vine_edit);

        if (getIntent() != null && getIntent().getData() != null) {
            callUri = getIntent().getData();
            mVineId = ContentUris.parseId(callUri);
        } else {
            Log.e(VineInfoActivity.class.getSimpleName(), "Can\'t get data.");
            finish();
            return;
        }

        RecyclerView photosRecyclerView = (RecyclerView) findViewById(R.id.vine_photos_recycler);
        mNameEditText = (EditText) findViewById(R.id.vine_name);
        mKindEditText = (EditText) findViewById(R.id.vine_kind);
        mPlantedEditText = (EditText) findViewById(R.id.vine_planted);

        photosToDelete = new ArraySet<>();
        adapter = new VineEditPhotosAdapter(this, photosToDelete);

        photosRecyclerView.setAdapter(adapter);

        int columnWidth = ((int)getResources().getDimension(R.dimen.vine_width)) + ((int)getResources().getDimension(R.dimen.vine_margin)) * 2;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        photosRecyclerView.setLayoutManager(new GridLayoutManager(this, width/columnWidth));

        findViewById(R.id.vine_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialog = new CustomDialog(VineEditActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomDialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getContentResolver().delete(callUri, null, null);
                        mCustomDialog.dismiss();
                        Intent intent = new Intent(VineEditActivity.this, VineListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, mNameEditText.getText().toString(), true);
                mCustomDialog.show();
            }
        });

        mDeletePhotosButton = (Button) findViewById(R.id.vine_photos_delete_button);
        mDeletePhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Implement deletion of vine photos
                mCustomDialog = new CustomDialog(VineEditActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomDialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentResolver contentResolver = getContentResolver();
                        for(long id : photosToDelete){
                            contentResolver.delete(ContentUris.withAppendedId(
                                    MyVineContract.VinePhotoEntries.CONTENT_URI,
                                    id
                            ), null, null);
                        }
                        mCustomDialog.dismiss();
                    }
                }, photosToDelete.size());
                mCustomDialog.show();
            }
        });



        getSupportLoaderManager().initLoader(LOADER_VINE_INFO, null, this);
        getSupportLoaderManager().initLoader(LOADER_VINE_PHOTOS, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vine_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_done){
            saveVineData();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveVineData(){
        String name = mNameEditText.getText().toString();
        String kind = mKindEditText.getText().toString();
        String planted = mPlantedEditText.getText().toString();

        ContentValues values = new ContentValues();
        values.put(MyVineContract.VineEntries.COLUMN_NAME, name);
        values.put(MyVineContract.VineEntries.COLUMN_KIND, kind);
        values.put(MyVineContract.VineEntries.COLUMN_PLANTED, planted);

        getContentResolver().update(callUri, values, null, null);
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

                    mNameEditText.setText(name);
                    mKindEditText.setText(kind);
                    mPlantedEditText.setText(planted);

                    Calendar calendar = Calendar.getInstance();
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("y.M.d");
                        Date date = format.parse(planted);
                        calendar.setTime(date);
                    }catch(ParseException e){
                        e.printStackTrace();
                    }

                    datePickerDialog = new DatePickerDialog(
                            this, VineEditActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.setTitle(R.string.when_was_this_vine_planted);

                    mPlantedEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            datePickerDialog.show();
                        }
                    });
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
                mNameEditText.setText("");
                mKindEditText.setText("");
                mPlantedEditText.setText("");
                break;
            case LOADER_VINE_PHOTOS:
                adapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month ++;
        mPlantedEditText.setText(year + "." +
                ((month<10)?"0"+month:month) + "." +
                ((dayOfMonth<10)?"0"+dayOfMonth:dayOfMonth));
    }

    @Override
    public void onPhotoClicked(long photoId, boolean delete) {
        if(delete){
            photosToDelete.add(photoId);
        }else{
            photosToDelete.remove(photoId);
        }
        if(photosToDelete.size() > 0){
            mDeletePhotosButton.setEnabled(true);
        }else{
            mDeletePhotosButton.setEnabled(false);
        }
    }
}
