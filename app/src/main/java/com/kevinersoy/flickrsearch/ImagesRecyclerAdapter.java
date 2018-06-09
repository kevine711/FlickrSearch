package com.kevinersoy.flickrsearch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevinersoy.flickrsearch.models.GalleryItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ImageViewHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    public List<GalleryItem> mList;

    public ImagesRecyclerAdapter(Context context, List<GalleryItem> list) {
        //Constructor - set fields and get column indices
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void updateList(List<GalleryItem> list){
        if(list == null){
            mList.clear();
        } else {
            mList = list;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_list, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = mList.get(position).getUrl();
        Picasso.with(mContext).load(imageUrl).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if(mList != null)
            return mList.size();
        else
            return 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        //using inner class to define ViewHolder since it will only be used with this Adapter
        public final ImageView mImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.image_card);

        }
    }
}
