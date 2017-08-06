package com.miymayster.myvine;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.miymayster.myvine.data.MyVineContract;

import java.util.Calendar;

public class AddVineActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    EditText vinePlanted;
    String planted;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vine);

        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(
                this, AddVineActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle(R.string.when_was_this_vine_planted);

        vinePlanted = (EditText) findViewById(R.id.vine_planted);
        vinePlanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        findViewById(R.id.fab_save_new_vine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kind = ((TextView)findViewById(R.id.vine_kind)).getText().toString();
                String name = ((TextView)findViewById(R.id.vine_name)).getText().toString();
                saveNewVine(kind, name, planted);
            }
        });
    }
    private void saveNewVine(String kind, String name, String planted){
        ContentValues values = new ContentValues();
        values.put(MyVineContract.VineEntries.COLUMN_NAME, name);
        values.put(MyVineContract.VineEntries.COLUMN_KIND, kind);
        values.put(MyVineContract.VineEntries.COLUMN_PLANTED, planted);

        Uri newVineUri = getContentResolver().insert(MyVineContract.VineEntries.CONTENT_URI, values);


        Intent infoIntent = new Intent(this, VineInfoActivity.class);
        infoIntent.setData(newVineUri);
        startActivity(infoIntent);
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month ++;
        planted = year + "." +
                ((month<10)?"0"+month:month) + "." +
                ((dayOfMonth<10)?"0"+dayOfMonth:dayOfMonth);
        vinePlanted.setText(planted);
    }
}
