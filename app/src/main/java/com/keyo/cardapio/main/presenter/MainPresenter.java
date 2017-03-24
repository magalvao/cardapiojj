package com.keyo.cardapio.main.presenter;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.keyo.cardapio.base.BasePresenter;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.main.DayFragment;
import com.keyo.cardapio.main.ViewPagerAdapter;
import com.keyo.cardapio.main.bo.CardapioBO;
import com.keyo.cardapio.main.view.MainView;
import com.keyo.cardapio.model.Cardapio;
import com.keyo.cardapio.task.AppTask;
import com.keyo.cardapio.task.TaskExecutor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mgalvao3 on 01/02/17.
 */
public class MainPresenter extends BasePresenter {

    private final CardapioBO mCardapioBO;
    private final TaskExecutor mTaskExecutor;
    private final MainView mView;
    private final AppPreferences mAppPreferences;
    private ViewPager mViewPager;
    private boolean firstTimeLoad = true;
    private int mLastSelectedWeekday = Calendar.MONDAY;

    public MainPresenter(CardapioBO cardapioBO, TaskExecutor taskExecutor, MainView view, AppPreferences appPreferences,
                         ViewPager viewPager) {
        mCardapioBO = cardapioBO;
        mTaskExecutor = taskExecutor;
        mView = view;
        mAppPreferences = appPreferences;
        mViewPager = viewPager;
    }

    public void startUpdate() {
        mView.updateList(new ArrayList<>(mAppPreferences.loadCardapio()));
        updateCardapio();
    }

    public void setViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    public void setLastSelectedWeekday(int mLastSelectedWeekday) {
        this.mLastSelectedWeekday = mLastSelectedWeekday;
    }

    public void displayTodayTab() {
        Calendar today = Calendar.getInstance();
        int todayWeekday = today.get(Calendar.DAY_OF_WEEK);

        displayTabByWeekend(todayWeekday);
    }

    private void displayTabByWeekend(int weekday) {
        if (mViewPager != null) {
            ViewPagerAdapter adapter = (ViewPagerAdapter) mViewPager.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                DayFragment dayFragment = (DayFragment) adapter.getItem(i);
                int tabDay = dayFragment.getWeekDay();

                if (weekday == tabDay) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }
    }

    public void updateCardapio() {
        if (mViewPager != null) {
            ViewPagerAdapter adapter = (ViewPagerAdapter) mViewPager.getAdapter();
            DayFragment day = (DayFragment) adapter.getItem(mViewPager.getCurrentItem());
            mLastSelectedWeekday = day.getWeekDay();
        }

        mView.setRefreshing(true);
        mTaskExecutor.async(new UpdateCardapioTask());
    }


    private class UpdateCardapioTask implements AppTask<List<Cardapio>> {

        @Override
        public List<Cardapio> execute() {
            return mCardapioBO.fetchCardapio();
        }

        @Override
        public void onPostExecute(@Nullable final List<Cardapio> result) {
            if (result != null && !result.isEmpty()) {
                mView.updateList(result);
                mView.setRefreshing(false);

                if (firstTimeLoad) {
                    displayTodayTab();
                } else {
                    displayTabByWeekend(mLastSelectedWeekday);
                }

                firstTimeLoad = false;

                mView.notifyUpdatedList();
            }
        }
    }
}
