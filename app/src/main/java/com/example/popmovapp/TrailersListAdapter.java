package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class TrailersListAdapter extends RecyclerView.Adapter<TrailersListAdapter.TrailerViewHolder>
        implements View.OnClickListener{

    private int mCount = 0;
    private Context mContext;
    private ArrayList<ContentValues> mData;
    private TrailersListAdapter.TrailerClickListener mTrailerClickListener;

    public TrailersListAdapter(Context context, ArrayList<ContentValues> data,
                               TrailerClickListener clickListener){
        mContext = context;
        mData = data;
        mTrailerClickListener = clickListener;
        if (mData != null)
            mCount = data.size();
    }



    public interface TrailerClickListener{
        void onTrailerClick(int position);
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.trailer_item,parent, false);

        return new TrailerViewHolder (itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else
            return 0;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateData(ArrayList<ContentValues> newData){
        mData = newData;
        this.notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // view Items
        TextView trailerTV;


        public TrailerViewHolder(View view) {
            super(view);

            trailerTV = view.findViewById(R.id.TrailerTV);
            view.setOnClickListener(this);
        }

        void bind(int index) {
            //populate the view
            trailerTV.setText("Trailer "+String.valueOf(index + 1));

        }

        @Override
        public void onClick(View v) {
            Log.i(this.getClass().getSimpleName(), "onClick: "+"Trailer Clicked.");
            mTrailerClickListener.onTrailerClick(getAdapterPosition());
        }

    }
}
