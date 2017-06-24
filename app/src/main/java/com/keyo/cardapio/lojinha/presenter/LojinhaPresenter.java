package com.keyo.cardapio.lojinha.presenter;

import android.support.annotation.Nullable;

import com.keyo.cardapio.base.BasePresenter;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.lojinha.bo.LojinhaBO;
import com.keyo.cardapio.lojinha.model.Track;
import com.keyo.cardapio.lojinha.view.LojinhaView;
import com.keyo.cardapio.task.AppTask;
import com.keyo.cardapio.task.AppTaskExecutor;

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

    private class UpdateTrackingTask implements AppTask<Track> {

        @Override
        public Track execute() {
            return mLojinhaBO.fetchTracking();
        }

        @Override
        public void onPostExecute(@Nullable final Track result) {

            if (result != null) {
                mView.updateTracking(result.getLastTrackNumber());
            } else {
                mView.showErrorMessage();
            }
        }
    }
}
