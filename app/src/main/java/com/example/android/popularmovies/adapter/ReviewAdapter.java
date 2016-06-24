package com.example.android.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kosrat on 6/24/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private Callback mCallback;
    private ArrayList<Review> mReviews;

    public interface Callback{
        void read(Review review, int position);
    }

    public ReviewAdapter(ArrayList<Review>reviews, Callback callback){
        mReviews = reviews;
        mCallback =callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Review review = mReviews.get(position);

        holder.mReview = review;

        holder.mAuthor.setText(review.getAuthor());
        holder.mContent.setText(review.getContent());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.read(review, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final View mView;
        private TextView mAuthor;
        private TextView mContent;
        public Review mReview;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAuthor = (TextView) itemView.findViewById(R.id.review_author);
            mContent = (TextView) itemView.findViewById(R.id.review_content);
        }
    }

    public void add(List<Review> reviews){
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviews(){
        return mReviews;
    }
}
