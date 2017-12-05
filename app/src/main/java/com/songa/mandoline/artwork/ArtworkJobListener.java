package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;

public interface ArtworkJobListener
{
    void onJobCompleted(@NonNull ArtworkJob job);
}
