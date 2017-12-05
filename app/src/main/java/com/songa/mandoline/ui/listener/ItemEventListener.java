package com.songa.mandoline.ui.listener;

import android.support.annotation.NonNull;
import android.view.View;

public interface ItemEventListener<T>
{
    void onClick(@NonNull T item, @NonNull View view);
}
