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
import com.keyo.cardapio.main.bo.Meal;
import com.keyo.cardapio.model.Cardapio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private Date         mDate;
    private RecyclerView mealsList;
    private MealsListAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Cardapio> mMeals;

    public static DayFragment newInstance(Date date, @NonNull final List<Cardapio> meals) {
        DayFragment fragment = new DayFragment();
        fragment.setDate(date);
        Bundle bundle = new Bundle(2);
        bundle.putLong(EXTRA_DATE, date.getTime());
        bundle.putSerializable(EXTRA_CARDAPIO, (ArrayList<Cardapio>)meals);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date date = new Date();
        date.setTime(getArguments().getLong(EXTRA_DATE));
        mDate = date;

        mMeals = (ArrayList<Cardapio>) getArguments().getSerializable(EXTRA_CARDAPIO);
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
        adapter.addMeals(generateMealList());
        mealsList.setAdapter(adapter);

        TextView title = (TextView) view.findViewById(R.id.date_title);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt","BR"));
        title.setText(capitalize(sdf.format(mDate)));
        return view;
    }

    private String capitalize(String text) {
        StringBuilder sb = new StringBuilder(text);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private List<Meal> generateMealList() {
        ArrayList<Meal> meals = new ArrayList<>();

        for (Cardapio item : mMeals) {
            meals.add(new Meal(item.getOptionName(), item.getDescription(), item.getLikes(), item.getDislikes()));
        }

        return meals;
    }

    public int getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setDate(Date date) {
        this.mDate = date;
    }
}
