package com.miymayster.myvine;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Olga on 29.07.2017.
 */

public class VineEditPhotosAdapter extends RecyclerView.Adapter<VineEditPhotosAdapter.VinePhotoHolder> {


    Cursor mCursor;
    OnItemClickListener listener;
    Set<Long> mPhotosToDelete;

    public interface OnItemClickListener{
        void onPhotoClicked(long photoId, boolean delete);
    }

    public VineEditPhotosAdapter(Context context, Set<Long> photosToDelete){
        if(!(context instanceof OnItemClickListener)){
            throw new ClassCastException("Context must implement VineEditPhotosAdapter.OnItemClickListener");
        }
        listener = (OnItemClickListener) context;
        mPhotosToDelete = photosToDelete;
    }

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
    public VineEditPhotosAdapter.VinePhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VineEditPhotosAdapter.VinePhotoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vine_photo_item_delete, parent, false));
    }

    @Override
    public void onBindViewHolder(final VineEditPhotosAdapter.VinePhotoHolder holder, int position) {
        if(mCursor.moveToPosition(position)) {
            int idColumnIndex = mCursor.getColumnIndex(MyVineContract.VinePhotoEntries._ID);
            int pathColumnIndex = mCursor.getColumnIndex(MyVineContract.VinePhotoEntries.COLUMN_PATH);
            int aboutColumnIndex = mCursor.getColumnIndex(MyVineContract.VinePhotoEntries.COLUMN_ABOUT);

            final long id = mCursor.getLong(idColumnIndex);
            String path = mCursor.getString(pathColumnIndex);
            String about = mCursor.getString(aboutColumnIndex);

            boolean delete = false;
            if(mPhotosToDelete.contains(id)){
                delete = true;
            }
            holder.bind(path, about, delete);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean delete = true;
                    if(mPhotosToDelete.contains(id)){
                        delete = false;
                    }
                    listener.onPhotoClicked(id, delete);
                    holder.setDelete(delete);
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
        ImageView deleteImage;

        public VinePhotoHolder(View itemView) {
            super(itemView);
            vineImage = (ImageView) itemView.findViewById(R.id.vine_image);
            deleteImage = (ImageView) itemView.findViewById(R.id.vine_delete);
            vineText = (TextView) itemView.findViewById(R.id.vine_text);
        }
        public void setDelete(boolean delete){
            if(delete){
                deleteImage.setVisibility(View.VISIBLE);
            }else{
                deleteImage.setVisibility(View.INVISIBLE);
            }
        }
        public void bind(String imagePath, String text, boolean delete){
            if(!TextUtils.isEmpty(imagePath)) {
                Picasso.with(vineImage.getContext())
                        .load("file:"+imagePath)
                        .resize(
                                itemView.getContext().getResources().getDimensionPixelSize(R.dimen.vine_width),
                                itemView.getContext().getResources().getDimensionPixelSize(R.dimen.vine_width)
                        ).centerCrop()
                        .into(vineImage);
                vineImage.setVisibility(View.VISIBLE);
            }else{
                vineImage.setVisibility(View.INVISIBLE);
            }
            setDelete(delete);
            vineText.setText(text);
            if(TextUtils.isEmpty(text)){
                vineText.setVisibility(View.INVISIBLE);
            }else{
                vineText.setVisibility(View.VISIBLE);
            }
        }
    }
}