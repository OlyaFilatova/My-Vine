package com.miymayster.myvine;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miymayster.myvine.data.MyVineContract;

import java.util.ArrayList;

public class VineListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_GET_VINE_LIST = 1000;
    VineListAdapter vineListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vine_list);

        // TODO: implement population using Cursor or Firebase
        ArrayList<Object> vines = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            vines.add("");
        }
        vineListAdapter = new VineListAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.vine_recycler_view);
        recyclerView.setAdapter(vineListAdapter);

        int columnWidth = ((int)getResources().getDimension(R.dimen.vine_width)) + ((int)getResources().getDimension(R.dimen.vine_margin)) * 2;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        recyclerView.setLayoutManager(new GridLayoutManager(this, width/columnWidth));

        findViewById(R.id.fab_add_vine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addVineIntent = new Intent(VineListActivity.this, AddVineActivity.class);
                startActivity(addVineIntent);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_GET_VINE_LIST, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vine_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                Intent loginIntent = new Intent(VineListActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_GET_VINE_LIST){
            return new CursorLoader(this, MyVineContract.VineEntries.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id == LOADER_GET_VINE_LIST){
            vineListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        int id = loader.getId();
        if(id == LOADER_GET_VINE_LIST){
            vineListAdapter.swapCursor(null);
        }
    }
}
