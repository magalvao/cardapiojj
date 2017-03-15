package com.keyo.cardapio.task;

import android.support.annotation.NonNull;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public interface TaskExecutor {

    <T> void async(@NonNull final AppTask<T> task);
}
