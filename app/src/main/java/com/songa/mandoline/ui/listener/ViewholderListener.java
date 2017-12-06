package com.songa.mandoline.ui.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Custom click listener for viewholders.
 *
 * @param <T> The viewholder's class
 */
public interface ViewholderListener<T extends RecyclerView.ViewHolder>
{
    /**
     * Click event.
     *
     * @param holder The viewholder receiving the click event
     * @param view The view receiving the click event
     */
    void onClick(@NonNull T holder, @NonNull View view);
}
