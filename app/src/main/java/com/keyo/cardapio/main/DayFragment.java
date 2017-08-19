package com.keyo.cardapio.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keyo.cardapio.R;
import com.keyo.cardapio.model.CardapioCategory;
import com.keyo.cardapio.model.CardapioDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by mgalvao3 on 01/02/17.
 */
public class DayFragment extends Fragment {

    private static final String EXTRA_DATE = "EXTRA_DATE";
    public static final String EXTRA_CARDAPIO = "EXTRA_CARDAPIO";
    private Date                    mDate;
    private RecyclerView            mealsList;
    private MealsListAdapter        adapter;
    private LinearLayoutManager     mLinearLayoutManager;
    private CardapioDate mCardapio;

    public static DayFragment newInstance(@Nullable Date date, @NonNull final CardapioDate meals) {
        DayFragment fragment = new DayFragment();
        fragment.setDate(date);
        Bundle bundle = new Bundle(2);
        if(date != null) {
            bundle.putLong(EXTRA_DATE, date.getTime());
        }
        bundle.putSerializable(EXTRA_CARDAPIO, meals);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date date = new Date();
        date.setTime(getArguments().getLong(EXTRA_DATE));
        mDate = date;

        mCardapio = (CardapioDate) getArguments().getSerializable(EXTRA_CARDAPIO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable 
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.day_tab, container, false);

        mealsList = (RecyclerView) view.findViewById(R.id.mealsList);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mealsList.setLayoutManager(mLinearLayoutManager);

        adapter = new MealsListAdapter(getContext());
        adapter.addCategories(generateCategoriesList());
        mealsList.setAdapter(adapter);

        TextView title = (TextView) view.findViewById(R.id.date_title);
        if(mDate != null && mDate.after(new Date(0L))) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
            title.setText(capitalize(sdf.format(mDate)));
        } else {
            title.setVisibility(View.GONE);
        }
        return view;
    }

    private String capitalize(String text) {
        StringBuilder sb = new StringBuilder(text);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private List<CardapioCategory> generateCategoriesList() {
        return mCardapio.getCategories();
    }

    public int getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        if(mDate != null) {
            calendar.setTime(mDate);
        }
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Date getDate() {
        return mDate;
    }
}
