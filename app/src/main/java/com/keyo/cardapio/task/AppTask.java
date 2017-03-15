package com.keyo.cardapio.task;

import android.support.annotation.Nullable;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public interface AppTask<T> {

    T execute();

    void onPostExecute(@Nullable final T result);

    abstract class SimpleAppTask implements AppTask<Boolean> {

        @Override
        public Boolean execute() {
            simpleExecute();
            return true;
        }

        @Override
        public void onPostExecute(@Nullable final Boolean result) {
            onPostExecute();
        }

        public abstract void simpleExecute();

        public abstract void onPostExecute();
    }
}
