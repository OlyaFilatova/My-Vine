package com.miymayster.myvine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Olga on 29.07.2017.
 */

public class CustomDialog extends AlertDialog {
    private boolean mIsVine;
    private String mName;
    private int mCount;
    private View.OnClickListener mNoOnClickListener;
    private View.OnClickListener mYesOnClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        if(mIsVine){
            ((TextView)findViewById(R.id.optional_name)).setText(mName);
            ((TextView)findViewById(R.id.message)).setText(getContext().getString(R.string.delete_vine_message));
        }else{
            ((TextView)findViewById(R.id.optional_name)).setText(mName);
            ((TextView)findViewById(R.id.message)).setText(getContext().getString(R.string.delete_photos_message, mCount));
        }
        findViewById(R.id.no).setOnClickListener(mNoOnClickListener);
        findViewById(R.id.yes).setOnClickListener(mYesOnClickListener);
    }

    protected CustomDialog(@NonNull Context context, View.OnClickListener noOnClickListener, View.OnClickListener yesOnClickListener, String name, boolean isVine) {
        super(context);
        mIsVine = isVine;
        mName = name;
        mNoOnClickListener = noOnClickListener;
        mYesOnClickListener = yesOnClickListener;
    }
    protected CustomDialog(@NonNull Context context, View.OnClickListener noOnClickListener, View.OnClickListener yesOnClickListener, int count) {
        super(context);
        mIsVine = false;
        mCount = count;
        mNoOnClickListener = noOnClickListener;
        mYesOnClickListener = yesOnClickListener;
    }
}
