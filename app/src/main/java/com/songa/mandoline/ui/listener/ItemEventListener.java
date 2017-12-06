package com.songa.mandoline.ui.listener;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Custom listener used to listen to click events. Allows us to pass objects while processing click
 * events without leaking view objects like viewholders.
 *
 * @param <T> The class
 */
public interface ItemEventListener<T>
{
    /**
     * When a click event occurs.
     *
     * @param item the object attached to the click event
     * @param view the view receiving the click event
     */
    void onClick(@NonNull T item, @NonNull View view);
}
