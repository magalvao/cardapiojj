package com.keyo.cardapio.lojinha.presenter;

import android.support.annotation.Nullable;

import com.keyo.cardapio.base.BasePresenter;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.lojinha.bo.LojinhaBO;
import com.keyo.cardapio.lojinha.model.Order;
import com.keyo.cardapio.lojinha.model.Track;
import com.keyo.cardapio.lojinha.view.LojinhaView;
import com.keyo.cardapio.task.AppTask;
import com.keyo.cardapio.task.AppTaskExecutor;

import java.util.List;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public class LojinhaPresenter extends BasePresenter {


    private final LojinhaBO mLojinhaBO;
    private final AppTaskExecutor mAppTaskExecutor;
    private final AppPreferences mAppPreferences;
    private final LojinhaView mView;

    public LojinhaPresenter(final LojinhaBO lojinhaBO, final AppTaskExecutor appTaskExecutor,
                            final AppPreferences appPreferences, LojinhaView view) {

        mLojinhaBO = lojinhaBO;
        mAppTaskExecutor = appTaskExecutor;
        mAppPreferences = appPreferences;
        mView = view;
    }

    public void startUpdate() {
        mAppTaskExecutor.async(new LojinhaPresenter.UpdateTrackingTask());
    }

    public void onAddClicked() {
        mView.showInputDialog();
    }

    public void saveOrder(final String value) {
        mLojinhaBO.saveOrder(value);
        mAppTaskExecutor.async(new FetchPedidosTask());

    }

    private class UpdateTrackingTask implements AppTask<Track> {

        @Override
        public Track execute() {
            return mLojinhaBO.fetchTracking();
        }

        @Override
        public void onPostExecute(@Nullable final Track result) {
            if (result != null) {
                mView.updateTracking(result.getLastTrackNumber());
                mAppTaskExecutor.async(new FetchPedidosTask());
            } else {
                mView.showErrorMessage();
            }
        }
    }

    private class FetchPedidosTask implements AppTask<List<Order>> {
        @Override
        public List<Order> execute() {
            return mLojinhaBO.fetchPedidos();
        }

        @Override
        public void onPostExecute(@Nullable final List<Order> result) {
            mView.showOrders(result);
        }
    }
}
