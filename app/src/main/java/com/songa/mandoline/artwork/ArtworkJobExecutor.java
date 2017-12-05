package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.songa.mandoline.artwork.lastfm.LastFmArtworkProvider;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ArtworkJobExecutor implements ArtworkJobTargetProvider, ArtworkJobListener
{
    private final Map<ImageView,ArtworkJob> allJobs = new ConcurrentHashMap<>();
    private final Map<ArtworkJob,ImageView> jobTargets = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor executor;
    private final BlockingQueue<Runnable> executorQueue;

    private final ArtworkProvider artworkProvider;

    public ArtworkJobExecutor()
    {
        executorQueue = new LinkedBlockingDeque<>();
        executor = new ThreadPoolExecutor(0, 2, 40, TimeUnit.SECONDS, executorQueue);

        artworkProvider = new LastFmArtworkProvider();
    }

    public void scheduleJob(@NonNull ArtworkRequest request, @NonNull ImageView target)
    {
        synchronized (this)
        {
            cancelJob(target);

            ArtworkJob job = new ArtworkJob(request, artworkProvider, this, this);
            allJobs.put(target, job);
            jobTargets.put(job, target);
            executor.execute(job);
        }
    }

    public void cancelJob(@NonNull ImageView imageView)
    {
        synchronized (this)
        {
            ArtworkJob toCancel = allJobs.remove(imageView);
            if (toCancel!=null) {
                jobTargets.remove(toCancel);
                executor.remove(toCancel);
            }

            artworkProvider.cancelLoad(imageView);
        }
    }

    @Override
    public void onJobCompleted(@NonNull ArtworkJob job)
    {
        synchronized (this)
        {
            ImageView target = jobTargets.remove(job);
            if (target!=null) {
                allJobs.remove(target);
            }
        }
    }

    @Nullable
    @Override
    public ImageView getTarget(@NonNull ArtworkJob job)
    {
        synchronized (this)
        {
            return jobTargets.get(job);
        }
    }

}
