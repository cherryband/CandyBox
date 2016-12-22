package org.quna.candybox.listener;

import java.io.IOException;

/**
 * Created by graphene on 2016-12-08.
 */

public interface CallbackListener {
    void onLoadFinished();

    void onIOException(IOException e);

    void onEmptyResult();
}
