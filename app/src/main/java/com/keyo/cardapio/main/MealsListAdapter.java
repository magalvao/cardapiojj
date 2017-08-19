package com.keyo.cardapio.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyo.cardapio.R;
import com.keyo.cardapio.model.CardapioCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgalvao3 on 02/02/17.
 */

public class MealsListAdapter extends RecyclerView.Adapter<MealsListAdapter.ViewHolder> {

    public static final int TRESHOLD_VOTES_MINIMUM = 10;
    private final Context mContext;
    private       List<CardapioCategory> mMeals;
    private ViewGroup        mParent;
    //private       DaysEventsClickListener mListener;

    public MealsListAdapter(@NonNull final Context context) {
        mMeals = new ArrayList<>();
        mContext = context;
    }

    public void addCategories(@NonNull final List<CardapioCategory> categories) {
        mMeals.clear();
        mMeals.addAll(categories);
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
        final CardapioCategory category = mMeals.get(position);
        final boolean isFirst = (position == 0);
        holder.bind(category, isFirst);
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
        private final TextView   mMealTitle;
        private final TextView   mMealDescription;
        private final TextView   mMealLikes;
        private final TextView   mMealDislikes;
        private final View       mCategoryView;
        private final ImageView  mCategoryIcon;
        private final TextView   mCategoryTitle;
        private CardapioCategory mCategory;

        public ViewHolder(final View itemView) {
            super(itemView);
            mMealTitle = (TextView) itemView.findViewById(R.id.meal_title);
            mMealDescription = (TextView) itemView.findViewById(R.id.meal_description);
            mMealLikes = (TextView) itemView.findViewById(R.id.meal_likes);
            mMealDislikes = (TextView) itemView.findViewById(R.id.meal_dislikes);

            mMealLikes.setVisibility(View.GONE);
            mMealDislikes.setVisibility(View.GONE);

            mCategoryView = itemView.findViewById(R.id.categoryView);
            mCategoryIcon = (ImageView) itemView.findViewById(R.id.categoryIcon);
            mCategoryTitle = (TextView) itemView.findViewById(R.id.categoryTitle);

            View like = itemView.findViewById(R.id.like);
            View dislike = itemView.findViewById(R.id.dislike);

            like.setOnClickListener(new LikeListener(1));
            dislike.setOnClickListener(new LikeListener(-1));

            //mEventBackground = itemView.findViewById(R.id.event_background);
            //itemView.setOnClickListener(new OnItemClicked());
        }

        public void bind(final CardapioCategory category, boolean isFirst) {
            mCategory = category;
            mMealTitle.setText(category.getName());
            mMealDescription.setText(category.getDescription());

            if(category.isFirst()) {
                mCategoryView.setVisibility(View.VISIBLE);
                mCategoryTitle.setText(category.getCategory());

                Drawable icon;

                switch (category.getCategory()) {
                    case CardapioCategory.CATEGORY_HEALTH:
                        icon = ContextCompat.getDrawable(mContext, R.drawable.apple_icon);
                        break;
                    case CardapioCategory.CATEGORY_HOMEMADE:
                        icon = ContextCompat.getDrawable(mContext, R.drawable.pan_icon);
                        break;
                    case CardapioCategory.CATEGORY_CHOICE:
                    default:
                        icon = ContextCompat.getDrawable(mContext, R.drawable.turkey_icon);
                        break;
                }

                mCategoryIcon.setImageDrawable(icon);
            } else {
                mCategoryView.setVisibility(View.GONE);
            }

            /*double total = meal.getLikes() + meal.getDislikes();
            if(total > TRESHOLD_VOTES_MINIMUM) {
                int likes = (int) ((meal.getLikes() / total) * 100);
                int dislikes = (int) ((meal.getDislikes() / total) * 100);

                mMealLikes.setText(likes + "%");
                mMealDislikes.setText(dislikes + "%");
            } else {
                mMealLikes.setText("-");
                mMealDislikes.setText("-");
            }*/
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
