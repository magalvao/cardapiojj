package com.keyo.cardapio.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keyo.cardapio.R;
import com.keyo.cardapio.main.bo.Meal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgalvao3 on 02/02/17.
 */

public class MealsListAdapter extends RecyclerView.Adapter<MealsListAdapter.ViewHolder> {

    public static final int TRESHOLD_VOTES_MINIMUM = 10;
    private final Context mContext;
    private       List<Meal> mMeals;
    private ViewGroup        mParent;
    //private       DaysEventsClickListener mListener;

    public MealsListAdapter(@NonNull final Context context) {
        mMeals = new ArrayList<>();
        mContext = context;
    }

    public void addMeals(@NonNull final List<Meal> meals) {
        mMeals.clear();
        mMeals.addAll(meals);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        mParent = parent;
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_list_item, parent, false);
        return new MealsListAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //position belongs to note list
        final Meal meal = mMeals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return mMeals.size();
    }

//    public void setOnClickListener(@NonNull final DaysEventsClickListener onEventsClickListener) {
//        mListener = onEventsClickListener;
//    }

//    interface DaysEventsClickListener {
//
//        void onEventClicked(@NonNull final Migraine event);
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //private final View     mEventBackground;
        private final TextView mMealTitle;
        private final TextView mMealDescription;
        private final TextView mMealLikes;
        private final TextView mMealDislikes;
        private       Meal     mMeal;

        public ViewHolder(final View itemView) {
            super(itemView);
            mMealTitle = (TextView) itemView.findViewById(R.id.meal_title);
            mMealDescription = (TextView) itemView.findViewById(R.id.meal_description);
            mMealLikes = (TextView) itemView.findViewById(R.id.meal_likes);
            mMealDislikes = (TextView) itemView.findViewById(R.id.meal_dislikes);

            mMealLikes.setVisibility(View.GONE);
            mMealDislikes.setVisibility(View.GONE);

            View like = itemView.findViewById(R.id.like);
            View dislike = itemView.findViewById(R.id.dislike);

            like.setOnClickListener(new LikeListener(1));
            dislike.setOnClickListener(new LikeListener(-1));

            //mEventBackground = itemView.findViewById(R.id.event_background);
            //itemView.setOnClickListener(new OnItemClicked());
        }

        public void bind(final Meal meal) {
            mMeal = meal;
            mMealTitle.setText(meal.getName());
            mMealDescription.setText(meal.getOption());

            double total = meal.getLikes() + meal.getDislikes();
            if(total > TRESHOLD_VOTES_MINIMUM) {
                int likes = (int) ((meal.getLikes() / total) * 100);
                int dislikes = (int) ((meal.getDislikes() / total) * 100);

                mMealLikes.setText(likes + "%");
                mMealDislikes.setText(dislikes + "%");
            } else {
                mMealLikes.setText("-");
                mMealDislikes.setText("-");
            }
        }
    }

    private class LikeListener implements View.OnClickListener {

        int mVote;

        public LikeListener(int vote) {
            mVote = vote;
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(mParent, "Votação ainda não implementada.  \uD83D\uDE41", Snackbar.LENGTH_LONG).show();
        }
    }
}
