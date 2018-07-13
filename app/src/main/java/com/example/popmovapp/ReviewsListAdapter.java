package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewViewHolder>{

    private int mCount = 0;
    private Context mContext;
    private ArrayList<ContentValues> mData;

    public ReviewsListAdapter(Context context, ArrayList<ContentValues> data){
        mContext = context;
        mData = data;
        if (mData != null)
            mCount = data.size();
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.review_item,parent, false);

        return new ReviewViewHolder (itemView);
    }

    @Override
    public void onBindViewHolder( ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else
            return 0;
    }

    public void updateData(ArrayList<ContentValues> newData){
        mData = newData;
        this.notifyDataSetChanged();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        // view Items
        TextView reviewerTV;
        TextView reviewTV;


        public ReviewViewHolder(View view) {
            super(view);

            reviewerTV = view.findViewById(R.id.ReviewerTV);
            reviewTV = view.findViewById(R.id.ReviewTV);
        }

        void bind(int index) {
            //populate the view
            reviewerTV.setText(mData.get(index).getAsString(APIUtils.REVIEWER_KEY));
              reviewTV.setText(mData.get(index).getAsString(APIUtils.REVIEW_KEY));
        }
    }
}
