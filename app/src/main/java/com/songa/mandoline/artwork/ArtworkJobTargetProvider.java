package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

public interface ArtworkJobTargetProvider
{
    @Nullable ImageView getTarget(@NonNull ArtworkJob job);
}
