package com.miymayster.myvine;

import android.content.ContentUris;
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

/**
 * Created by Olga on 25.07.2017.
 */

public class VineListAdapter extends RecyclerView.Adapter<VineListAdapter.VineHolder> {
    Cursor mCursor;

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        if (mCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public VineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VineHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vine_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VineHolder holder, int position) {

        if (mCursor.moveToPosition(position)) {
            int idColumnIndex = mCursor.getColumnIndex(MyVineContract.VineEntries._ID);
            int nameColumnIndex = mCursor.getColumnIndex(MyVineContract.VineEntries.COLUMN_NAME);
            int kindColumnIndex = mCursor.getColumnIndex(MyVineContract.VineEntries.COLUMN_KIND);
            int imageColumnIndex = mCursor.getColumnIndex(MyVineContract.VineEntries.COLUMN_LAST_IMAGE_PATH);

            final long id = mCursor.getLong(idColumnIndex);
            String name = mCursor.getString(nameColumnIndex);
            String kind = mCursor.getString(kindColumnIndex);
            String image = mCursor.getString(imageColumnIndex);


            holder.bind(image, name, kind);
            holder.itemView.setTag(id);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent vineInfoIntent = new Intent(v.getContext(), VineInfoActivity.class);
                    vineInfoIntent.setData(ContentUris.withAppendedId(MyVineContract.VineEntries.CONTENT_URI, id));
                    v.getContext().startActivity(vineInfoIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    public class VineHolder extends RecyclerView.ViewHolder {
        ImageView vineImage;
        TextView vineName;
        TextView vineKind;

        public VineHolder(View itemView) {
            super(itemView);
            vineImage = (ImageView) itemView.findViewById(R.id.vine_image);
            vineName = (TextView) itemView.findViewById(R.id.vine_name);
            vineKind = (TextView) itemView.findViewById(R.id.vine_kind);
        }

        public void bind(int imageRes, String name, String kind) {
            vineImage.setImageResource(imageRes);
            vineName.setText(name);
            vineKind.setText(kind);
        }

        public void bind(String imagePath, String name, String kind) {
            if(!TextUtils.isEmpty(imagePath)) {
                Picasso.with(vineImage.getContext())
                        .load("file:"+imagePath)
                        .resize(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.vine_width), itemView.getContext().getResources().getDimensionPixelSize(R.dimen.vine_width))
                        .centerCrop()
                        .into(vineImage);
            }else{
                Picasso.with(vineImage.getContext())
                        .load(R.drawable.ic_no_photo)
                        .into(vineImage);
            }
            vineName.setText(name);
            vineKind.setText(kind);
        }
    }
}
