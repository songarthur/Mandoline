package com.songa.mandoline.artwork;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import java.io.File;

/**
 * An artwork load request.
 * Do not instanciate the class yourself, use instead {@link Artwork#load(String, String)}.
 */
public class ArtworkRequest
{
    private final @NonNull ArtworkJobExecutor executor;

    private final @Nullable String artistName;
    private final @Nullable String albumName;

    private @DrawableRes @Nullable Integer errorDrawable;
    private @DrawableRes @Nullable Integer placeholderDrawable;

    private @Nullable String localArtworkFallback;

    /**
     * A new artwork request.
     * If you wish to load an artwork for an artist, albumName must be left null.
     * For an album, both parameters must be filled.
     *
     * @param executor
     * @param artistName
     * @param albumName
     */
    public ArtworkRequest(@NonNull ArtworkJobExecutor executor, @Nullable String artistName, @Nullable String albumName)
    {
        this.executor = executor;
        this.artistName = artistName;
        this.albumName = albumName;
        this.errorDrawable = null;
        this.placeholderDrawable = null;
        this.localArtworkFallback = null;
    }

    /**
     * Drawable to be used in case of an error during the load.
     *
     * @param errorDrawable
     * @return
     */
    public ArtworkRequest error(@DrawableRes int errorDrawable)
    {
        this.errorDrawable = errorDrawable;
        return this;
    }

    /**
     * Placeholder to be used while loading the artwork.
     *
     * @param placeholderDrawable
     * @return
     */
    public ArtworkRequest placeholder(@DrawableRes int placeholderDrawable)
    {
        this.placeholderDrawable = placeholderDrawable;
        return this;
    }

    /**
     * Local artwork to be used in case of an error, or if no other artwork could be found.
     *
     * @param localArtworkFallback
     * @return
     */
    public ArtworkRequest localArtworkFallback(@Nullable String localArtworkFallback)
    {
        this.localArtworkFallback = localArtworkFallback;
        return this;
    }

    /**
     * Execute the load.
     *
     * @param imageView Where to load the artwork
     */
    public void into(@NonNull ImageView imageView)
    {
        if (placeholderDrawable!=null) {
            imageView.setImageResource(placeholderDrawable);
        }
        executor.scheduleJob(this, imageView);
    }

    /**
     * @return The artist featured in the artwork
     */
    @Nullable
    public String getArtistName() {
        return artistName;
    }

    /**
     * @return The album featured in the artwork
     */
    @Nullable
    public String getAlbumName() {
        return albumName;
    }

    /**
     * @return The error drawable
     */
    @Nullable
    public Integer getErrorDrawable() {
        return errorDrawable;
    }

    /**
     * @return The drawable displayed while loading
     */
    @Nullable
    public Integer getPlaceholderDrawable() {
        return placeholderDrawable;
    }

    /**
     * @return The local artwork to be used in case of an error, or if no other artwork could be found.
     */
    @Nullable
    public String getLocalArtworkFallback() {
        return localArtworkFallback;
    }
}
