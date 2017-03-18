package com.keyo.cardapio.main;

import android.content.Context;
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
import com.keyo.cardapio.main.presenter.MainPresenter;
import com.keyo.cardapio.main.view.MainView;
import com.keyo.cardapio.model.Cardapio;
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

    private ViewPager          viewPager;
    private TabLayout          tabLayout;
    private SwipeRefreshLayout swipeRefreshLayoutyout;
    private View               mEmptyView;
    private SweetAlertDialog   mDialog;

    @NonNull
    @Override
    public String getScreenName() {
        return Screens.MAIN;
    }

    @NonNull
    @Override
    protected MainPresenter createPresenter(@NonNull Context context) {
        return new MainPresenter(new CardapioDAO(BuildConfig.SERVER_BASE_URL),
                                 new AppTaskExecutor(MainActivity.this),
                                 this,
                                 new AppPreferences(this),
                                 viewPager);
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
        if(mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void updateList(@NonNull final List<Cardapio> list) {

        if (list.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {

            mEmptyView.setVisibility(View.GONE);

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

            TreeMap<Date, List<Cardapio>> map = (TreeMap<Date, List<Cardapio>>) orderListByDate
                    (hash);

            for (Map.Entry<Date, List<Cardapio>> entry : map.entrySet()) {
                Date key = entry.getKey();
                List<Cardapio> value = entry.getValue();

                String weekdayName = sdfWeek.format(key);
                adapter.addFragment(DayFragment.newInstance(key, (ArrayList<Cardapio>) value),
                                    weekdayName
                                            + "\n" + sdfDay.format(key));
            }


            viewPager.setAdapter(adapter);
            mPresenter.setViewPager(viewPager);
        }

        setRefreshing(false);

    }

    @Override
    public void setRefreshing(boolean state) {
       if(state) {
            mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mDialog.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mDialog.setTitleText("Aguarde...");
            mDialog.setCancelable(false);
            mDialog.show();
        } else {
            if(mDialog != null) {
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
        Snackbar.make(findViewById(R.id.rootLayout), "Cardápio atualizado!", Snackbar
                .LENGTH_SHORT).show();
    }

    @Override
    public void notifyError() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                mDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                mDialog.setTitleText("Ops...");
                mDialog.setContentText("Erro ao atualizar cardápio!\nVerifique sua conexão com a Internet.");
                mDialog.setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        setRefreshing(false);
                        mPresenter.displayTodayTab();
                    }
                });
                mDialog.show();
            }
        });
    }

    private Map<Date, List<Cardapio>> orderListByDate(HashMap<Date, List<Cardapio>> hash) {
        Map<Date, List<Cardapio>> map = new TreeMap<Date, List<Cardapio>>(hash);
        return map;
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
            mPresenter.updateCardapio();
            return true;
        }

        if (id == R.id.action_info) {
            startActivity(HelpActivity.createIntent(this));
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
