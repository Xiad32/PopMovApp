package com.example.popmovapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>
    implements View.OnClickListener{
    //private int mCount = 0;
    private Context mContext;
    private ArrayList<MovieEntry> mData;

    private MovieClickListener mMovieClickListener;

    public interface MovieClickListener{
        void onMovieClick(int position);
    }

    public MovieListAdapter(Context context, MovieClickListener movieClickListener, ArrayList<MovieEntry> data){
        mContext = context;
        mData = data;
        mMovieClickListener = movieClickListener;
//        if (mData != null)
//            mCount = data.size();
    }

//    @Override
//    public void onBindViewHolder(MovieViewHolder holder, int position, List<Object> payloads) {
//        super.onBindViewHolder(holder, position, payloads);
//        holder.BindView(position);
//    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.movie_item,parent, false);

        //Set the height of the entry:
        int parentHeight = parent.getMeasuredHeight();
        itemView.setMinimumHeight((int) Math.floor(parentHeight/4));
        MovieViewHolder viewHolder = new MovieViewHolder(itemView);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        if (mData!= null)
            return mData.size();
        else
            return 0;
    }

    public void updateData(ArrayList<MovieEntry> newData){
        mData = newData;
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        // view Items
        ImageView posterIV;
        TextView movieTitleTV;


        public MovieViewHolder(View view){
            super(view);

            posterIV = view.findViewById(R.id.movie_poster_iv);
            movieTitleTV = view.findViewById(R.id.movie_tilte_tv);

            view.setOnClickListener(this);
        }

        void bind (int index){
            //populate the view
            Picasso.get()
                    .load(APIUtils.resolveImageURL(mData.get(index).getPosterURL()))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.emptyiconcrop))
                    .error(mContext.getResources().getDrawable(R.drawable.emptyiconcrop))
                    .into(posterIV);
            movieTitleTV.setText(mData.get(index).getTitle());
        }


        @Override
        public void onClick(View v) {
            Log.i(this.getClass().getSimpleName(), "onClick: "+"Item Clicked.");
            mMovieClickListener.onMovieClick(getAdapterPosition());
        }
    }



}
