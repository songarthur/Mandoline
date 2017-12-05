package com.songa.mandoline.ui.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface ViewholderListener<T extends RecyclerView.ViewHolder>
{
    void onClick(@NonNull T holder, @NonNull View view);
}
