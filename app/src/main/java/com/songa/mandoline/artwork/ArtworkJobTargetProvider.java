package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

/**
 * Interface to be implemented by the class responsible for providing the target imageView for a
 * given job.
 */
public interface ArtworkJobTargetProvider
{
    /**
     * @param job
     * @return The imageView the job should load its artwork into
     */
    @Nullable ImageView getTarget(@NonNull ArtworkJob job);
}
