package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;

/**
 * Interface to be implemented for any class that wants to be notified when an artwork load job is
 * completed.
 */
public interface ArtworkJobListener
{
    /**
     * Called when a job completes. Successfully or not.
     *
     * @param job the completed job
     */
    void onJobCompleted(@NonNull ArtworkJob job);
}
