package com.miymayster.myvine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.miymayster.myvine.data.MyVineContract;
import com.miymayster.myvine.photo.PhotoUtils;
import com.squareup.picasso.Picasso;

public class VinePhotoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean textEditorOpened = false;
    private EditText textEditor;
    private FloatingActionButton fab;
    private TextView photoText;
    private FrameLayout textDisplay;
    private ImageView mPhotoImageView;

    private Uri mCallUri;
    private long mPhotoId;

    private String mText;

    private static final int LOADER_PHOTO_INFO = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vine_photo);


        if (getIntent() != null && getIntent().getData() != null) {
            mCallUri = getIntent().getData();
            mPhotoId = ContentUris.parseId(mCallUri);
        } else {
            Log.e(VineInfoActivity.class.getSimpleName(), "Can\'t get data.");
            finish();
            return;
        }

        textEditor = (EditText) findViewById(R.id.text_editor);
        photoText = (TextView) findViewById(R.id.photo_text);
        fab = (FloatingActionButton) findViewById(R.id.fab_add_photo_note);
        textDisplay = (FrameLayout) findViewById(R.id.text_display);
        mPhotoImageView = (ImageView) findViewById(R.id.vine_image);
        mText = "";
        photoText.setText(mText);
        textEditor.setText(mText);

        View.OnClickListener toggleTextEditor = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textEditorOpened){
                    closeTextEditor();
                }else{
                    openTextEditor();
                }
            }
        };

        fab.setOnClickListener(toggleTextEditor);
        textDisplay.setOnClickListener(toggleTextEditor);

        refreshClosedEditor();

        getSupportLoaderManager().initLoader(LOADER_PHOTO_INFO, null, this);
    }

    private void refreshClosedEditor(){

        if(TextUtils.isEmpty(mText)){
            fab.setVisibility(View.VISIBLE);
            textDisplay.setVisibility(View.GONE);
        }else{
            fab.setVisibility(View.GONE);
            textDisplay.setVisibility(View.VISIBLE);
        }
    }

    private void openTextEditor(){

        fab.setImageResource(R.drawable.ic_check_24dp);
        fab.setVisibility(View.VISIBLE);
        textEditor.setVisibility(View.VISIBLE);
        textDisplay.setVisibility(View.GONE);

        textEditorOpened = true;
    }
    private void closeTextEditor(){

        String text = textEditor.getText().toString();
        if(!text.equals(mText)){
            ContentValues values = new ContentValues();
            values.put(MyVineContract.VinePhotoEntries.COLUMN_ABOUT, text);
            getContentResolver().update(mCallUri, values, null, null);
            mText = text;
            photoText.setText(mText);
        }

        fab.setImageResource(R.drawable.ic_notes_24dp);
        textEditor.setVisibility(View.INVISIBLE);
        textEditorOpened = false;

        refreshClosedEditor();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_PHOTO_INFO){
            return new CursorLoader(this, mCallUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id == LOADER_PHOTO_INFO){
            if(data.moveToFirst()) {
                int aboutColumnIndex = data.getColumnIndex(MyVineContract.VinePhotoEntries.COLUMN_ABOUT);
                int pathColumnIndex = data.getColumnIndex(MyVineContract.VinePhotoEntries.COLUMN_PATH);

                mText = data.getString(aboutColumnIndex);
                String path = data.getString(pathColumnIndex);


                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                Picasso.with(this)
                        .load("file:"+path)
                        .into(mPhotoImageView);

                textEditor.setText(mText);
                photoText.setText(mText);
                refreshClosedEditor();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mText = "";

        textEditor.setText(mText);
        photoText.setText(mText);
        refreshClosedEditor();
    }
}
