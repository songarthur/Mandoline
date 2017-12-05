package com.songa.mandoline.artwork;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

public class ArtworkJob implements Runnable, Palette.PaletteAsyncListener
{
    private final @NonNull ArtworkRequest request;
    private final @NonNull ArtworkProvider provider;
    private final @NonNull ArtworkJobTargetProvider targetProvider;
    private final @NonNull ArtworkJobListener completionListener;

    public ArtworkJob(@NonNull ArtworkRequest request,
                      @NonNull ArtworkProvider provider,
                      @NonNull ArtworkJobTargetProvider targetProvider,
                      @NonNull ArtworkJobListener completionListener)
    {
        this.request = request;
        this.provider = provider;
        this.targetProvider = targetProvider;
        this.completionListener = completionListener;
    }

    @Override
    public void run()
    {
        ArtworkResult artwork = provider.getArtworkUrl(request.getArtistName(), request.getAlbumName());

        final ImageView target = targetProvider.getTarget(this);

        if (target == null) {
            completionListener.onJobCompleted(this);
            return;
        }

        if (artwork == null) {

            String fallback = request.getLocalArtworkFallback();

            if (fallback!=null && !fallback.isEmpty()) {
                provider.loadArtwork(
                        new ArtworkResult(fallback, true),
                        target,
                        request.getPlaceholderDrawable(),
                        request.getErrorDrawable(),
                        this);

            } else if (request.getErrorDrawable() != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        target.setImageResource(request.getErrorDrawable());
                    }
                });
            }

        } else {
            provider.loadArtwork(
                    artwork,
                    target,
                    request.getPlaceholderDrawable(),
                    request.getErrorDrawable(),
                    this);
        }

        completionListener.onJobCompleted(this);
    }

    @Override
    public void onGenerated(Palette palette)
    {
        Artwork.cachePalette(request.getArtistName(), request.getAlbumName(), palette);
    }
}
