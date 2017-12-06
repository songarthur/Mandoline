package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.songa.mandoline.artwork.lastfm.LastFmArtworkProvider;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class in charge of scheduling and executing artwork loading jobs. The class is backed by its own
 * {@link ThreadPoolExecutor}.
 */
public class ArtworkJobExecutor implements ArtworkJobTargetProvider, ArtworkJobListener
{
    /**
     * Keeps track of scheduled/running jobs. Mirrors jobTargets.
     * Must keep a one to one relationship between an Imageview and an ArtworkJob.
     */
    private final Map<ImageView,ArtworkJob> allJobs = new ConcurrentHashMap<>();

    /**
     * Keeps track of scheduled/running jobs. Mirrors allJobs.
     * Must keep a one to one relationship between an Imageview and an ArtworkJob.
     */
    private final Map<ArtworkJob,ImageView> jobTargets = new ConcurrentHashMap<>();

    /**
     * The executor's thread pool.
     */
    private final ThreadPoolExecutor executor;

    /**
     * The executor's job queue.
     */
    private final BlockingQueue<Runnable> executorQueue;

    /**
     * The artwork provider.
     */
    private final ArtworkProvider artworkProvider;

    public ArtworkJobExecutor()
    {
        executorQueue = new LinkedBlockingDeque<>();
        executor = new ThreadPoolExecutor(0, 3, 60, TimeUnit.SECONDS, executorQueue);

        artworkProvider = new LastFmArtworkProvider();
    }

    /**
     * Schedules an artwork loading job for a given request. If the thread pool is busy, the job is
     * enqueued and executed later following a FIFO policy. <br>
     * If a job has previously been scheduled for the same imageView it will be canceled before
     * starting this one.
     *
     * @param request The artwork load request
     * @param target The imageView to load the artwork into
     */
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

    /**
     * Cancels any job scheduled for the given imageView.
     *
     * @param imageView
     */
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

    /**
     * Called when a job has completed. Removes any reference to this job and to the target ImageView.
     *
     * @param job
     */
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

    /**
     * Returns the target {@link ImageView} for a given job.
     *
     * @param job
     * @return
     */
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
