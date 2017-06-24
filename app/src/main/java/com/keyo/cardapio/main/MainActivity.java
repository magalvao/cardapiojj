package com.keyo.cardapio.main;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.keyo.cardapio.BuildConfig;
import com.keyo.cardapio.R;
import com.keyo.cardapio.base.BaseActivity;
import com.keyo.cardapio.base.Screens;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.dao.CardapioDAO;
import com.keyo.cardapio.help.view.HelpActivity;
import com.keyo.cardapio.lojinha.view.LojinhaActivity;
import com.keyo.cardapio.main.bo.CardapioBO;
import com.keyo.cardapio.main.dao.NotificationDAO;
import com.keyo.cardapio.main.presenter.MainPresenter;
import com.keyo.cardapio.main.view.MainView;
import com.keyo.cardapio.model.Cardapio;
import com.keyo.cardapio.service.CalendarDateUtils;
import com.keyo.cardapio.task.AppTaskExecutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mgalvao3 on 01/02/17.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayoutyout;
    private View mEmptyView;
    private SweetAlertDialog mDialog;
    private boolean mForcedRefresh = false;
    private Dialog mFeaturePopup;

    @NonNull
    @Override
    public String getScreenName() {
        return Screens.MAIN;
    }

    @NonNull
    @Override
    protected MainPresenter createPresenter(@NonNull Context context) {
        AppPreferences appPreferences = new AppPreferences(context);
        return new MainPresenter(new CardapioBO(new CardapioDAO(BuildConfig.SERVER_BASE_URL, appPreferences),
                new NotificationDAO(context, appPreferences, (AlarmManager) getSystemService(Context.ALARM_SERVICE))),
                new AppTaskExecutor(MainActivity.this), this, new AppPreferences(this), viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayoutyout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        mEmptyView = findViewById(R.id.emptyView);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Cardápio");
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(myToolbar);

        swipeRefreshLayoutyout.setOnRefreshListener(new CardapioRefresh());
        swipeRefreshLayoutyout.setEnabled(false);

        mPresenter.startUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if(mFeaturePopup != null) {
            mFeaturePopup.dismiss();
        }
    }


    @Override
    public void updateList(@NonNull final List<Cardapio> list) {

        tabLayout.removeAllTabs();

        if (list.isEmpty()
                || (!list.isEmpty() && list.get(0).getDate().before(CalendarDateUtils.getThisWeekMonday()))) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(DayFragment.newInstance(null, new ArrayList<Cardapio>()), "");
            viewPager.setVisibility(View.GONE);
            viewPager.setAdapter(adapter);
            mPresenter.setViewPager(viewPager);
            mEmptyView.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
        } else {

            mEmptyView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);


            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

            SimpleDateFormat sdfWeek = new SimpleDateFormat("EEE", new Locale("pt", "BR"));
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM", new Locale("pt", "BR"));

            HashMap<Date, List<Cardapio>> hash = new HashMap<>();

            for (Cardapio cardapio : list) {

                if (hash.containsKey(cardapio.getDate())) {
                    List<Cardapio> item = hash.get(cardapio.getDate());
                    item.add(cardapio);
                    hash.put(cardapio.getDate(), item);
                } else {
                    List<Cardapio> item = new ArrayList<>();
                    item.add(cardapio);
                    hash.put(cardapio.getDate(), item);
                }
            }

            TreeMap<Date, List<Cardapio>> map = (TreeMap<Date, List<Cardapio>>) orderListByDate(hash);
            for (Map.Entry<Date, List<Cardapio>> entry : map.entrySet()) {
                Date key = entry.getKey();
                List<Cardapio> value = entry.getValue();

                String weekdayName = sdfWeek.format(key);
                adapter.addFragment(DayFragment.newInstance(key, (ArrayList<Cardapio>) value),
                        weekdayName + "\n" + sdfDay.format(key));
            }


            viewPager.setAdapter(adapter);
            mPresenter.setViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            adapter.notifyDataSetChanged();
        }

        setRefreshing(false);
        tabLayout.invalidate();

    }

    @Override
    public void setRefreshing(boolean state) {
        if (state) {
            mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mDialog.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mDialog.setTitleText("Aguarde...");
            mDialog.setCancelable(false);
            mDialog.show();
        } else {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }
    }

    @Override
    public void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public void notifyUpdatedList() {
        Snackbar.make(findViewById(R.id.rootLayout), "Cardápio atualizado!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void notifyError() {
        Snackbar.make(findViewById(R.id.rootLayout),
            "Não foi possível atualizar. Verifique sua conexão com a Internet.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLastData() {
        AppPreferences prefs = new AppPreferences(this);
        updateList(new ArrayList<>(prefs.loadCardapio()));
    }

    @Override
    public void shareCardapio(final String textToShare, final Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE (dd/MMM)", Locale.getDefault());

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Cardápio de " + sdf.format(currentDate));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(sharingIntent, "Compartilhar onde?"));
    }

    @Override
    public void notifyNewFeature() {
        mFeaturePopup = new Dialog(MainActivity.this);
        mFeaturePopup.setContentView(R.layout.popup_feature_orders);
        mFeaturePopup.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mFeaturePopup.dismiss();
                mPresenter.popupClosed();
            }
        });
        mFeaturePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFeaturePopup.show();
    }

    private Map<Date, List<Cardapio>> orderListByDate(HashMap<Date, List<Cardapio>> hash) {
        return new TreeMap<Date, List<Cardapio>>(hash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
            //mPresenter.updateCardapio();
            return true;
        }

        if (id == R.id.action_alarm) {
            startActivity(ActivityAlarm.createIntent(this));
            return true;
        }

        if (id == R.id.action_info) {
            startActivity(HelpActivity.createIntent(this));
            return true;
        }

        if (id == R.id.action_lojinha) {
            startActivity(LojinhaActivity.createIntent(this));
            return true;
        }

        if(id == R.id.action_share) {
            if(tabLayout.getTabCount() > 1) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                DayFragment tab = (DayFragment) adapter.getItem(tabLayout.getSelectedTabPosition());
                Date currentDate = tab.getDate();

                AppPreferences pref = new AppPreferences(this);

                mPresenter.prepareToShare(currentDate, pref.loadCardapio());

            } else {
                Snackbar.make(findViewById(R.id.rootLayout),
                              "Nenhum cardápio disponível!", Snackbar.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CardapioRefresh implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            mPresenter.updateCardapio();
        }
    }
}
