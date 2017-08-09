package com.keyo.cardapio.lojinha.view;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keyo.cardapio.BuildConfig;
import com.keyo.cardapio.R;
import com.keyo.cardapio.base.BaseActivity;
import com.keyo.cardapio.base.Screens;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.lojinha.bo.LojinhaBO;
import com.keyo.cardapio.lojinha.dao.LojinhaDAO;
import com.keyo.cardapio.lojinha.model.Order;
import com.keyo.cardapio.lojinha.presenter.LojinhaPresenter;
import com.keyo.cardapio.task.AppTaskExecutor;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public class LojinhaActivity extends BaseActivity<LojinhaPresenter> implements LojinhaView {

    private TextView mLastOrderTextView;
    private TextView mOrderDateTextView;
    private SweetAlertDialog mDialog;
    private OrderListAdapter mAdapter;
    private View mEmptyState;
    private Dialog mOrderDialog;
    private Dialog mInputDialog;

    public static Intent createIntent(final Context context) {
        return new Intent(context, LojinhaActivity.class);
    }

    @NonNull
    @Override
    public String getScreenName() {
        return Screens.LOJINHA;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog != null) {
            mDialog.dismiss();
        }
        if(mOrderDialog != null) {
            mOrderDialog.cancel();
        }
        if(mInputDialog != null) {
            mInputDialog.cancel();
        }
    }

    @NonNull
    @Override
    protected LojinhaPresenter createPresenter(@NonNull final Context context) {
        AppPreferences appPreferences = new AppPreferences(context);
        return new LojinhaPresenter(new LojinhaBO(new LojinhaDAO(BuildConfig.TRACKING_BASE_URL, appPreferences)),
                new AppTaskExecutor(LojinhaActivity.this), new AppPreferences(this), this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lojinha);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        mEmptyState = findViewById(R.id.emptyState);
        View fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> mPresenter.onAddClicked());
        myToolbar.setTitle("Lojinha");
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLastOrderTextView = (TextView) findViewById(R.id.last_order);
        mOrderDateTextView = (TextView) findViewById(R.id.order_date);

        mAdapter = new OrderListAdapter(this);
        mAdapter.setOnItemClickedListener(new OrderItemClicked());
        list.setAdapter(mAdapter);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        showLastData();
        mPresenter.startUpdate();

        mLastOrderTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Último pedido", mLastOrderTextView.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(LojinhaActivity.this, "Copiado!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lojinha, menu);
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

        if (id == R.id.action_share) {
            if (!mLastOrderTextView.getText().toString().equals("-")) {
                shareNumber();
            } else {
                Snackbar.make(findViewById(R.id.rootLayout),
                        "Número indisponível!", Snackbar.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareNumber() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Último pedido na Lojinha");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Último pedido disponível na Lojinha: "
                + mLastOrderTextView.getText() + " (" + mOrderDateTextView.getText() + ")");
        startActivity(Intent.createChooser(sharingIntent, "Compartilhar onde?"));
    }

    @Override
    public void updateTracking(final String lastTrackNumber) {
        mLastOrderTextView.setText(lastTrackNumber);
        mAdapter.setLastOrder(lastTrackNumber);

        DateTime dt = DateTime.now();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        mOrderDateTextView.setText(dt.toString(fmt));
    }

    @Override
    public void deleteOrder(final String order) {
        mPresenter.deleteOrder(order);
    }

    @Override
    public void showLastData() {
        AppPreferences pref = new AppPreferences(this);
        if (pref.getLastTrackingNumber().equals("")) {
            mLastOrderTextView.setText("-");
            mOrderDateTextView.setText("-");
        } else {
            mLastOrderTextView.setText(pref.getLastTrackingNumber());

            DateTime dt = new DateTime(pref.getLastTrackingTime());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
            mOrderDateTextView.setText(dt.toString(fmt));
        }
    }

    @Override
    public void showErrorMessage() {
        Snackbar.make(findViewById(R.id.rootLayout),
                "Não foi possível atualizar. Verifique sua conexão com a Internet.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showOrders(final List<Order> result) {
        mAdapter.setOrders(result);

        if (result.isEmpty()) {
            mEmptyState.setVisibility(View.VISIBLE);
        } else {
            mEmptyState.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showInputDialog() {
        mInputDialog = new Dialog(LojinhaActivity.this);
        mInputDialog.setContentView(R.layout.dialog_input);

        TextView cancel = (TextView) mInputDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener( v -> mInputDialog.cancel() );

        TextView ok = (TextView) mInputDialog.findViewById(R.id.ok);
        ok.setOnClickListener( v -> {
            String value = ((EditText) mInputDialog.findViewById(R.id.input)).getText().toString();
            if(value.length() > 0) {
                mPresenter.saveOrder(value);
                mInputDialog.dismiss();
            }
        } );

        mInputDialog.show();
    }

    @Override
    public void showOrderDialog(final String order, final boolean ready) {
        mOrderDialog = new Dialog(LojinhaActivity.this);
        mOrderDialog.setContentView(R.layout.dialog_vieworder);
        mOrderDialog.findViewById(R.id.closeButton).setOnClickListener(v -> mOrderDialog.dismiss());
        ((TextView) mOrderDialog.findViewById(R.id.orderNumber)).setText(order);

        if(!ready) {
            ((ImageView) mOrderDialog.findViewById(R.id.orderStatus)).setImageResource(R.drawable.check_deny);
            ((TextView) mOrderDialog.findViewById(R.id.orderStatusText)).setText("Não está pronto");
        }

        mOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mOrderDialog.show();
    }

    private class OrderItemClicked implements OrderListAdapter.OnItemClicked {

        @Override
        public void deleteClicked(final String order) {
            deleteOrder(order);
        }

        @Override
        public void itemClicked(final String order, boolean ready) {
            showOrderDialog(order, ready);
        }
    }
}
