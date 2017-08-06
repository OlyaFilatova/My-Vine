package com.miymayster.myvine;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miymayster.myvine.data.MyVineContract;
import com.miymayster.myvine.photo.PhotoUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Olga on 29.07.2017.
 */

public class VineInfoPhotosAdapter extends RecyclerView.Adapter<VineInfoPhotosAdapter.VinePhotoHolder> {

    Cursor mCursor;

    public void swapCursor(Cursor cursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = cursor;
        if(mCursor != null){
            this.notifyDataSetChanged();
        }
    }

    @Override
    public VinePhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VinePhotoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vine_photo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VinePhotoHolder holder, int position) {
        if(mCursor.moveToPosition(position)) {
            int idColumnIndex = mCursor.getColumnIndex(MyVineContract.VinePhotoEntries._ID);
            int pathColumnIndex = mCursor.getColumnIndex(MyVineContract.VinePhotoEntries.COLUMN_PATH);
            int aboutColumnIndex = mCursor.getColumnIndex(MyVineContract.VinePhotoEntries.COLUMN_ABOUT);

            final long id = mCursor.getLong(idColumnIndex);
            String path = mCursor.getString(pathColumnIndex);
            String about = mCursor.getString(aboutColumnIndex);

            holder.bind(path, about);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoInfoIntent = new Intent(v.getContext(), VinePhotoActivity.class);
                    photoInfoIntent.setData(ContentUris.withAppendedId(MyVineContract.VinePhotoEntries.CONTENT_URI, id));
                    v.getContext().startActivity(photoInfoIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mCursor == null) {
            return 0;
        }else{
            return mCursor.getCount();
        }
    }

    public class VinePhotoHolder extends RecyclerView.ViewHolder{
        ImageView vineImage;
        TextView vineText;

        public VinePhotoHolder(View itemView) {
            super(itemView);
            vineImage = (ImageView) itemView.findViewById(R.id.vine_image);
            vineText = (TextView) itemView.findViewById(R.id.vine_text);
        }
        public void bind(int imageRes, String text){
            vineImage.setImageResource(imageRes);
            vineText.setText(text);
        }
        public void bind(String imagePath, String text){
            if(!TextUtils.isEmpty(imagePath)) {
                Picasso.with(vineImage.getContext())
                        .load("file:"+imagePath)
                        .resize(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.vine_width), itemView.getContext().getResources().getDimensionPixelSize(R.dimen.vine_width))
                        .centerCrop()
                        .into(vineImage);
                vineImage.setVisibility(View.VISIBLE);
            }else{
                vineImage.setVisibility(View.INVISIBLE);
            }
            vineText.setText(text);
            if(TextUtils.isEmpty(text)){
                vineText.setVisibility(View.INVISIBLE);
            }else{
                vineText.setVisibility(View.VISIBLE);
            }
        }
    }
}
