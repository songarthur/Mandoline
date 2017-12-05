package com.songa.mandoline.artwork;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import java.io.File;

public class ArtworkRequest
{
    private final @NonNull ArtworkJobExecutor executor;

    private final @Nullable String artistName;
    private final @Nullable String albumName;

    private @DrawableRes @Nullable Integer errorDrawable;
    private @DrawableRes @Nullable Integer placeholderDrawable;

    private @Nullable String localArtworkFallback;

    private @Nullable Palette.PaletteAsyncListener paletteListener;

    public ArtworkRequest(@NonNull ArtworkJobExecutor executor, @Nullable String artistName, @Nullable String albumName)
    {
        this.executor = executor;
        this.artistName = artistName;
        this.albumName = albumName;
        this.errorDrawable = null;
        this.placeholderDrawable = null;
        this.localArtworkFallback = null;
    }

    public ArtworkRequest error(@DrawableRes int errorDrawable)
    {
        this.errorDrawable = errorDrawable;
        return this;
    }

    public ArtworkRequest placeholder(@DrawableRes int placeholderDrawable)
    {
        this.placeholderDrawable = placeholderDrawable;
        return this;
    }

    public ArtworkRequest localArtworkFallback(@Nullable String localArtworkFallback)
    {
        this.localArtworkFallback = localArtworkFallback;
        return this;
    }


    public void into(@NonNull ImageView imageView)
    {
        if (placeholderDrawable!=null) {
            imageView.setImageResource(placeholderDrawable);
        }
        executor.scheduleJob(this, imageView);
    }

    @Nullable
    public String getArtistName() {
        return artistName;
    }

    @Nullable
    public String getAlbumName() {
        return albumName;
    }

    @Nullable
    public Integer getErrorDrawable() {
        return errorDrawable;
    }

    @Nullable
    public Integer getPlaceholderDrawable() {
        return placeholderDrawable;
    }

    @Nullable
    public String getLocalArtworkFallback() {
        return localArtworkFallback;
    }
}
